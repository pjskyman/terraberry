package sky.terraberry;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.ArrayList;
import java.util.List;

public class SwitchManager
{
    private static final GpioPinDigitalInput SWITCH1;
    private static final GpioPinDigitalInput SWITCH2;
    private static final List<SwitchListener> SWITCH_LISTENERS1;
    private static final List<SwitchListener> SWITCH_LISTENERS2;

    static
    {
        GpioPinDigitalInput zwitch1=null;
        try
        {
            zwitch1=GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_02);
            zwitch1.addListener(new GpioPinListenerDigital()
            {
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
                {
//                    System.out.println("click "+event.getState()+" "+System.currentTimeMillis());
                    if(event.getState()==PinState.HIGH)
                        SWITCH_LISTENERS1.forEach(SwitchListener::switched);
                }
            });
        }
        catch(Exception e)
        {
            Logger.LOGGER.error("Unable to get switch 1 pin ("+e.toString()+")");
            e.printStackTrace();
            System.exit(1);
        }
        SWITCH1=zwitch1;
        SWITCH_LISTENERS1=new ArrayList<>();
        GpioPinDigitalInput zwitch2=null;
        try
        {
            zwitch2=GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_07);//TODO à vérifier
            zwitch2.addListener(new GpioPinListenerDigital()
            {
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
                {
//                    System.out.println("click "+event.getState()+" "+System.currentTimeMillis());
                    if(event.getState()==PinState.HIGH)
                        SWITCH_LISTENERS2.forEach(SwitchListener::switched);
                }
            });
        }
        catch(Exception e)
        {
            Logger.LOGGER.error("Unable to get switch 2 pin ("+e.toString()+")");
            e.printStackTrace();
            System.exit(1);
        }
        SWITCH2=zwitch2;
        SWITCH_LISTENERS2=new ArrayList<>();
    }

    public static void addSwitchListener1(SwitchListener switchListener)
    {
        SWITCH_LISTENERS1.add(switchListener);
    }

    public static void addSwitchListener2(SwitchListener switchListener)
    {
        SWITCH_LISTENERS2.add(switchListener);
    }
}
