package sky.terraberry;

public class Currently
{
    private final long time;
    private final String summary;
    private final String icon;
    private final double precipIntensity;
    private final double precipProbability;
    private final double temperature;
    private final double apparentTemperature;
    private final double dewPoint;
    private final double humidity;
    private final double pressure;
    private final double windSpeed;
    private final double windGust;
    private final int windBearing;
    private final double cloudCover;
    private final int uvIndex;
    private final double visibility;
    private final double ozone;

    public Currently(long time,
            String summary,
            String icon,
            double precipIntensity,
            double precipProbability,
            double temperature,
            double apparentTemperature,
            double dewPoint,
            double humidity,
            double pressure,
            double windSpeed,
            double windGust,
            int windBearing,
            double cloudCover,
            int uvIndex,
            double visibility,
            double ozone)
    {
        this.time=time;
        this.summary=summary;
        this.icon=icon;
        this.precipIntensity=precipIntensity;
        this.precipProbability=precipProbability;
        this.temperature=temperature;
        this.apparentTemperature=apparentTemperature;
        this.dewPoint=dewPoint;
        this.humidity=humidity;
        this.pressure=pressure;
        this.windSpeed=windSpeed;
        this.windGust=windGust;
        this.windBearing=windBearing;
        this.cloudCover=cloudCover;
        this.uvIndex=uvIndex;
        this.visibility=visibility;
        this.ozone=ozone;
    }

    public long getTime()
    {
        return time;
    }

    public String getSummary()
    {
        return summary;
    }

    public String getIcon()
    {
        return icon;
    }

    public double getPrecipIntensity()
    {
        return precipIntensity;
    }

    public double getPrecipProbability()
    {
        return precipProbability;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public double getApparentTemperature()
    {
        return apparentTemperature;
    }

    public double getDewPoint()
    {
        return dewPoint;
    }

    public double getHumidity()
    {
        return humidity;
    }

    public double getPressure()
    {
        return pressure;
    }

    public double getWindSpeed()
    {
        return windSpeed;
    }

    public double getWindGust()
    {
        return windGust;
    }

    public int getWindBearing()
    {
        return windBearing;
    }

    public double getCloudCover()
    {
        return cloudCover;
    }

    public int getUvIndex()
    {
        return uvIndex;
    }

    public double getVisibility()
    {
        return visibility;
    }

    public double getOzone()
    {
        return ozone;
    }
}
