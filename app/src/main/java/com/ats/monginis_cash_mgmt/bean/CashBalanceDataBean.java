package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 27/12/17.
 */

public class CashBalanceDataBean {

    private Integer trId;
    private String opDatetime;
    private float opBalance;
    private float moneyIn;
    private float approvedMoney;
    private float ongoingTrMoney;
    private float physicalCash;
    private float closingCash;
    private String closingDatetime;
    private Integer status;

    public Integer getTrId() {
        return trId;
    }

    public void setTrId(Integer trId) {
        this.trId = trId;
    }

    public String getOpDatetime() {
        return opDatetime;
    }

    public void setOpDatetime(String opDatetime) {
        this.opDatetime = opDatetime;
    }

    public float getOpBalance() {
        return opBalance;
    }

    public void setOpBalance(float opBalance) {
        this.opBalance = opBalance;
    }

    public float getMoneyIn() {
        return moneyIn;
    }

    public void setMoneyIn(float moneyIn) {
        this.moneyIn = moneyIn;
    }

    public float getApprovedMoney() {
        return approvedMoney;
    }

    public void setApprovedMoney(float approvedMoney) {
        this.approvedMoney = approvedMoney;
    }

    public float getOngoingTrMoney() {
        return ongoingTrMoney;
    }

    public void setOngoingTrMoney(float ongoingTrMoney) {
        this.ongoingTrMoney = ongoingTrMoney;
    }

    public float getPhysicalCash() {
        return physicalCash;
    }

    public void setPhysicalCash(float physicalCash) {
        this.physicalCash = physicalCash;
    }

    public float getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(float closingCash) {
        this.closingCash = closingCash;
    }

    public String getClosingDatetime() {
        return closingDatetime;
    }

    public void setClosingDatetime(String closingDatetime) {
        this.closingDatetime = closingDatetime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CashBalanceDataBean{" +
                "trId=" + trId +
                ", opDatetime='" + opDatetime + '\'' +
                ", opBalance=" + opBalance +
                ", moneyIn=" + moneyIn +
                ", approvedMoney=" + approvedMoney +
                ", ongoingTrMoney=" + ongoingTrMoney +
                ", physicalCash=" + physicalCash +
                ", closingCash=" + closingCash +
                ", closingDatetime='" + closingDatetime + '\'' +
                ", status=" + status +
                '}';
    }
}
