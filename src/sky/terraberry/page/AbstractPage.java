package sky.terraberry.page;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import sky.terraberry.Logger;
import sky.terraberry.Screen;

public abstract class AbstractPage implements Page
{
    protected final Screen screen;
    protected final Page parentPage;
    public static final Font FREDOKA_ONE_FONT;
    public static final Font BALOO_FONT;

    static
    {
        Font font=null;
        try(FileInputStream inputStream=new FileInputStream(new File("FredokaOne-Regular.ttf")))
        {
            font=Font.createFont(Font.TRUETYPE_FONT,inputStream);
            Logger.LOGGER.info("Font loaded successfully");
        }
        catch(IOException|FontFormatException e)
        {
            Logger.LOGGER.error("Unable to load the font ("+e.toString()+")");
            e.printStackTrace();
        }
        FREDOKA_ONE_FONT=font;
        font=null;
        try(FileInputStream inputStream=new FileInputStream(new File("Baloo-Regular.ttf")))
        {
            font=Font.createFont(Font.TRUETYPE_FONT,inputStream);
            Logger.LOGGER.info("Font loaded successfully");
        }
        catch(IOException|FontFormatException e)
        {
            Logger.LOGGER.error("Unable to load the font ("+e.toString()+")");
        }
        BALOO_FONT=font;
    }

    protected AbstractPage(Page parentPage)
    {
        screen=new Screen();
        this.parentPage=parentPage;
    }

    public Page getParentPage()
    {
        return parentPage;
    }

    public Screen getScreen()
    {
        return screen;
    }
}
