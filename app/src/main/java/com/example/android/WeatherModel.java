package com.example.android.weatherapp;

/**
 * Created by Glen on 10/07/2017.
 */

public class WeatherModel {

    private double minTemperature;

    private double maxTemperature;

    private int weatherId;

    private String mainWeatherDescription;

    private String subWeatherDescription;

    private double windSpeed;

    private Long date;

    private String city;

    private String country;

    public WeatherModel(String country, String city, double windSpeed, double minTemperature, double maxTemperature, int weatherId, String mainWeatherDescription, String subWeatherDescription, Long date)   {
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.weatherId = weatherId;
        this.mainWeatherDescription = mainWeatherDescription;
        this.subWeatherDescription = subWeatherDescription;
        this.windSpeed = windSpeed;
        this.date = date;
        this.city = city;
        this.country = country;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public String getMainWeatherDescription() {
        return mainWeatherDescription;
    }

    public void setMainWeatherDescription(String mainWeatherDescription) {
        this.mainWeatherDescription = mainWeatherDescription;
    }

    public String getSubWeatherDescription() {
        return subWeatherDescription;
    }

    public void setSubWeatherDescription(String subWeatherDescription) {
        this.subWeatherDescription = subWeatherDescription;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
