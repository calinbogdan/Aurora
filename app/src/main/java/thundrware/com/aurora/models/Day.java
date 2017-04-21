package thundrware.com.aurora.models;

public class Day {
    private long mTime;
    private double mMinTemperature;
    private double mMaxTemperature;
    private double mPrecipitationProbability;

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
}
