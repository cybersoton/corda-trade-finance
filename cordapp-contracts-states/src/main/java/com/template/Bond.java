package com.template;


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

    public int getBondValue() {
        return bondValue;
    }
}
