package com.template;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;


public class CreateBond {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final String externalBondID;
        private final Integer bondValue;
        /* the node running the flow is the exporter (these sign the transaction)*/
        private final Party bank;
        private final Party ukef;

        private final Step GENERATING_EXP_TRANSACTION = new Step("Generating transaction based on new Bond Request.");
        private final Step VERIFYING_TRANSACTION = new Step("Verifying contract constraints.");
        private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key.");
        private final Step GATHERING_SIGS = new Step("Gathering the counterparty's signature.") {
            @Override
            public ProgressTracker childProgressTracker() {
                return CollectSignaturesFlow.Companion.tracker();
            }
        };
        private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction.") {
            @Override
            public ProgressTracker childProgressTracker() {
                return FinalityFlow.Companion.tracker();
            }
        };


        private final ProgressTracker progressTracker = new ProgressTracker(
                GENERATING_EXP_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                GATHERING_SIGS,
                FINALISING_TRANSACTION
        );

        /**
         *
         * @param bondId    Id of the bond id to be processed
         * @param bondValue Value of the bond submitted by the exporter
         * @param bank      contractual party
         * @param ukef      contractual party
         */
        public Initiator(String bondId, Integer bondValue, Party bank, Party ukef) {
            this.externalBondID = bondId;
            this.bondValue = bondValue;
            this.bank = bank;
            this.ukef = ukef;
        }

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            //Notary for the transaction
            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            //Stage1 - generate transaction
            progressTracker.setCurrentStep(GENERATING_EXP_TRANSACTION);

            UKTFBondState outputState = new UKTFBondState(externalBondID, new Bond(bondValue), getOurIdentity(), bank, ukef);
            List<PublicKey> requiredSigners =  Arrays.asList(getOurIdentity().getOwningKey(), bank.getOwningKey(), ukef.getOwningKey());
            final Command<UKTFContract.Commands.Create> cmd = new Command<>(new UKTFContract.Commands.Create(), requiredSigners);
            final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(outputState, UKTFContract.UKTF_CONTRACT_ID)
                    .addCommand(cmd);


            // Stage 2 - verifying trx
            progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
            txBuilder.verify(getServiceHub());

            // Stage 3 - signing trx
            progressTracker.setCurrentStep(SIGNING_TRANSACTION);
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);


            //Step 4 - gathering trx signs
            progressTracker.setCurrentStep(GATHERING_SIGS);

            //bank & ukef signatures
            FlowSession bankSession = initiateFlow(bank);
            FlowSession ukefSession = initiateFlow(ukef);
            SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                    partSignedTx,  Arrays.asList(bankSession, ukefSession), CollectSignaturesFlow.tracker()));


            //Step 5 - finalising
            progressTracker.setCurrentStep(FINALISING_TRANSACTION);

            return subFlow(new FinalityFlow(fullySignedTx));
        }

    }

    @InitiatedBy(Initiator.class)
    public static class CreateBondResponder extends FlowLogic<SignedTransaction> {

        private FlowSession exporter;

        public CreateBondResponder(FlowSession exporter) {
            this.exporter = exporter;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            class SignExpTxFlow extends SignTransactionFlow {

                private SignExpTxFlow(FlowSession exporter, ProgressTracker progressTracker) {
                    super(exporter, progressTracker);
                }

                @Override
                protected void checkTransaction(SignedTransaction stx) {
                    requireThat(require -> {
                        ContractState output = stx.getTx().getOutputs().get(0).getData();
                        require.using("This must be an UKTF transaction.", output instanceof UKTFBondState);
                        UKTFBondState bond = (UKTFBondState) output;
                        require.using("The UKTF bond's value can't be null.", bond.getBondValue() > 0);
                        return null;
                    });
                }
            }


            return subFlow(new SignExpTxFlow(exporter, SignTransactionFlow.Companion.tracker()));
        }
    }

}
