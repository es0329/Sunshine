package com.es0329.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.es0329.sunshine.FetchWeatherTask.WeatherListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainFragment extends Fragment implements WeatherListener {
    private final String POSTAL_CODE = "34786";

    private ArrayList<String> weekForecast = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public MainFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                new FetchWeatherTask(this).execute(POSTAL_CODE);
                Log.i(getClass().getSimpleName(), "FetchWeatherTask#execute");
                return true;
            case R.id.action_settings:
                launchSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

//        weekForecast = createForecastEntries();
        adapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);

        FrameLayout frameLayout
                = (FrameLayout) inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) frameLayout.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                launchDetail(adapter.getItem(position));
            }
        });

        return frameLayout;
    }

    private void launchDetail(String weatherDescription) {
        Intent detailIntent = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
        detailIntent.putExtra(DetailFragment.KEY_WEATHER_DESCRIPTION, weatherDescription);
        startActivity(detailIntent);
    }

    private void launchSettings() {
        Intent settingsIntent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
        startActivity(settingsIntent);
    }

//    private ArrayList<String> createForecastEntries() {
//        String[] forecastData = {
//                "Today - Sunny - 88 / 63",
//                "Tomorrow - Foggy - 70 / 46",
//                "Friday - Cloudy - 72 / 63",
//                "Saturday - Asteroids - 64 / 51",
//                "Sunday - Rainy - 70 / 46",
//                "Monday - Snowy - 76 / 68"
//        };
//        return new ArrayList<>(Arrays.asList(forecastData));
//    }

    @Override
    public void onComplete(String[] forecasts) {
        weekForecast.clear();
        Collections.addAll(weekForecast, forecasts);
        adapter.notifyDataSetChanged();
    }
}
