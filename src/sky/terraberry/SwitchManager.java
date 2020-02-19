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
    private static final GpioPinDigitalInput SWITCH;
    private static final List<SwitchListener> SWITCH_LISTENERS;

    static
    {
        GpioPinDigitalInput zwitch=null;
        try
        {
            zwitch=GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_02);
            zwitch.addListener(new GpioPinListenerDigital()
            {
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
                {
//                    System.out.println("click "+event.getState()+" "+System.currentTimeMillis());
                    if(event.getState()==PinState.HIGH)
                        SWITCH_LISTENERS.forEach(SwitchListener::switched);
                }
            });
        }
        catch(Exception e)
        {
            Logger.LOGGER.error("Unable to get click pin ("+e.toString()+")");
            e.printStackTrace();
            System.exit(1);
        }
        SWITCH=zwitch;
        SWITCH_LISTENERS=new ArrayList<>();
    }

    public static void addSwitchListener(SwitchListener switchListener)
    {
        SWITCH_LISTENERS.add(switchListener);
    }
}
