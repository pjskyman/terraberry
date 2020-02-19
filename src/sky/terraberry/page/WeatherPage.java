package sky.terraberry.page;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pi4j.platform.Platform;
import com.pi4j.system.SystemInfoProvider;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
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
import sky.program.Duration;
import sky.terraberry.Icons;
import sky.terraberry.Logger;
import sky.terraberry.ThermometerManager;

public class WeatherPage extends AbstractSinglePage
{
    private long lastWeatherForecastVerificationTime;
    private Currently currently;
    private static final boolean INTERNET_ACTIVE=true;

    public WeatherPage(Page parentPage)
    {
        super(parentPage);
        lastWeatherForecastVerificationTime=0L;
        currently=null;
    }

    public String getName()
    {
        return "Météo";
    }

    protected long getMinimalRefreshDelay()
    {
        return Duration.of(10).secondPlus(291).millisecond();
    }

    protected void populateImage(Graphics2D g2d) throws VetoException,Exception
    {
        long now=System.currentTimeMillis();
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
                e.printStackTrace();
            }
        }
        else
            if(!INTERNET_ACTIVE)
                currently=new Currently(now,"Nuages épars et quelques averses","partly-cloudy-day",2.3d,.1d,28.7d,26.3d,15.2d,.64d,1032.8d,32d,45d,165,.67d,10,10d,444d);

        Font bigFont=FREDOKA_ONE_FONT.deriveFont(26f).deriveFont(AffineTransform.getScaleInstance(.85,1d));
        Font littleFont=FREDOKA_ONE_FONT.deriveFont(18f).deriveFont(AffineTransform.getScaleInstance(.8d,1d));

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
            ip=".xxx";
        ip="IP "+ip;

        SystemInfoProvider systemInfoProvider=null;
        try
        {
            systemInfoProvider=Platform.getSystemInfoProvider(Platform.RASPBERRYPI);
        }
        catch(Exception e)
        {
        }

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
        freeMemory="RAM "+freeMemory+" Mio libres";

        g2d.setFont(littleFont);

        String line1=time+" \u2013 "+ip+" \u2013 "+freeMemory;
        int line1Width=(int)Math.ceil(g2d.getFont().getStringBounds(line1,g2d.getFontRenderContext()).getWidth());
        g2d.drawString(line1,148-line1Width/2,13);

        String deskTemperature;
        if(INTERNET_ACTIVE)
            try
            {
                deskTemperature=new DecimalFormat("###0.0").format(ThermometerManager.getTemperature());
            }
            catch(Exception e)
            {
                deskTemperature="?";
            }
        else
            deskTemperature="23,2";
        deskTemperature+="°C";

        if(currently!=null)
        {
            String line2=currently.getSummary();
            int line2Width=(int)Math.ceil(g2d.getFont().getStringBounds(line2,g2d.getFontRenderContext()).getWidth());

            g2d.drawImage(Icons.getIcon(currently.getIcon()),Math.max(0,148-(line2Width+32)/2),18,null);

            g2d.drawString(line2,32+Math.max(0,148-(line2Width+32)/2),30);

            String temperature=new DecimalFormat("###0.0").format(currently.getTemperature())+"°C";

            String humidity=new DecimalFormat("###0").format(currently.getHumidity()*100d)+"%";

            g2d.setFont(bigFont);

            String line3="Int: "+deskTemperature+" Ext: "+temperature+" "+humidity;
            int line3Width=(int)Math.ceil(g2d.getFont().getStringBounds(line3,g2d.getFontRenderContext()).getWidth());
            g2d.drawString(line3,148-line3Width/2,54);

            String precipIntensity=new DecimalFormat("###0.0").format(currently.getPrecipIntensity())+" mm/h";

            String precipProbability=new DecimalFormat("###0").format(currently.getPrecipProbability()*100d)+"%";
            String line4="Pluie: "+precipIntensity+" @ "+precipProbability;
            int line4Width=(int)Math.ceil(g2d.getFont().getStringBounds(line4,g2d.getFontRenderContext()).getWidth());
            g2d.drawString(line4,148-(line4Width+26)/2,77);

            drawCheese(g2d,line4Width+15+148-(line4Width+26)/2,67,currently.getPrecipProbability());

            String wind=new DecimalFormat("###0").format(currently.getWindSpeed())+"~"+new DecimalFormat("###0").format(currently.getWindGust())+" km/h";

            String windBearing=convertWindAngle(currently.getWindBearing());

            String line5String="Vent: "+wind+" "+windBearing;
            int line5Width=(int)Math.ceil(g2d.getFont().getStringBounds(line5String,g2d.getFontRenderContext()).getWidth());
            g2d.drawString(line5String,148-(line5Width+23)/2,100);

            drawArrow(g2d,line5Width+12+148-(line5Width+23)/2,90,currently.getWindBearing());

            String pressure=new DecimalFormat("###0.0").format(currently.getPressure())+" hPa";

            String cloudCover=new DecimalFormat("###0").format(currently.getCloudCover()*100d)+"%";
            String line6=pressure+" Nuages: "+cloudCover;
            int line6Width=(int)Math.ceil(g2d.getFont().getStringBounds(line6,g2d.getFontRenderContext()).getWidth());
            g2d.drawString(line6,148-(line6Width+26)/2,123);

            drawCheese(g2d,line6Width+15+148-(line6Width+26)/2,113,currently.getCloudCover());
        }
    }

    private static void drawCheese(Graphics2D g2d,int centerX,int centerY,double ratio)
    {
        g2d.setColor(Color.BLACK);
        g2d.fillOval(centerX-11,centerY-11,22,22);
        g2d.setColor(Color.WHITE);
        g2d.fillArc(centerX-9,centerY-9,18,18,90,(int)(360d-ratio*360d));
        g2d.setColor(Color.BLACK);
    }

    private static void drawArrow(Graphics2D g2d,int centerX,int centerY,int angle)
    {
        g2d.setStroke(new BasicStroke(3f));
        g2d.draw(new Line2D.Double(
                (double)centerX+Math.sin((double)angle*Math.PI/180d)*8d,
                (double)centerY-Math.cos((double)angle*Math.PI/180d)*8d,
                (double)centerX-Math.sin((double)angle*Math.PI/180d)*6d,
                (double)centerY+Math.cos((double)angle*Math.PI/180d)*6d
        ));
        Path2D path=new Path2D.Double();
        path.moveTo(
                (double)centerX-Math.sin((double)angle*Math.PI/180d)*10d,
                (double)centerY+Math.cos((double)angle*Math.PI/180d)*10d
        );
        path.lineTo(
                (double)centerX-Math.sin((double)(angle+90)*Math.PI/180d)*7d,
                (double)centerY+Math.cos((double)(angle+90)*Math.PI/180d)*7d
        );
        path.lineTo(
                (double)centerX-Math.sin((double)(angle-90)*Math.PI/180d)*7d,
                (double)centerY+Math.cos((double)(angle-90)*Math.PI/180d)*7d
        );
        path.closePath();
        g2d.fill(path);
    }

    protected String getDebugImageFileName()
    {
        return "weather.png";
    }

    public static void main(String[] args)
    {
        new WeatherPage(null).potentiallyUpdate();
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
