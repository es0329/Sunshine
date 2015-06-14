package com.es0329.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.es0329.sunshine.R.id.weatherDescription;

public class DetailFragment extends Fragment {
    static final String KEY_WEATHER_DESCRIPTION = "weatherDescription";

    public static DetailFragment newInstance(String intentExtra) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_WEATHER_DESCRIPTION, intentExtra);

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    public DetailFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                launchSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchSettings() {
        Intent settingsIntent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView textView = (TextView) layout.findViewById(weatherDescription);
        textView.setText(getArguments().getString(KEY_WEATHER_DESCRIPTION));
        return layout;
    }
}
