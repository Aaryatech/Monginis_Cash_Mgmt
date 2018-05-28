package com.ats.monginis_cash_mgmt.bean;

/**
 * Created by maxadmin on 22/12/17.
 */

public class PersonList {

    private Integer personId;
    private String personName;
    private Integer deptId;
    private Float personLimit;
    private String personMobile;
    private Integer isActive;
    private Integer delStatus;

    public PersonList() {
    }

    public PersonList(Integer personId, String personName, Integer deptId, Float personLimit, String personMobile, Integer isActive, Integer delStatus) {
        this.personId = personId;
        this.personName = personName;
        this.deptId = deptId;
        this.personLimit = personLimit;
        this.personMobile = personMobile;
        this.isActive = isActive;
        this.delStatus = delStatus;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Float getPersonLimit() {
        return personLimit;
    }

    public void setPersonLimit(Float personLimit) {
        this.personLimit = personLimit;
    }

    public String getPersonMobile() {
        return personMobile;
    }

    public void setPersonMobile(String personMobile) {
        this.personMobile = personMobile;
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
        return "PersonList{" +
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
