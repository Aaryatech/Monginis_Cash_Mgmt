package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 22/12/17.
 */

public class MoneyInBean {

    private int trId;
    private String inDate;
    private String inTime;
    private float inAmt;
    private int inSource;
    private int userId;
    private String inRemark;

    public MoneyInBean(int trId, float inAmt, int inSource, int userId, String inRemark) {
        this.trId = trId;
        this.inAmt = inAmt;
        this.inSource = inSource;
        this.userId = userId;
        this.inRemark = inRemark;
    }

    public int getTrId() {
        return trId;
    }

    public void setTrId(int trId) {
        this.trId = trId;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public float getInAmt() {
        return inAmt;
    }

    public void setInAmt(float inAmt) {
        this.inAmt = inAmt;
    }

    public int getInSource() {
        return inSource;
    }

    public void setInSource(int inSource) {
        this.inSource = inSource;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getInRemark() {
        return inRemark;
    }

    public void setInRemark(String inRemark) {
        this.inRemark = inRemark;
    }

    @Override
    public String toString() {
        return "MoneyInBean{" +
                "trId=" + trId +
                ", inDate='" + inDate + '\'' +
                ", inTime='" + inTime + '\'' +
                ", inAmt=" + inAmt +
                ", inSource=" + inSource +
                ", userId=" + userId +
                ", inRemark='" + inRemark + '\'' +
                '}';
    }
}
