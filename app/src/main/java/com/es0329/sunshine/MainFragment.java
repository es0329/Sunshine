package com.es0329.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.es0329.sunshine.FetchWeatherTask.WeatherListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainFragment extends Fragment implements WeatherListener {
    private ArrayList<String> weekForecast = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);

        ListView listView = (ListView) layout.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                launchDetailActivity(adapter.getItem(position));
            }
        });

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onWeatherReceived(String[] forecasts) {
        weekForecast.clear();

        if (forecasts == null) {
            Toast.makeText(getActivity(), getString(R.string.weatherUnavailable), Toast.LENGTH_SHORT).show();
        } else {
            Collections.addAll(weekForecast, forecasts);
            adapter.notifyDataSetChanged();
        }
    }

    private void launchDetailActivity(String weatherDescription) {
        Intent detailIntent = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
        detailIntent.putExtra(DetailFragment.KEY_WEATHER_DESCRIPTION, weatherDescription);
        startActivity(detailIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateWeather();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateWeather() {
        new FetchWeatherTask(getActivity().getApplicationContext(), adapter).execute(getLocationPreference(), getUnitPreference());
    }

    private String getLocationPreference() {
        String location_key = getString(R.string.pref_location_key);
        String location_default = getString(R.string.pref_location_default);

        return PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getString(location_key, location_default);
    }

    private String getUnitPreference() {
        String units_key = getString(R.string.pref_units_key);
        String units_default = getString(R.string.pref_units_default);

        return PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getString(units_key, units_default);
    }
}
