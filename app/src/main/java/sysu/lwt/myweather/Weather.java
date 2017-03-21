package sysu.lwt.myweather;

/**
 * Created by 12136 on 2017/3/21.
 */

public class Weather {
    private String data, Weather_description, Temperature;

    public Weather(String data, String Weather_description, String Temperature) {
        this.data = data;
        this.Weather_description = Weather_description;
        this.Temperature = Temperature;
    }

    public String getData() {
        return data;
    }

    public String getWeather_description() {
        return Weather_description;
    }

    public String getTemperature() {
        return Temperature;
    }

}

