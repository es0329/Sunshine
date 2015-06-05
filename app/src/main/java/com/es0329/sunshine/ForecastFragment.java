package com.es0329.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.es0329.sunshine.FetchWeatherTask.WeatherListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ForecastFragment extends Fragment implements WeatherListener {
    private final String POSTAL_CODE = "34786";
    private ArrayList<String> weekForecast;
    private ArrayAdapter<String> adapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new FetchWeatherTask(this).execute(POSTAL_CODE);

        weekForecast = createForecastEntries();
        adapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);

        FrameLayout frameLayout
                = (FrameLayout) inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) frameLayout.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        return frameLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            new FetchWeatherTask(this).execute(POSTAL_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> createForecastEntries() {
        String[] forecastData = {
                "Today - Sunny - 88 / 63",
                "Tomorrow - Foggy - 70 / 46",
                "Friday - Cloudy - 72 / 63",
                "Saturday - Asteroids - 64 / 51",
                "Sunday - Rainy - 70 / 46",
                "Monday - Snowy - 76 / 68"
        };
        return new ArrayList<>(Arrays.asList(forecastData));
    }

    @Override
    public void onComplete(String[] forecasts) {
        weekForecast.clear();
        Collections.addAll(weekForecast, forecasts);
        adapter.notifyDataSetChanged();
    }
}
