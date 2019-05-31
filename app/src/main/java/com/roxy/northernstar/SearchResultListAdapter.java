package com.roxy.northernstar;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.search.AutoSuggestPlace;

import java.util.ArrayList;

public class SearchResultListAdapter extends RecyclerView.Adapter<SearchResultListAdapter.MyViewHolder>{

    ArrayList<AutoSuggestPlace> resultList;
    private static String TAG = "SearchResultListAdapter";
    Context appContext;
    GeoCoordinate currentGeoCoordiante;

    public SearchResultListAdapter(ArrayList<AutoSuggestPlace> list, Context context){

        this.resultList = list;
        this.appContext = context;
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    private void getCurrentLocation(){
//        CommonMethods.printLog(TAG, "getCurrentLocation");
        System.out.print("getCurrentLocation");

        currentGeoCoordiante= PositioningManager.getInstance().getPosition().getCoordinate();
        System.out.println(currentGeoCoordiante);

    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //binds the view holders to their data, using the view holder's position to determine what the contents should be, based on its list position.

        AutoSuggestPlace autoSuggestPlace = resultList.get(position);
        holder.header.setText(autoSuggestPlace.getTitle());

        String description = autoSuggestPlace.getVicinity().replace("<br/>"," ");

        if(currentGeoCoordiante != null){
            description = Utils.roundNumber((Double) (currentGeoCoordiante.distanceTo(autoSuggestPlace.getPosition())/1000),1) + " KM " +description;
        } else {
//
            getCurrentLocation();

            Toast.makeText(appContext,"GeoCordinates not found!",Toast.LENGTH_SHORT).show();
        }
        holder.desc.setText(description);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView header,desc;

        public MyViewHolder(View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.textHeader);
            desc = itemView.findViewById(R.id.textDesc);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new SearchResultListAdapter.MyViewHolder(itemView);
    }
}