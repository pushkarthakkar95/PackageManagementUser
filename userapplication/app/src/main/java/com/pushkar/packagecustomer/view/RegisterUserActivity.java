package com.pushkar.packagecustomer.view;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pushkar.packagecustomer.R;
import com.pushkar.packagecustomer.databinding.ActivityRegisterUserBinding;
import com.pushkar.packagecustomer.model.RegistrationData;
import com.pushkar.packagecustomer.model.service.ErrorResponse;
import com.pushkar.packagecustomer.model.service.IResponse;
import com.pushkar.packagecustomer.model.service.RegisteredUserDataHolder;
import com.pushkar.packagecustomer.utils.Validate;
import com.pushkar.packagecustomer.viewmodel.RegisterUserViewModel;

public class RegisterUserActivity extends BaseActivity {
    private final String PROGRESS_DIALOG_PERSISTENCE_KEY = "DIALOG_SHOW";
    private ActivityRegisterUserBinding binding;
    private RegisterUserViewModel viewModel;
    private ProgressDialog progressDialog;
    private boolean isDialogShown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(RegisterUserActivity.this,R.layout.activity_register_user);
        viewModel = ViewModelProviders.of(this).get(RegisterUserViewModel.class);
        if (savedInstanceState != null) {
            if(savedInstanceState.getBoolean(PROGRESS_DIALOG_PERSISTENCE_KEY)){
                showProgressDialog("Registration in progress","please wait....");
            }
        }
        viewModel.getRegisterUserResponseLiveData().observe(this, new Observer<IResponse>() {
            @Override
            public void onChanged(IResponse iResponse) {
                if(iResponse != null){
                    if(progressDialog != null){
                        progressDialog.dismiss();
                        isDialogShown = false;
                    }
                    if(iResponse instanceof RegisteredUserDataHolder){
                        onBackPressed();
                        Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (iResponse instanceof ErrorResponse){
                        showErrorToast(getApplicationContext(), (ErrorResponse) iResponse);
                    }else {
                        showGeneralErrorToast(getApplicationContext());
                    }
                }
                viewModel.getRegisterUserResponseLiveData().postValue(null);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PROGRESS_DIALOG_PERSISTENCE_KEY,isDialogShown);
    }

    private void showProgressDialog(String title, String message){
        progressDialog = new ProgressDialog(RegisterUserActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setTitle(title);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        isDialogShown = true;
    }

    public void registerClicked(View view) {
        RegistrationData data = new RegistrationData(
                binding.registerUserEmailET.getText().toString(),
                binding.registerUserPasswordET.getText().toString(),
                binding.registerUserConfirmPasswordET.getText().toString(),
                binding.registerUserNameET.getText().toString()
        );
        if(!Validate.isRegistrationDataValid(data)){
            Toast.makeText(getApplicationContext(),R.string.incorrect_registration_input_toast_message
                    ,Toast.LENGTH_SHORT).show();
        }else{
            showProgressDialog("Registration in progress","please wait....");
            viewModel.attemptRegistration(data);
        }
    }
}
