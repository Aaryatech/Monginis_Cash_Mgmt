package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 25/12/17.
 */

public class MoneyOutBean {

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

    public MoneyOutBean() {
    }

    public MoneyOutBean(int trId, String outDate, String outTime, float outAmt, int personId, int deptId, int purposeId, String outRemark, int outBy, int isSettle) {
        this.trId = trId;
        this.outDate = outDate;
        this.outDatetime = outTime;
        this.outAmt = outAmt;
        this.personId = personId;
        this.deptId = deptId;
        this.purposeId = purposeId;
        this.outRemark = outRemark;
        this.outBy = outBy;
        this.isSettle = isSettle;
    }

    public MoneyOutBean(int trId, String outDate, String outTime, float outAmt, int personId, int deptId, int purposeId, String outRemark, int outBy, int isSettle, int settleUserid, String settleDate, float returnAmt, String returnReason, int isBill, String billPhoto, int isApproved, String approvalDate, String approvalDatetime, int approvedBy, String rejReason, float expAmt, float rejReturnAmt, float trClosedAmt) {
        this.trId = trId;
        this.outDate = outDate;
        this.outDatetime = outTime;
        this.outAmt = outAmt;
        this.personId = personId;
        this.deptId = deptId;
        this.purposeId = purposeId;
        this.outRemark = outRemark;
        this.outBy = outBy;
        this.isSettle = isSettle;
        this.settleUserid = settleUserid;
        this.settleDate = settleDate;
        this.returnAmt = returnAmt;
        this.returnReason = returnReason;
        this.isBill = isBill;
        this.billPhoto = billPhoto;
        this.isApproved = isApproved;
        this.approvalDate = approvalDate;
        this.approvalDatetime = approvalDatetime;
        this.approvedBy = approvedBy;
        this.rejReason = rejReason;
        this.expAmt = expAmt;
        this.rejReturnAmt = rejReturnAmt;
        this.trClosedAmt = trClosedAmt;
    }

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

    @Override
    public String toString() {
        return "MoneyOutBean{" +
                "trId=" + trId +
                ", outDate='" + outDate + '\'' +
                ", outTime='" + outDatetime + '\'' +
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
