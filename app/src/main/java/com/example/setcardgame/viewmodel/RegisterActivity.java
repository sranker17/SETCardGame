package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;
import com.example.setcardgame.exception.RefreshException;
import com.example.setcardgame.listener.AuthResponseListener;
import com.example.setcardgame.model.Error;
import com.example.setcardgame.model.auth.AuthUser;
import com.example.setcardgame.service.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private final AuthService authService = new AuthService(RegisterActivity.this);
    private EditText usernameET;
    private EditText passwordET;
    private static final String REGISTER = "REGISTER";
    private static final String USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameET = findViewById(R.id.usernameInput);
        passwordET = findViewById(R.id.passwordInput);
    }

    public void sendRegister(View view) {
        if (!usernameET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty()) {
            //TODO add more validation
            AuthUser authUser = new AuthUser(usernameET.getText().toString(), passwordET.getText().toString());
            authService.register(authUser, new AuthResponseListener() {
                @Override
                public void onError(Error errorResponse) {
                    Log.e(REGISTER, errorResponse.toString());
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
                        case 409:
                            toastMessage = getString(R.string.takenUsername);
                            break;
                        case 500:
                            toastMessage = getString(R.string.internalServerError);
                            break;
                        case 503:
                            toastMessage = getString(R.string.serverUnavailable);
                            switchToMain();
                            break;
                        default:
                            toastMessage = errorResponse.getDescription();
                    }
                    Toast.makeText(RegisterActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(JSONObject loginResponse) {
                    Log.i(REGISTER, loginResponse.toString());
                    try {
                        String username = loginResponse.getString(USERNAME);
                        JSONObject roleObject = loginResponse.getJSONObject("role");
                        String roleName = roleObject.getString("name");
                        Log.d(REGISTER, "Successfully registered user: " + username + " with role: " + roleName);
                    } catch (JSONException e) {
                        Log.e(REGISTER, "Error parsing login response", e);
                        throw new RuntimeException(e);
                    }
                    try {
                        authService.refreshToken(isOnline -> {
                            Log.i(REGISTER, "Server status online: " + isOnline);
                            switchToMain();
                        });
                        switchToMain();
                    } catch (RefreshException e) {
                        Log.e(REGISTER, "Error refreshing token", e);
                    }
                }
            });
        }
    }

    public void switchToMain() {
        Intent d = new Intent(this, MainActivity.class);
        startActivity(d);
    }
}