package com.ats.monginis_cash_mgmt.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.bean.MUser;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.fragment.AddPersonFragment;
import com.ats.monginis_cash_mgmt.fragment.AddUserFragment;
import com.ats.monginis_cash_mgmt.fragment.ApprovalFragment;
import com.ats.monginis_cash_mgmt.fragment.CashBalanceFragment;
import com.ats.monginis_cash_mgmt.fragment.CompanyMasterFragment;
import com.ats.monginis_cash_mgmt.fragment.DepartmentMasterFragment;
import com.ats.monginis_cash_mgmt.fragment.EditMoneyOutFragment;
import com.ats.monginis_cash_mgmt.fragment.HomeFragment;
import com.ats.monginis_cash_mgmt.fragment.MoneyInEntriesFragment;
import com.ats.monginis_cash_mgmt.fragment.MoneyInFragment;
import com.ats.monginis_cash_mgmt.fragment.MoneyOutEntriesFragment;
import com.ats.monginis_cash_mgmt.fragment.MoneyOutFragment;
import com.ats.monginis_cash_mgmt.fragment.PersonMasterFragment;
import com.ats.monginis_cash_mgmt.fragment.PurposeMasterFragment;
import com.ats.monginis_cash_mgmt.fragment.RejectedEntryFragment;
import com.ats.monginis_cash_mgmt.fragment.ReportsFragment;
import com.ats.monginis_cash_mgmt.fragment.TransactionSettlementFragment;
import com.ats.monginis_cash_mgmt.fragment.UserManagementFragment;
import com.ats.monginis_cash_mgmt.util.PermissionUtil;
import com.google.gson.Gson;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private LinearLayout tvHome, tvMoneyIn, tvMoneyOut, tvTranscSettle, tvCashBalance, tvMoneyInEntries, tvMoneyOutEntries, tvApproval, tvCompany, tvDepartment, tvPerson, tvPurpose, tvUserMgmt, tvLogout, tvRejectedEntry, tvMenuReports;

    int userId, userType, company;
    String userName, mob, pwd;

    public static String BASE_URL = "";
    public static String COMPANY_NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvHome = findViewById(R.id.tvAdminMenuHome);
        tvMoneyIn = findViewById(R.id.tvAdminMenuMoneyIn);
        tvMoneyOut = findViewById(R.id.tvAdminMenuMoneyOut);
        tvTranscSettle = findViewById(R.id.tvAdminMenuTranscSettle);
        tvCashBalance = findViewById(R.id.tvAdminMenuCashBalance);
        tvMoneyInEntries = findViewById(R.id.tvAdminMenuMoneyInEntries);
        tvMoneyOutEntries = findViewById(R.id.tvAdminMenuMoneyOutEntries);
        tvApproval = findViewById(R.id.tvAdminMenuApproval);
        tvRejectedEntry = findViewById(R.id.tvAdminMenuReject);
        tvMenuReports = findViewById(R.id.tvAdminMenuReports);
        tvCompany = findViewById(R.id.tvAdminMenuCompanySelection);
        tvDepartment = findViewById(R.id.tvAdminMenuDept);
        tvPerson = findViewById(R.id.tvAdminMenuPerson);
        tvPurpose = findViewById(R.id.tvAdminMenuPurpose);
        tvUserMgmt = findViewById(R.id.tvAdminMenuUserMgmt);
        tvLogout = findViewById(R.id.tvAdminMenuLogout);

        // tvCompany.setVisibility(View.GONE);

        tvHome.setOnClickListener(this);
        tvMoneyIn.setOnClickListener(this);
        tvMoneyOut.setOnClickListener(this);
        tvTranscSettle.setOnClickListener(this);
        tvCashBalance.setOnClickListener(this);
        tvMoneyInEntries.setOnClickListener(this);
        tvMoneyOutEntries.setOnClickListener(this);
        tvApproval.setOnClickListener(this);
        tvRejectedEntry.setOnClickListener(this);
        tvMenuReports.setOnClickListener(this);
        tvCompany.setOnClickListener(this);
        tvDepartment.setOnClickListener(this);
        tvPerson.setOnClickListener(this);
        tvPurpose.setOnClickListener(this);
        tvUserMgmt.setOnClickListener(this);
        tvLogout.setOnClickListener(this);

        if (PermissionUtil.checkAndRequestPermissions(this)) {

        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = pref.getString("userData", "");
        MUser userBean = gson.fromJson(json2, MUser.class);
        //Log.e("User Bean : ", "---------------" + userBean);
        try {

            if (userBean != null) {
                userId = userBean.getUserId();
                userName = userBean.getUserName();
                userType = userBean.getUserType();
                mob = userBean.getUserMobile();
                pwd = userBean.getPassword();

            } else {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }


        } catch (Exception e) {
            //Log.e("HomeActivity : ", " Exception : " + e.getMessage());
            e.printStackTrace();
        }


        try {
            company = pref.getInt("Company", 0);
            //Log.e("Company : ", "---- " + company);
            //Log.e("HOME : ", "  : BASE_URL : " + BASE_URL);
            // BASE_URL = pref.getString("Base_Url", "");
        } catch (Exception e) {
            company = 0;
        }

        if (company == 0) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        } else if (company == 1) {
            BASE_URL = Constants.BASE_URL_1;
            COMPANY_NAME = getResources().getString(R.string.company_1);
        } else if (company == 2) {
            BASE_URL = Constants.BASE_URL_2;
            COMPANY_NAME = getResources().getString(R.string.company_2);
        } else if (company == 3) {
            BASE_URL = Constants.BASE_URL_3;
            COMPANY_NAME = getResources().getString(R.string.company_3);
        } else if (company == 4) {
            BASE_URL = Constants.BASE_URL_4;
            COMPANY_NAME = getResources().getString(R.string.company_4);
        }

