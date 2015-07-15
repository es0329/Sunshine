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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.es0329.sunshine.data.WeatherContract;
import com.es0329.sunshine.data.WeatherContract.WeatherEntry;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
    public static final String TAG = "DetailFragment";
    public static final String URI = "mUri";

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    private ShareActionProvider shareActionProvider;
    private String mForecast;
    private Uri mUri;

    @Bind(R.id.day) TextView day;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.temperatureHigh) TextView temperatureHigh;
    @Bind(R.id.temperatureLow) TextView temperatureLow;
    @Bind(R.id.humidity) TextView humidity;
    @Bind(R.id.wind) TextView wind;
    @Bind(R.id.pressure) TextView pressure;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.icon) ImageView icon;
    @Bind(R.id.compass) CompassView compass;

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
        Bundle bundle = getArguments();

        if (bundle != null) {
            mUri = bundle.getParcelable(DetailFragment.URI);
        }
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void onLocationChanged(String location) {
        Uri uri = mUri;

        if (uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            mUri = WeatherEntry.buildWeatherLocationWithDate(location, date);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
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

        intent.putExtra(Intent.EXTRA_TEXT, mForecast + SHARE_HASHTAG);
        return intent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mUri != null) {
            return new CursorLoader(getActivity(), mUri, DETAIL_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) {
            return;
        }

        // Read weather icon ID from cursor
        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
        icon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        long dateLong = data.getLong(COL_WEATHER_DATE);
        String dayValue = Utility.getDayName(getActivity(), dateLong);
        day.setText(dayValue);

//        String dateString = Utility.getFriendlyDayString(getActivity(), date);
        String dateValue = Utility.getFormattedMonthDay(getActivity(), dateLong);
        date.setText(dateValue);

        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(getActivity().getApplicationContext(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        temperatureHigh.setText(high);

        String low = Utility.formatTemperature(getActivity().getApplicationContext(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        temperatureLow.setText(low);

        double humidityValue = data.getDouble(COL_WEATHER_HUMIDITY);
        humidity.setText(getString(R.string.format_humidity, humidityValue));

        String windValue = Utility.getFormattedWind(getActivity().getApplicationContext(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES));
        wind.setText(windValue);

        double pressureValue = data.getDouble(COL_WEATHER_PRESSURE);
        pressure.setText(getString(R.string.format_pressure, pressureValue));

        String descriptionValue = data.getString(COL_WEATHER_DESC);
        description.setText(descriptionValue);

        mForecast = String.format("%s - %s - %s/%s", dateValue, descriptionValue, high, low);

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
