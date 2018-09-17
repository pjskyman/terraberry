package sky.terraberry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pi4j.platform.Platform;
import com.pi4j.system.SystemInfoProvider;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
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
        if(now-lastRefreshTime>Duration.of(10).secondPlus(291).millisecond())
        {
            lastRefreshTime=now;
            try
            {
                BufferedImage sourceImage=new BufferedImage(250,128,BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics2D g2d=sourceImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0,0,250,128);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0,6,250,122);
                g2d.setColor(Color.BLACK);

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
                        currently=new Currently(now,"Nuages épars et quelques averses","partly-cloudy-day",2.3d,.1d,28.7d,26.3d,15.2d,.64d,1032.8d,32d,45d,332,.37d,10,10d,444d);

                Font infoFont=Terraberry.FONT.deriveFont(21f).deriveFont(AffineTransform.getScaleInstance(.9d,1d));
                Font slightlyCondensedInfoFont=Terraberry.FONT.deriveFont(21f).deriveFont(AffineTransform.getScaleInstance(.8d,1d));
                Font heavilyCondensedInfoFont=Terraberry.FONT.deriveFont(21f).deriveFont(AffineTransform.getScaleInstance(.8d,1d));//on essaye avec .8d aussi

                g2d.setFont(infoFont);

                String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());

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

                SystemInfoProvider systemInfoProvider=null;
                try
                {
                    systemInfoProvider=Platform.getSystemInfoProvider(Platform.RASPBERRYPI);
                }
                catch(Exception e)
                {
                }

//                String processorTemperature;
//                if(systemInfoProvider!=null)
//                    try
//                    {
//                        processorTemperature=new DecimalFormat("###0.0").format((double)systemInfoProvider.getCpuTemperature());
//                    }
//                    catch(Exception e)
//                    {
//                        processorTemperature="?";
//                    }
//                else
//                    processorTemperature="53,7";
//                processorTemperature+="°C";
                String deskTemperature;
                try
                {
                    deskTemperature=new DecimalFormat("###0.0").format(ThermometerManager.getTemperature());
                }
                catch(Exception e)
                {
                    deskTemperature="?";
                }
                deskTemperature+="°C";

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
                freeMemory+="Mo";
                g2d.drawString(time+"  "+ip+"  Ici:"+deskTemperature+"  "+freeMemory,0,20);

                if(currently!=null)
                {
//                    g2d.drawLine(0,24,250,24);
//                    g2d.drawLine(0,25,250,25);

                    g2d.setFont(heavilyCondensedInfoFont);

                    g2d.drawImage(Icons.getIcon(currently.getIcon()),0,32,null);

                    String summary=currently.getSummary();
                    g2d.drawString(summary,32,44);

                    g2d.setFont(infoFont);

                    String temperature=new DecimalFormat("###0.0").format(currently.getTemperature())+"°C";

                    String humidity=new DecimalFormat("###0").format(currently.getHumidity()*100d)+"%";

                    String dewPoint=new DecimalFormat("###0.0").format(currently.getDewPoint())+"°C";
                    g2d.drawString("Ext:"+temperature+"  "+humidity+"  PdR:"+dewPoint,0,64);

                    String precipIntensity=new DecimalFormat("###0.0").format(currently.getPrecipIntensity())+"mm/h";

                    String precipProbability=new DecimalFormat("###0").format(currently.getPrecipProbability()*100d)+"%";
                    String rainString="Pluie:"+precipIntensity+"  Prob:"+precipProbability;
                    int rainStringWidth=(int)Math.ceil(infoFont.getStringBounds(rainString,g2d.getFontRenderContext()).getWidth());
                    g2d.drawString(rainString,0,84);

                    g2d.fillOval(rainStringWidth+3,68,18,18);
                    g2d.setColor(Color.WHITE);
                    g2d.fillArc(rainStringWidth+5,70,14,14,90,(int)(360d-currently.getPrecipProbability()*360d));
                    g2d.setColor(Color.BLACK);

                    String wind=new DecimalFormat("###0").format(currently.getWindSpeed())+"~"+new DecimalFormat("###0").format(currently.getWindGust())+"km/h";

                    String windBearing=convertWindAngle(currently.getWindBearing());

                    String windString="Vent:"+wind+"  Dir:"+windBearing;
                    int windStringWidth=(int)Math.ceil(infoFont.getStringBounds(windString,g2d.getFontRenderContext()).getWidth());
                    g2d.drawString(windString,0,104);

                    g2d.setStroke(new BasicStroke(2.5f));
                    g2d.drawLine(
                            windStringWidth+12+(int)(Math.sin((double)currently.getWindBearing()*Math.PI/180d)*8d),
                            98-(int)(Math.cos((double)currently.getWindBearing()*Math.PI/180d)*8d),
                            windStringWidth+12-(int)(Math.sin((double)currently.getWindBearing()*Math.PI/180d)*6d),
                            98+(int)(Math.cos((double)currently.getWindBearing()*Math.PI/180d)*6d)
                    );
                    Path2D path=new Path2D.Double();
                    path.moveTo(
                            (double)windStringWidth+12d-Math.sin((double)currently.getWindBearing()*Math.PI/180d)*10d,
                            98d+Math.cos((double)currently.getWindBearing()*Math.PI/180d)*10d
                    );
                    path.lineTo(
                            (double)windStringWidth+12d-Math.sin((double)(currently.getWindBearing()+90)*Math.PI/180d)*5d,
                            98d+Math.cos((double)(currently.getWindBearing()+90)*Math.PI/180d)*5d
                    );
                    path.lineTo(
                            (double)windStringWidth+12d-Math.sin((double)(currently.getWindBearing()-90)*Math.PI/180d)*5d,
                            98d+Math.cos((double)(currently.getWindBearing()-90)*Math.PI/180d)*5d
                    );
                    path.closePath();
                    g2d.fill(path);

                    g2d.setFont(slightlyCondensedInfoFont);

                    String pressure=new DecimalFormat("###0.0").format(currently.getPressure())+"hPa";

                    String uvIndex=new DecimalFormat("###0").format(currently.getUvIndex());

                    String cloudCover=new DecimalFormat("###0").format(currently.getCloudCover()*100d)+"%";
                    String addString=pressure+"  UV:"+uvIndex+"  Nuag:"+cloudCover;
                    int addStringWidth=(int)Math.ceil(slightlyCondensedInfoFont.getStringBounds(addString,g2d.getFontRenderContext()).getWidth());
                    g2d.drawString(addString,0,124);

                    g2d.fillOval(addStringWidth+3,108,18,18);
                    g2d.setColor(Color.WHITE);
                    g2d.fillArc(addStringWidth+5,110,14,14,90,(int)(360d-currently.getCloudCover()*360d));
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
