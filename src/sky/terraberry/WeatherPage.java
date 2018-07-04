package sky.terraberry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pi4j.platform.Platform;
import com.pi4j.system.SystemInfoProvider;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;

public class WeatherPage extends AbstractPage
{
    private long lastRefreshTime;
    private long lastWeatherForecastVerificationTime;
    private Currently currently;
    private static final boolean INTERNET_ACTIVE=true;

    public WeatherPage()
    {
        lastRefreshTime=0L;
        lastWeatherForecastVerificationTime=0L;
        currently=null;
    }

    public int getSerial()
    {
        return 2;
    }

    public String getName()
    {
        return "Météo";
    }

    public synchronized Page potentiallyUpdate()
    {
        long now=System.currentTimeMillis();
        if(now-lastRefreshTime>Duration.of(1).minutePlus(3).secondPlus(700).millisecond())
        {
            lastRefreshTime=now;
            try
            {
                BufferedImage sourceImage=new BufferedImage(250,128,BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics2D g2d=sourceImage.createGraphics();
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0,0,250,128);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0,6,250,122);
                g2d.setColor(Color.BLACK);

                Font infoFont=Terraberry.FONT.deriveFont(21f).deriveFont(AffineTransform.getScaleInstance(.9d,1d));
                Font condensedInfoFont=Terraberry.FONT.deriveFont(21f).deriveFont(AffineTransform.getScaleInstance(.6d,1d));

                now=System.currentTimeMillis();
                if(INTERNET_ACTIVE&&now-lastWeatherForecastVerificationTime>Duration.of(10).minutePlus(5).secondPlus(300).millisecond())
                {
                    lastWeatherForecastVerificationTime=now;
                    try
                    {
                        StringBuilder stringBuilder=new StringBuilder();
                        HttpURLConnection connection=null;
                        try
                        {
                            String apiKey="";
                            String latitude="";
                            String longitude="";
                            try(BufferedReader reader=new BufferedReader(new FileReader(new File("darksky.ini"))))
                            {
                                apiKey=reader.readLine();
                                latitude=reader.readLine();
                                longitude=reader.readLine();
                            }
                            catch(IOException e)
                            {
                                Logger.LOGGER.error("Unable to read Darksky access informations from the config file ("+e.toString()+")");
                            }
                            connection=(HttpURLConnection)new URL("https://api.darksky.net/forecast/"+apiKey+"/"+latitude+","+longitude+"?exclude=daily,hourly,minutely&lang=fr&units=ca").openConnection();
                            connection.setConnectTimeout(5000);
                            connection.setReadTimeout(5000);
                            connection.setRequestMethod("GET");
                            connection.setAllowUserInteraction(false);
                            connection.setDoOutput(true);
                            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String line;
                            while((line=bufferedReader.readLine())!=null)
                            {
                                stringBuilder.append(line);
                                stringBuilder.append("\n");
                            }
                            connection.disconnect();
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                        finally
                        {
                            if(connection!=null)
                                connection.disconnect();
                        }
                        JsonObject response=new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();
                        JsonObject currentlyObject=response.get("currently").getAsJsonObject();
                        currently=new Currently(currentlyObject.has("time")?currentlyObject.get("time").getAsLong()*1000L:0L,
                                currentlyObject.has("summary")?currentlyObject.get("summary").getAsString():"",
                                currentlyObject.has("icon")?currentlyObject.get("icon").getAsString():"",
                                currentlyObject.has("precipIntensity")?currentlyObject.get("precipIntensity").getAsDouble():0d,
                                currentlyObject.has("precipProbability")?currentlyObject.get("precipProbability").getAsDouble():0d,
                                currentlyObject.has("temperature")?currentlyObject.get("temperature").getAsDouble():0d,
                                currentlyObject.has("apparentTemperature")?currentlyObject.get("apparentTemperature").getAsDouble():0d,
                                currentlyObject.has("dewPoint")?currentlyObject.get("dewPoint").getAsDouble():0d,
                                currentlyObject.has("humidity")?currentlyObject.get("humidity").getAsDouble():0d,
                                currentlyObject.has("pressure")?currentlyObject.get("pressure").getAsDouble():0d,
                                currentlyObject.has("windSpeed")?currentlyObject.get("windSpeed").getAsDouble():0d,
                                currentlyObject.has("windGust")?currentlyObject.get("windGust").getAsDouble():0d,
                                currentlyObject.has("windBearing")?currentlyObject.get("windBearing").getAsInt():0,
                                currentlyObject.has("cloudCover")?currentlyObject.get("cloudCover").getAsDouble():0d,
                                currentlyObject.has("uvIndex")?currentlyObject.get("uvIndex").getAsInt():0,
                                currentlyObject.has("visibility")?currentlyObject.get("visibility").getAsDouble():0d,
                                currentlyObject.has("ozone")?currentlyObject.get("ozone").getAsDouble():0d);
                    }
                    catch(Exception e)
                    {
                        Logger.LOGGER.error(e.toString());
                    }
                }
                else
                    if(!INTERNET_ACTIVE)
                        currently=new Currently(now,"Temps ensoleillé avec des averses en fin de journée.","todo",2.3d,.75d,28.7d,26.3d,15.2d,.64d,1032.8d,32d,45d,138,.86d,10,10d,444d);

                g2d.setFont(infoFont);

                String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
                g2d.drawString(time,0,20);

                String ip="";
                if(INTERNET_ACTIVE)
                {
                    mainLoop:for(NetworkInterface networkInterface:Collections.list(NetworkInterface.getNetworkInterfaces()))
                        for(InetAddress inetAddress:Collections.list(networkInterface.getInetAddresses()))
                            if(inetAddress.getHostAddress().contains("192.168"))
                            {
                                ip=inetAddress.getHostAddress();
                                break mainLoop;
                            }
                    if(!ip.isEmpty())
                        ip=ip.substring(ip.lastIndexOf("."));
                }
                else
                    ip=".161";
                g2d.drawString(ip,60,20);

                SystemInfoProvider systemInfoProvider=null;
                try
                {
                    systemInfoProvider=Platform.getSystemInfoProvider(Platform.RASPBERRYPI);
                }
                catch(Exception e)
                {
                }

                String processorTemperature;
                if(systemInfoProvider!=null)
                    try
                    {
                        processorTemperature=new DecimalFormat("###0.0").format((double)systemInfoProvider.getCpuTemperature());
                    }
                    catch(Exception e)
                    {
                        processorTemperature="?";
                    }
                else
                    processorTemperature="53,7";
                processorTemperature+=" °C";
                g2d.drawString(processorTemperature,104,20);

                String freeMemory;
                if(systemInfoProvider!=null)
                    try
                    {
                        freeMemory=new DecimalFormat("###0").format((double)systemInfoProvider.getMemoryFree()/1_048_576d);
                    }
                    catch(Exception e)
                    {
                        freeMemory="?";
                    }
                else
                    freeMemory="289";
                freeMemory+=" Mio";
                g2d.drawString(freeMemory,176,20);

                if(currently!=null)
                {
                    String temperature=new DecimalFormat("###0.0").format(currently.getTemperature())+" °C";
                    g2d.drawString(temperature,0,40);

                    String humidity=new DecimalFormat("###0").format(currently.getHumidity()*100d)+" %";
                    g2d.drawString(humidity,66,40);

                    String dewPoint=new DecimalFormat("###0.0").format(currently.getDewPoint())+" °C";
                    g2d.drawString(dewPoint,116,40);

                    String precipIntensity=new DecimalFormat("###0.0").format(currently.getPrecipIntensity())+" mm/h";
                    g2d.drawString(precipIntensity,0,60);

                    String precipProbability=new DecimalFormat("###0").format(currently.getPrecipProbability()*100d)+" %";
                    g2d.drawString(precipProbability,90,60);

                    String windSpeed=new DecimalFormat("###0").format(currently.getWindSpeed())+" km/h";
                    g2d.drawString(windSpeed,0,80);

                    String windGust=new DecimalFormat("###0").format(currently.getWindGust())+" km/h";
                    g2d.drawString(windGust,80,80);

                    String windBearing=convertWindAngle(currently.getWindBearing());
                    g2d.drawString(windBearing,160,80);

                    String pressure=new DecimalFormat("###0.0").format(currently.getPressure())+" hPa";
                    g2d.drawString(pressure,0,100);

                    String uvIndex=new DecimalFormat("###0").format(currently.getUvIndex());
                    g2d.drawString(uvIndex,110,100);

                    String cloudCover=new DecimalFormat("###0").format(currently.getCloudCover()*100d)+" %";
                    g2d.drawString(cloudCover,140,100);

                    g2d.setFont(condensedInfoFont);

                    String summary=currently.getSummary();
                    g2d.drawString(summary,0,120);
                }

                g2d.dispose();
//                try(OutputStream outputStream=new FileOutputStream(new File("weather.png")))
//                {
//                    ImageIO.write(sourceImage,"png",outputStream);
//                }
                pixels=new Pixels().writeImage(sourceImage);
                Logger.LOGGER.info("Page "+getSerial()+" updated successfully");
            }
            catch(Exception e)
            {
                Logger.LOGGER.error("Unknown error ("+e.toString()+")");
            }
        }
        return this;
    }

