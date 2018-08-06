package com.template;


import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class Bond {

    private int bondValue;

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
    public Bond(int bondValue, String bankSupplyContractID, double exporterTurnover, double exporterNet, int bankRiskLevel, double bankCreditScore, String UKEFSupplyContractID, Boolean isUKEFSupported) {
        this.bondValue = bondValue;
        this.bankSupplyContractID = bankSupplyContractID;
        this.exporterTurnover = exporterTurnover;
        this.exporterNet = exporterNet;
        this.bankRiskLevel = bankRiskLevel;
        this.bankCreditScore = bankCreditScore;
        this.UKEFSupplyContractID = UKEFSupplyContractID;
        this.isUKEFSupported = isUKEFSupported;
    }

    public Bond(int bondValue){
        this.bondValue = bondValue;
        this.bankSupplyContractID = "";
        this.exporterTurnover = 0;
        this.exporterNet = 0;
        this.bankRiskLevel = 0;
        this.bankCreditScore = 0;
        this.UKEFSupplyContractID = "";
        this.isUKEFSupported = false;
    }

    public int getBondValue() {
        return bondValue;
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
}
