package com.pushkar.packagecustomer.view;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pushkar.packagecustomer.R;
import com.pushkar.packagecustomer.databinding.ActivityMainBinding;
import com.pushkar.packagecustomer.model.LoginRequestData;
import com.pushkar.packagecustomer.model.service.ErrorResponse;
import com.pushkar.packagecustomer.model.service.IResponse;
import com.pushkar.packagecustomer.model.service.LoginSuccessResponse;
import com.pushkar.packagecustomer.service.MyBeaconApplication;
import com.pushkar.packagecustomer.utils.Constants;
import com.pushkar.packagecustomer.utils.Validate;
import com.pushkar.packagecustomer.view.home.HomeActivity;
import com.pushkar.packagecustomer.viewmodel.LoginUserViewModel;

public class LoginUserActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private LoginUserViewModel viewModel;
    private ProgressDialog progressDialog;
    private final String DIALOG_PERSISTENCE_KEY = "DIALOG_SHOW";
    private boolean isDialogShown = false;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        sharedPreferences = getSharedPreferences(Constants.PREFERENCES_KEY_USER,MODE_PRIVATE);
        viewModel = ViewModelProviders.of(this).get(LoginUserViewModel.class);
        if (savedInstanceState != null) {
            if(savedInstanceState.getBoolean(DIALOG_PERSISTENCE_KEY)){
                showProgressDialog("Login in progress","please wait....");
            }
        }
        viewModel.getLoginResponseLiveData().observe(this, new Observer<IResponse>() {
            @Override
            public void onChanged(IResponse iResponse) {
                if(iResponse!=null){
                    if(progressDialog != null){
                        progressDialog.dismiss();
                        isDialogShown = false;
                    }
                    if(iResponse instanceof LoginSuccessResponse){
                        updateUserInfo((LoginSuccessResponse) iResponse);
                        goToMainScreen();
                    }else if (iResponse instanceof ErrorResponse){
                        showErrorToast(getApplicationContext(), (ErrorResponse) iResponse);
                    }else{
                        showGeneralErrorToast(getApplicationContext());
                    }
                }
                viewModel.getLoginResponseLiveData().postValue(null);
            }
        });
    }

    private void updateUserInfo(LoginSuccessResponse loginSuccessResponse){
        String name = loginSuccessResponse.getNameOfUser();
        String email = binding.loginUserEmailET.getText().toString();
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        if(!name.equals(sharedPreferences.getString(Constants.PREFERENCES_NAME_KEY,""))){
            preferencesEditor.putString(Constants.PREFERENCES_NAME_KEY,name);
        }
        if(!email.equals(sharedPreferences.getString(Constants.PREFERENCES_EMAIL_KEY,""))){
            preferencesEditor.putString(Constants.PREFERENCES_EMAIL_KEY,email);
        }
        preferencesEditor.putBoolean(Constants.PREFERENCES_IS_LOGGED_IN_KEY,true);
        preferencesEditor.apply();
        MyBeaconApplication beaconApplication = new MyBeaconApplication();
        beaconApplication.setUserEmail(this);
    }

    private String getDeviceToken(){
        return sharedPreferences.getString(Constants.PREFERENCES_DEVICE_TOKEN_KEY,"");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DIALOG_PERSISTENCE_KEY,isDialogShown);
    }

    public void loginClickedOnLoginScreen(View view) {
        LoginRequestData loginRequestData = new LoginRequestData(binding.loginUserEmailET.getText().toString(),
                binding.loginUserPasswordET.getText().toString(), getDeviceToken());
        if(!Validate.isLoginDataValid(loginRequestData)){
            Toast.makeText(getApplicationContext(),R.string.incorrect_registration_input_toast_message
                    ,Toast.LENGTH_SHORT).show();
        }else{
            showProgressDialog("Login in progress","please wait....");
            viewModel.attemptLogin(loginRequestData);
        }
    }

    private void goToMainScreen(){
        Intent intent =  new Intent(LoginUserActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog != null){
            progressDialog.dismiss();
            isDialogShown = false;
        }
    }

    private void showProgressDialog(String title, String message){
        progressDialog = new ProgressDialog(LoginUserActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setTitle(title);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        isDialogShown = true;
    }

    public void goToRegisterActivity(View view) {
        Intent intent =  new Intent(LoginUserActivity.this,RegisterUserActivity.class);
        startActivity(intent);
    }
}
