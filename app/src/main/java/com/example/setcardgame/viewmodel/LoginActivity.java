package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;
import com.example.setcardgame.listener.AuthResponseListener;
import com.example.setcardgame.model.Error;
import com.example.setcardgame.model.auth.AuthUser;
import com.example.setcardgame.service.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameET;
    private EditText passwordET;
    private final AuthService authService = new AuthService(LoginActivity.this);
    private static final String LOGIN = "LOGIN";
    private static final String TOKEN = "token";
    private static final String EXPIRES_IN = "expiresIn";
    private static final String AUTH = "auth";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET = findViewById(R.id.usernameInput);
        passwordET = findViewById(R.id.passwordInput);
    }

    public void sendLogin(View view) {
        if (!usernameET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty()) {
            //TODO add more validation
            AuthUser authUser = new AuthUser(usernameET.getText().toString(), passwordET.getText().toString());
            authService.login(authUser, new AuthResponseListener() {
                @Override
                public void onError(Error errorResponse) {
                    Log.e(LOGIN, errorResponse.toString());
                    String toastMessage;
                    switch (errorResponse.getStatus()) {
                        case 400:
                            toastMessage = getString(R.string.invalidParameters);
                            break;
                        case 401:
                            toastMessage = getString(R.string.badCredentials);
                            break;
                        case 403:
                            toastMessage = getString(R.string.accountError);
                            break;
                        case 500:
                            toastMessage = getString(R.string.internalServerError);
                            break;
                        default:
                            toastMessage = errorResponse.getDescription();
                    }
                    Toast.makeText(LoginActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(JSONObject loginResponse) {
                    Log.i(LOGIN, loginResponse.toString());
                    try {
                        String token = loginResponse.getString(TOKEN);
                        long expiresIn = loginResponse.getLong(EXPIRES_IN);

                        SharedPreferences sp = getSharedPreferences(AUTH, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(TOKEN, token);
                        editor.putLong(EXPIRES_IN, expiresIn);
                        editor.putString(USERNAME, authUser.getUsername());
                        editor.putString(PASSWORD, authUser.getPassword());
                        editor.apply();

                        Log.i(LOGIN, "Token stored successfully: " + token);
                    } catch (JSONException e) {
                        Log.e(LOGIN, "Error parsing login response", e);
                        throw new RuntimeException(e);
                    }
                    switchToMain();
                }
            });
        }
    }

    public void switchToMain() {
        Intent d = new Intent(this, MainActivity.class);
        startActivity(d);
    }
}