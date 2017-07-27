package com.example.android.weatherapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Glen on 15/07/2017.
 */
public class RecyclerViewAdapterTwo extends RecyclerView.Adapter<RecyclerViewAdapterTwo.ViewHolder> {

    public Context context;
    public List<HourlyModel> HourlyList;


    public RecyclerViewAdapterTwo(List<HourlyModel> hourlyList, Context context) {
        this.context = context;
        this.HourlyList = hourlyList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView temperature;
        public TextView time;
        public ImageView weatherIcon;
        public RelativeLayout recyclerGrid;

        public ViewHolder(View itemView)    {

            super(itemView);
            temperature = (TextView) itemView.findViewById(R.id.temperature);
            time = (TextView) itemView.findViewById(R.id.time);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weather_icon);
            recyclerGrid = (RelativeLayout) itemView.findViewById(R.id.recycler_grid);
        }
    }

    @Override
    public RecyclerViewAdapterTwo.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)    {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_grid_list_item, parent, false);

        return new RecyclerViewAdapterTwo.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterTwo.ViewHolder Holder, int position)   {

        final HourlyModel hourlyModel = HourlyList.get(position);
        Holder.temperature.setText(String.valueOf(" " + Math.round(hourlyModel.getMinTemperature() - 273.15)) + "°");
        //Holder.temperature.setTypeface(tf);
        Holder.time.setText(DateFormat.format("EEE " + "HH:mm", hourlyModel.getDate() * 1000L));

       /* if (position %2 == 0) {
            Holder.recyclerGrid.setBackgroundResource(R.color.MainColorTwo);
        } if (position %2 == 1) {
            Holder.recyclerGrid.setBackgroundResource(R.color.MainColorFour);
        }*/

        int weatherId = (int) ((Math.floor(hourlyModel.getWeatherId())) / 100);

        if (weatherId == 8) {
            if (hourlyModel.getTemperature() <= 19) {
                Holder.weatherIcon.setImageResource(R.drawable.ic_icicle);
            } else if (hourlyModel.getTemperature() > 19) {
                Holder.weatherIcon.setImageResource(R.drawable.ic_sun);
            }
        } else {
            switch (weatherId) {
                case 2: // ID: 2xx, then Weather is Thunderstorm.
                    Holder.weatherIcon.setImageResource(R.drawable.ic_lightning);
                    break;
                case 5: // ID: 5xx, then Weather is Rain.
                    Holder.weatherIcon.setImageResource(R.drawable.ic_drop);
                    break;
                case 6: // ID: 6xx, then Weather is Snow.
                    Holder.weatherIcon.setImageResource(R.drawable.ic_snowman);
                    break;
                case 9: // ID: 9xx, then Weather is Windy.
                    Holder.weatherIcon.setImageResource(R.drawable.ic_fog);
                    break;
                case 3: // ID: 3xx, then Weather is Drizzle.
                    Holder.weatherIcon.setImageResource(R.drawable.ic_drizzle);
                    break;
                case 7: // ID: 7xx, then Weather is Atmosphere.
                    Holder.weatherIcon.setImageResource(R.drawable.ic_fog);
                    break;
            }

        }

    }

    @Override
    public int getItemCount()   { return HourlyList.size(); }
}
