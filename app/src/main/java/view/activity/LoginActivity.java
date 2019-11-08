package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.s7k.doctroid.R;

import dialog.ErrorDialog;
import dialog.PopupDialog;
import dialog.ProgressViewDialog;
import helpers.Validator;
import view.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    public LoginActivity() {
        super(R.layout.activity_login, true);
    }

    EditText email, password;
    TextView login, forgetPassword, createAccount, errorMessage;
    ImageView errorDialog;

    @Override
    protected void doOnCreate(Bundle bundle) {
        toolbarTextView.setText("Sign in");

        initializeComponents();
        setListeners();
    }

    private void initializeComponents() {
        email = findViewById(R.id.login_email_editText);
        password = findViewById(R.id.login_password_editText);
        errorMessage = findViewById(R.id.login_errorMessage_textView);
        errorDialog = findViewById(R.id.login_errorDialog_imageView);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        login = findViewById(R.id.login_login_button);
        forgetPassword = findViewById(R.id.login_forgetPassword_textView);
        createAccount = findViewById(R.id.login_createAccount_textView);
    }

    private void setListeners() {
        login.setOnClickListener(view -> {

            //ErrorDialog.showMessageDialog(getString(R.string.no_internet_connection), "xd", LoginActivity.this);

//            PopupDialog popupDialog = new PopupDialog(new PopupDialog.ErrorDialogListener() {
//                @Override
//                public void onOkClick() {
//                    Toast.makeText(LoginActivity.this, "Hiii", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onCancelClick() {
//
//                }
//            });
//            popupDialog.showMessageDialog("lol", "xd", this);

            ProgressViewDialog progressViewDialog = new ProgressViewDialog(this);
            progressViewDialog.isShowing();
            progressViewDialog.setDialogCancelable(false);
            progressViewDialog.setCanceledOnTouchOutside(false);
            progressViewDialog.showProgressDialog("Checking information");

            if(validate())
            {
                navigateToMain();
            }
            else
            {
                progressViewDialog.hideDialog();
            }

        });
        forgetPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        });
        createAccount.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }

    private boolean validate() {
        String emailSTR = email.getText().toString().trim();
        String passSTR = password.getText().toString().trim();

        if (!Validator.isValidEmail(email.getText().toString()) && passSTR.length() < 6)
        {
            errorDialog.setVisibility(View.VISIBLE);
            errorMessage.setText("Email/Password not valid");
            return false;
        }

        if (Validator.isValidEmail(emailSTR) && passSTR.length() < 6)
        {
            errorDialog.setVisibility(View.VISIBLE);
            errorMessage.setText(getString(R.string.password_not_valid));
            return false;
        }
        if (!Validator.isValidEmail(email.getText().toString()) && passSTR.length() >= 6)
        {
            errorDialog.setVisibility(View.VISIBLE);
            errorMessage.setText(getString(R.string.email_not_valid));
            return false;
        }

        if (Validator.isValidEmail(emailSTR) && passSTR.length() >= 6) {
            // TODO Login Validation
            errorDialog.setVisibility(View.VISIBLE);
            errorMessage.setText("Welcome");
            return true;
        }
        return false;
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
