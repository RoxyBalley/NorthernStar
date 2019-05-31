package com.roxy.northernstar;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapView;
import com.here.android.mpa.search.AutoSuggest;
import com.here.android.mpa.search.AutoSuggestPlace;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.Location;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest2;
import com.here.android.mpa.search.TextAutoSuggestionRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaceSearchView {
    public static ArrayList<AutoSuggestPlace> autoSuggestList = new ArrayList<>();
    Handler handler = new Handler();
    MapMarker mapMarker;
    // handler is used to start search after the user types a certain word not after every input character
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    View view;
    SearchResultListAdapter searchResultListAdapter;
    private ArrayList<AutoSuggestPlace> tempautoSuggestList;
    private FragmentActivity m_activity;
    private MapEngine mapEngine;
//    private SupportMapFragment m_mapFragment;
    private RecyclerView searchRecyclerView;
    private RelativeLayout mapLayout;
    private String query;
    private Map m_map;
    private GeoCoordinate startLocGeoCoordiante = null;
    private GeoCoordinate endLocGeoCoordiante = null;
    private TextView queryDestination;
    private MapView mapView;
    private String TAG = "PlaceSearchView";
    // Example request listener
    private ResultListener<List<AutoSuggest>> listResultListener = new ResultListener<List<AutoSuggest>>() {

        @Override
        public void onCompleted(List<AutoSuggest> autoSuggests, ErrorCode error) {
            if (error == ErrorCode.NONE)
                System.out.print("Error code NONE");
            if (autoSuggests == null) {
                System.out.print("Autosuggested List is Empty");
                return;
            }
            for (AutoSuggest suggest : autoSuggests) {
                try {
                    // set title
                    String title = suggest.getTitle();

                    if (suggest instanceof AutoSuggestPlace) {

                        AutoSuggestPlace autoSuggestPlace = (AutoSuggestPlace) suggest;

                        // vicinity
                        if (autoSuggestPlace.getVicinity() != null) {
                            String vicinity = autoSuggestPlace.getVicinity();
                        }

                        // set position
                        if (autoSuggestPlace.getPosition() != null) {
                            String position = autoSuggestPlace.getPosition().toString();
                        }

                        // set boundaryBox
                        if (((AutoSuggestPlace) suggest).getBoundingBox() != null) {
                            String boundingBox = ((AutoSuggestPlace) suggest).getBoundingBox().toString();
                        }
                    }
                    if (suggest instanceof AutoSuggestPlace) {

                        autoSuggestList.add((AutoSuggestPlace) suggest);

//                        AutoSuggestPlace autoSuggestPlace = (AutoSuggestPlace) suggest;

                    }
                } catch (Exception e) {
                    //Handle invalid create search request parameters
                    Log.e("ERROR: ", e.getMessage());
                }
                tempautoSuggestList = autoSuggestList;
//                int NUM_RESULTS = tempautoSuggestList.size();

                if (tempautoSuggestList.size() == 0) {
                    // to search again

                    searchRecyclerView.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.VISIBLE);

                } else {
                    for (AutoSuggestPlace autoSuggestPlace : tempautoSuggestList) {
                        System.out.println("autoSuggestPlace :" + autoSuggestPlace.getTitle());
//                        CommonMethods.printLog(TAG,"autoSuggestPlace :" + autoSuggestPlace.getTitle());
                    }

                    searchRecyclerView.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.GONE);
                    searchResultListAdapter.notifyDataSetChanged();
                }
            }

        }
    };
    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500) && query != null) {
                // TODO: do what you need here
                autoSuggestList.clear();
                TextAutoSuggestionRequest textAutoSuggestionRequest = new TextAutoSuggestionRequest(query);

                textAutoSuggestionRequest.setSearchCenter(m_map.getCenter());
//                PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);

                m_map.setCenter(PositioningManager.getInstance().getPosition().getCoordinate(), Map.Animation.NONE);

                textAutoSuggestionRequest.execute(listResultListener);
            }
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //You need to remove this to run only once
            handler.removeCallbacks(input_finish_checker);

        }

        @Override
        public void afterTextChanged(Editable s) {

            try {
                if (!s.toString().isEmpty()) {

                    query = s.toString();
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);

                } else {
                    tempautoSuggestList.clear();
                    searchResultListAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print("Map Engine did not get initialised in last attempt");
//                initMapEngine("");
            }


        }
    };
    private RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(m_activity, searchRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            //hiding keyboard
            View keyboardView = m_activity.getCurrentFocus();
            if (keyboardView != null) {
                InputMethodManager imm = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
            }

            clearMap();

            //location selected, show it on map
            AutoSuggestPlace autoSuggestPlace = tempautoSuggestList.get(position);

            // show map fragment
            searchRecyclerView.setVisibility(View.GONE);
            mapLayout.setVisibility(View.VISIBLE);
//            TODO: hide search bar and add back button when map is visible
//            isSearchBarVisible =false;

            //set name
            queryDestination.setText(autoSuggestPlace.getTitle());
            query = null;
            GeoCoordinate geoCoordinate = autoSuggestPlace.getPosition();

//            android.location.Location location = new android.location.Location("endloc");
//            location.setLatitude(geoCoordinate.getLatitude());
//            location.setLongitude(geoCoordinate.getLongitude());

            // set destination
//            tripCurrentData.setNavLocation(location);


            endLocGeoCoordiante = geoCoordinate;
            addMarkerAtPlace(geoCoordinate);
//            showMapForDestiantion();
//            markCurrentLocation();

        }

        @Override
        public void onLongItemClick(View view, int position) {

        }
    });

    public PlaceSearchView(FragmentActivity activity, View view) {
        m_activity = activity;
        this.view = view;
        queryDestination = (TextView) view.findViewById(R.id.query);
        mapView = (MapView) view.findViewById(R.id.mapfragment);
        mapLayout = (RelativeLayout) view.findViewById(R.id.maplayout);
        query = queryDestination.getText().toString();
        queryDestination.addTextChangedListener(textWatcher);
        searchRecyclerView = view.findViewById(R.id.recyclerViewPlace);
        setupRecyclerView();
        mapView = getMapView();
        initMapEngine();
    }

    private void initMapEngine() {

        if (!MapEngine.isInitialized()) {

            // Set path of isolated disk cache
            String diskCacheRoot = m_activity.getExternalFilesDir(null).getAbsolutePath() + File.separator + ".isolated-here-maps";

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
                mapEngine = MapEngine.getInstance();
                ApplicationContext appContext = new ApplicationContext(m_activity);
                mapEngine.init(appContext, new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(Error error) {

                        if (error == Error.NONE) {
                            PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
                            startLocGeoCoordiante= PositioningManager.getInstance().getPosition().getCoordinate();
                            setMapProperties(startLocGeoCoordiante);
                        }
                    }
                });
            }
        } else {
            Toast t = Toast.makeText(m_activity, " Turn on GPS, current location not found ", Toast.LENGTH_SHORT);
            t.show();        }
    }

    private void setMapProperties(GeoCoordinate currentGeoCoordiante) {

        m_map = new Map();
        mapView.setMap(m_map);
//        m_map.setZoomLevel((m_map.getMaxZoomLevel() + m_map.getMinZoomLevel()) / 2);
        m_map.setZoomLevel(15);

//      m_map.setCenter(PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK));
//        PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
//        startLocGeoCoordiante= PositioningManager.getInstance().getPosition().getCoordinate();
      m_map.setCenter(currentGeoCoordiante, Map.Animation.NONE);
    }

    private MapView getMapView() {
        return view.findViewById(R.id.mapfragment);
    }

    private void setupRecyclerView() {
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(m_activity, LinearLayoutManager.VERTICAL, false);

        searchRecyclerView.setLayoutManager(horizontalLayoutManager);
        tempautoSuggestList = autoSuggestList;
        searchResultListAdapter = new SearchResultListAdapter(tempautoSuggestList, m_activity);
        searchRecyclerView.setAdapter(searchResultListAdapter);

        searchRecyclerView.addOnItemTouchListener(recyclerItemClickListener);
    }

    private void addMarkerAtPlace(GeoCoordinate geoCoordinate) {
        Image img = new Image();
        try {
            img.setImageResource(R.mipmap.location_pin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        m_map.setCenter(geoCoordinate, Map.Animation.NONE);
        m_map.setZoomLevel(15);
//        m_map.setZoomLevel((m_map.getMaxZoomLevel() + m_map.getMinZoomLevel()) / 2);
        mapMarker = new MapMarker();
        mapMarker.setIcon(img);
        mapMarker.setCoordinate(geoCoordinate);
        m_map.addMapObject(mapMarker);
        mapMarker.setDraggable(true);

        mapView.setMapMarkerDragListener(new MapMarker.OnDragListener() {
            @Override
            public void onMarkerDrag(MapMarker mapMarker) {

            }

            @Override
            public void onMarkerDragEnd(MapMarker mapMarker) {
                GeoCoordinate coordinate = mapMarker.getCoordinate();
                ReverseGeocodeRequest2 revGecodeRequest = new ReverseGeocodeRequest2(coordinate);
                revGecodeRequest.execute(new ResultListener<Location>() {
                    @Override
                    public void onCompleted(Location location, ErrorCode errorCode) {
                        if (errorCode == ErrorCode.NONE) {
                            /*
                             * From the location object, we retrieve the address and display to the screen.
                             * Please refer to HERE Android SDK doc for other supported APIs.
                             */
                            Toast t = Toast.makeText(m_activity, "updated location " + location.getAddress().toString(), Toast.LENGTH_SHORT);
                            t.show();
                        } else {
                            Toast t = Toast.makeText(m_activity, " location name not found ", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }
                });
            }

            @Override
            public void onMarkerDragStart(MapMarker mapMarker) {

            }
        });
    }

    private void clearMap() {

        try {
           /* if(m_map !=null){
                m_map.removeMapObject(mapRoute);
            }

            if(m_positionIndicatorEnd!= null){
                m_map.removeMapObject(m_positionIndicatorEnd);
            }
            if(m_positionIndicatorStart!= null){
                m_map.removeMapObject(m_positionIndicatorStart);
            }*/
            if (mapMarker != null) {
                m_map.removeMapObject(mapMarker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
