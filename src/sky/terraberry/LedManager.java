package sky.terraberry;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class LedManager
{
    private static final GpioPinDigitalOutput BLUE_LED;
    private static final GpioPinDigitalOutput GREEN_LED;
    private static final GpioPinDigitalOutput ORANGE_LED;
    private static final GpioPinDigitalOutput RED_LED;

    static
    {
        if(System.getProperty("os.name").toLowerCase().contains("windows"))
        {
            BLUE_LED=null;
            GREEN_LED=null;
            ORANGE_LED=null;
            RED_LED=null;
        }
        else
        {
            GpioPinDigitalOutput blueLed=null;
            try
            {
                for(int i=0;i<10;i++)
                    try
                    {
                        blueLed=GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_03,PinState.LOW);
                        Logger.LOGGER.info("GPIO pin 3 opened");
                        break;
                    }
                    catch(RuntimeException e)
                    {
                        Logger.LOGGER.warn("Unable to open the GPIO pin 3");
                        Thread.sleep(Duration.of(200).millisecond());
                    }
            }
            catch(InterruptedException e)
            {
            }
            if(blueLed==null)
            {
                Logger.LOGGER.error("Unable to open the GPIO pin 3 after 10 attempts");
                System.exit(1);
            }
            BLUE_LED=blueLed;
            BLUE_LED.setShutdownOptions(Boolean.TRUE,PinState.LOW);
            GpioPinDigitalOutput greenLed=null;
            try
            {
                for(int i=0;i<10;i++)
                    try
                    {
                        greenLed=GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_04,PinState.LOW);
                        Logger.LOGGER.info("GPIO pin 4 opened");
                        break;
                    }
                    catch(RuntimeException e)
                    {
                        Logger.LOGGER.warn("Unable to open the GPIO pin 4");
                        Thread.sleep(Duration.of(200).millisecond());
                    }
            }
            catch(InterruptedException e)
            {
            }
            if(greenLed==null)
            {
                Logger.LOGGER.error("Unable to open the GPIO pin 4 after 10 attempts");
                System.exit(1);
            }
            GREEN_LED=greenLed;
            GREEN_LED.setShutdownOptions(Boolean.TRUE,PinState.LOW);
            GpioPinDigitalOutput orangeLed=null;
            try
            {
                for(int i=0;i<10;i++)
                    try
                    {
                        orangeLed=GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_21,PinState.LOW);
                        Logger.LOGGER.info("GPIO pin 21 opened");
                        break;
                    }
                    catch(RuntimeException e)
                    {
                        Logger.LOGGER.warn("Unable to open the GPIO pin 21");
                        Thread.sleep(Duration.of(200).millisecond());
                    }
            }
            catch(InterruptedException e)
            {
            }
            if(orangeLed==null)
            {
                Logger.LOGGER.error("Unable to open the GPIO pin 21 after 10 attempts");
                System.exit(1);
            }
            ORANGE_LED=orangeLed;
            ORANGE_LED.setShutdownOptions(Boolean.TRUE,PinState.LOW);
            GpioPinDigitalOutput redLed=null;
            try
            {
                for(int i=0;i<10;i++)
                    try
                    {
                        redLed=GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_22,PinState.LOW);
                        Logger.LOGGER.info("GPIO pin 22 opened");
                        break;
                    }
                    catch(RuntimeException e)
                    {
                        Logger.LOGGER.warn("Unable to open the GPIO pin 22");
                        Thread.sleep(Duration.of(200).millisecond());
                    }
            }
            catch(InterruptedException e)
            {
            }
            if(redLed==null)
            {
                Logger.LOGGER.error("Unable to open the GPIO pin 22 after 10 attempts");
                System.exit(1);
            }
            RED_LED=redLed;
            RED_LED.setShutdownOptions(Boolean.TRUE,PinState.LOW);
            Logger.LOGGER.info("GPIO pins successfully initialized");
        }
    }

    private LedManager()
    {
    }

    public static synchronized void setLed(LedColor ledColor)
    {
        try
        {
            LedColor oldLedColor;
            if(BLUE_LED.isHigh()&&GREEN_LED.isHigh()&&ORANGE_LED.isHigh()&&RED_LED.isHigh())
                oldLedColor=null;
            else
                if(BLUE_LED.isHigh())
                    oldLedColor=LedColor.BLUE;
                else
                    if(GREEN_LED.isHigh())
                        oldLedColor=LedColor.GREEN;
                    else
                        if(ORANGE_LED.isHigh())
                            oldLedColor=LedColor.ORANGE;
                        else
                            if(RED_LED.isHigh())
                                oldLedColor=LedColor.RED;
                            else
                                oldLedColor=null;
            if(ledColor==oldLedColor)
                return;//pas de changement à effectuer
            if(ledColor==LedColor.BLUE)
                if(oldLedColor==null)
                    changeLedFromAll(BLUE_LED);
                else
                    if(oldLedColor==LedColor.GREEN)
                        changeLedWithBlinking(GREEN_LED,BLUE_LED);
                    else
                        if(oldLedColor==LedColor.ORANGE)
                            changeLed(ORANGE_LED,BLUE_LED);
                        else
                            if(oldLedColor==LedColor.RED)
                                changeLed(RED_LED,BLUE_LED);
                            else//en théorie ce cas n'arrive jamais
                                legacyChangeLed(BLUE_LED);
            else
                if(ledColor==LedColor.GREEN)
                    if(oldLedColor==null)
                        changeLedFromAll(GREEN_LED);
                    else
                        if(oldLedColor==LedColor.BLUE)
                            changeLed(BLUE_LED,GREEN_LED);
                        else
                            if(oldLedColor==LedColor.ORANGE)
                                changeLed(ORANGE_LED,GREEN_LED);
                            else
                                if(oldLedColor==LedColor.RED)
                                    changeLed(RED_LED,GREEN_LED);
                                else//en théorie ce cas n'arrive jamais
                                    legacyChangeLed(GREEN_LED);
                else
                    if(ledColor==LedColor.ORANGE)
                        if(oldLedColor==null)
                            changeLedFromAll(ORANGE_LED);
                        else
                            if(oldLedColor==LedColor.BLUE)
                                changeLed(BLUE_LED,ORANGE_LED);
                            else
                                if(oldLedColor==LedColor.GREEN)
                                    changeLedWithBlinking(GREEN_LED,ORANGE_LED);
                                else
                                    if(oldLedColor==LedColor.RED)
                                        changeLed(RED_LED,ORANGE_LED);
                                    else//en théorie ce cas n'arrive jamais
                                        legacyChangeLed(ORANGE_LED);
                    else
                        if(ledColor==LedColor.RED)
                            if(oldLedColor==null)
                                changeLedFromAll(RED_LED);
                            else
                                if(oldLedColor==LedColor.BLUE)
                                    changeLed(BLUE_LED,RED_LED);
                                else
                                    if(oldLedColor==LedColor.GREEN)
                                        changeLedWithBlinking(GREEN_LED,RED_LED);
                                    else
                                        if(oldLedColor==LedColor.ORANGE)
                                            changeLedWithBlinking(ORANGE_LED,RED_LED);
                                        else//en théorie ce cas n'arrive jamais
                                            legacyChangeLed(RED_LED);
                        else//en théorie ce cas n'arrive jamais
                            legacyChangeLed(null);
        }
        catch(InterruptedException e)
        {
        }
    }

    private static void changeLed(GpioPinDigitalOutput fromLed,GpioPinDigitalOutput toLed) throws InterruptedException
    {
        toLed.high();
        Thread.sleep(Duration.of(500).millisecond());
        fromLed.low();
    }

    private static void changeLedWithBlinking(GpioPinDigitalOutput fromLed,GpioPinDigitalOutput toLed) throws InterruptedException
    {
        toLed.high();
        Thread.sleep(Duration.of(500).millisecond());
        fromLed.low();
        Thread.sleep(Duration.of(500).millisecond());
        toLed.low();
        Thread.sleep(Duration.of(500).millisecond());
        toLed.high();
        Thread.sleep(Duration.of(500).millisecond());
        toLed.low();
        Thread.sleep(Duration.of(500).millisecond());
        toLed.high();
        Thread.sleep(Duration.of(500).millisecond());
        toLed.low();
        Thread.sleep(Duration.of(500).millisecond());
        toLed.high();
        Thread.sleep(Duration.of(500).millisecond());
        toLed.low();
        Thread.sleep(Duration.of(500).millisecond());
        toLed.high();
    }

    private static void changeLedFromAll(GpioPinDigitalOutput toLed) throws InterruptedException
    {
        BLUE_LED.low();
        GREEN_LED.low();
        ORANGE_LED.low();
        RED_LED.low();
        Thread.sleep(Duration.of(1).second());
        toLed.high();
    }

    private static void legacyChangeLed(GpioPinDigitalOutput toLed) throws InterruptedException
    {
        if(toLed==null)
        {
            if(BLUE_LED.isHigh())
                BLUE_LED.low();
            if(GREEN_LED.isHigh())
                GREEN_LED.low();
            if(ORANGE_LED.isHigh())
                ORANGE_LED.low();
            if(RED_LED.isHigh())
                RED_LED.low();
            Thread.sleep(Duration.of(1).second());
            BLUE_LED.high();
            GREEN_LED.high();
            ORANGE_LED.high();
            RED_LED.high();
            return;
        }
        if(toLed==BLUE_LED)
            BLUE_LED.high();
        else
            BLUE_LED.low();
        if(toLed==GREEN_LED)
            GREEN_LED.high();
        else
            GREEN_LED.low();
        if(toLed==ORANGE_LED)
            ORANGE_LED.high();
        else
            ORANGE_LED.low();
        if(toLed==RED_LED)
            RED_LED.high();
        else
            RED_LED.low();
    }
}
