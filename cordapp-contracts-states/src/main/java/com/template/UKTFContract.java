package com.template;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;


public class UKTFContract implements Contract {

    // This is used to identify the contract when building a transaction.
    public static final String UKTF_CONTRACT_ID = "com.template.UKTFContract";


    public static class Commands implements CommandData {

        /**
         * The command create: issued by the exporter towards a bank (UKEF is added by the bank)
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

    @Override
    public void verify(LedgerTransaction tx) {

        if (tx.getCommands().size() != 1) {
            throw new IllegalArgumentException("Illegal number of commands");
        }

        Command<CommandData> command = tx.getCommand(0);

        if (command.getValue() instanceof Commands.Create) {

            requireThat(check -> {

                // Constraints on the states.
                check.using("No inputs should be consumed when issuing bond application", tx.getInputs().isEmpty());
                check.using("There should be one output state of type UKTFState.", tx.getOutputs().size() == 1);
                final UKTFBondState out = tx.outputsOfType(UKTFBondState.class).get(0);
                check.using("The bond's value must be non-negative.", out.getBondValue() > 0);

                //signers
                final Party exporter = out.getExporter();
                final Party bank = out.getBank();
                check.using("The parties involved cannot be the same entity.", exporter != bank);

                final List<PublicKey> signers = command.getSigners();
                check.using("There must be three signers.", signers.size() == 3);
                check.using("All parties involved must be signers.", signers.containsAll(
                        Arrays.asList(exporter.getOwningKey(), bank.getOwningKey())));

                return null;
            });

        } else if (command.getValue() instanceof Commands.BankAssess){

            requireThat(check -> {

                // Constraints on the states.
                check.using("Inputs should be consumed when doing a bank assess application", !tx.getInputs().isEmpty());
                check.using("There should be one output state of type UKTFState.", tx.getOutputs().size() == 1);
                final UKTFBondState out = tx.outputsOfType(UKTFBondState.class).get(0);

                //signers
                final Party exporter = out.getExporter();
                final Party ukef = out.getUkef();
                check.using("The parties involved cannot be the same entity.", exporter != ukef);

                final List<PublicKey> signers = command.getSigners();
                check.using("There must be three signers.", signers.size() == 3);
                check.using("All parties involved must be signers.", signers.containsAll(
                        Arrays.asList(exporter.getOwningKey(), ukef.getOwningKey())));

                return null;
            });

        } else if (command.getValue() instanceof Commands.UKEFAssess){

            requireThat(check -> {

                // Constraints on the states.
                check.using("Inputs should be consumed when doing a UKEF assess application", !tx.getInputs().isEmpty());
                check.using("There should be one output state of type UKTFState.", tx.getOutputs().size() == 1);
                final UKTFBondState out = tx.outputsOfType(UKTFBondState.class).get(0);

                //signers
                final Party exporter = out.getExporter();
                final Party bank = out.getBank();
                check.using("The parties involved cannot be the same entity.", exporter != bank);

                final List<PublicKey> signers = command.getSigners();
                check.using("There must be three signers.", signers.size() == 3);
                check.using("All parties involved must be signers.", signers.containsAll(
                        Arrays.asList(exporter.getOwningKey(), bank.getOwningKey())));

                return null;
            });

        } else {
            throw new IllegalArgumentException("Unrecognised command");
        }

    }


}