package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 20/12/17.
 */

public class PurposeBean {

    private int purposeId;
    private String purpose;
    private int isActive;
    private int delStatus;

    public int getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(int purposeId) {
        this.purposeId = purposeId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(int delStatus) {
        this.delStatus = delStatus;
    }

    @Override
    public String toString() {
        return "PurposeBean{" +
                "purposeId=" + purposeId +
                ", purpose='" + purpose + '\'' +
                ", isActive=" + isActive +
                ", delStatus=" + delStatus +
                '}';
    }
}
