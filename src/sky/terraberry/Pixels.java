package sky.terraberry;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class Pixels
{
    private final Pixel[][] pixels;

    public Pixels()
    {
        pixels=new Pixel[EpaperScreenManager.getEpaperScreenSize().getLittleWidth()][EpaperScreenManager.getEpaperScreenSize().getBigHeight()];
        for(int j=0;j<EpaperScreenManager.getEpaperScreenSize().getLittleWidth();j++)
            for(int i=0;i<EpaperScreenManager.getEpaperScreenSize().getBigHeight();i++)
                pixels[j][i]=Pixel.WHITE;
    }

    public Pixels writeImage(BufferedImage image)
    {
        int[] sourcePixel=new int[4];
        WritableRaster sourceRaster=image.getRaster();
        for(int x=0;x<image.getWidth();x++)
            for(int y=0;y<image.getHeight();y++)
                pixels[EpaperScreenManager.getEpaperScreenSize().getLittleWidth()-1-y][x]=(sourcePixel=sourceRaster.getPixel(x,y,sourcePixel))[1]==128?Pixel.TRANSPARENT:sourcePixel[1]>0?Pixel.WHITE:Pixel.BLACK;
        return this;
    }

    public Pixels incrustTransparentImage(Pixels image)
    {
        Pixels newPixels=new Pixels();
        for(int j=0;j<EpaperScreenManager.getEpaperScreenSize().getLittleWidth();j++)
            for(int i=0;i<EpaperScreenManager.getEpaperScreenSize().getBigHeight();i++)
                if(image.pixels[j][i]!=Pixel.TRANSPARENT)
                    newPixels.pixels[j][i]=image.pixels[j][i];
                else
                    newPixels.pixels[j][i]=pixels[j][i];
        return newPixels;
    }

    public Pixel getPixel(int i,int j)
    {
        return pixels[i][j];
    }

    public boolean isIOk(int i)
    {
        return i<pixels.length;
    }
}
