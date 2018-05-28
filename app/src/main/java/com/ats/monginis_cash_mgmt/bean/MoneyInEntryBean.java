package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 23/12/17.
 */

public class MoneyInEntryBean {

    private Integer trId;
    private String inDate;
    private String inTime;
    private float inAmt;
    private Integer inSource;
    private Integer userId;
    private String inRemark;
    private String userName;

    public Integer getTrId() {
        return trId;
    }

    public void setTrId(Integer trId) {
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

    public Integer getInSource() {
        return inSource;
    }

    public void setInSource(Integer inSource) {
        this.inSource = inSource;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getInRemark() {
        return inRemark;
    }

    public void setInRemark(String inRemark) {
        this.inRemark = inRemark;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "MoneyInEntryBean{" +
                "trId=" + trId +
                ", inDate='" + inDate + '\'' +
                ", inTime='" + inTime + '\'' +
                ", inAmt=" + inAmt +
                ", inSource=" + inSource +
                ", userId=" + userId +
                ", inRemark='" + inRemark + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
