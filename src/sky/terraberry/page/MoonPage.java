package sky.terraberry.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.imageio.ImageIO;
import sky.program.Duration;
import sky.terraberry.Logger;

public class MoonPage extends AbstractSinglePage
{
    public MoonPage(Page parentPage)
    {
        super(parentPage);
    }

    public String getName()
    {
        return "Lune actuelle";
    }

    protected long getMinimalRefreshDelay()
    {
        return Duration.of(2).hourMinus(2).minutePlus(5).second();
    }

    protected void populateImage(Graphics2D g2d) throws VetoException,Exception
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
                e.printStackTrace();
            }
            connection=(HttpURLConnection)new URL("https://api.darksky.net/forecast/"+apiKey+"/"+latitude+","+longitude+"?exclude=currently,hourly,minutely&lang=fr&units=ca").openConnection();
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
        JsonObject dailyObject=response.get("daily").getAsJsonObject();
        JsonArray dataArray=dailyObject.get("data").getAsJsonArray();
        List<Daily> dailies=new ArrayList<>();
        for(int i=0;i<dataArray.size();i++)
        {
            JsonObject object=dataArray.get(i).getAsJsonObject();
            dailies.add(new Daily(object.has("time")?object.get("time").getAsLong()*1000L:0L,
                    object.has("summary")?object.get("summary").getAsString():"",
                    object.has("icon")?object.get("icon").getAsString():"",
                    object.has("sunriseTime")?object.get("sunriseTime").getAsLong()*1000L:0L,
                    object.has("sunsetTime")?object.get("sunsetTime").getAsLong()*1000L:0L,
                    object.has("moonPhase")?object.get("moonPhase").getAsDouble():0d,
                    object.has("precipIntensity")?object.get("precipIntensity").getAsDouble():0d,
                    object.has("precipIntensityMax")?object.get("precipIntensityMax").getAsDouble():0d,
                    object.has("precipIntensityMaxTime")?object.get("precipIntensityMaxTime").getAsLong()*1000L:0L,
                    object.has("precipProbability")?object.get("precipProbability").getAsDouble():0d,
                    object.has("precipType")?object.get("precipType").getAsString():"",
                    object.has("temperatureHigh")?object.get("temperatureHigh").getAsDouble():0d,
                    object.has("temperatureHighTime")?object.get("temperatureHighTime").getAsLong()*1000L:0L,
                    object.has("temperatureLow")?object.get("temperatureLow").getAsDouble():0d,
                    object.has("temperatureLowTime")?object.get("temperatureLowTime").getAsLong()*1000L:0L,
                    object.has("apparentTemperatureHigh")?object.get("apparentTemperatureHigh").getAsDouble():0d,
                    object.has("apparentTemperatureHighTime")?object.get("apparentTemperatureHighTime").getAsLong()*1000L:0L,
                    object.has("apparentTemperatureLow")?object.get("apparentTemperatureLow").getAsDouble():0d,
                    object.has("apparentTemperatureLowTime")?object.get("apparentTemperatureLowTime").getAsLong()*1000L:0L,
                    object.has("dewPoint")?object.get("dewPoint").getAsDouble():0d,
                    object.has("humidity")?object.get("humidity").getAsDouble():0d,
                    object.has("pressure")?object.get("pressure").getAsDouble():0d,
                    object.has("windSpeed")?object.get("windSpeed").getAsDouble():0d,
                    object.has("windGust")?object.get("windGust").getAsDouble():0d,
                    object.has("windGustTime")?object.get("windGustTime").getAsLong()*1000L:0L,
                    object.has("windBearing")?object.get("windBearing").getAsInt():0,
                    object.has("cloudCover")?object.get("cloudCover").getAsDouble():0d,
                    object.has("uvIndex")?object.get("uvIndex").getAsInt():0,
                    object.has("uvIndexTime")?object.get("uvIndexTime").getAsLong()*1000L:0L,
                    object.has("visibility")?object.get("visibility").getAsDouble():0d,
                    object.has("ozone")?object.get("ozone").getAsDouble():0d,
                    object.has("temperatureMin")?object.get("temperatureMin").getAsDouble():0d,
                    object.has("temperatureMinTime")?object.get("temperatureMinTime").getAsLong()*1000L:0L,
                    object.has("temperatureMax")?object.get("temperatureMax").getAsDouble():0d,
                    object.has("temperatureMaxTime")?object.get("temperatureMaxTime").getAsLong()*1000L:0L,
                    object.has("apparentTemperatureMin")?object.get("apparentTemperatureMin").getAsDouble():0d,
                    object.has("apparentTemperatureMinTime")?object.get("apparentTemperatureMinTime").getAsLong()*1000L:0L,
                    object.has("apparentTemperatureMax")?object.get("apparentTemperatureMax").getAsDouble():0d,
                    object.has("apparentTemperatureMaxTime")?object.get("apparentTemperatureMaxTime").getAsLong()*1000L:0L));
        }
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0,0,296,128);
        g2d.setColor(Color.WHITE);
        GregorianCalendar calendar=new GregorianCalendar();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        double absolute=(double)hour/24d+(double)minute/60d/24d;
        double phase1=dailies.get(0).getMoonPhase();
        double phase2=dailies.get(1).getMoonPhase();
        if(phase2<phase1)
            phase2+=1d;
        double phase=phase1*(1d-absolute)+phase2*absolute;
        if(phase>1d)
            phase-=1d;
        Path2D path=new Path2D.Double();
        path.moveTo(148d,0d);
        if(phase<=.5d)
        {
            for(int degree=0;degree<=180;degree+=2)
                path.lineTo(148d+63.5d*Math.sin((double)degree*Math.PI/180d),63.5d-63.5d*Math.cos((double)degree*Math.PI/180d));
            for(int degree=180;degree>=0;degree-=2)
                path.lineTo(148d+63.5d*Math.sin((double)degree*Math.PI/180d)*Math.cos(phase*Math.PI/.5d),63.5d-63.5d*Math.cos((double)degree*Math.PI/180d));
        }
        else
        {
            for(int degree=0;degree<=180;degree+=2)
                path.lineTo(148d-63.5d*Math.sin((double)degree*Math.PI/180d)*Math.cos((1d-phase)*Math.PI/.5d),63.5d-63.5d*Math.cos((double)degree*Math.PI/180d));
            for(int degree=180;degree>=0;degree-=2)
                path.lineTo(148d-63.5d*Math.sin((double)degree*Math.PI/180d),63.5d-63.5d*Math.cos((double)degree*Math.PI/180d));
        }
        g2d.fill(new Area(path));
        g2d.setColor(Color.BLACK);
        try(InputStream inputStream=new FileInputStream(new File("moon_incrust.png")))
        {
            BufferedImage moonIncrust=ImageIO.read(inputStream);
            WritableRaster sourceRaster=moonIncrust.getRaster();
            int[] pixelData=new int[4];
            for(int y=0;y<moonIncrust.getHeight();y++)
                for(int x=0;x<moonIncrust.getWidth();x++)
                {
                    pixelData=sourceRaster.getPixel(x,y,pixelData);
                    if(pixelData[0]!=0)
                        g2d.drawLine(148-63+x,y,148-63+x,y);
                }
        }
    }

    protected String getDebugImageFileName()
    {
        return "moon.png";
    }

    public static void main(String[] args)
    {
        new MoonPage(null).potentiallyUpdate();
    }
}
