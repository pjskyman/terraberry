package sky.terraberry;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public abstract class AbstractPage implements Page
{
    protected Pixels pixels;
    protected static final DecimalFormat TEMPERATURE_FORMAT=new DecimalFormat("###0.0");
    protected static final DecimalFormat WIND_FORMAT=new DecimalFormat("###0");
    protected static final DecimalFormat ENERGY_FORMAT=new DecimalFormat("###0.000");
    protected static final DecimalFormat PRICE_FORMAT=new DecimalFormat("###0.00");
    protected static final DateFormat TIME_FORMAT=new SimpleDateFormat("HH:mm");

    protected AbstractPage()
    {
        pixels=new Pixels();
    }
}
