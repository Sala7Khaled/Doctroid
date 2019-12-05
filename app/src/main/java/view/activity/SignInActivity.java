package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.s7k.doctroid.R;

import java.util.HashMap;

import androidx.annotation.NonNull;

import dialog.ProgressViewDialog;
import helpers.Validator;
import network.api.ApiClient;
import network.api.ApiInterface;
import network.model.SignInForm;
import network.model.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilities.PrefManager;
import view.base.BaseActivity;

public class SignInActivity extends BaseActivity {

    public SignInActivity() {
        super(R.layout.activity_sign_in, true);
    }

    EditText email, password;
    TextView forgetPassword, createAccount, errorMessage;
    ImageView errorDialog;
    Button signIn;
    ProgressViewDialog progressViewDialog;

    @Override
    protected void onStart() {
        super.onStart();

        if(PrefManager.getToken(SignInActivity.this)!= null)
        {
            navigateToMain();
        }

    }

    @Override
    protected void doOnCreate(Bundle bundle) {
        toolbarTextView.setText("Sign in");

        initializeComponents();
        setListeners();
    }

    private void initializeComponents() {
        email = findViewById(R.id.signIn_email_editText);
        email.setText("Sala7Khaled.S7K@gmail.com");
        password = findViewById(R.id.signIn_password_editText);
        errorMessage = findViewById(R.id.signIn_errorMessage_textView);
        errorDialog = findViewById(R.id.signIn_errorDialog_imageView);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        signIn = findViewById(R.id.signIn_login_button);
        forgetPassword = findViewById(R.id.signIn_forgetPassword_textView);
        createAccount = findViewById(R.id.signIn_createAccount_textView);
    }

    private void setListeners() {
        signIn.setOnClickListener(view -> {

            progressViewDialog = new ProgressViewDialog(this);
            progressViewDialog.isShowing();
            progressViewDialog.setDialogCancelable(false);
            progressViewDialog.setCanceledOnTouchOutside(false);
            progressViewDialog.showProgressDialog("Checking information");

            if (validate()) {
                String emailSTR = email.getText().toString().toLowerCase().trim();
                String passSTR = password.getText().toString().trim();

                SignInForm signInForm = new SignInForm(emailSTR, passSTR);

                signIn(signInForm);

            } else {
                progressViewDialog.hideDialog();
            }

        });
        forgetPassword.setOnClickListener(view ->
                startActivity(new Intent(SignInActivity.this, ForgetPasswordActivity.class)));
        createAccount.setOnClickListener(view ->
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
    }

    private boolean validate() {
        String emailSTR = email.getText().toString().trim();
        String passSTR = password.getText().toString().trim();

        if (!Validator.isValidEmail(email.getText().toString()) && passSTR.length() < 6) {
            errorDialog.setVisibility(View.VISIBLE);
            errorMessage.setText(getString((R.string.email_and_password_not_valid)));
            email.requestFocus();
            return false;
        }

        if (Validator.isValidEmail(emailSTR) && passSTR.length() < 6) {
            errorDialog.setVisibility(View.VISIBLE);
            errorMessage.setText(getString(R.string.password_not_valid));
            password.requestFocus();
            return false;
        }
        if (!Validator.isValidEmail(email.getText().toString()) && passSTR.length() >= 6) {
            errorDialog.setVisibility(View.VISIBLE);
            errorMessage.setText(getString(R.string.email_not_valid));
            return false;
        }

        if (Validator.isValidEmail(emailSTR) && passSTR.length() >= 6) {
            errorDialog.setVisibility(View.VISIBLE);
            errorMessage.setText(getString(R.string.loading));
            return true;
        }
        return false;
    }

    private void signIn(SignInForm signInForm) {

        HashMap<String, String> headers = ApiClient.getHeaders();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.signIn(headers, signInForm).enqueue(new Callback<Token>() {

            @Override
            public void onResponse(@NonNull Call<Token> call,
                                   @NonNull Response<Token> response) {

                if (response.isSuccessful()) {

                    PrefManager.saveToken(SignInActivity.this, response.body().getAccessToken());
                    Log.v("token", response.body().getAccessToken());

                    progressViewDialog.hideDialog();
                    navigateToMain();

                } else {

                    errorDialog.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.GONE);
                    Toast.makeText(SignInActivity.this, R.string.email_and_password_not_valid, Toast.LENGTH_SHORT).show();
                    progressViewDialog.hideDialog();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Token> call,
                                  @NonNull Throwable t) {
                Toast.makeText(SignInActivity.this, R.string.request_error, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}