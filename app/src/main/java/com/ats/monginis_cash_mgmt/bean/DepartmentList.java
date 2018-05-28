package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 22/12/17.
 */

public class DepartmentList {

    private Integer deptId;
    private String deptCode;
    private String deptName;
    private Integer isActive;
    private Integer delStatus;

    public DepartmentList() {
    }

    public DepartmentList(Integer deptId, String deptCode, String deptName, Integer isActive, Integer delStatus) {
        this.deptId = deptId;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.isActive = isActive;
        this.delStatus = delStatus;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
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
        return "DepartmentList{" +
                "deptId=" + deptId +
                ", deptCode='" + deptCode + '\'' +
                ", deptName='" + deptName + '\'' +
                ", isActive=" + isActive +
                ", delStatus=" + delStatus +
                '}';
    }
}
