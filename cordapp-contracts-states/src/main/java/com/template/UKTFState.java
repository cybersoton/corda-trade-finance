package com.template;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.List;


public class UKTFState implements ContractState {

    private final UniqueIdentifier bondID;
    private final int bondValue;
    private final Party exporter;
    private final Party bank;
//    private final Party ukef;

    //  details on the contract
    private UKTFBond bondDetails;

    //bank
    private String bankSupplyContractID;

    //ukef
    private String UKEFSupplyContractID;
    private Boolean isUKEFSupported;


    public UKTFState(UniqueIdentifier bondID, int bondValue, Party exporter, Party bank
//            , Party ukef
    ) {
        this.bondID = bondID;
        this.bondValue = bondValue;
        this.exporter = exporter;
        this.bank = bank;
//        this.ukef = ukef;
    }

    public int getBondValue() {
        return bondValue;
    }

    public Party getExporter() {
        return exporter;
    }

    public Party getBank() {
        return bank;
    }

    @Override
    public List<AbstractParty> getParticipants() {

        return ImmutableList.of(exporter, bank
//                , ukef
        );
    }

    @Override
    public String toString() {
        return this.bondID.getId().toString();
    }
}