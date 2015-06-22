package com.es0329.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.es0329.sunshine.data.WeatherContract;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FORECAST_LOADER = 0;
    private ForecastAdapter adapter;

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
        adapter = new ForecastAdapter(getActivity(), null, 0);

        ListView listView = (ListView) layout.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
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
        new FetchWeatherTask(getActivity()).execute(getLocationPreference(), getUnitPreference());
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());
        return new CursorLoader(getActivity(), weatherForLocationUri, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
