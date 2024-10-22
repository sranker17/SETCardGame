package com.example.setcardgame.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.setcardgame.R;
import com.example.setcardgame.listener.BaseListener;
import com.example.setcardgame.model.Error;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorHandlerService {
    private static final String ERROR_HANDLER = "ErrorHandler";

    public static void handleErrorResponse(VolleyError error, BaseListener listener, Context context) {
        Error errorResponse = new Error();
        if (error.networkResponse != null && error.networkResponse.statusCode == 503) {
            errorResponse.setTitle(context.getString(R.string.serverUnavailable));
            errorResponse.setDetail(context.getString(R.string.serverUnavailable));
            errorResponse.setDescription(context.getString(R.string.serverUnavailable));
            errorResponse.setStatus(503);
        } else if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String errorData = new String(error.networkResponse.data);
                Log.e(ERROR_HANDLER, errorData);
                JSONObject errorJson = new JSONObject(errorData);
                String title = errorJson.optString("title", "Error");
                int statusCode = errorJson.getInt("status");
                String detail = errorJson.optString("detail", "No details provided");
                String instance = errorJson.optString("instance", "");
                String description = errorJson.optString("description", "No description provided");
                errorResponse = new Error(title, statusCode, detail, instance, description);

            } catch (JSONException e) {
                Log.e(ERROR_HANDLER, "Error parsing error response in handleErrorResponse", e);
            }
        }

        Log.e(ERROR_HANDLER, "Error response in handleErrorResponse: " + errorResponse);
        listener.onError(errorResponse);
    }
}
