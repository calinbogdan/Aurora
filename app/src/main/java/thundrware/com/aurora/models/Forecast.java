package thundrware.com.aurora.models;

public class Forecast {
    private String mSummary;
    private double mTemperature;
    private Day[] mDaily;
    private Hour[] mHourly;

    public String getSummary() {
        return mSummary;
    }

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

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }


}
