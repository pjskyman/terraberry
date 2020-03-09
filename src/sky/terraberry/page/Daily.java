package sky.terraberry.page;

public class Daily
{
    private final long time;
    private final String summary;
    private final String icon;
    private final long sunriseTime;
    private final long sunsetTime;
    private final double moonPhase;
    private final double precipIntensity;
    private final double precipIntensityMax;
    private final long precipIntensityMaxTime;
    private final double precipProbability;
    private final String precipType;
    private final double temperatureHigh;
    private final long temperatureHighTime;
    private final double temperatureLow;
    private final long temperatureLowTime;
    private final double apparentTemperatureHigh;
    private final long apparentTemperatureHighTime;
    private final double apparentTemperatureLow;
    private final long apparentTemperatureLowTime;
    private final double dewPoint;
    private final double humidity;
    private final double pressure;
    private final double windSpeed;
    private final double windGust;
    private final long windGustTime;
    private final int windBearing;
    private final double cloudCover;
    private final int uvIndex;
    private final long uvIndexTime;
    private final double visibility;
    private final double ozone;
    private final double temperatureMin;
    private final long temperatureMinTime;
    private final double temperatureMax;
    private final long temperatureMaxTime;
    private final double apparentTemperatureMin;
    private final long apparentTemperatureMinTime;
    private final double apparentTemperatureMax;
    private final long apparentTemperatureMaxTime;

    public Daily(long time,
            String summary,
            String icon,
            long sunriseTime,
            long sunsetTime,
            double moonPhase,
            double precipIntensity,
            double precipIntensityMax,
            long precipIntensityMaxTime,
            double precipProbability,
            String precipType,
            double temperatureHigh,
            long temperatureHighTime,
            double temperatureLow,
            long temperatureLowTime,
            double apparentTemperatureHigh,
            long apparentTemperatureHighTime,
            double apparentTemperatureLow,
            long apparentTemperatureLowTime,
            double dewPoint,
            double humidity,
            double pressure,
            double windSpeed,
            double windGust,
            long windGustTime,
            int windBearing,
            double cloudCover,
            int uvIndex,
            long uvIndexTime,
            double visibility,
            double ozone,
            double temperatureMin,
            long temperatureMinTime,
            double temperatureMax,
            long temperatureMaxTime,
            double apparentTemperatureMin,
            long apparentTemperatureMinTime,
            double apparentTemperatureMax,
            long apparentTemperatureMaxTime)
    {
        this.time=time;
        this.summary=summary;
        this.icon=icon;
        this.sunriseTime=sunriseTime;
        this.sunsetTime=sunsetTime;
        this.moonPhase=moonPhase;
        this.precipIntensity=precipIntensity;
        this.precipIntensityMax=precipIntensityMax;
        this.precipIntensityMaxTime=precipIntensityMaxTime;
        this.precipProbability=precipProbability;
        this.precipType=precipType;
        this.temperatureHigh=temperatureHigh;
        this.temperatureHighTime=temperatureHighTime;
        this.temperatureLow=temperatureLow;
        this.temperatureLowTime=temperatureLowTime;
        this.apparentTemperatureHigh=apparentTemperatureHigh;
        this.apparentTemperatureHighTime=apparentTemperatureHighTime;
        this.apparentTemperatureLow=apparentTemperatureLow;
        this.apparentTemperatureLowTime=apparentTemperatureLowTime;
        this.dewPoint=dewPoint;
        this.humidity=humidity;
        this.pressure=pressure;
        this.windSpeed=windSpeed;
        this.windGust=windGust;
        this.windGustTime=windGustTime;
        this.windBearing=windBearing;
        this.cloudCover=cloudCover;
        this.uvIndex=uvIndex;
        this.uvIndexTime=uvIndexTime;
        this.visibility=visibility;
        this.ozone=ozone;
        this.temperatureMin=temperatureMin;
        this.temperatureMinTime=temperatureMinTime;
        this.temperatureMax=temperatureMax;
        this.temperatureMaxTime=temperatureMaxTime;
        this.apparentTemperatureMin=apparentTemperatureMin;
        this.apparentTemperatureMinTime=apparentTemperatureMinTime;
        this.apparentTemperatureMax=apparentTemperatureMax;
        this.apparentTemperatureMaxTime=apparentTemperatureMaxTime;
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

    public long getSunriseTime()
    {
        return sunriseTime;
    }

    public long getSunsetTime()
    {
        return sunsetTime;
    }

    public double getMoonPhase()
    {
        return moonPhase;
    }

    public double getPrecipIntensity()
    {
        return precipIntensity;
    }

    public double getPrecipIntensityMax()
    {
        return precipIntensityMax;
    }

    public long getPrecipIntensityMaxTime()
    {
        return precipIntensityMaxTime;
    }

    public double getPrecipProbability()
    {
        return precipProbability;
    }

    public String getPrecipType()
    {
        return precipType;
    }

    public double getTemperatureHigh()
    {
        return temperatureHigh;
    }

    public long getTemperatureHighTime()
    {
        return temperatureHighTime;
    }

    public double getTemperatureLow()
    {
        return temperatureLow;
    }

    public long getTemperatureLowTime()
    {
        return temperatureLowTime;
    }

    public double getApparentTemperatureHigh()
    {
        return apparentTemperatureHigh;
    }

    public long getApparentTemperatureHighTime()
    {
        return apparentTemperatureHighTime;
    }

    public double getApparentTemperatureLow()
    {
        return apparentTemperatureLow;
    }

    public long getApparentTemperatureLowTime()
    {
        return apparentTemperatureLowTime;
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

    public long getWindGustTime()
    {
        return windGustTime;
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

    public long getUvIndexTime()
    {
        return uvIndexTime;
    }

    public double getVisibility()
    {
        return visibility;
    }

    public double getOzone()
    {
        return ozone;
    }

    public double getTemperatureMin()
    {
        return temperatureMin;
    }

    public long getTemperatureMinTime()
    {
        return temperatureMinTime;
    }

    public double getTemperatureMax()
    {
        return temperatureMax;
    }

    public long getTemperatureMaxTime()
    {
        return temperatureMaxTime;
    }

    public double getApparentTemperatureMin()
    {
        return apparentTemperatureMin;
    }

    public long getApparentTemperatureMinTime()
    {
        return apparentTemperatureMinTime;
    }

    public double getApparentTemperatureMax()
    {
        return apparentTemperatureMax;
    }

    public long getApparentTemperatureMaxTime()
    {
        return apparentTemperatureMaxTime;
    }
}
