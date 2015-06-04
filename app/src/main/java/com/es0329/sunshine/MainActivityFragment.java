package com.es0329.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<String> weekForecast = createForecastEntries();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);

        FrameLayout frameLayout
                = (FrameLayout) inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) frameLayout.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        return frameLayout;
    }

    private List<String> createForecastEntries() {
        String[] forecastData = {
                "Today - Sunny - 88 / 63",
                "Tomorrow - Foggy - 70 / 46",
                "Friday - Cloudy - 72 / 63",
                "Saturday - Asteroids - 64 / 51",
                "Sunday - Rainy - 70 / 46",
                "Monday - Snowy - 76 / 68"
        };
        return Arrays.asList(forecastData);
    }
}
