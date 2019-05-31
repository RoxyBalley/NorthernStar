package com.roxy.northernstar;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.search.Request;
import com.nokia.maps.restrouting.Response;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.File;
import java.net.URL;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestView {
    private AppCompatActivity m_activity;
    private TextView m_resultTextView;
    String url = "https://places.cit.api.here.com/places/v1/autosuggest?app_id=Lg2lMHSGM1GbMkCFfJkk&app_code=S41DXs0J4ACvIaY9eLXsRA&at=12.9279,77.6271&q=shell&pretty";
    //https://places.cit.api.here.com/places/v1/autosuggest?app_id=Lg2lMHSGM1GbMkCFfJkk&app_code=S41DXs0J4ACvIaY9eLXsRA&at=12.9279,77.6271&q=shell&pretty


    public RequestView(AppCompatActivity activity) {
        m_activity = activity;
        initMapEngine();
        initUIElements();
        URLrequest();
    }

    private void initMapEngine() {
        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            /*
             * Even though we don't display a map view in this application, in order to access any
             * services that HERE Android SDK provides, the MapEngine must be initialized as the
             * prerequisite.
             */
            MapEngine.getInstance().init(new ApplicationContext(m_activity), new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    Toast.makeText(m_activity, "Map Engine initialized with error code:" + error,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initUIElements() {
        m_resultTextView = (TextView) m_activity.findViewById(R.id.resultTextView);
        Button submitButton = (Button) m_activity.findViewById(R.id.submitbtn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                EditText locationText = (EditText) m_activity.findViewById(R.id.query);
            }
        });
    }


    private void URLrequest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://places.cit.api.here.com/places/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        // create an instance of the ApiService
        ApiService apiService = retrofit.create(ApiService.class);
        // make a request by calling the corresponding method
        String app_id = "Lg2lMHSGM1GbMkCFfJkk";
        String app_code = "S41DXs0J4ACvIaY9eLXsRA";
        String latlong = "12.9279,77.6271";
        String q = "emirates";

        Single<ResponseModel> result = apiService.getPersonData(app_id ,app_code ,latlong , q);

    }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        mTextView.setText("Response: " + response.toString());
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO: Handle error
//
//                    }
//                });
//
//// Access the RequestQueue through your singleton class.
//        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
//        private void updateTextView ( final String txt){
//            m_activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    m_resultTextView.setText(txt);
//                }
//            });
//        }
//    }
}