package com.es0329.sunshine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private String mLocation;
    private boolean mIsMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocation = Utility.getPreferredLocation(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ForecastFragment.newInstance(),
                            ForecastFragment.TAG_FRAGMENT_FORECAST).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        boolean isMetric = Utility.isMetric(this);

        if (location != null && !location.equals(mLocation) || isMetric != mIsMetric) {
            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentByTag(ForecastFragment.TAG_FRAGMENT_FORECAST);

            if (forecastFragment != null) {
                forecastFragment.onLocationChanged();
            }
            mLocation = location;
            mIsMetric = isMetric;
        }
    }
}
