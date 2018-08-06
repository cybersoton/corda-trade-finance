package com.template;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;


public class UKTFBond implements ContractState {

//    private UniqueIdentifier bondID;
    private final String bondID;
    private final int bondValue;
    private final Party exporter;
    private final Party bank;
    private final Party ukef;

    //bank
    private String bankSupplyContractID;
    private double exporterTurnover;
    private double exporterNet;
    private int bankRiskLevel;
    private double bankCreditScore;

    //ukef
    private String UKEFSupplyContractID;
    private Boolean isUKEFSupported;


    @ConstructorForDeserialization
    public UKTFBond(String bondID, int bondValue, Party exporter, Party bank, Party ukef, String bankContract, double exporterTurnover, double exporterNet, int bankRiskLevel, double bankCreditScore, String UKEFContract, boolean isUKEFSupported) {
        this.bondID = bondID;
        this.bondValue = bondValue;
        this.exporter = exporter;
        this.bank = bank;
        this.ukef = ukef;
        this.bankSupplyContractID = bankContract;
        this.exporterTurnover = exporterTurnover;
        this.exporterNet = exporterNet;
        this.bankRiskLevel = bankRiskLevel;
        this.bankCreditScore = bankCreditScore;
        this.UKEFSupplyContractID = UKEFContract;
        this.isUKEFSupported = isUKEFSupported;
    }

    public UKTFBond(String bondID, int bondValue, Party exporter, Party bank, Party ukef) {
//        this.bondID = new UniqueIdentifier(bondID, UUID.randomUUID());
        this.bondID = bondID;
        this.bondValue = bondValue;
        this.exporter = exporter;
        this.bank = bank;
        this.ukef = ukef;
    }

    public UKTFBond copy(){
        return new UKTFBond(
                this.bondID,
                this.bondValue,
                this.exporter,
                this.bank,
                this.ukef,
                this.bankSupplyContractID,
                this.exporterTurnover,
                this.exporterNet,
                this.bankRiskLevel,
                this.bankCreditScore,
                this.UKEFSupplyContractID,
                this.isUKEFSupported);
    }

    public void setBankSupplyContractID(String bankSupplyContractID) {
        this.bankSupplyContractID = bankSupplyContractID;
    }

    public void setExporterTurnover(double exporterTurnover) {
        this.exporterTurnover = exporterTurnover;
    }

    public void setExporterNet(double exporterNet) {
        this.exporterNet = exporterNet;
    }

    public void setBankRiskLevel(int bankRiskLevel) {
        this.bankRiskLevel = bankRiskLevel;
    }

    public void setBankCreditScore(double bankCreditScore) {
        this.bankCreditScore = bankCreditScore;
    }

    public void setUKEFSupplyContractID(String UKEFSupplyContractID) {
        this.UKEFSupplyContractID = UKEFSupplyContractID;
    }

    public void setUKEFSupported(Boolean UKEFSupported) {
        isUKEFSupported = UKEFSupported;
    }

    public String getBondID() {
        return bondID;
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

        return ImmutableList.of(exporter, bank, ukef
        );
    }

    @Override
    public String toString() {
        return this.bondID;
    }

}