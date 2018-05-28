package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 22/12/17.
 */

public class PurposeList {

    private Integer purposeId;
    private String purpose;
    private Integer isActive;
    private Integer delStatus;

    public PurposeList() {
    }

    public PurposeList(Integer purposeId, String purpose, Integer isActive, Integer delStatus) {
        this.purposeId = purposeId;
        this.purpose = purpose;
        this.isActive = isActive;
        this.delStatus = delStatus;
    }

    public Integer getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(Integer purposeId) {
        this.purposeId = purposeId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    @Override
    public String toString() {
        return "PurposeList{" +
                "purposeId=" + purposeId +
                ", purpose='" + purpose + '\'' +
                ", isActive=" + isActive +
                ", delStatus=" + delStatus +
                '}';
    }
}
