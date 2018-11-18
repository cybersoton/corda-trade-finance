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

    public Bond(int bondValue, String bankSupplyContractID, double exporterTurnover, double exporterNet, int bankRiskLevel, double bankCreditScore) {
        this.bondValue = bondValue;
        this.bankSupplyContractID = bankSupplyContractID;
        this.exporterTurnover = exporterTurnover;
        this.exporterNet = exporterNet;
        this.bankRiskLevel = bankRiskLevel;
        this.bankCreditScore = bankCreditScore;
        this.UKEFSupplyContractID = "";
        this.isUKEFSupported = false;
    }

    public Bond(Bond inputBond, String UKEFSupplyContractID, boolean isUKEFSupported) {
        this.bondValue = inputBond.getBondValue();
        this.bankSupplyContractID = inputBond.getBankSupplyContractID();
        this.exporterTurnover = inputBond.getExporterTurnover();
        this.exporterNet = inputBond.getExporterNet();
        this.bankRiskLevel = inputBond.getBankRiskLevel();
        this.bankCreditScore = inputBond.getBankCreditScore();
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


    public double getBankCreditScore (){
        return this.bankCreditScore;
    }

    public int getBankRiskLevel() {
        return bankRiskLevel;
    }

    public String getBankSupplyContractID() {
        return bankSupplyContractID;
    }

    public double getExporterTurnover() {
        return exporterTurnover;
    }

    public double getExporterNet() {
        return exporterNet;
    }

    public Boolean getUKEFSupported() {
        return isUKEFSupported;
    }
}
