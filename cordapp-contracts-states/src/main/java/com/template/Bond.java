package com.template;


import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class Bond {

    private int bondValue;
    private int bondUKValue;

    //bank
    private String bankSupplyContractID;
    private double exporterTurnover;
    private double exporterNet;
    private int bankDefaultProbability;
    private Rating bankCreditRating;
    private int requestedUKEFsupport;

    //ukef
    private String UKEFSupplyContractID;
    private Boolean isUKEFSupported;

    @ConstructorForDeserialization
    public Bond(int bondValue, int bondUKValue, String bankSupplyContractID, double exporterTurnover, double exporterNet, int bankDefaultProbability, Rating bankCreditRating, String UKEFSupplyContractID, int requestedUKEFsupport, Boolean isUKEFSupported) {
        this.bondValue = bondValue;
        this.bondUKValue = bondUKValue;

        this.bankSupplyContractID = bankSupplyContractID;
        this.exporterTurnover = exporterTurnover;
        this.exporterNet = exporterNet;
        this.bankDefaultProbability = bankDefaultProbability;
        this.bankCreditRating = bankCreditRating;
        this.requestedUKEFsupport = requestedUKEFsupport;

        this.UKEFSupplyContractID = UKEFSupplyContractID;
        this.isUKEFSupported = isUKEFSupported;
    }

    /**
     * Construcutor used by Bank to create a new UKTFBond output state
     */
    public Bond(int bondValue, int bondUKValue, String bankSupplyContractID, double exporterTurnover, double exporterNet, int bankDefaultProbability, Rating bankCreditRating, int requestedUKEFSupport) {
        this.bondValue = bondValue;
        this.bondUKValue = bondUKValue;

        this.bankSupplyContractID = bankSupplyContractID;
        this.exporterTurnover = exporterTurnover;
        this.exporterNet = exporterNet;
        this.bankDefaultProbability = bankDefaultProbability;
        this.bankCreditRating = bankCreditRating;
        this.requestedUKEFsupport = requestedUKEFSupport;

        this.UKEFSupplyContractID = "";
        this.isUKEFSupported = false;
    }


    /**
     * Constructur used by UKEF to create a new UKTBond output state
     *
     */
    public Bond(Bond inputBond, String UKEFSupplyContractID, boolean isUKEFSupported) {
        this.bondValue = inputBond.getBondValue();
        this.bondUKValue = inputBond.getBondUKValue();

        this.bankSupplyContractID = inputBond.getBankSupplyContractID();
        this.exporterTurnover = inputBond.getExporterTurnover();
        this.exporterNet = inputBond.getExporterNet();
        this.bankDefaultProbability = inputBond.getBankDefaultProbability();
        this.bankCreditRating = inputBond.getBankCreditRating();
        this.requestedUKEFsupport = inputBond.getRequestedUKEFsupport();

        this.UKEFSupplyContractID = UKEFSupplyContractID;
        this.isUKEFSupported = isUKEFSupported;
    }


    public Bond(int bondValue, int bondUKValue){
        this.bondValue = bondValue;
        this.bondUKValue = bondUKValue;

        this.bankSupplyContractID = "";
        this.exporterTurnover = 0;
        this.exporterNet = 0;
        this.bankDefaultProbability = 0;
        this.bankCreditRating = Rating.NULL;
        this.requestedUKEFsupport = 0;


        this.UKEFSupplyContractID = "";
        this.isUKEFSupported = false;
    }

    public int getBondValue() {
        return bondValue;
    }

    public int getBondUKValue() {
        return bondUKValue;
    }

    public String getUKEFSupplyContractID() {
        return UKEFSupplyContractID;
    }

    public int getRequestedUKEFsupport() {
        return requestedUKEFsupport;
    }

    public Rating getBankCreditRating(){
        return this.bankCreditRating;
    }

    public int getBankDefaultProbability() {
        return bankDefaultProbability;
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
