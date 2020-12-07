package ai.chadda.myruns_4.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ai.chadda.myruns_4.R;

public class LoginActivity extends AppCompatActivity {

    // Constants
    public static final String EXTRA_KEY_EMAIL = "email";
    public static final String EXTRA_KEY_PASSWORD = "password";
    public final static String PROFILE_ORIGIN_KEY = "origin";
    public final static String PROFILE_ORIGIN_VALUE = "login";

    // Globals
    private Button mSignInButton, mRegisterButton;
    private EditText mEmailEText, mPasswordEText;
    public String mEmail, mPassword;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up layout
        setContentView(R.layout.activity_login);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Sign In");

        // Initialize layout
        mSignInButton = findViewById(R.id.ssignin_button);
        mRegisterButton = findViewById(R.id.sregister_button);
        mEmailEText = findViewById(R.id.semail_etext);
        mPasswordEText = findViewById(R.id.spassword_etext);

        // Load entries if necessary
        if (savedInstanceState != null) {
            mEmailEText.setText(savedInstanceState.getString(EXTRA_KEY_EMAIL));
            mPasswordEText.setText(savedInstanceState.getString(EXTRA_KEY_PASSWORD));
        }

        // Set up listeners
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra(PROFILE_ORIGIN_KEY, PROFILE_ORIGIN_VALUE);
                startActivity(intent);
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
                if (mEmail.length() > 0 && mPassword.length() > 0) {

                    // Check if email and password are correct, if so go to MainActivity
                    if ((mEmail.equals(LoginActivity.this.mEmailEText.getText().toString())) && (mPassword.equals(LoginActivity.this.mPasswordEText.getText().toString()))) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Email or Password is Incorrect", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please Register First", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(RegisterActivity.SHARED_PREF, MODE_PRIVATE);
        mEmail = sharedPreferences.getString(EXTRA_KEY_EMAIL, "");
        mPassword = sharedPreferences.getString(EXTRA_KEY_PASSWORD, "");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_EMAIL, LoginActivity.this.mEmailEText.getText().toString());
        outState.putString(EXTRA_KEY_PASSWORD, LoginActivity.this.mPasswordEText.getText().toString());
    }

    @Override
    public void onBackPressed() {
    }
}
