package thundrware.com.aurora.models;

import thundrware.com.aurora.R;

public class Forecast {
    private String mTimezone;
    private Currently mCurrently;
    private Day[] mDaily;
    private Hour[] mHourly;


    public Day[] getDaily() {
        return mDaily;
    }

    public void setDaily(Day[] daily) {
        mDaily = daily;
    }

    public Hour[] getHourly() {
        return mHourly;
    }

    public void setHourly(Hour[] hour) {
        mHourly = hour;
    }

    public static int getIconId(String iconString) {
        // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night.
        int iconId = R.drawable.clear_day;

        if (iconString.equals("clear-day")) {
            iconId = R.drawable.clear_day;
        }
        else if (iconString.equals("clear-night")) {
            iconId = R.drawable.clear_night;
        }
        else if (iconString.equals("rain")) {
            iconId = R.drawable.rain;
        }
        else if (iconString.equals("snow")) {
            iconId = R.drawable.snow;
        }
        else if (iconString.equals("sleet")) {
            iconId = R.drawable.sleet;
        }
        else if (iconString.equals("wind")) {
            iconId = R.drawable.wind;
        }
        else if (iconString.equals("fog")) {
            iconId = R.drawable.fog;
        }
        else if (iconString.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        }
        else if (iconString.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy_day;
        }
        else if (iconString.equals("partly-cloudy-night")) {
            iconId = R.drawable.partly_cloudy_night;
        }
        else if (iconString.equals("thunderstorm")) {
            iconId = R.drawable.thunderstorm;
        }
        else if (iconString.equals("hail")) {
            iconId = R.drawable.hail;
        }
        else if (iconString.equals("tornado")) {
            iconId = R.drawable.tornado;
        }
        else if (iconString.equals("sunset")) {
            iconId = R.drawable.sunset;
        }
        else if (iconString.equals("sunrise")) {
            iconId = R.drawable.sunrise;
        }

        return iconId;

    }


    public Currently getCurrently() {
        return mCurrently;
    }

    public void setCurrently(Currently currently) {
        mCurrently = currently;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }
}
