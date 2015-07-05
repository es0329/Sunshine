package com.es0329.sunshine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
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
                        .add(R.id.weather_detail_container, DetailFragment.newInstance(),
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
            mLocation = location;
            mIsMetric = isMetric;
        }
    }
}
