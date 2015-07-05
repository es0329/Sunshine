package com.es0329.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {
    private String mLocation;
    private boolean isTwoPane;
    private boolean mIsMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            isTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, new DetailFragment(),
                                DetailFragment.TAG).commit();
            }
        } else {
            isTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        boolean isMetric = Utility.isMetric(this);

        if (location != null && !location.equals(mLocation) || isMetric != mIsMetric) {
            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_forecast);

            if (forecastFragment != null) {
                forecastFragment.onLocationChanged();
            }

            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(DetailFragment.TAG);

            if (detailFragment != null) {
                detailFragment.onLocationChanged(location);
            }

            mLocation = location;
            mIsMetric = isMetric;
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {

        if (isTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(DetailFragment.URI, dateUri);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, detailFragment, DetailFragment.TAG)
                    .commit();
        } else {
            Intent detailIntent = new Intent(this, DetailActivity.class).setData(dateUri);
            startActivity(detailIntent);
        }
    }
}
