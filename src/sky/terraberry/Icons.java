package sky.terraberry;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Icons
{
    public static final Image NULL_IMAGE;
    public static final Image CLEAR_DAY_IMAGE;
    public static final Image CLEAR_NIGHT_IMAGE;
    public static final Image RAIN_IMAGE;
    public static final Image SNOW_IMAGE;
    public static final Image SLEET_IMAGE;
    public static final Image WIND_IMAGE;
    public static final Image FOG_IMAGE;
    public static final Image CLOUDY_IMAGE;
    public static final Image PARTLY_CLOUDY_DAY_IMAGE;
    public static final Image PARTLY_CLOUDY_NIGHT_IMAGE;

    static
    {
        Image image=null;
        try(InputStream inputStream=new FileInputStream(new File("null.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon null");
        }
        NULL_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("clear-day.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon clear-day");
        }
        CLEAR_DAY_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("clear-night.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon clear-night");
        }
        CLEAR_NIGHT_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("rain.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon rain");
        }
        RAIN_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("snow.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon snow");
        }
        SNOW_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("sleet.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon sleet");
        }
        SLEET_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("wind.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon wind");
        }
        WIND_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("fog.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon fog");
        }
        FOG_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("cloudy.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon cloudy");
        }
        CLOUDY_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("partly-cloudy-day.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon partly-cloudy-day");
        }
        PARTLY_CLOUDY_DAY_IMAGE=image;
        image=null;
        try(InputStream inputStream=new FileInputStream(new File("partly-cloudy-night.png")))
        {
            image=ImageIO.read(inputStream);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Error when reading icon partly-cloudy-night");
        }
        PARTLY_CLOUDY_NIGHT_IMAGE=image;
    }

    private Icons()
    {
    }

    public static Image getIcon(String icon)
    {
        if(icon.equals("clear-day"))
            return CLEAR_DAY_IMAGE;
        else
            if(icon.equals("clear-night"))
                return CLEAR_NIGHT_IMAGE;
            else
                if(icon.equals("rain"))
                    return RAIN_IMAGE;
                else
                    if(icon.equals("snow"))
                        return SNOW_IMAGE;
                    else
                        if(icon.equals("sleet"))
                            return SLEET_IMAGE;
                        else
                            if(icon.equals("wind"))
                                return WIND_IMAGE;
                            else
                                if(icon.equals("fog"))
                                    return FOG_IMAGE;
                                else
                                    if(icon.equals("cloudy"))
                                        return CLOUDY_IMAGE;
                                    else
                                        if(icon.equals("partly-cloudy-day"))
                                            return PARTLY_CLOUDY_DAY_IMAGE;
                                        else
                                            if(icon.equals("partly-cloudy-night"))
                                                return PARTLY_CLOUDY_NIGHT_IMAGE;
                                            else
                                                return NULL_IMAGE;
    }
}
