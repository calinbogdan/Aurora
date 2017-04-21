package thundrware.com.aurora.models;

public class Hour {
    private double mTemperature;
    private long mTime;
    private double mPrecipitationProbability;
    private double mApparentTemperature;

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
}
