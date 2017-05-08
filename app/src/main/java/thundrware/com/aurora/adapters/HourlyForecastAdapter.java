package thundrware.com.aurora.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import thundrware.com.aurora.R;
import thundrware.com.aurora.helpers.FONTS;
import thundrware.com.aurora.helpers.Typefaces;
import thundrware.com.aurora.models.Hour;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastViewHolder> {

    private Hour[] mHours;
    private Context mContext;

    public HourlyForecastAdapter(Context context, Hour[] hours) {
        mHours = hours;
        mContext = context;
    }

    @Override
    public HourlyForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hourly_forecast_list_layout, parent, false);
        return new HourlyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HourlyForecastViewHolder holder, int position) {
        holder.onBind(mHours[position]);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    public class HourlyForecastViewHolder extends RecyclerView.ViewHolder {

        TextView hourLabel;
        TextView temperatureLabel;
        ImageView iconView;

        public HourlyForecastViewHolder(View itemView) {
            super(itemView);
            hourLabel = (TextView) itemView.findViewById(R.id.hourlyForecastHourLabel);
            temperatureLabel = (TextView) itemView.findViewById(R.id.hourlyForecastTemperatureLabel);
            iconView = (ImageView) itemView.findViewById(R.id.hourlyForecastIconView);
        }

        public void onBind(Hour hour) {

            SimpleDateFormat formatter;
            boolean isSunsetOrSunrise = false;
            if (hour.getIcon().equals("sunset") || hour.getIcon().equals("sunrise")) {
                isSunsetOrSunrise = true;
                formatter = new SimpleDateFormat("HH:mm");
            } else {
                formatter = new SimpleDateFormat("H");
            }
            formatter.setTimeZone(TimeZone.getTimeZone(hour.getTimezone()));
            Date date = new Date(hour.getTime());
            if (Arrays.asList(mHours).indexOf(hour) != 0) {
                hourLabel.setText(formatter.format(date));
              //  hourLabel.setTypeface(Typefaces.getTypeface(FONTS.REGULAR));
            } else {
                hourLabel.setText("Acum"); // string resource
                hourLabel.setTypeface(Typeface.DEFAULT_BOLD);
            }

            if (isSunsetOrSunrise) {
                if (hour.getIcon().equals("sunset")) {
                    temperatureLabel.setText("Apus");
                } else if (hour.getIcon().equals("sunrise")) {
                    temperatureLabel.setText("Răsărit");
                }
            } else {
                temperatureLabel.setText(String.valueOf((int)Math.round(hour.getTemperature())) + mContext.getString(R.string.temp_degree));
            }

            iconView.setImageResource(hour.getIconId());
        }
    }
}
