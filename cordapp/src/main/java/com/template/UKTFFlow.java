package com.template;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.StateAndContract;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;


import java.security.PublicKey;
import java.util.List;

import static com.template.UKTFContract.UKTF_CONTRACT_ID;

/**
 * Define your flow here.
 */
// Replace TemplateFlow's definition with:
@InitiatingFlow //startable by the node
@StartableByRPC //startable via RPC
public class UKTFFlow extends FlowLogic<Void> {
    private final Integer bondValue;
    private final Party otherParty;

    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();

    public UKTFFlow(Integer bondValue, Party otherParty) {
        this.bondValue = bondValue;
        this.otherParty = otherParty; //the node running the flow is the exporter (this one is the bank, that need to sign the transaction)
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    /**
     * The flow logic is encapsulated within the call() method.
     */
    @Suspendable //MUST be here
    @Override
    public Void call() throws FlowException {


        /*
         * ServiceHub provides all the info within a flow on the external world
         */
       final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

       /* THIS VERSION IS WITHOUT CHECKING THE CONTRACT (aka assuming an empty command */

        // We retrieve the notary identity from the network map.
        //The notary endorses the transaction avoiding double-spending attacks


        /*
         * STEP1 : To create a transaction:
         *  1. creating its components
         *  2. adding the component to the transaction builder
         */

        // 1. We create the transaction components.
//        UKTFState outputState = new UKTFState(bondValue, getOurIdentity(), otherParty); //This is the state that will be added to the ledger


        /*
         * Command specifies 'intent of a transaction' and 'required signers'
         */
//        CommandData cmdType = new UKTFContract.Create());
//        Command cmd = new Command<>(cmdType, getOurIdentity().getOwningKey()); //This lists the signer of the transaction (aka the leader node)

        // 2. We create a transaction builder and add the components.
        /* Arguents
         * 1. outputState
         * 2. Command
         */
//        final TransactionBuilder txBuilder = new TransactionBuilder(notary)
//                .addOutputState(outputState, UKTF_CONTRACT_ID)
//                .addCommand(cmd);

        // STEP2:  Signing the transaction.
//        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // STEP3&4: (Recording and broadcasting to all relevant party): Finalising the transaction.
//        subFlow(new FinalityFlow(signedTx));

        /* ---------------------------------------- */


        /* THIS VERSION checks THE CONTRACT */

        // We create a transaction builder.
        final TransactionBuilder txBuilder = new TransactionBuilder();
        txBuilder.setNotary(notary);

        // We create the transaction components.
        UKTFState outputState = new UKTFState(bondValue, getOurIdentity(), otherParty);
        StateAndContract outputContractAndState = new StateAndContract(outputState, UKTFContract.UKTF_CONTRACT_ID);
        List<PublicKey> requiredSigners = ImmutableList.of(getOurIdentity().getOwningKey(), otherParty.getOwningKey());
        Command cmd = new Command<>(new UKTFContract.Create(), requiredSigners); // create the command generating the transaction

        // We add the items to the builder.
        txBuilder.withItems(outputContractAndState, cmd); // CREATE the transaction

        // Verifying the transaction.
        txBuilder.verify(getServiceHub()); // this calls the contract that verify the transaction

        // Signing the transaction.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        /*
        This establish a session where the flowlogic is evaluated
         */

        // Creating a session with the other party.
        FlowSession otherpartySession = initiateFlow(otherParty);

        // Obtaining the counterparty's signature.
        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                signedTx, ImmutableList.of(otherpartySession), CollectSignaturesFlow.tracker()));

        // Finalising the transaction.
        subFlow(new FinalityFlow(fullySignedTx));

        return null;

    }

//    /**
//     * You can add a constructor to each FlowLogic subclass to pass objects into the flow.
//     */
//    @InitiatingFlow
//    @StartableByRPC
//    public static class Initiator extends FlowLogic<Void> {
//        /**
//         * Define the initiator's flow logic here.
//         */
//        @Suspendable
//        @Override public Void call() { return null; }
//    }
//
//    @InitiatedBy(Initiator.class)
//    public static class Responder extends FlowLogic<Void> {
//        private FlowSession counterpartySession;
//
//        public Responder(FlowSession counterpartySession) {
//            this.counterpartySession = counterpartySession;
//        }
//
//        /**
//         * Define the acceptor's flow logic here.
//         */
//        @Suspendable
//        @Override
//        public Void call() { return null; }
//    }




}
