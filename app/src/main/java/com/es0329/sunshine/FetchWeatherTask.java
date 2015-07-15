///*
// * Copyright (C) 2014 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.es0329.sunshine;
//
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.text.format.Time;
//import android.util.Log;
//
//import com.es0329.sunshine.data.WeatherContract;
//import com.es0329.sunshine.data.WeatherContract.WeatherEntry;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Vector;
//
//public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
//    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//    private Context mContext;
//
//    public FetchWeatherTask(Context context) {
//        mContext = context;
//    }
//
//    /**
//     * Helper method to handle insertion of a new location in the weather database.
//     *
//     * @param locationSetting The location string used to request updates from the server.
//     * @param cityName A human-readable city name, e.g "Mountain View"
//     * @param lat the latitude of the city
//     * @param lon the longitude of the city
//     * @return the row ID of the added location.
//     */
//    long addLocation(String locationSetting, String cityName, double lat, double lon) {
//        long locationId;
//        Cursor cursor = mContext.getContentResolver().query(
//                WeatherContract.LocationEntry.CONTENT_URI,
//                new String[] { WeatherContract.LocationEntry._ID },
//                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
//                new String[] { locationSetting },
//                null);
//
//        if (cursor.moveToFirst()) {
//            int index = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
//            locationId = cursor.getLong(index);
//        } else {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
//            contentValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
//            contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
//            contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);
//
//            Uri newRow = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, contentValues);
//            locationId = ContentUris.parseId(newRow);
//        }
//        cursor.close();
//        return locationId;
//    }
//
//    /**
//     * Take the String representing the complete forecast in JSON Format and
//     * pull out the data we need to construct the Strings needed for the wireframes.
//     *
//     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//     * into an Object hierarchy for us.
//     */
//    private void getWeatherDataFromJson(String forecastJsonStr, String locationSetting)
//            throws JSONException {
//        final String OWM_CITY = "city";
//        final String OWM_CITY_NAME = "name";
//        final String OWM_COORD = "coord";
//
//        final String OWM_LATITUDE = "lat";
//        final String OWM_LONGITUDE = "lon";
//
//        final String OWM_LIST = "list";
//
//        final String OWM_PRESSURE = "pressure";
//        final String OWM_HUMIDITY = "humidity";
//        final String OWM_WINDSPEED = "speed";
//        final String OWM_WIND_DIRECTION = "deg";
//
//        final String OWM_TEMPERATURE = "temp";
//        final String OWM_MAX = "max";
//        final String OWM_MIN = "min";
//
//        final String OWM_WEATHER = "weather";
//        final String OWM_DESCRIPTION = "main";
//        final String OWM_WEATHER_ID = "id";
//
//        try {
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
//            String cityName = cityJson.getString(OWM_CITY_NAME);
//
//            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
//            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
//            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
//
//            long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);
//
//            Vector<ContentValues> cVVector = new Vector<>(weatherArray.length());
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            for(int i = 0; i < weatherArray.length(); i++) {
//                long dateTime;
//                double pressure;
//                int humidity;
//                double windSpeed;
//                double windDirection;
//
//                double high;
//                double low;
//
//                String description;
//                int weatherId;
//
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//                dateTime = dayTime.setJulianDay(julianStartDay+i);
//
//                pressure = dayForecast.getDouble(OWM_PRESSURE);
//                humidity = dayForecast.getInt(OWM_HUMIDITY);
//                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
//                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);
//
//                JSONObject weatherObject =
//                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//                weatherId = weatherObject.getInt(OWM_WEATHER_ID);
//
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                high = temperatureObject.getDouble(OWM_MAX);
//                low = temperatureObject.getDouble(OWM_MIN);
//
//                ContentValues weatherValues = new ContentValues();
//                weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationId);
//                weatherValues.put(WeatherEntry.COLUMN_DATE, dateTime);
//                weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
//                weatherValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);
//                weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
//                weatherValues.put(WeatherEntry.COLUMN_DEGREES, windDirection);
//                weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, high);
//                weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, low);
//                weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, description);
//                weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);
//                cVVector.add(weatherValues);
//            }
//            int rowsInserted = 0;
//
//            if ( cVVector.size() > 0 ) {
//                ContentValues[] contentValues = new ContentValues[cVVector.size()];
//                cVVector.toArray(contentValues);
//                rowsInserted =
//                        mContext.getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, contentValues);
//            }
//            Log.d(LOG_TAG, "FetchWeatherTask Completed inserting "+ rowsInserted +" rows.");
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected Void doInBackground(String... params) {
//
//        if (params == null || params.length == 0) {
//            return null;
//        }
//
//        String locationQuery = params[0];
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        String forecastJsonStr;
//
//        String format = "json";
//        String units = "metric";
//        int numDays = 14;
//
//        try {
//            final String FORECAST_BASE_URL =
//                    "http://api.openweathermap.org/data/2.5/forecast/daily?";
//            final String QUERY_PARAM = "q";
//            final String FORMAT_PARAM = "mode";
//            final String UNITS_PARAM = "units";
//            final String DAYS_PARAM = "cnt";
//
//            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
//                    .appendQueryParameter(QUERY_PARAM, params[0])
//                    .appendQueryParameter(FORMAT_PARAM, format)
//                    .appendQueryParameter(UNITS_PARAM, units)
//                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
//                    .build();
//
//            URL url = new URL(builtUri.toString());
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            InputStream inputStream = urlConnection.getInputStream();
//            StringBuilder stringBuilder = new StringBuilder();
//
//            if (inputStream == null) {
//                return null;
//            }
//
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line).append("\n");
//            }
//
//            if (stringBuilder.length() == 0) {
//                return null;
//            }
//
//            forecastJsonStr = stringBuilder.toString();
//            getWeatherDataFromJson(forecastJsonStr, locationQuery);
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "IOException ", e);
//            return null;
//        } catch (JSONException e) {
//            Log.i(LOG_TAG, "JSONException: Parsing Issue.");
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (final IOException e) {
//                    Log.e(LOG_TAG, "Error closing stream", e);
//                }
//            }
//        }
//        return null;
//    }
//}