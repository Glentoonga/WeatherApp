package com.example.android.weatherapp;

import android.content.Context;
import android.graphics.Typeface;
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
 * Created by Glen on 10/07/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>   {

    public Context context;
    public List<WeatherModel> WeatherList;
    Typeface tf;
    Typeface ft;


    public RecyclerViewAdapter(List<WeatherModel> weatherList, Context context) {
        this.context = context;
        this.WeatherList = weatherList;
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/LemonMilkbold.otf");
        ft = Typeface.createFromAsset(context.getAssets(), "fonts/BebasNeue.otf");
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView weatherDescription;
        public TextView minimumTemperature;
        public TextView maximumTemperature;
        public TextView day;
        public TextView windSpeed;
        public ImageView weatherIcon;
        public RelativeLayout recyclerRow;

        public ViewHolder(View itemView)    {

            super(itemView);
            weatherDescription = (TextView) itemView.findViewById(R.id.weather_description);
            minimumTemperature = (TextView) itemView.findViewById(R.id.min_temperature_figure);
            maximumTemperature = (TextView) itemView.findViewById(R.id.max_temperature_figure);
            day = (TextView) itemView.findViewById(R.id.weather_day);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weather_icon);
            recyclerRow = (RelativeLayout) itemView.findViewById(R.id.recycler_row);
            // windSpeed = (TextView) itemView.findViewById(R.id.wind_speed_figure);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)    {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_row_list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder Holder, int position)   {

        final WeatherModel weatherModel = WeatherList.get(position);
        Holder.weatherDescription.setText(weatherModel.getMainWeatherDescription() + ": " + weatherModel.getSubWeatherDescription());
        Holder.minimumTemperature.setText(String.valueOf(" " + Math.round(weatherModel.getMinTemperature() - 273.15)) + "°");
        Holder.minimumTemperature.setTypeface(tf);
        Holder.maximumTemperature.setText(String.valueOf(" " + Math.round(weatherModel.getMaxTemperature() - 273.15)) + "°");
        Holder.maximumTemperature.setTypeface(tf);
        Holder.day.setText(DateFormat.format("EEE dd MMM", weatherModel.getDate() * 1000L));
        // Holder.day.setTypeface(tf);
        // Holder.windSpeed.setText(String.valueOf(Math.round(weatherModel.getWindSpeed())) + "ᵐˢ");


        if (position %3 == 1) {
                Holder.recyclerRow.setBackgroundResource(R.color.ColorAccentSix);
        } if (position %3 == 2) {
            Holder.recyclerRow.setBackgroundResource(R.color.ColorAccentSeven);
        }

        int weatherId = (int) ((Math.floor(weatherModel.getWeatherId())) / 100);

        if (weatherId == 8) {
            if (weatherModel.getMaxTemperature() <= 19) {
                Holder.weatherIcon.setImageResource(R.drawable.ic_icicle);
            } else if (weatherModel.getMaxTemperature() > 19) {
                Holder.weatherIcon.setImageResource(R.drawable.ic_sunone);
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
    public int getItemCount()   { return WeatherList.size(); }
}
