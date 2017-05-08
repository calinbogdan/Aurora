package thundrware.com.aurora.models;

import android.support.annotation.NonNull;

public class Hour implements Comparable<Hour> {
    private String mIcon;
    private double mTemperature;
    private long mTime;
    private double mPrecipitationProbability;
    private double mApparentTemperature;
    private String mTimezone;

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public double getPrecipitationProbability() {
        return mPrecipitationProbability;
    }

    public void setPrecipitationProbability(double precipitationProbability) {
        mPrecipitationProbability = precipitationProbability;
    }

    public double getApparentTemperature() {
        return mApparentTemperature;
    }

    public void setApparentTemperature(double apparentTemperature) {
        mApparentTemperature = apparentTemperature;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId() {
        return Forecast.getIconId(mIcon);
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    @Override
    public int compareTo(@NonNull Hour hour) {
        Long time1 = mTime;
        Long timeToCompare = hour.getTime();
        return time1.compareTo(timeToCompare);
    }
}
