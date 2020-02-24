package sky.terraberry;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.ArrayList;
import java.util.List;
import sky.program.Duration;

public class SwitchManager
{
    private static final GpioPinDigitalInput SWITCH1;
    private static final GpioPinDigitalInput SWITCH2;
    private static final List<SwitchListener> SWITCH1_LISTENERS;
    private static final List<SwitchListener> SWITCH2_LISTENERS;

    static
    {
        GpioPinDigitalInput switch1=null;
        try
        {
            for(int i=0;i<10;i++)
                try
                {
                    switch1=GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_02);
                    switch1.addListener(new GpioPinListenerDigital()
                    {
                        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
                        {
//                            System.out.println("click "+event.getState()+" "+System.currentTimeMillis());
                            if(event.getState()==PinState.HIGH)
                                SWITCH1_LISTENERS.forEach(SwitchListener::switched);
                        }
                    });
                    break;
                }
                catch(RuntimeException e)
                {
                    Logger.LOGGER.error("Unable to get switch 1 pin ("+e.toString()+")");
                    Thread.sleep(Duration.of(200).millisecond());
                }
        }
        catch(InterruptedException e)
        {
        }
        if(switch1==null)
        {
            Logger.LOGGER.error("Unable to open the GPIO pin 2 after 10 attempts");
            System.exit(1);
        }
        SWITCH1=switch1;
        SWITCH1_LISTENERS=new ArrayList<>();
        GpioPinDigitalInput switch2=null;
        try
        {
            for(int i=0;i<10;i++)
                try
                {
                    switch2=GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_07);//TODO à vérifier, et accorder avec la sortie log ci-dessous
                    switch2.addListener(new GpioPinListenerDigital()
                    {
                        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
                        {
//                            System.out.println("click "+event.getState()+" "+System.currentTimeMillis());
                            if(event.getState()==PinState.HIGH)
                                SWITCH2_LISTENERS.forEach(SwitchListener::switched);
                        }
                    });
                    break;
                }
                catch(RuntimeException e)
                {
                    Logger.LOGGER.error("Unable to get switch 2 pin ("+e.toString()+")");
                    Thread.sleep(Duration.of(200).millisecond());
                }
        }
        catch(InterruptedException e)
        {
        }
        if(switch2==null)
        {
            Logger.LOGGER.error("Unable to open the GPIO pin 7 after 10 attempts");
            System.exit(1);
        }
        SWITCH2=switch2;
        SWITCH2_LISTENERS=new ArrayList<>();
    }

    public static void addSwitch1Listener(SwitchListener switchListener)
    {
        SWITCH1_LISTENERS.add(switchListener);
    }

    public static void addSwitch2Listener(SwitchListener switchListener)
    {
        SWITCH2_LISTENERS.add(switchListener);
    }
}
