package thundrware.com.aurora.models;

public class Day {
    private String mIcon;
    private long mTime;
    private double mMinTemperature;
    private double mMaxTemperature;
    private double mPrecipitationProbability;
    private String mTimezone;

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public double getMinTemperature() {
        return mMinTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        mMinTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return mMaxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        mMaxTemperature = maxTemperature;
    }

    public double getPrecipitationProbability() {
        return mPrecipitationProbability;
    }

    public void setPrecipitationProbability(double precipitationProbability) {
        mPrecipitationProbability = precipitationProbability;
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
}
