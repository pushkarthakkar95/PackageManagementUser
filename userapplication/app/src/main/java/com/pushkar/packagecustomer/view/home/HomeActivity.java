package com.pushkar.packagecustomer.view.home;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.pushkar.packagecustomer.R;
import com.pushkar.packagecustomer.databinding.ActivityHomeBinding;
import com.pushkar.packagecustomer.model.service.ErrorResponse;
import com.pushkar.packagecustomer.model.service.IResponse;
import com.pushkar.packagecustomer.model.service.ListOfOrderSuccessResponse;
import com.pushkar.packagecustomer.model.service.LogoutSuccessResponse;
import com.pushkar.packagecustomer.utils.Constants;
import com.pushkar.packagecustomer.view.BaseActivity;
import com.pushkar.packagecustomer.view.LoginUserActivity;
import com.pushkar.packagecustomer.viewmodel.HomeViewModel;

public class HomeActivity extends BaseActivity {
    private final String TAG = HomeActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private HomeViewModel viewModel;
    private ProgressDialog progressDialog;
    private boolean isDialogShown = false;
    private String dialogTitle = "";
    private String dialogMessage = "";
    private final String DIALOG_PERSISTENCE_KEY_HOME = "IS_HOME_PROGRESS_DIALOG_SHOWN";
    private ActivityHomeBinding binding;
    private OrdersListAdapter orderListAdapter;
    private final String DIALOG_TITLE_KEY_HOME = "TITLE_KEY_FOR_DIALOG_HOME";
    private final String DIALOG_MESSAGE_KEY_HOME = "MESSAGE_KEY_FOR_DIALOG_HOME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        setSupportActionBar(binding.toolbar);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCES_KEY_USER,MODE_PRIVATE);
        viewModel = ViewModelProviders.of(HomeActivity.this).get(HomeViewModel.class);
        if(savedInstanceState != null){
            if(savedInstanceState.getBoolean(DIALOG_PERSISTENCE_KEY_HOME)){
                showProgressDialog(savedInstanceState.getString(DIALOG_TITLE_KEY_HOME),
                        savedInstanceState.getString(DIALOG_MESSAGE_KEY_HOME));
                isDialogShown = true;
            }
        }
        orderListAdapter = new OrdersListAdapter(this);
        binding.contentId.ordersRV.setAdapter(orderListAdapter);
        binding.contentId.ordersRV.setLayoutManager(new LinearLayoutManager(this));
        registerGetAllPackagesResponseHandler();
        registerLogoutResponseHandler();
        binding.contentId.swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchLatestPackagesForUser(false);
        });
        fetchLatestPackagesForUser(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id  == R.id.action_logoff){
            logoutClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerGetAllPackagesResponseHandler(){
        viewModel.getGetAllPackagesMutableLiveData().observe(this, new Observer<IResponse>() {
            @Override
            public void onChanged(IResponse iResponse) {
                if(iResponse != null){
                    dismissProgressUIIfAvailable();
                    if(iResponse instanceof ListOfOrderSuccessResponse){
                        updateListOfPackages((ListOfOrderSuccessResponse) iResponse);
                    }else if(iResponse instanceof ErrorResponse){
                        showErrorToast(HomeActivity.this, (ErrorResponse) iResponse);
                    }else{
                        showGeneralErrorToast(HomeActivity.this);
                    }
                }
                viewModel.getGetAllPackagesMutableLiveData().postValue(null);
            }
        });
    }

    private void registerLogoutResponseHandler(){
        viewModel.getLogoutResponseMutableLiveData().observe(this, new Observer<IResponse>() {
            @Override
            public void onChanged(IResponse iResponse) {
                if(iResponse != null){
                    dismissProgressUIIfAvailable();
                    if(iResponse instanceof LogoutSuccessResponse){
                        resetSharedPreferences();
                        goToLoginScreen();
                    }else if(iResponse instanceof ErrorResponse){
                        showErrorToast(HomeActivity.this, (ErrorResponse) iResponse);
                    }else{
                        showGeneralErrorToast(HomeActivity.this);
                    }
                }
                viewModel.getLogoutResponseMutableLiveData().postValue(null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.contentId.ordersPlaceHolderTV.setText(sharedPreferences.getString(Constants.PREFERENCES_NAME_KEY,"User")
                +"'s Orders");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    private void fetchLatestPackagesForUser(boolean showDialog){
        if(showDialog)
            showProgressDialog("Loading Orders","please wait ...");
        viewModel.getAllPackages(sharedPreferences.getString(Constants.PREFERENCES_EMAIL_KEY,""));
    }

    private void dismissProgressUIIfAvailable(){
        if(progressDialog != null){
            progressDialog.dismiss();
            isDialogShown = false;
        }
        if(binding.contentId.swipeRefreshLayout.isRefreshing()){
            binding.contentId.swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateListOfPackages(ListOfOrderSuccessResponse response){
        Log.d(TAG,"List size is: "+response.getPackageOrderList().size());
        if(response.getPackageOrderList().size() <= 0){
            binding.contentId.ordersRV.setVisibility(View.GONE);
            binding.contentId.noPackageTV.setVisibility(View.VISIBLE);
        }else{
            binding.contentId.ordersRV.setVisibility(View.VISIBLE);
            binding.contentId.noPackageTV.setVisibility(View.GONE);
        }
        orderListAdapter.setData(response.getPackageOrderList());
    }

    private void goToLoginScreen(){
        Intent intent = new Intent(HomeActivity.this, LoginUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DIALOG_PERSISTENCE_KEY_HOME,isDialogShown);
        outState.putString(DIALOG_TITLE_KEY_HOME,dialogTitle);
        outState.putString(DIALOG_MESSAGE_KEY_HOME,dialogMessage);
    }

    private void logoutClicked() {
        Log.d(TAG, "logoutClicked");
        showProgressDialog("Logging Out","please wait ...");
        viewModel.attemptLogout(sharedPreferences.getString(Constants.PREFERENCES_EMAIL_KEY,""));
    }

    private void resetSharedPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREFERENCES_DEVICE_TOKEN_KEY,"");
        editor.putString(Constants.PREFERENCES_NAME_KEY,"");
        editor.putString(Constants.PREFERENCES_EMAIL_KEY,"");
        editor.putBoolean(Constants.PREFERENCES_IS_LOGGED_IN_KEY,false);
        editor.apply();
    }

    private void showProgressDialog(String title, String message){
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setTitle(title);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        dialogMessage = message;
        dialogTitle = title;
        isDialogShown = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog != null){
            progressDialog.dismiss();
            isDialogShown = false;
        }
    }

}
