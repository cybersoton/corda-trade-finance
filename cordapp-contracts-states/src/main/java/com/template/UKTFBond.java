package com.template;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;


public class UKTFBond implements ContractState {

    private final UniqueIdentifier bondID;
    private final String externalID;
    private final int bondValue;
    private final Party exporter;
    private final Party bank;
    private final Party ukef;

    //bank
    private String bankSupplyContractID;
    private ExporterBond bondDetails;

    //ukef
    private String UKEFSupplyContractID;
    private Boolean isUKEFSupported;


    public UKTFBond(String bondID, int bondValue, Party exporter, Party bank, Party ukef
    ) {
        this.bondID = new UniqueIdentifier(bondID, UUID.randomUUID());
        this.externalID = bondID;
        this.bondValue = bondValue;
        this.exporter = exporter;
        this.bank = bank;
        this.ukef = ukef;
    }

    public UKTFBond(String bondID, UniqueIdentifier uID, int bondValue, Party exporter, Party bank, Party ukef
    ) {
        this.bondID = uID;
        this.externalID = bondID;
        this.bondValue = bondValue;
        this.exporter = exporter;
        this.bank = bank;
        this.ukef = ukef;
    }


    public UKTFBond bankCopy (){
        return new UKTFBond(this.externalID, this.bondID,this.bondValue,this.exporter , this.bank, this.ukef);
    }

    public String getBankSupplyContractID() {
        return bankSupplyContractID;
    }

    public void setBankSupplyContractID(String bankSupplyContractID) {
        this.bankSupplyContractID = bankSupplyContractID;
    }

    public String getUKEFSupplyContractID() {
        return UKEFSupplyContractID;
    }

    public void setUKEFSupplyContractID(String UKEFSupplyContractID) {
        this.UKEFSupplyContractID = UKEFSupplyContractID;
    }

    public Boolean getUKEFSupported() {
        return isUKEFSupported;
    }

    public void setUKEFSupported(Boolean UKEFSupported) {
        isUKEFSupported = UKEFSupported;
    }

    public ExporterBond getBondDetails() {
        return bondDetails;
    }

    public void setBondDetails(ExporterBond bondDetails) {
        this.bondDetails = bondDetails;
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

    public String getExternalID() {
        return externalID;
    }

    @Override
    public List<AbstractParty> getParticipants() {

        return ImmutableList.of(exporter, bank, ukef
        );
    }

    @Override
    public String toString() {
        return this.bondID.getId().toString();
    }

}