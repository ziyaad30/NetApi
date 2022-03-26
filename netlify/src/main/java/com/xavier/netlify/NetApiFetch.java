package com.xavier.netlify;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONObject;

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
        AndroidNetworking.get(apiUrl)
                .addHeaders("content-type", "application/json")
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject jsonObject) {
                        if (okHttpResponse.code() == 200) {
                            listener.onSuccess(jsonObject);
                            listener.onComplete("Netlify fetch, found "
                                    + jsonObject.length() +
                                    " results.");
                        } else {
                            listener.onFailed("Failed with " + okHttpResponse.message());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        listener.onFailed(anError.getMessage());
                        showLog(" Netlify runner had errors " + anError.getLocalizedMessage());
                    }
                });
    }
}
