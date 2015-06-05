package com.es0329.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = getClass().getSimpleName();
    private WeatherListener listener;

    public interface WeatherListener {
        void onComplete(String[] forecasts);
    }

    public FetchWeatherTask(WeatherListener listener) {
        this.listener = listener;
    }

    @Override
    protected String[] doInBackground(String... postalCodes) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJson;

        try {
            URL url = new URL(getUrl(postalCodes[0]));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();

            if (inputStream == null) {
                return null; // Do nothing.
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            if (stringBuilder.length() == 0) {
                return null;
            }
            forecastJson = stringBuilder.toString();
            WeatherParser weatherParser = new WeatherParser();
            return weatherParser.getWeatherDataFromJson(forecastJson, 7);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream.", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String[] forecasts) {
        super.onPostExecute(forecasts);
        listener.onComplete(forecasts);
    }

    private String getUrl(String postalCode) {
        final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String QUERY = "q";
        final String FORMAT = "mode";
        final String UNITS = "units";
        final String DAYS = "cnt";

        final String JSON = "json";
        final String METRIC = "metric";
        final String WEEK = "7";

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY, postalCode)
                .appendQueryParameter(FORMAT, JSON)
                .appendQueryParameter(UNITS, METRIC)
                .appendQueryParameter(DAYS, WEEK).build();
        return uri.toString();
    }
}
