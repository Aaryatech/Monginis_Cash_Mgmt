package com.ats.monginis_cash_mgmt.interfaces;

import com.ats.monginis_cash_mgmt.bean.CashBalanceDataBean;
import com.ats.monginis_cash_mgmt.bean.CompanyListData;
import com.ats.monginis_cash_mgmt.bean.DepartmentList;
import com.ats.monginis_cash_mgmt.bean.DepartmentListData;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.ats.monginis_cash_mgmt.bean.LoginData;
import com.ats.monginis_cash_mgmt.bean.MoneyInBean;
import com.ats.monginis_cash_mgmt.bean.MoneyInEntryBean;
import com.ats.monginis_cash_mgmt.bean.MoneyOutBean;
import com.ats.monginis_cash_mgmt.bean.PersonList;
import com.ats.monginis_cash_mgmt.bean.PersonListData;
import com.ats.monginis_cash_mgmt.bean.PurposeList;
import com.ats.monginis_cash_mgmt.bean.PurposeListData;
import com.ats.monginis_cash_mgmt.bean.UserList;
import com.ats.monginis_cash_mgmt.bean.UserListData;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by maxadmin on 19/12/17.
 */

public interface InterfaceApi {

//    public static final String IMAGE_URL_1 = "http://132.148.143.124:8080/cashmgmt/uploads/bill/";
//    public static final String IMAGE_URL_2 = "http://132.148.143.124:8080/mrg/uploads/bill/";
//    public static final String IMAGE_URL_3 = "http://132.148.143.124:8080/gfpllogi/uploads/bill/";
//    public static final String IMAGE_URL_4 = "http://132.148.143.124:8080/cp/uploads/bill/";

    public static final String IMAGE_URL_1 = "http://132.148.151.41:8080/cashmgmt_uploads/gfpl/bill/";
    public static final String IMAGE_URL_2 = "http://132.148.151.41:8080/cashmgmt_uploads/mrg/bill/";
    public static final String IMAGE_URL_3 = "http://132.148.151.41:8080/cashmgmt_uploads/gfpllogi/bill/";
    public static final String IMAGE_URL_4 = "http://132.148.151.41:8080/cashmgmt_uploads/cp/bill/";

    @POST("login")
    Call<LoginData> getLogin(@Query("userMobile") String userMobile, @Query("password") String password);

//    @POST("insertNewCompany")
//    Call<ErrorMessage> insertCompany(@Body List bean);

    @POST("allCompanyList")
    Call<CompanyListData> getAllCompanyList();

    @POST("insertNewDept")
    Call<ErrorMessage> insertDepartment(@Body DepartmentList departmentBean);

    @GET("getAllDeptList")
    Call<DepartmentListData> getAllDepartmentList();

    @POST("deleteDept")
    Call<ErrorMessage> deleteDepartment(@Query("deptId") int deptId);

    @POST("getAllPurposeList")
    Call<PurposeListData> getAllPurposeList();

    @POST("insertPurpose")
    Call<ErrorMessage> insertPurpose(@Body PurposeList purposeBean);

    @POST("deletePurposeById")
    Call<ErrorMessage> deletePurpose(@Query("purposeId") int purposeId);

    @POST("personList")
    Call<PersonListData> getAllPersonList();

    @POST("insertPerson")
    Call<ErrorMessage> insertPerson(@Body PersonList personList);

    @POST("deletePersonById")
    Call<ErrorMessage> deletePerson(@Query("personId") int personId);

    @GET("getAllUsers")
    Call<UserListData> getAllUserList();

    @POST("insertUser")
    Call<ErrorMessage> insertUser(@Body UserList userList);

    @POST("editUser")
    Call<ErrorMessage> editUser(@Body UserList userList);

    @POST("deleteUserById")
    Call<ErrorMessage> deleteUser(@Query("userId") int userId);

    @POST("moneyIn")
    Call<ErrorMessage> insertMoneyIn(@Body MoneyInBean moneyInBean);

    @GET("moneyInList")
    Call<ArrayList<MoneyInEntryBean>> getMoneyInEntries();

    @POST("newMoneyOutEntry")
    Call<ErrorMessage> insertMoneyOut(@Body MoneyOutBean moneyOutBean);

    @POST("settleMoneyOutEntry")
    Call<ErrorMessage> insertSettleMoneyOut(@Body MoneyOutBean moneyOutBean);

    @POST("approveOrRejectMoneyOutEntry")
    Call<ErrorMessage> insertApproveRejectMoneyOut(@Body ArrayList<MoneyOutBean> moneyOutBean);

    @POST("moneyOutListByEmpId")
    Call<ArrayList<GetMoneyOutData>> getMoneyOutEntries(@Query("empId") int empId);

    @POST("getAllMoneyOutList")
    Call<ArrayList<GetMoneyOutData>> getAllMoneyOutEntries(@Query("empId") int empId);

    @POST("getMoneyOutListForSettlement")
    Call<ArrayList<GetMoneyOutData>> getTransactionSettlementEntries(@Query("empId") int empId);

    @GET("getMoneyOutListForApproval")
    Call<ArrayList<GetMoneyOutData>> getApprovalEntries();

    @GET("getRejectedList")
    Call<ArrayList<GetMoneyOutData>> getRejectedEntries(@Query("empId") int empId);

    @Multipart
    @POST("billPhotoUpload")
    Call<JSONObject> imageUpload(@Part MultipartBody.Part file, @Part("imageName") RequestBody name);

    @GET("getCashBalance")
    Call<CashBalanceDataBean> getCashBalance();

    @POST("getMoneyOutByOutByBetDate")
    Call<ArrayList<GetMoneyOutData>> getAllMoneyOutEntriesByDate(@Query("empId") int empId, @Query("fromDate") String fromDate, @Query("toDate") String toDate);

    @POST("getMoneyOutByBetDateAndPerson")
    Call<ArrayList<GetMoneyOutData>> getAllMoneyOutEntriesByPerson(@Query("empId") int empId, @Query("fromDate") String fromDate, @Query("toDate") String toDate, @Query("personId") int personId);

    @POST("getMoneyOutByBetDateAndPurpose")
    Call<ArrayList<GetMoneyOutData>> getAllMoneyOutEntriesByPurpose(@Query("empId") int empId, @Query("fromDate") String fromDate, @Query("toDate") String toDate, @Query("purposeId") int purposeId);

    @Headers({"Accept: application/pdf"})
    @GET("getMoneyOutByBetDate")
    Call<ResponseBody> getPdfReport(@Query("fromDate") String fromDate, @Query("toDate") String toDate, @Query("empId") int empId, @Query("personId") int personId, @Query("purposeId") int purposeId);


}
