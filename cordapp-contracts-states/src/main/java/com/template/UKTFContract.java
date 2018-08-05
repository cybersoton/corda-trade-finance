package com.template;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;


public class UKTFContract implements Contract {

    // This is used to identify the contract when building a transaction.
    public static final String UKTF_CONTRACT_ID = "com.template.UKTFContract";


    /*
     * All command must implement the interface CommandData.
     * This allow to have that different check can be done according to the type of command
     */

    public static class Commands implements CommandData {

        /**
         * The command create: issued by the exporter towards a bank and the UKEF.
         * All the three parties must be singners
         */
        public static class Create implements CommandData {

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Create;
            }
        }

        /**
         * The command BankAssess to submit its evaluation of the exporter contract
         */
        public static class BankAssess implements CommandData {

            @Override
            public boolean equals(Object obj) {
                return obj instanceof BankAssess;
            }
        }

        /**
         * The command UKEFAssess to submit its evaluation of the exporter contract
         */
        public static class UKEFAssess implements CommandData {

            @Override
            public boolean equals(Object obj) {
                return obj instanceof UKEFAssess;
            }
        }

    }


    /*
     * A transaction is considered valid if the verify() function of the contract of each of the transaction's input
     * and output states does not throw an exception.
     */
    @Override
    public void verify(LedgerTransaction tx) {

        /*
         * To get get access to the transaction tx. inputs | outputs | commands
         */

        // requireSingleCommand checks if the transaction contains only one single command
        final CommandWithParties<Commands.Create> command = requireSingleCommand(tx.getCommands(), UKTFContract.Commands.Create.class);

        requireThat(check -> {
            // Constraints on the shape of the transaction.
            check.using("No inputs should be consumed when issuing an IOU.", tx.getInputs().isEmpty());
            check.using("There should be one output state of type IOUState.", tx.getOutputs().size() == 1);

            // checking generated state
            final UKTFState out = tx.outputsOfType(UKTFState.class).get(0);
            final Party exporter = out.getExporter();
            final Party bank = out.getBank();
            check.using("The bond's value must be non-negative.", out.getBondValue() > 0);
            check.using("The parties involved cannot be the same entity.", exporter != bank);

            // checking that signers are different
            final List<PublicKey> signers = command.getSigners();
            check.using("There must be two signers.", signers.size() == 2);
            check.using("All parties involved must be signers.", signers.containsAll(
                    ImmutableList.of(exporter.getOwningKey(), bank.getOwningKey())));

            return null;
        });
    }


}