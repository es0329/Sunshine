package com.es0329.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
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
    private TextView textView;

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
        inflater.inflate(R.menu.detail, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        shareActionProvider.setShareIntent(getDefaultShareIntent());
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent getDefaultShareIntent() {
        final String SHARE_HASHTAG = " #SunshineApp";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            //noinspection deprecation
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        intent.putExtra(Intent.EXTRA_TEXT, textView.getText().toString() + SHARE_HASHTAG);
        return intent;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_detail, container, false);
        textView = (TextView) layout.findViewById(weatherDescription);
        textView.setText(getArguments().getString(KEY_WEATHER_DESCRIPTION));
        return layout;
    }

    private String getLocationPreference() {
        String location_key = getString(R.string.pref_location_key);
        String location_default = getString(R.string.pref_location_default);

        return PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getString(location_key, location_default);
    }
}
