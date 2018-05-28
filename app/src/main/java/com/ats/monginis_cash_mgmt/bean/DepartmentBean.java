package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 20/12/17.
 */

public class DepartmentBean {

    private int deptId;
    private String deptCode;
    private String deptName;
    private int isActive;
    private int delStatus;

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
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
        return "DepartmentBean{" +
                "deptId=" + deptId +
                ", deptCode='" + deptCode + '\'' +
                ", deptName='" + deptName + '\'' +
                ", isActive=" + isActive +
                ", delStatus=" + delStatus +
                '}';
    }
}
