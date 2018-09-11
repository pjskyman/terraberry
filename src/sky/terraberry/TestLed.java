package sky.terraberry;

public class TestLed
{
    private TestLed()
    {
    }

    public static void main(String[] args)
    {
        LedColor mode=null;
        if(args.length>=1)
            if(Integer.parseInt(args[0])==1)
                mode=LedColor.BLUE;
            else
                if(Integer.parseInt(args[0])==2)
                    mode=LedColor.GREEN;
                else
                    if(Integer.parseInt(args[0])==3)
                        mode=LedColor.ORANGE;
                    else
                        if(Integer.parseInt(args[0])==4)
                            mode=LedColor.RED;
        Logger.LOGGER.info("Starting "+TestLed.class.getSimpleName()+"...");
        try
        {
            while(true)
            {
                if(mode==null||mode==LedColor.BLUE)
                {
                    LedManager.setLed(LedColor.BLUE);
                    System.out.println(ThermometerManager.getTemperature()+" °C");
                    Thread.sleep(Duration.of(1).second());
                }
                if(mode==null||mode==LedColor.GREEN)
                {
                    LedManager.setLed(LedColor.GREEN);
                    System.out.println(ThermometerManager.getTemperature()+" °C");
                    Thread.sleep(Duration.of(1).second());
                }
                if(mode==null||mode==LedColor.ORANGE)
                {
                    LedManager.setLed(LedColor.ORANGE);
                    System.out.println(ThermometerManager.getTemperature()+" °C");
                    Thread.sleep(Duration.of(1).second());
                }
                if(mode==null||mode==LedColor.RED)
                {
                    LedManager.setLed(LedColor.RED);
                    System.out.println(ThermometerManager.getTemperature()+" °C");
                    Thread.sleep(Duration.of(1).second());
                }
            }
        }
        catch(Exception e)
        {
            Logger.LOGGER.error("Unknown error ("+e.toString()+")");
            e.printStackTrace();
        }
    }
}
