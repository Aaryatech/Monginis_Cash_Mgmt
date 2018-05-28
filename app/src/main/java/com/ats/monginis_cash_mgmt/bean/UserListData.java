package com.ats.monginis_cash_mgmt.bean;

import java.util.List;

/**
 * Created by maxadmin on 22/12/17.
 */

public class UserListData {

    private List<UserList> userList;
    private ErrorMessage errorMessage;

    public List<UserList> getUserList() {
        return userList;
    }

    public void setUserList(List<UserList> userList) {
        this.userList = userList;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "UserListData{" +
                "userList=" + userList +
                ", errorMessage=" + errorMessage +
                '}';
    }
}
