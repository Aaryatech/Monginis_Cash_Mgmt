package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 25/12/17.
 */

public class GetMoneyOutData {

    private int trId;
    private String outDate;
    private String outDatetime;
    private float outAmt;
    private int personId;
    private int deptId;
    private int purposeId;
    private String outRemark;
    private int outBy;
    private int isSettle;
    private int settleUserid;
    private String settleDate;
    private float returnAmt;
    private String returnReason;
    private int isBill;
    private String billPhoto;
    private int isApproved;
    private String approvalDate;
    private String approvalDatetime;
    private int approvedBy;
    private String rejReason;
    private float expAmt;
    private float rejReturnAmt;
    private float trClosedAmt;
    private String personName;
    private String purpose;
    private String userName;

    public int getTrId() {
        return trId;
    }

    public void setTrId(int trId) {
        this.trId = trId;
    }

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public String getOutDatetime() {
        return outDatetime;
    }

    public void setOutDatetime(String outDatetime) {
        this.outDatetime = outDatetime;
    }

    public float getOutAmt() {
        return outAmt;
    }

    public void setOutAmt(float outAmt) {
        this.outAmt = outAmt;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(int purposeId) {
        this.purposeId = purposeId;
    }

    public String getOutRemark() {
        return outRemark;
    }

    public void setOutRemark(String outRemark) {
        this.outRemark = outRemark;
    }

    public int getOutBy() {
        return outBy;
    }

    public void setOutBy(int outBy) {
        this.outBy = outBy;
    }

    public int getIsSettle() {
        return isSettle;
    }

    public void setIsSettle(int isSettle) {
        this.isSettle = isSettle;
    }

    public int getSettleUserid() {
        return settleUserid;
    }

    public void setSettleUserid(int settleUserid) {
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

    public int getIsBill() {
        return isBill;
    }

    public void setIsBill(int isBill) {
        this.isBill = isBill;
    }

    public String getBillPhoto() {
        return billPhoto;
    }

    public void setBillPhoto(String billPhoto) {
        this.billPhoto = billPhoto;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
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

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(int approvedBy) {
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

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "GetMoneyOutData{" +
                "trId=" + trId +
                ", outDate='" + outDate + '\'' +
                ", outDatetime='" + outDatetime + '\'' +
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
                ", personName='" + personName + '\'' +
                ", purpose='" + purpose + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
