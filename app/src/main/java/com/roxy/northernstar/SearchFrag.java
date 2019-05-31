package com.roxy.northernstar;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFrag extends Fragment {


    public SearchFrag() {
        // Required empty public constructor
    }

    View view;

    PlaceSearchView m_placeView;
    Context appContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=  inflater.inflate(R.layout.fragment_search, container, false);

        setupPlaceSearchView();


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContext=  context;
    }

    private void setupPlaceSearchView(){
        m_placeView = new PlaceSearchView(getActivity(),view);
    }

}
