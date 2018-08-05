package com.template;


// Add these imports:
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// Define ExporterFlow:
@InitiatedBy(UKTFFlow.class) //This is who initiates this flow (aka receiving a message from an Initiator from another class)
public class UKTFExporterFlow extends FlowLogic<Void> { //Extending the class for defining flows

    private final FlowSession otherPartySession;

    public UKTFExporterFlow(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        //SignTransactionFlow is a standard process for receiving a transaction signatures
        /*
            it checks the transaction, check its validity and send signature
         */
        class SignTxFlow extends SignTransactionFlow {
            private SignTxFlow(FlowSession otherPartySession, ProgressTracker progressTracker) {
                super(otherPartySession, progressTracker);
            }

            /*
            Checks on the transactions.
            This case needs to be less than 100
             */

            @Override
            protected void checkTransaction(SignedTransaction stx) {
                requireThat(require -> {
                    ContractState output = stx.getTx().getOutputs().get(0).getData();
                    require.using("This must be an UKTF transaction.", output instanceof UKTFState);
                    UKTFState bond = (UKTFState) output;
                    require.using("The UKTF bond's value can't be too high.", bond.getValue() < 100);
                    return null;
                });
            }
        }

        subFlow(new SignTxFlow(otherPartySession, SignTransactionFlow.Companion.tracker()));

        return null;
    }

}
