package com.ats.monginis_cash_mgmt.bean;

import java.util.List;

/**
 * Created by maxadmin on 22/12/17.
 */

public class DepartmentListData {

    private java.util.List<DepartmentList> departmentList;
    private ErrorMessage errorMessage;

    public List<DepartmentList> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<DepartmentList> departmentList) {
        this.departmentList = departmentList;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "DepartmentListData{" +
                "departmentList=" + departmentList +
                ", errorMessage=" + errorMessage +
                '}';
    }
}
