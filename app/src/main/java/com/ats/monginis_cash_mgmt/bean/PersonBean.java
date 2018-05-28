package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 20/12/17.
 */

public class PersonBean {

    private int personId;
    private String personName;
    private int deptId;
    private float personLimit;
    private String personMobile;
    private int isActive;
    private int delStatus;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public float getPersonLimit() {
        return personLimit;
    }

    public void setPersonLimit(float personLimit) {
        this.personLimit = personLimit;
    }

    public String getPersonMobile() {
        return personMobile;
    }

    public void setPersonMobile(String personMobile) {
        this.personMobile = personMobile;
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
        return "PersonBean{" +
                "personId=" + personId +
                ", personName='" + personName + '\'' +
                ", deptId=" + deptId +
                ", personLimit=" + personLimit +
                ", personMobile='" + personMobile + '\'' +
                ", isActive=" + isActive +
                ", delStatus=" + delStatus +
                '}';
    }
}



