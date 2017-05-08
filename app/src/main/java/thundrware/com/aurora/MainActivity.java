package thundrware.com.aurora;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import thundrware.com.aurora.adapters.DailyForecastAdapter;
import thundrware.com.aurora.adapters.HourlyForecastAdapter;
import thundrware.com.aurora.helpers.FONTS;
import thundrware.com.aurora.helpers.Typefaces;
import thundrware.com.aurora.models.Currently;
import thundrware.com.aurora.models.Day;
import thundrware.com.aurora.models.Forecast;
import thundrware.com.aurora.models.Hour;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private double mLatitude;
    private double mLongitude;
    private Forecast mForecast;
    private boolean mLocationLabelHasBeenSet = false;

    @BindView(R.id.currentLocationLabel)
    TextView mCurrentLocationLabel;

    @BindView(R.id.currentWeatherTemperatureLabel)
    TextView mCurrentWeatherTemperatureLabel;

    @BindView(R.id.dailyForecastRecyclerView)
    RecyclerView mDailyForecastRecyclerView;

    @BindView(R.id.hourlyForecastRecyclerView)
    RecyclerView mHourlyForecastRecyclerView;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); // always after setContentView
        mContext = this;


        // setting the Typefaces
       // mCurrentLocationLabel.setTypeface(Typefaces.getTypeface(FONTS.REGULAR));
       // mCurrentWeatherTemperatureLabel.setTypeface(Typefaces.getTypeface(FONTS.LIGHT));


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void createWeatherRequest() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.darksky.net/forecast/bfd3a552cc1f7a5abd4ba6484a531d1b/" + mLatitude + "," + mLongitude + "?units=auto")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                try {
                    final String jsonData = response.body().string();
                    parseApiResponse(jsonData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDisplay();
                        }
                    });
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void createLocationRequest() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?latlng="
                        + mLatitude + "," + mLongitude +
                        "&key=AIzaSyBrhslxwUuXcMBhngbiZ18ewCiaKODlnAg")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        parseLocationData(jsonData);
                    } catch (Exception ex) {
                        Log.e("ERROR_JSON_TAG", ex.getMessage());
                    }
                }
            }
        });
    }

    private void parseLocationData(String jsonData) throws JSONException {
        JSONObject location = new JSONObject(jsonData);
        JSONArray resultsJson = location.getJSONArray("results");
        for (int i=0; i < resultsJson.length(); i++) {
            JSONArray addressComponentsJson = resultsJson.getJSONObject(i).getJSONArray("address_components");
            for (int j=0; j < addressComponentsJson.length(); j++) {
                JSONObject addressComponentJson = addressComponentsJson.getJSONObject(j);
                JSONArray addressTypesJson = addressComponentJson.getJSONArray("types");
                for (int k=0; k < addressTypesJson.length(); k++) {
                    if (addressTypesJson.optString(k).contains("locality")) {
                        setCurrentLocation(addressComponentJson.getString("long_name"));
                        mLocationLabelHasBeenSet = true;
                        break;
                    }
                }
                if (!mLocationLabelHasBeenSet) {
                    for (int k=0; k < addressTypesJson.length(); k++) {
                        if (addressTypesJson.optString(k).contains("country")) {
                            setCurrentLocation(addressComponentJson.getString("long_name"));
                            mLocationLabelHasBeenSet = true;
                            break;
                        }
                    }
                }
            }
        }


    }

    private void setCurrentLocation(final String locationName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentLocationLabel.setText(locationName);
            }
        });

    }

    private void parseApiResponse(String jsonData) throws Exception{
        JSONObject forecast = new JSONObject(jsonData);

        mForecast = new Forecast();

        mForecast.setCurrently(getCurrentForecast(jsonData));
        mForecast.setDaily(getDailyForecast(jsonData));
        mForecast.setHourly(getHourlyForecast(jsonData));
    }

    private Currently getCurrentForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currentlyJson = forecast.getJSONObject("currently");

        Currently currently = new Currently();
        currently.setSummary(currentlyJson.getString("summary"));
        currently.setApparentTemperature(currentlyJson.getDouble("apparentTemperature"));
        currently.setTemperature(currentlyJson.getDouble("temperature"));
        currently.setIcon(currentlyJson.getString("icon"));
        currently.setTimezone(forecast.getString("timezone"));

        return currently;
    }
    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject hourlyJsonObject = forecast.getJSONObject("hourly");
        JSONArray hourlyJsonData = hourlyJsonObject.getJSONArray("data");


        JSONObject todayJson = forecast.getJSONObject("daily").getJSONArray("data").getJSONObject(0);

        Long sunriseTime = todayJson.getLong("sunriseTime");
        Long sunsetTime = todayJson.getLong("sunsetTime");

        int sunsetSunrise = 2;

        Long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime > sunriseTime) {
            JSONObject tomorrowJson = forecast.getJSONObject("daily").getJSONArray("data").getJSONObject(1);
            sunriseTime = tomorrowJson.getLong("sunriseTime");
            if (currentTime > sunsetTime) {
                sunsetTime = tomorrowJson.getLong("sunsetTime");
            }
        }

        int numberOfHoursProvided = 24 + sunsetSunrise; // regular hours + sunset and sunrise

        Hour[] hours = new Hour[numberOfHoursProvided];

        for (int i=0; i < numberOfHoursProvided; i++) {
            JSONObject hourJson = hourlyJsonData.getJSONObject(i);

            Hour hour = new Hour();
            hour.setTimezone(forecast.getString("timezone"));
            hour.setIcon(hourJson.getString("icon"));
            hour.setTime(hourJson.getLong("time")*1000); // Java is expecting milliseconds
            hour.setPrecipitationProbability(hourJson.getDouble("precipProbability"));
            hour.setTemperature(hourJson.getDouble("temperature"));
            hour.setApparentTemperature(hourJson.getDouble("apparentTemperature"));

            hours[i] = hour;
        }



        Hour sunsetHour = new Hour();
        sunsetHour.setTimezone(forecast.getString("timezone"));
        sunsetHour.setIcon("sunset");
        sunsetHour.setTime(sunsetTime * 1000);
        hours[24] = sunsetHour;

        Hour sunriseHour = new Hour();
        sunriseHour.setTimezone(forecast.getString("timezone"));
        sunriseHour.setIcon("sunrise");
        sunriseHour.setTime(sunriseTime * 1000);
        hours[25] = sunriseHour;

        Arrays.sort(hours);


        hours[0].setTemperature((int)Math.round(mForecast.getCurrently().getTemperature()));
        hours[0].setIcon(mForecast.getCurrently().getIcon());


        return hours;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject dailyJsonObject = forecast.getJSONObject("daily");
        JSONArray dailyJsonData = dailyJsonObject.getJSONArray("data");

        Day[] days = new Day[dailyJsonData.length()];

        for (int i=0; i < days.length; i++) {
            JSONObject dayJson = dailyJsonData.getJSONObject(i);
            Day day = new Day();
            day.setTimezone(forecast.getString("timezone"));
            day.setTime(dayJson.getLong("time")*1000); // Java is expecting milliseconds
            day.setMaxTemperature(dayJson.getDouble("temperatureMax"));
            day.setMinTemperature(dayJson.getDouble("temperatureMin"));
            day.setPrecipitationProbability(dayJson.getDouble("precipProbability"));
            day.setIcon(dayJson.getString("icon"));

            days[i] = day;
        }

        return days;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        haveNoIdeaHowToNameThisForNow();
    }

    private void haveNoIdeaHowToNameThisForNow() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                setCurrentLocationCoordinates(mLastLocation);
                createWeatherRequest();
                createLocationRequest();
            }
        } else {
            int requestCode = 3;
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
        }
    }

    private void setCurrentLocationCoordinates(Location currentLocation) {
        mLatitude = currentLocation.getLatitude();
        mLongitude = currentLocation.getLongitude();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 3) { // un cod de care tre' sa tin cu dintii
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                haveNoIdeaHowToNameThisForNow();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateDisplay() {
        setTemperature(mForecast.getCurrently().getTemperature());
       // mCurrentWeatherSummaryLabel.setText(mForecast.getCurrently().getSummary());

        LinearLayoutManager dailyForecastLayoutManager = new LinearLayoutManager(this);
        DailyForecastAdapter dailyForecastAdapter = new DailyForecastAdapter(this, mForecast.getDaily());
        mDailyForecastRecyclerView.setAdapter(dailyForecastAdapter);
        mDailyForecastRecyclerView.setLayoutManager(dailyForecastLayoutManager);

        LinearLayoutManager hourlyForecastLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        HourlyForecastAdapter hourlyForecastAdapter = new HourlyForecastAdapter(this, mForecast.getHourly());
        mHourlyForecastRecyclerView.setAdapter(hourlyForecastAdapter);
        mHourlyForecastRecyclerView.setLayoutManager(hourlyForecastLayoutManager);

    }

    private void setTemperature(double temperature) {
        mCurrentWeatherTemperatureLabel.setText(String.valueOf((int)Math.round(temperature)));
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
