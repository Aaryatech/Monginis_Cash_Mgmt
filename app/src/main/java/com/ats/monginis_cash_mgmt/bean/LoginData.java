package com.ats.monginis_cash_mgmt.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by maxadmin on 19/12/17.
 */

public class LoginData {

    private ErrorMessage errorMessage;
    private MUser mUser;

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MUser getMUser() {
        return mUser;
    }

    public void setMUser(MUser mUser) {
        this.mUser = mUser;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "errorMessage=" + errorMessage +
                ", mUser=" + mUser +
                '}';
    }
}
