package com.ats.monginis_cash_mgmt.bean;

import java.util.List;

/**
 * Created by maxadmin on 22/12/17.
 */

public class PersonListData {

    private List<PersonList> personList;
    private ErrorMessage errorMessage;

    public List<PersonList> getPersonList() {
        return personList;
    }

    public void setPersonList(List<PersonList> personList) {
        this.personList = personList;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "PersonListData{" +
                "personList=" + personList +
                ", errorMessage=" + errorMessage +
                '}';
    }
}
