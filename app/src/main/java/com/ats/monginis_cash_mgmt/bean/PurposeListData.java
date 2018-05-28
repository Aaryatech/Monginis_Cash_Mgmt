package com.ats.monginis_cash_mgmt.bean;

import java.util.List;

/**
 * Created by maxadmin on 22/12/17.
 */

public class PurposeListData {

    private List<PurposeList> purposeList;
    private ErrorMessage errorMessage;

    public List<PurposeList> getPurposeList() {
        return purposeList;
    }

    public void setPurposeList(List<PurposeList> purposeList) {
        this.purposeList = purposeList;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "PurposeListData{" +
                "purposeList=" + purposeList +
                ", errorMessage=" + errorMessage +
                '}';
    }
}
