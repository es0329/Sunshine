package com.es0329.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.es0329.sunshine.data.WeatherContract.WeatherEntry;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WEATHER_ID
    };

    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND = 6;
    private static final int COL_WEATHER_DEGREES = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_ID = 9;

    private ShareActionProvider shareActionProvider;

    @Bind(R.id.date) TextView date;
    @Bind(R.id.temperatureHigh) TextView temperatureHigh;
    @Bind(R.id.temperatureLow) TextView temperatureLow;
    @Bind(R.id.humidity) TextView humidity;
    @Bind(R.id.wind) TextView wind;
    @Bind(R.id.pressure) TextView pressure;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.icon) ImageView icon;

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        shareActionProvider.setShareIntent(createShareIntent());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, layout);

        Intent intent = getActivity().getIntent();

        if (intent != null) {
            description.setText(intent.getDataString());
        }
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_map:
                showMap(getLocationPreference());
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showMap(String zipCode) {
        String nativeMap = "geo:0,0?q=" + zipCode;
        String webMap = "http://maps.google.com/maps?f=q&q=" + zipCode;
        Intent mapIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(nativeMap));

        if (mapIntent.resolveActivity(getActivity().getPackageManager()) == null) {
            mapIntent.setData(Uri.parse(webMap));
        }
        startActivity(mapIntent);
    }

    private String getLocationPreference() {
        String location_key = getString(R.string.pref_location_key);
        String location_default = getString(R.string.pref_location_default);

        return PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getString(location_key, location_default);
    }

    private Intent createShareIntent() {
        final String SHARE_HASHTAG = " #SunshineApp";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            //noinspection deprecation
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        intent.putExtra(Intent.EXTRA_TEXT, description.getText().toString() + SHARE_HASHTAG);
        return intent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();

        if (intent == null) {
            return null;
        }
        return new CursorLoader(getActivity(), intent.getData(), FORECAST_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(getClass().getSimpleName(), "In onLoadFinished");

        if (!data.moveToFirst()) {
            return;
        }

        int weatherId = data.getInt(COL_WEATHER_ID);
        icon.setImageResource(R.mipmap.ic_launcher);

        String dateString = Utility.getFriendlyDayString(getActivity(), data.getLong(COL_WEATHER_DATE));
        date.setText(dateString);

        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(getActivity().getApplicationContext(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        temperatureHigh.setText(high);

        String low = Utility.formatTemperature(getActivity().getApplicationContext(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        temperatureLow.setText(low);

        double humidityValue = data.getDouble(COL_WEATHER_HUMIDITY);
        humidity.setText(getString(R.string.format_humidity, humidityValue));

        String windValue = Utility.getFormattedWind(getActivity().getApplicationContext(), data.getFloat(COL_WEATHER_WIND), data.getFloat(COL_WEATHER_DEGREES));
        wind.setText(windValue);

        double pressureValue = data.getDouble(COL_WEATHER_PRESSURE);
        pressure.setText(getString(R.string.format_pressure, pressureValue));

//        String mForecast = String.format("%s - %s - %s/%s", dateString, description, high, low);
        String descriptionValue = data.getString(COL_WEATHER_DESC);
        description.setText(descriptionValue);

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