    public Pixels getPixels()
    {
        return pixels;
    }

    public boolean hasHighFrequency()
    {
        return false;
    }

    public static void main(String[] args)
    {
        new WeatherPage().potentiallyUpdate();
    }

    public static String convertWindAngle(int angle)
    {
        return convertWindAngle((double)angle);
    }

    public static String convertWindAngle(double angle)
    {
        if(angle<11.25d)
            return "N";
        else
            if(angle<33.75d)
                return "NNE";
            else
                if(angle<56.25d)
                    return "NE";
                else
                    if(angle<78.75d)
                        return "ENE";
                    else
                        if(angle<101.25d)
                            return "E";
                        else
                            if(angle<123.75d)
                                return "ESE";
                            else
                                if(angle<146.25d)
                                    return "SE";
                                else
                                    if(angle<168.75d)
                                        return "SSE";
                                    else
                                        if(angle<191.25d)
                                            return "S";
                                        else
                                            if(angle<213.75d)
                                                return "SSO";
                                            else
                                                if(angle<236.25d)
                                                    return "SO";
                                                else
                                                    if(angle<258.75d)
                                                        return "OSO";
                                                    else
                                                        if(angle<281.25d)
                                                            return "O";
                                                        else
                                                            if(angle<303.75d)
                                                                return "ONO";
                                                            else
                                                                if(angle<326.25d)
                                                                    return "NO";
                                                                else
                                                                    if(angle<348.75d)
                                                                        return "NNO";
                                                                    else
                                                                        return "N";
    }
}
