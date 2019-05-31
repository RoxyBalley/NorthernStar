package com.roxy.northernstar;

import android.support.v4.app.FragmentActivity;
import android.view.View;

public class RouteView {
    private FragmentActivity m_activity;
    View view;

    public RouteView(FragmentActivity activity, View view){
        m_activity = activity;
        this.view = view;
    }
}
