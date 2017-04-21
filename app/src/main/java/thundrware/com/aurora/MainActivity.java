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

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import thundrware.com.aurora.adapters.DailyForecastAdapter;
import thundrware.com.aurora.models.Day;
import thundrware.com.aurora.models.Forecast;
import thundrware.com.aurora.models.Hour;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private double mLatitude;
    private double mLongitude;
    private Forecast mForecast;

    @BindView(R.id.currentWeatherTemperatureLabel) TextView mCurrentWeatherTemperatureLabel;
    @BindView(R.id.currentWeatherSummaryLabel) TextView mCurrentWeatherSummaryLabel;
    @BindView(R.id.dailyForecastRecyclerView) RecyclerView mDailyForecastRecyclerView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); // always after setContentView
        mContext = this;


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        LinearLayout horizontalScrollView = (LinearLayout) findViewById(R.id.horizontalScrollView);
        for (int i=0; i < 35; i++) {
            TextView textView = new TextView(this);
            textView.setText(String.valueOf(i));
            textView.setPadding(10, 10, 10, 10);
            horizontalScrollView.addView(textView);
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

    private void parseApiResponse(String jsonData) throws Exception{
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject jsonCurrently = forecast.getJSONObject("currently");

        mForecast = new Forecast();

        mForecast.setSummary(jsonCurrently.getString("summary"));
        mForecast.setTemperature(jsonCurrently.getDouble("temperature"));
        mForecast.setDaily(getDailyForecast(jsonData));
        mForecast.setHourly(getHourlyForecast(jsonData));
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject hourlyJsonObject = forecast.getJSONObject("hourly");
        JSONArray hourlyJsonData = hourlyJsonObject.getJSONArray("data");

        Hour[] hours = new Hour[hourlyJsonData.length()];

        for (int i=0; i < hours.length; i++) {
            JSONObject hourJson = hourlyJsonData.getJSONObject(i);

            Hour hour = new Hour();
            hour.setTime(hourJson.getLong("time")*1000); // Java is expecting milliseconds
            hour.setPrecipitationProbability(hourJson.getDouble("precipProbability"));
            hour.setTemperature(hourJson.getDouble("temperature"));
            hour.setApparentTemperature(hourJson.getDouble("apparentTemperature"));

            hours[i] = hour;
        }

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
            day.setTime(dayJson.getLong("time")*1000); // Java is expecting milliseconds
            day.setMaxTemperature(dayJson.getDouble("temperatureMax"));
            day.setMinTemperature(dayJson.getDouble("temperatureMin"));
            day.setPrecipitationProbability(dayJson.getDouble("precipProbability"));

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
        if (requestCode == 3) {
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

    public void updateDisplay() {
        mCurrentWeatherTemperatureLabel.setText(String.valueOf((int)mForecast.getTemperature()));
        mCurrentWeatherSummaryLabel.setText(mForecast.getSummary());

        LinearLayoutManager manager = new LinearLayoutManager(this);
        DailyForecastAdapter dailyForecastAdapter = new DailyForecastAdapter(this, mForecast.getDaily());
        mDailyForecastRecyclerView.setAdapter(dailyForecastAdapter);
        mDailyForecastRecyclerView.setLayoutManager(manager);
    }

    public int roundTemperature(double temperature) {
        double difference = temperature - (int) temperature;
        if (difference >= 0.5) {
            return (int) (temperature + 1);
        } else {
            return (int) temperature;
        }
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
