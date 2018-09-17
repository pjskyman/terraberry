package sky.terraberry;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Terraberry
{
    private static int currentPage=0;
    private static int currentlySelectedPage=-1;
    public static final Font FONT;

    static
    {
        Font font=null;
        try(FileInputStream inputStream=new FileInputStream(new File("Baloo-Regular.ttf")))
        {
            font=Font.createFont(Font.TRUETYPE_FONT,inputStream);
            Logger.LOGGER.info("Font loaded successfully");
        }
        catch(IOException|FontFormatException e)
        {
            Logger.LOGGER.error("Unable to load the font ("+e.toString()+")");
        }
        FONT=font;
    }

    private Terraberry()
    {
    }

    public static void main(String[] args)
    {
        Logger.LOGGER.info("Starting "+Terraberry.class.getSimpleName()+"...");
        try
        {
            List<Page> pages=new ArrayList<>();
            pages.add(new NextTrainPage().potentiallyUpdate());
            pages.add(new WeatherPage().potentiallyUpdate());
            Pixels currentPixels=pages.get(0).potentiallyUpdate().getPixels();
            long lastCompleteRefresh=System.currentTimeMillis();
            new Thread("ledUpdater")
            {
                @Override
                public void run()
                {
                    try
                    {
                        LedManager.setLed(null);
                        Thread.sleep(Duration.of(2).second());
                        while(true)
                        {
                            double temperature=ThermometerManager.getTemperature();
                            if(temperature<20.5d)
                                LedManager.setLed(LedColor.BLUE);
                            else
                                if(temperature<21.5d)
                                    LedManager.setLed(LedColor.GREEN);
                                else
                                    if(temperature<22.5d)
                                        LedManager.setLed(LedColor.ORANGE);
                                    else
                                        LedManager.setLed(LedColor.RED);
                            Thread.sleep(Duration.of(2).secondPlus(479).millisecond());
                        }
                    }
                    catch(InterruptedException e)
                    {
                    }
                }
            }.start();
            totalRefresh();
            EpaperScreen213Manager.displayPage(currentPixels,true,false);
            Logger.LOGGER.info("Display content successfully updated from page "+pages.get(0).getSerial()+" (total refresh)");
            Logger.LOGGER.info(Terraberry.class.getSimpleName()+" is ready!");
            new Thread("pageUpdater")
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(Duration.of(1).second());
                        while(true)
                        {
                            try
                            {
                                pages.forEach(Page::potentiallyUpdate);
                            }
                            catch(Throwable t)
                            {
                                Logger.LOGGER.error("Unmanaged throwable during refresh ("+t.toString()+")");
                            }
                            Thread.sleep(Duration.of(100).millisecond());
                        }
                    }
                    catch(InterruptedException e)
                    {
                    }
                }
            }.start();
            new Thread("pageSelector")
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(Duration.of(15).second());
                        while(true)
                        {
                            currentPage=(currentPage+1)%2;
                            Thread.sleep(Duration.of(10).second());
                        }
                    }
                    catch(InterruptedException e)
                    {
                    }
                }
            }.start();
            try
            {
                int lastDrawnIncrust=currentlySelectedPage;
                while(true)
                {
                    int currentPageCopy=currentPage;
                    int currentlySelectedPageCopy=currentlySelectedPage;
                    Pixels newPixels=pages.get(currentPageCopy).getPixels();
                    if(newPixels!=currentPixels||currentlySelectedPageCopy!=lastDrawnIncrust)
                    {
                        long now=System.currentTimeMillis();
                        boolean partialRefresh=true;
                        if(currentlySelectedPageCopy==-1&&now-lastCompleteRefresh>Duration.of(5).minute())
                        {
                            partialRefresh=false;
                            lastCompleteRefresh=now;
                        }
                        Pixels pixels=currentlySelectedPageCopy==-1?newPixels:newPixels.incrustTransparentImage(new IncrustGenerator(pages.get(currentlySelectedPageCopy)).generateIncrust());
                        boolean fastMode=false;
                        if(!partialRefresh)
                            totalRefresh();
                        else
                            EpaperScreen213Manager.displayPage(pixels,partialRefresh,fastMode);
                        Logger.LOGGER.info("Display content successfully updated from page "+pages.get(currentPageCopy).getSerial()+" ("+(partialRefresh?"partial":"total")+" refresh)");
                        currentPixels=newPixels;
                        lastDrawnIncrust=currentlySelectedPageCopy;
                    }
                    Thread.sleep(Duration.of(50).millisecond());
                }
            }
            catch(InterruptedException e)
            {
            }
        }
        catch(Exception e)
        {
            Logger.LOGGER.error("Unknown error ("+e.toString()+")");
        }
    }

    /**
     * Unfortunately, our screen has a problem and can't make proper
     * total-refresh anymore. Here is an alternative method to clear the screen
     * quite finely. If you are discovering this code, please use the regular
     * total-refresh with the correct boolean value passed to the method
     * EpaperScreen213Manager.displayPage(...).
     */
    private static void totalRefresh()
    {
        int count=0;
        for(int i=0;i<2;i++)
        {
            BufferedImage image=new BufferedImage(250,128,BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g2d=image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0,0,250,128);
            g2d.setColor(Color.BLACK);
            int currentY=count%128;
            count+=32;
            for(int j=0;j<32;j++)
                g2d.drawLine(0,currentY+j,250,currentY+j);
            g2d.dispose();
            EpaperScreen213Manager.displayPage(new Pixels().writeImage(image),true,false);
        }
        for(int i=0;i<4;i++)
        {
            BufferedImage image=new BufferedImage(250,128,BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g2d=image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0,0,250,128);
            g2d.setColor(Color.BLACK);
            int currentY=count%128;
            count+=16;
            for(int j=0;j<16;j++)
                g2d.drawLine(0,currentY+j,250,currentY+j);
            g2d.dispose();
            EpaperScreen213Manager.displayPage(new Pixels().writeImage(image),true,false);
        }
        EpaperScreen213Manager.displayPage(new Pixels(Pixel.WHITE),true,false);
    }
}
