package com.ats.monginis_cash_mgmt.bean;

import java.util.List;

/**
 * Created by maxadmin on 25/12/17.
 */

public class MoneyOutListData {

    private List<MoneyOutList> moneyOutList;
    private ErrorMessage errorMessage;

    public List<MoneyOutList> getMoneyOutList() {
        return moneyOutList;
    }

    public void setMoneyOutList(List<MoneyOutList> moneyOutList) {
        this.moneyOutList = moneyOutList;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "MoneyOutListData{" +
                "moneyOutList=" + moneyOutList +
                ", errorMessage=" + errorMessage +
                '}';
    }
}
