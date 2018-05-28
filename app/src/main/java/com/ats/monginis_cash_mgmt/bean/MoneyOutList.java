package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 25/12/17.
 */

public class MoneyOutList {

    private Integer trId;
    private String outDate;
    private String outTime;
    private float outAmt;
    private Integer personId;
    private Integer deptId;
    private Integer purposeId;
    private String outRemark;
    private Integer outBy;
    private Integer isSettle;
    private Integer settleUserid;
    private String settleDate;
    private float returnAmt;
    private String returnReason;
    private Integer isBill;
    private String billPhoto;
    private Integer isApproved;
    private String approvalDate;
    private String approvalDatetime;
    private Integer approvedBy;
    private String rejReason;
    private float expAmt;
    private float rejReturnAmt;
    private float trClosedAmt;

    public Integer getTrId() {
        return trId;
    }

    public void setTrId(Integer trId) {
        this.trId = trId;
    }

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public float getOutAmt() {
        return outAmt;
    }

    public void setOutAmt(float outAmt) {
        this.outAmt = outAmt;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Integer getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(Integer purposeId) {
        this.purposeId = purposeId;
    }

    public String getOutRemark() {
        return outRemark;
    }

    public void setOutRemark(String outRemark) {
        this.outRemark = outRemark;
    }

    public Integer getOutBy() {
        return outBy;
    }

    public void setOutBy(Integer outBy) {
        this.outBy = outBy;
    }

    public Integer getIsSettle() {
        return isSettle;
    }

    public void setIsSettle(Integer isSettle) {
        this.isSettle = isSettle;
    }

    public Integer getSettleUserid() {
        return settleUserid;
    }

    public void setSettleUserid(Integer settleUserid) {
        this.settleUserid = settleUserid;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public float getReturnAmt() {
        return returnAmt;
    }

    public void setReturnAmt(float returnAmt) {
        this.returnAmt = returnAmt;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public Integer getIsBill() {
        return isBill;
    }

    public void setIsBill(Integer isBill) {
        this.isBill = isBill;
    }

    public String getBillPhoto() {
        return billPhoto;
    }

    public void setBillPhoto(String billPhoto) {
        this.billPhoto = billPhoto;
    }

    public Integer getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Integer isApproved) {
        this.isApproved = isApproved;
    }

    public String getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApprovalDatetime() {
        return approvalDatetime;
    }

    public void setApprovalDatetime(String approvalDatetime) {
        this.approvalDatetime = approvalDatetime;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getRejReason() {
        return rejReason;
    }

    public void setRejReason(String rejReason) {
        this.rejReason = rejReason;
    }

    public float getExpAmt() {
        return expAmt;
    }

    public void setExpAmt(float expAmt) {
        this.expAmt = expAmt;
    }

    public float getRejReturnAmt() {
        return rejReturnAmt;
    }

    public void setRejReturnAmt(float rejReturnAmt) {
        this.rejReturnAmt = rejReturnAmt;
    }

    public float getTrClosedAmt() {
        return trClosedAmt;
    }

    public void setTrClosedAmt(float trClosedAmt) {
        this.trClosedAmt = trClosedAmt;
    }

    @Override
    public String toString() {
        return "MoneyOutList{" +
                "trId=" + trId +
                ", outDate='" + outDate + '\'' +
                ", outTime='" + outTime + '\'' +
                ", outAmt=" + outAmt +
                ", personId=" + personId +
                ", deptId=" + deptId +
                ", purposeId=" + purposeId +
                ", outRemark='" + outRemark + '\'' +
                ", outBy=" + outBy +
                ", isSettle=" + isSettle +
                ", settleUserid=" + settleUserid +
                ", settleDate='" + settleDate + '\'' +
                ", returnAmt=" + returnAmt +
                ", returnReason='" + returnReason + '\'' +
                ", isBill=" + isBill +
                ", billPhoto='" + billPhoto + '\'' +
                ", isApproved=" + isApproved +
                ", approvalDate='" + approvalDate + '\'' +
                ", approvalDatetime='" + approvalDatetime + '\'' +
                ", approvedBy=" + approvedBy +
                ", rejReason='" + rejReason + '\'' +
                ", expAmt=" + expAmt +
                ", rejReturnAmt=" + rejReturnAmt +
                ", trClosedAmt=" + trClosedAmt +
                '}';
    }
}
