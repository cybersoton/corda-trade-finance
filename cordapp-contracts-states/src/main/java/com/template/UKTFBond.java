package com.template;

public class UKTFBond {

    private double exporterTurnover;
    private double exporterNet;
    private int bankRiskLevel;
    private double bankCreditScore;

    public UKTFBond() {
    }

    public UKTFBond(double exporterTurnover, double exporterNet, int bankRiskLevel, double bankCreditScore) {
        this.exporterTurnover = exporterTurnover;
        this.exporterNet = exporterNet;
        this.bankRiskLevel = bankRiskLevel;
        this.bankCreditScore = bankCreditScore;
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

    public double getExporterTurnover() {
        return exporterTurnover;
    }

    public double getExporterNet() {
        return exporterNet;
    }

    public int getBankRiskLevel() {
        return bankRiskLevel;
    }

    public double getBankCreditScore() {
        return bankCreditScore;
    }
}
