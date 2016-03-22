package com.copia.copiasalesmobile;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.services.ServiceSyncAgent;
import com.copia.copiasalesmobile.services.ServiceSyncProd;
import com.copia.copiasalesmobile.utilities.AlertDialogManagerCancelSettings;
import com.copia.copiasalesmobile.utilities.CheckConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeScreen extends AppCompatActivity {
    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.example.android.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "copia.com";
    // The account name
    public static final String ACCOUNT = "copiasyncaccount";



    AlertDialogManagerCancelSettings alert_cs = new AlertDialogManagerCancelSettings();
    private DrawerLayout mDrawerLayout;
    DatabaseConnectorSqlite dbConnector;

    public String sUsername = "";
    Boolean isInternetPresent = false;
    CheckConnection connectionDetector;
    Account mAccount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Create the dummy account
        //mAccount = CreateSyncAccount(this);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (viewPager != null) {
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setupWithViewPager(viewPager);
        }

        dbConnector = new DatabaseConnectorSqlite(getApplicationContext());



        connectionDetector = new CheckConnection();
        isInternetPresent = connectionDetector.isOnline();

        //get user data from session
        /*HashMap<String, String> user = session.getUserDetails();
        sUsername = user.get(SessionManager.KEY_USERNAME);*/

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new CustomersOrdersFragment(), "Customers Orders");
        adapter.addFragment(new DraftOrdersFragment(), "Draft Orders");
        adapter.addFragment(new SalesOrdersFragment(), "Sales Orders");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        if (menuItem.getItemId() == android.R.id.home){
                            mDrawerLayout.openDrawer(GravityCompat.START);
                            return true;
                        }
                        //nav_sync_orders
                        else if (menuItem.getItemId() == R.id.nav_sync_orders){
                            Intent srv = new Intent(getApplicationContext(), ServiceSyncAgent.class);
                            startService(srv);
                            return true;
                        }else if (menuItem.getItemId() == R.id.nav_sync_prod){
                            Intent srv = new Intent(getApplicationContext(), ServiceSyncProd.class);
                            startService(srv);
                            return true;
                        }else if (menuItem.getItemId() == R.id.nav_sync_agents){
                            Intent srv = new Intent(getApplicationContext(), ServiceSyncAgent.class);
                            startService(srv);
                            return true;
                        }
                       /*else if (menuItem.getItemId() == R.id.nav_home){
                            startActivity(new Intent(getApplicationContext(), DashBoard.class));
                            return true;
                        }
                        else if (menuItem.getItemId() == R.id.nav_orders){
                            Intent nwOrder = new Intent(getApplicationContext(), AddCustomerActivity.class);
                            startActivity(nwOrder);
                            return true;
                        }
                        else if (menuItem.getItemId() == R.id.nav_new_orders){
                            Intent intCatSqlite = new Intent(getApplicationContext(), CategoryActivitySqlite.class);
                            startActivity(intCatSqlite);
                            return true;
                        }
                        else if (menuItem.getItemId() == R.id.nav_reports){
                            Intent intRe = new Intent(getApplicationContext(), ReportsDialog.class);
                            startActivity(intRe);
                            return true;
                        }
                        else if (menuItem.getItemId() == R.id.nav_products){
                            startActivity(new Intent(getApplicationContext(), ProductDetailsSqlite.class));
                            return true;
                        }
                        else if (menuItem.getItemId() == R.id.nav_media){
                            Intent intMe = new Intent(getApplicationContext(), MediaActivityGrid.class);
                            startActivity(intMe);
                            return true;
                        }
                        else if (menuItem.getItemId() == R.id.nav_sync){
                            // Launch Sync Screen
                            if (isInternetPresent) {
                                Intent srv = new Intent(getApplicationContext(), ProductsSync.class);
                                setProgressBarIndeterminateVisibility(true);
                                getApplicationContext().startService(srv);
                                Toast.makeText(getApplicationContext(), "Syncing...Please wait!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                alert_cs.showAlertDialog(HomeScreen.this,
                                        "No Internet Connection",
                                        "Ecatalogue requires a network connection to sync, \n" +
                                                "Please confirm data connection is on and you have sufficient credit.", false);
                            }
                        }
                        else if (menuItem.getItemId() ==R.id.nav_deliveries){
                            Intent intDeli = new Intent(getApplicationContext(), DeliveryAgentsHistory.class);
                            startActivity(intDeli);
                            return true;
                        }
                        else if (menuItem.getItemId() == R.id.nav_about){
                            Intent intAbt = new Intent(getApplicationContext(), AboutActivity.class);
                            startActivity(intAbt);
                            return true;
                        }
                        else if (menuItem.getItemId() ==R.id.nav_logout){
                            Toast.makeText(getApplicationContext(), "Logged Out!", Toast.LENGTH_LONG).show();
                            session.logoutUser();
                            dbConnector.deleteUserType();
                            return true;}*/
                        return true;
                    }
                });
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }*/

        if (item.getItemId() == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Don't let the system handle the back button
        //Intent intDB = new Intent(getApplicationContext(), DashBoard.class);
        //startActivity(intDB);
    }
}
