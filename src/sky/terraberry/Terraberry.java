package sky.terraberry;

import java.awt.Font;
import java.awt.FontFormatException;
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
            System.out.println("Font loaded successfully");
        }
        catch(IOException|FontFormatException e)
        {
            System.err.println("Unable to load the font ("+e.toString()+")");
        }
        FONT=font;
    }

    private Terraberry()
    {
    }

    public static void main(String[] args)
    {
        EpaperScreenManager.setEpaperScreenSize(EpaperScreenSize._2_13);
        System.out.println("Starting "+Terraberry.class.getSimpleName()+"...");
        try
        {
            List<Page> pages=new ArrayList<>();
            pages.add(new NextTrainPage().potentiallyUpdate());
            pages.sort((o1,o2)->Integer.compare(o1.getSerial(),o2.getSerial()));//au cas où...
            Pixels currentPixels=pages.get(0).potentiallyUpdate().getPixels();
            long lastCompleteRefresh=System.currentTimeMillis();
            EpaperScreenManager.displayPage(currentPixels,false,false);
            System.out.println("Display content successfully updated from page "+pages.get(0).getSerial()+" (total refresh)");
            System.out.println(Terraberry.class.getSimpleName()+" is ready!");
            new Thread("pageUpdater")
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(Time.get(1).second());
                        while(true)
                        {
                            try
                            {
                                pages.forEach(page->page.potentiallyUpdate());
                            }
                            catch(Throwable t)
                            {
                                System.err.println("Unmanaged throwable during refresh ("+t.toString()+")");
                            }
                            Thread.sleep(Time.get(100).millisecond());
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
                        if(currentlySelectedPageCopy==-1&&now-lastCompleteRefresh>Time.get(30).minute())
                        {
                            partialRefresh=false;
                            lastCompleteRefresh=now;
                        }
                        Pixels pixels=currentlySelectedPageCopy==-1?newPixels:newPixels.incrustTransparentImage(new IncrustGenerator(pages.get(currentlySelectedPageCopy)).generateIncrust());
                        boolean fastMode=false;
                        EpaperScreenManager.displayPage(pixels,partialRefresh,fastMode);
                        System.out.println("Display content successfully updated from page "+pages.get(currentPageCopy).getSerial()+" ("+(partialRefresh?"partial":"total")+" refresh)");
                        currentPixels=newPixels;
                        lastDrawnIncrust=currentlySelectedPageCopy;
                    }
                    Thread.sleep(Time.get(50).millisecond());
                }
            }
            catch(InterruptedException e)
            {
            }
        }
        catch(Exception e)
        {
            System.err.println("Unknown error ("+e.toString()+")");
        }
    }
}
