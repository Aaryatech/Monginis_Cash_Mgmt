package com.ats.monginis_cash_mgmt.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by maxadmin on 19/12/17.
 */

public class MUser {

    private Integer userId;
    private String userName;
    private Integer userType;
    private String password;
    private String userMobile;
    private String userEmailId;
    private Integer isActive;
    private Integer delStatus;
    private float maxLimit;

    public MUser(Integer userId, String userName, Integer userType, String password, String userMobile, String userEmailId, Integer isActive, Integer delStatus, float maxLimit) {
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        this.password = password;
        this.userMobile = userMobile;
        this.userEmailId = userEmailId;
        this.isActive = isActive;
        this.delStatus = delStatus;
        this.maxLimit = maxLimit;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getCmPassword() {
        return password;
    }

    public void setCmPassword(String cmPassword) {
        this.password = cmPassword;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmailId() {
        return userEmailId;
    }

    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public float getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(float maxLimit) {
        this.maxLimit = maxLimit;
    }

    @Override
    public String toString() {
        return "MUser{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userType=" + userType +
                ", password='" + password + '\'' +
                ", userMobile='" + userMobile + '\'' +
                ", userEmailId='" + userEmailId + '\'' +
                ", isActive=" + isActive +
                ", delStatus=" + delStatus +
                ", maxLimit=" + maxLimit +
                '}';
    }
}
