package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.s7k.doctroid.R;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import dialog.ProgressViewDialog;
import es.dmoral.toasty.Toasty;
import helpers.Validator;
import network.api.ApiClient;
import network.api.ApiInterface;
import network.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import view.base.BaseActivity;

import static es.dmoral.toasty.Toasty.LENGTH_LONG;

public class SignUpActivity extends BaseActivity {

    public SignUpActivity() {
        super(R.layout.activity_sign_up, true);
    }

    EditText firstName, lastName, email, password, confirmPassword, phone;
    ImageView errorDialog, male, female;
    TextView errorMessage;
    Button signUp;
    ProgressViewDialog progressViewDialog;
    boolean maleSelected = false;
    boolean femaleSelected = false;

    @Override
    protected void doOnCreate(Bundle bundle) {
        toolbarTextView.setText("Sign Up");
        toolbarBackImageView.setVisibility(View.VISIBLE);

        initializeComponents();
        setListeners();

    }

    private void initializeComponents() {
        firstName = findViewById(R.id.signUp_firstName_editText);
        lastName = findViewById(R.id.signUp_lastName_editText);
        email = findViewById(R.id.signUp_email_editText);
        password = findViewById(R.id.signUp_password_editText);
        confirmPassword = findViewById(R.id.signUp_confirmPassword_editText);
        male = findViewById(R.id.signUp_male_imageView);
        female = findViewById(R.id.signUp_female_imageView);
        signUp = findViewById(R.id.signUp_signUp_button);
        phone = findViewById(R.id.signUp_phone_editText);
        errorMessage = findViewById(R.id.signUp_errorMessage_textView);
        errorDialog = findViewById(R.id.signUp_errorDialog_imageView);
    }

    private void setListeners() {

        male.setOnClickListener(view -> {
            maleSelected = true;
            femaleSelected = false;
            male.setImageDrawable(getResources().getDrawable(R.drawable.signup_male_selected));
            female.setImageDrawable(getResources().getDrawable(R.drawable.signup_female_deselected));
        });

        female.setOnClickListener(view -> {
            maleSelected = false;
            femaleSelected = true;
            male.setImageDrawable(getResources().getDrawable(R.drawable.signup_male_deselected));
            female.setImageDrawable(getResources().getDrawable(R.drawable.signup_female_selected));
        });

        signUp.setOnClickListener(view -> {

            if (firstName.getText().toString().trim().isEmpty()) {
                errorDialog.setVisibility(view.VISIBLE);
                errorMessage.setVisibility(view.VISIBLE);
                errorMessage.setText("Enter your First name");
                firstName.requestFocus();
            } else if (lastName.getText().toString().trim().isEmpty()) {
                errorDialog.setVisibility(view.VISIBLE);
                errorMessage.setVisibility(view.VISIBLE);
                errorMessage.setText("Enter your Last name");
                lastName.requestFocus();
            } else if (!Validator.isValidEmail(email.getText().toString().trim())) {
                errorDialog.setVisibility(view.VISIBLE);
                errorMessage.setVisibility(view.VISIBLE);
                errorMessage.setText(getString(R.string.email_not_valid));
                email.requestFocus();
            } else if (!Validator.isValidPhoneNumber(phone.getText().toString().trim())) {
                errorDialog.setVisibility(view.VISIBLE);
                errorMessage.setVisibility(view.VISIBLE);
                errorMessage.setText(getString(R.string.phone_not_valid));
                phone.requestFocus();
            } else if (!Validator.isConfirmPassMatchPass(password.getText().toString().trim(),
                    confirmPassword.getText().toString().trim())) {
                errorDialog.setVisibility(view.VISIBLE);
                errorMessage.setVisibility(view.VISIBLE);
                errorMessage.setText(getString(R.string.password_not_valid));
            } else if (!maleSelected && !femaleSelected) {
                errorDialog.setVisibility(view.VISIBLE);
                errorMessage.setVisibility(view.VISIBLE);
                errorMessage.setText("Please select a gender");
            } else {
                errorDialog.setVisibility(view.INVISIBLE);
                errorMessage.setVisibility(view.INVISIBLE);
            }

            if (Validator.registerValidation(this, firstName, lastName,
                    email, password, confirmPassword, phone)) {
                if (maleSelected || femaleSelected) {
                    createNewUser();
                }
            }

        });

    }

    private void createNewUser() {

        String firstNameSTR = firstName.getText().toString().trim();
        String lastNameSTR = lastName.getText().toString().trim();
        String emailSTR = email.getText().toString().toLowerCase().trim();
        String phoneSTR = phone.getText().toString().trim();
        String passwordSTR = password.getText().toString().trim();
        String genderSTR;

        if (maleSelected) {
            genderSTR = "Male";
        } else if (femaleSelected) {
            genderSTR = "Female";
        } else {
            genderSTR = "Not assigned";
        }

        progressViewDialog = new ProgressViewDialog(this);
        progressViewDialog.isShowing();
        progressViewDialog.setDialogCancelable(false);
        progressViewDialog.setCanceledOnTouchOutside(false);
        progressViewDialog.showProgressDialog("Creating new account");

        User user = new User(firstNameSTR, lastNameSTR,
                emailSTR, phoneSTR, passwordSTR, genderSTR, false);

        signUp(user);
    }

    private void signUp(User user) {
        HashMap<String, String> headers = ApiClient.getHeaders();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiService.signUp(headers, user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    Toasty.info(SignUpActivity.this, "Account created! Please sign in to continue.", Toast.LENGTH_SHORT).show();
                    progressViewDialog.hideDialog();
                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                    finish();

                } else {

                    Toast.makeText(SignUpActivity.this, response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,
                                  @NonNull Throwable t) {
                Toasty.error(SignUpActivity.this, Objects.requireNonNull(t.getMessage()), LENGTH_LONG).show();
            }
        });

    }

}
