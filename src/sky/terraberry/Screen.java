package sky.terraberry;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import sky.program.Duration;

public class Screen
{
    private PixelMatrix workingPixelMatrix;
    private PixelMatrix validatedPixelMatrix;
    private int modificationCount;
    private final Object lockObject;
    private static final List<PixelMatrix> PIXEL_MATRIX_RECYCLE_BIN=new ArrayList<>();

    public Screen()
    {
        workingPixelMatrix=generatePixelMatrix();
        workingPixelMatrix.addReferer(this);
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
            if(validatedPixelMatrix!=null)
            {
                validatedPixelMatrix.removeReferer(this);
                recyclePixelMatrix(validatedPixelMatrix);
            }
            validatedPixelMatrix=workingPixelMatrix;
            workingPixelMatrix=generatePixelMatrix();
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
            if(validatedPixelMatrix!=null)
            {
                validatedPixelMatrix.removeReferer(this);
                recyclePixelMatrix(validatedPixelMatrix);
            }
            validatedPixelMatrix=workingPixelMatrix;
            workingPixelMatrix=generatePixelMatrix();
        }
        return this;
    }

    public Screen setImage(BufferedImage image)
    {
        synchronized(lockObject)
        {
            workingPixelMatrix.setImage(image);
            if(validatedPixelMatrix!=null)
            {
                validatedPixelMatrix.removeReferer(this);
                recyclePixelMatrix(validatedPixelMatrix);
            }
            validatedPixelMatrix=workingPixelMatrix;
            modificationCount++;
            workingPixelMatrix=generatePixelMatrix();
        }
        return this;
    }

    public Screen setContentWithIncrust(Screen content,Screen incrust)
    {
        synchronized(lockObject)
        {
            workingPixelMatrix.setContentWithIncrust(content.getPixelMatrix(),incrust.getPixelMatrix());
            recyclePixelMatrix(validatedPixelMatrix);
            validatedPixelMatrix=workingPixelMatrix;
            modificationCount++;
            workingPixelMatrix=generatePixelMatrix();
        }
        return this;
    }

    public Screen setTransparent()
    {
        synchronized(lockObject)
        {
            workingPixelMatrix.initializeTransparent();
            recyclePixelMatrix(validatedPixelMatrix);
            validatedPixelMatrix=workingPixelMatrix;
            modificationCount++;
            workingPixelMatrix=generatePixelMatrix();
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

    private static PixelMatrix generatePixelMatrix()
    {
        synchronized(PIXEL_MATRIX_RECYCLE_BIN)
        {
            int index=0;
            while(index<PIXEL_MATRIX_RECYCLE_BIN.size())
            {
                if(!PIXEL_MATRIX_RECYCLE_BIN.get(index).isReferenced()||PIXEL_MATRIX_RECYCLE_BIN.get(index).isReferenced()&&System.currentTimeMillis()-PIXEL_MATRIX_RECYCLE_BIN.get(index).getLastRead()>Duration.of(2).hour())//un délai de deux heures permet de prévoir large par rapport au taux de rafraîchissement des pages les plus lentes
                {
                    PixelMatrix removed=PIXEL_MATRIX_RECYCLE_BIN.remove(index);
                    System.out.println("There is now "+PIXEL_MATRIX_RECYCLE_BIN.size()+" pixel matrices in the recycle bin (-1)");
                    return removed;
                }
                index++;
            }
            return new PixelMatrix();
        }
    }

    private static void recyclePixelMatrix(PixelMatrix pixelMatrix)
    {
        synchronized(PIXEL_MATRIX_RECYCLE_BIN)
        {
            PIXEL_MATRIX_RECYCLE_BIN.add(pixelMatrix);
            System.out.println("There is now "+PIXEL_MATRIX_RECYCLE_BIN.size()+" pixel matrices in the recycle bin (+1)");
        }
    }
}
