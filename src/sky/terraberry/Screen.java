package sky.terraberry;

import java.awt.image.BufferedImage;

public class Screen
{
    private PixelMatrix workingPixelMatrix;
    private PixelMatrix validatedPixelMatrix;
    private int modificationCount;
    private final Object lockObject;

    public Screen()
    {
        workingPixelMatrix=new PixelMatrix();
        validatedPixelMatrix=null;
        modificationCount=0;
        lockObject=new Object();
    }

    public Screen initializeBlank()
    {
        if(modificationCount!=0)
            throw new IllegalStateException("Can't initialize a live screen");
        synchronized(lockObject)
        {
            workingPixelMatrix.initializeBlank();
            validatedPixelMatrix=workingPixelMatrix;
            workingPixelMatrix=new PixelMatrix();
        }
        return this;
    }

    public Screen initializeTransparent()
    {
        if(modificationCount!=0)
            throw new IllegalStateException("Can't initialize a live screen");
        synchronized(lockObject)
        {
            workingPixelMatrix.initializeTransparent();
            validatedPixelMatrix=workingPixelMatrix;
            workingPixelMatrix=new PixelMatrix();
        }
        return this;
    }

    public Screen setImage(BufferedImage image)
    {
        synchronized(lockObject)
        {
            workingPixelMatrix.setImage(image);
            validatedPixelMatrix=null;
            validatedPixelMatrix=workingPixelMatrix;
            modificationCount++;
            workingPixelMatrix=new PixelMatrix();
        }
        return this;
    }

    public Screen setContentWithIncrust(Screen content,Screen incrust)
    {
        synchronized(lockObject)
        {
            workingPixelMatrix.setContentWithIncrust(content.getPixelMatrix(),incrust.getPixelMatrix());
            validatedPixelMatrix=null;
            validatedPixelMatrix=workingPixelMatrix;
            modificationCount++;
            workingPixelMatrix=new PixelMatrix();
        }
        return this;
    }

    public Screen setTransparent()
    {
        synchronized(lockObject)
        {
            workingPixelMatrix.initializeTransparent();
            validatedPixelMatrix=null;
            validatedPixelMatrix=workingPixelMatrix;
            modificationCount++;
            workingPixelMatrix=new PixelMatrix();
        }
        return this;
    }

    public PixelMatrix getPixelMatrix()
    {
        synchronized(lockObject)
        {
            return validatedPixelMatrix;
        }
    }

    public int getModificationCount()
    {
        synchronized(lockObject)
        {
            return modificationCount;
        }
    }
}
