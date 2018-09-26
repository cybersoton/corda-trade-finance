package com.template;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;


import java.util.List;

public class UKTFBond implements ContractState {

//    private UniqueIdentifier bondID;
    private final String bondID;
    private final Bond bond;
    private final Party exporter;
    private final Party bank;
    private final Party ukef;

    public UKTFBond(String bondID, Bond bond, Party exporter, Party bank, Party ukef) {
//        this.bondID = new UniqueIdentifier(bondID, UUID.randomUUID());
        this.bondID = bondID;
        this.bond = bond;
        this.exporter = exporter;
        this.bank = bank;
        this.ukef = ukef;
    }

    public UKTFBond copy(Bond newBond){
        return new UKTFBond(this.bondID, newBond, this.exporter, this.bank, this.ukef);
    }

    public String getBondID() {
        return bondID;
    }

    public int getBondValue() {
        return this.bond.getBondValue();
    }

    public double getCreditScore() {return this.bond.getBankCreditScore();}

    public int getRiskLevel() {return this.bond.getBankRiskLevel();}

    public double getTurnover(){return this.bond.getExporterTurnover();}

    public Party getExporter() {
        return exporter;
    }

    public Party getBank() {
        return bank;
    }

    public Party getUkef() { return ukef; }

    public Bond getBond() {
        return bond;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(exporter, bank, ukef);
    }

    @Override
    public String toString() {
        return this.bondID;
    }

}