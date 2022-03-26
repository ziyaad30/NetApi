package com.xavier.netlify;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetApiFetch {
    private final String TAG = NetApiFetch.class.getSimpleName();

    String apiUrl;

    public void showLog(String message) {
        Log.e(TAG, message);
    }

    private onFetchApiListener listener;

    public void setListener(onFetchApiListener listener) {
        this.listener = listener;
    }

    public interface onFetchApiListener {
        void onSuccess (JSONObject jsonObject);
        void onFailed (String errorMessage);
        void onComplete (String message);
    }

    public NetApiFetch(String apiUrl) {
        this.listener = null;
        this.apiUrl = apiUrl;
        fetchFromApi();
    }

    private void fetchFromApi(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(this.apiUrl)
                .addHeader("content-type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e(TAG, "Request executed: " + call.isExecuted());
                if (response.code() != 200) {
                    Log.e(TAG, "RESPONSE CODE: " + response.code());
                    return;
                }
                if (response.isRedirect()) {
                    Log.e(TAG, "REDIRECTED: " + response.isRedirect());
                    return;
                }
                if (!response.isSuccessful()) {
                    Log.e(TAG, "SUCCESS: " + response.isSuccessful());
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                    Log.e(TAG, "NetApiFetch, found "
                            + jsonObject.length() +
                            " results.");
                    Log.e(TAG, jsonObject.toString());
                    listener.onSuccess(jsonObject);
                } catch (JSONException e) {
                    listener.onFailed(e.getMessage());
                }
                
                listener.onComplete("NetApiFetch has completed!");
            }
        });
    }
}
