package sky.terraberry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TestEpaper
{
    public static void main(String[] args)
    {
        try
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
        catch(Exception e)
        {
            Logger.LOGGER.error("Unknown error ("+e.toString()+")");
        }
    }
}