//8793338336
        if (userType == 1) {
            tvMoneyIn.setVisibility(View.GONE);
            //tvMoneyInEntries.setVisibility(View.GONE);
            //tvMoneyOutEntries.setVisibility(View.GONE);
            tvApproval.setVisibility(View.GONE);
            //tvCompany.setVisibility(View.GONE);
            tvDepartment.setVisibility(View.GONE);
            tvPerson.setVisibility(View.GONE);
            tvPurpose.setVisibility(View.GONE);
            tvUserMgmt.setVisibility(View.GONE);
            //tvMenuReports.setVisibility(View.GONE);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView tvNavHeadName = navigationView.findViewById(R.id.tvNavHeadName);
        TextView tvNavHeadCompany = navigationView.findViewById(R.id.tvNavHeadCompany);
        tvNavHeadName.setText("" + userName);
        if (company == 1) {
            tvNavHeadCompany.setText("" + getResources().getString(R.string.company_1));
        } else if (company == 2) {
            tvNavHeadCompany.setText("" + getResources().getString(R.string.company_2));
        } else if (company == 3) {
            tvNavHeadCompany.setText("" + getResources().getString(R.string.company_3));
        } else if (company == 4) {
            tvNavHeadCompany.setText("" + getResources().getString(R.string.company_4));
        }


        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new HomeFragment(), "Home");
            ft.commit();
        }

    }

    @Override
    public void onBackPressed() {

        Fragment home = getSupportFragmentManager().findFragmentByTag("Home");
        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag("HomeFragment");
        Fragment personMaster = getSupportFragmentManager().findFragmentByTag("PersonMaster");
        Fragment userMaster = getSupportFragmentManager().findFragmentByTag("UserMaster");
        Fragment moneyOutEntry = getSupportFragmentManager().findFragmentByTag("MoneyOutEntry");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (home instanceof HomeFragment && home.isVisible()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogTheme);
            //builder.setTitle("Confirm Action");
            builder.setMessage("Exit Application ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (homeFragment instanceof MoneyInFragment && homeFragment.isVisible() ||
                homeFragment instanceof MoneyOutFragment && homeFragment.isVisible() ||
                homeFragment instanceof TransactionSettlementFragment && homeFragment.isVisible() ||
                homeFragment instanceof ApprovalFragment && homeFragment.isVisible() ||
                homeFragment instanceof MoneyInEntriesFragment && homeFragment.isVisible() ||
                homeFragment instanceof MoneyOutEntriesFragment && homeFragment.isVisible() ||
                homeFragment instanceof CompanyMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof DepartmentMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof PersonMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof UserManagementFragment && homeFragment.isVisible() ||
                homeFragment instanceof CashBalanceFragment && homeFragment.isVisible() ||
                homeFragment instanceof RejectedEntryFragment && homeFragment.isVisible() ||
                homeFragment instanceof PurposeMasterFragment && homeFragment.isVisible() ||
                homeFragment instanceof ReportsFragment && homeFragment.isVisible()) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new HomeFragment(), "Home");
            ft.commit();

        } else if (personMaster instanceof AddPersonFragment && personMaster.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new PersonMasterFragment(), "HomeFragment");
            ft.commit();
        } else if (userMaster instanceof AddUserFragment && userMaster.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new UserManagementFragment(), "HomeFragment");
            ft.commit();
        } else if (moneyOutEntry instanceof EditMoneyOutFragment && moneyOutEntry.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyOutEntriesFragment(), "HomeFragment");
            ft.commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tvAdminMenuHome) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new HomeFragment(), "Home");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuMoneyIn) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyInFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuMoneyOut) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyOutFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuTranscSettle) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new TransactionSettlementFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuCashBalance) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new CashBalanceFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuMoneyInEntries) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyInEntriesFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuMoneyOutEntries) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyOutEntriesFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuApproval) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new ApprovalFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuReject) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new RejectedEntryFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuCompanySelection) {
            Intent intent = new Intent(this, CompanySelectionActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("mobile", mob);
            bundle.putString("password", pwd);
            intent.putExtras(bundle);
            startActivity(intent);
            // finish();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.content_frame, new CompanyMasterFragment(), "HomeFragment");
//            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuReports) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new ReportsFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuDept) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new DepartmentMasterFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuPerson) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new PersonMasterFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuPurpose) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new PurposeMasterFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuUserMgmt) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new UserManagementFragment(), "HomeFragment");
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.tvAdminMenuLogout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogTheme);
            builder.setTitle("Logout");
            builder.setMessage("Are You Sure You Want To Logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}
