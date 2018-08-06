package com.template;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;

import java.security.PublicKey;
import java.util.List;


@InitiatingFlow
@StartableByRPC
public class BankAssess extends FlowLogic<Void> {
    private final String bondID;
    /* the node running the flow is the exporter (this one is the bank, that need to sign the transaction) */
    private final Party exporter;
    private final Party ukef;
    private final String bankSupplyId;
    private double exporterTurnover;
    private double exporterNet;
    private int bankRiskLevel;
    private double bankCreditScore;

    private final Step PREPARATION = new Step("Retrieve state to amend.");
    //    private final Step GENERATING_UKEF_TRANSACTION = new Step("Generating transaction based on UKEF activity.");
    private final Step GENERATING_BANK_TRANSACTION = new Step("Creating bank transaction.");
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
            GENERATING_BANK_TRANSACTION,
            PREPARATION,
            SIGNING_TRANSACTION,
            GATHERING_SIGS,
            FINALISING_TRANSACTION
    );

    /**
     * @param bankSupplyContractID UUID created internally from the bank
     * @param turnover             exporter turnover
     * @param net                  exporter net income
     * @param riskLevel            [0 - lowest, 5 - highest]
     * @param creditScore          [0.0 lowest - 4.0 highest]
     * @param exporter             party
     * @param ukef                 party
     */
    public BankAssess(String bondID, String bankSupplyContractID, Double turnover, Double net, int riskLevel, Double creditScore, Party exporter, Party ukef) {
        this.bondID = bondID;
        this.exporter = exporter;
        this.ukef = ukef;
        this.bankSupplyId = bankSupplyContractID;
        this.exporterTurnover = turnover;
        this.exporterNet = net;
        this.bankRiskLevel = riskLevel;
        this.bankCreditScore = creditScore;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {

        //Notary for the transaction
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //Stage1 - generate transaction
        progressTracker.setCurrentStep(PREPARATION);

        //gather previous state to amend
        StateAndRef<UKTFBond> inputState = getUKTFBond(this.bondID);
        UKTFBond inputBond = inputState.getState().getData();

        if (!inputBond.getBank().equals(getOurIdentity())) {
            throw new FlowException("Assessment of exporter bond can only be done by the bank reported in the bond");
        }

        //UKTFBond outputBond = inputBond.copy(new Bond());
//        outputBond.setBankSupplyContractID(this.bankSupplyId);
//        outputBond.setBankCreditScore(this.bankCreditScore);
//        outputBond.setBankRiskLevel(this.bankRiskLevel);
//        outputBond.setExporterNet(this.exporterNet);
//        outputBond.setExporterTurnover(this.exporterNet);

        // Stage 2 - verifying trx
        progressTracker.setCurrentStep(GENERATING_BANK_TRANSACTION);

        List<PublicKey> requiredSigners = ImmutableList.of(getOurIdentity().getOwningKey(), exporter.getOwningKey(), ukef.getOwningKey());
        final Command<UKTFContract.Commands.BankAssess> cmd = new Command<>(new UKTFContract.Commands.BankAssess(), requiredSigners);
        final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addInputState(inputState)
              //  .addOutputState(outputBond, UKTFContract.UKTF_CONTRACT_ID)
                .addCommand(cmd);

        txBuilder.verify(getServiceHub());

        // Stage 3 - signing trx
        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);


        //Step 4 - gathering trx signs
        progressTracker.setCurrentStep(GATHERING_SIGS);

        //bank & ukef signatures
        FlowSession bankSession = initiateFlow(exporter);
        FlowSession ukefSession = initiateFlow(ukef);
        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                partSignedTx, ImmutableList.of(bankSession, ukefSession), CollectSignaturesFlow.tracker()));


        //Step 5 - finalising
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);

        subFlow(new FinalityFlow(fullySignedTx));

        return null;
    }


    StateAndRef<UKTFBond> getUKTFBond(String bondID) throws FlowException {

        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        List<StateAndRef<UKTFBond>> bonds = getServiceHub().getVaultService().queryBy(UKTFBond.class, criteria).getStates();

        while (bonds.iterator().hasNext()) {
            StateAndRef<UKTFBond> state = bonds.iterator().next();
            if (state.getState().getData().getBondID() == bondID)
                return state;
        }

        throw new FlowException(String.format("Bond with id %s not found", bondID));
    }


//    @InitiatedBy(BankAssess.class)
//    public static class UKTFBankFlow extends FlowLogic<Void> {
//
//        private FlowSession exporter;
//
//        public UKTFBankFlow(FlowSession exporter) {
//            this.exporter = exporter;
//        }
//
//        @Suspendable
//        @Override
//        public Void call() throws FlowException {
//
//            class SignExpTxFlow extends SignTransactionFlow {
//
//                private SignExpTxFlow(FlowSession exporter, ProgressTracker progressTracker) {
//                    super(exporter, progressTracker);
//                }
//
//                @Override
//                protected void checkTransaction(SignedTransaction stx) {
//                    requireThat(require -> {
//                        ContractState output = stx.getTx().getOutputs().get(0).getData();
//                        require.using("This must be an UKTF transaction.", output instanceof UKTFState);
//                        UKTFState bond = (UKTFState) output;
//                        require.using("The UKTF bond's value can't be null.", bond.getBondValue() > 0);
//                        return null;
//                    });
//                }
//            }
//
//
//            subFlow(new SignExpTxFlow(exporter, SignTransactionFlow.Companion.tracker()));
//
//            return null;
//        }
//    }

//
//    @InitiatedBy(UKTFBankFlow.class)
//    public static class UKTFUKEFFlow extends FlowLogic<Void> {
//
//        private FlowSession exporter;
//
//        public UKTFUKEFFlow (FlowSession exporter) {
//            this.exporter = exporter;
//        }
//
//        @Suspendable
//        @Override
//        public Void call() throws FlowException {
//
//            class SignExpTxFlow extends SignTransactionFlow {
//
//                private SignExpTxFlow(FlowSession exporter, ProgressTracker progressTracker) {
//                    super(exporter, progressTracker);
//                }
//
//                @Override
//                protected void checkTransaction(SignedTransaction stx) {
//                    requireThat(require -> {
//                        ContractState output = stx.getTx().getOutputs().get(0).getData();
//                        require.using("This transacation must involve a bank.", stx.getTx().getRequiredSigningKeys().size() == 2);
////                        UKTFState bond = (UKTFState) output;
////                        require.using("The UKTF bond's value can't be null.", bond.getBondValue() > 0);
//                        return null;
//                    });
//                }
//            }
//
//
//            subFlow(new SignExpTxFlow(exporter, SignTransactionFlow.Companion.tracker()));
//
//            return null;
//        }
//    }

}
