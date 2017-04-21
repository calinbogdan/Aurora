package thundrware.com.aurora.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import thundrware.com.aurora.R;
import thundrware.com.aurora.models.Day;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder> {

    private Context mContext;
    private Day[] mDays;

    public DailyForecastAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }

    @Override
    public DailyForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.daily_forecast_list_layout, parent, false);
        return new DailyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyForecastViewHolder holder, int position) {
        holder.onBind(mDays[position]);
    }

    @Override
    public int getItemCount() {
        return mDays.length;
    }

    public class DailyForecastViewHolder extends RecyclerView.ViewHolder {

        TextView dayName;
        TextView minTemperature;
        TextView maxTemperature;

        public DailyForecastViewHolder(View itemView) {
            super(itemView);
            dayName = (TextView) itemView.findViewById(R.id.dailyForecastDayNameLabel);
            minTemperature = (TextView) itemView.findViewById(R.id.dailyForecastMinTemperatureLabel);
            maxTemperature = (TextView) itemView.findViewById(R.id.dailyForecastMaxTemperatureLabel);
        }

        public void onBind(Day day) {

            // Setting the dayName
            Date date = new Date(day.getTime());
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
            formatter.setTimeZone(TimeZone.getDefault());
            dayName.setText(formatter.format(date));

            // Setting the minimum temperature
            minTemperature.setText(String.valueOf((int)day.getMinTemperature()));

            // Setting the maximum temperature
            maxTemperature.setText(String.valueOf((int)day.getMaxTemperature()));
        }
    }
}
