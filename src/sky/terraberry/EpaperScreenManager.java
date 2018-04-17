package sky.terraberry;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;
import java.io.IOException;

public class EpaperScreenManager
{
    private static final SpiDevice DEVICE;
    private static final GpioPinDigitalOutput RESET;
    private static final GpioPinDigitalOutput DC;
    private static final GpioPinDigitalInput BUSY;
    private static EpaperScreenSize epaperScreenSize;

    static
    {
        if(System.getProperty("os.name").toLowerCase().contains("windows"))
        {
            DEVICE=null;
            RESET=null;
            DC=null;
            BUSY=null;
        }
        else
        {
            SpiDevice device=null;
            try
            {
                device=SpiFactory.getInstance(SpiChannel.CS0,2000000,SpiMode.MODE_0);
            }
            catch(IOException e)
            {
                Logger.LOGGER.error("Unable to get SPI device ("+e.toString()+")");
                System.exit(1);
            }
            DEVICE=device;
            Logger.LOGGER.info("SPI device successfully initialized");
            RESET=GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_00,PinState.HIGH);
            DC=GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_06,PinState.LOW);
            DC.setShutdownOptions(Boolean.TRUE,PinState.LOW);
            BUSY=GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_05);
//            BUSY.addListener(new GpioPinListenerDigital()
//            {
//                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
//                {
//                    Logger.LOGGER.debug("BUSY is now "+event.getState());
//                }
//            });
            Logger.LOGGER.info("GPIO pins successfully initialized");
        }
        epaperScreenSize=EpaperScreenSize._2_9;
    }

    private EpaperScreenManager()
    {
    }

    public static EpaperScreenSize getEpaperScreenSize()
    {
        return epaperScreenSize;
    }

    public static void setEpaperScreenSize(EpaperScreenSize epaperScreenSize)
    {
        EpaperScreenManager.epaperScreenSize=epaperScreenSize;
    }

    public static synchronized void displayPage(Pixels pixels,boolean partialMode,boolean fastMode)
    {
        try
        {
            RESET.low();
            Thread.sleep(Time.get(150).millisecond());
            RESET.high();
            Thread.sleep(Time.get(150).millisecond());
            DC.low();
            DEVICE.write((byte)0x01);//DRIVER_OUTPUT_CONTROL
            DC.high();
            DEVICE.write((byte)((epaperScreenSize.getBigHeight()-1)&0xFF),(byte)(((epaperScreenSize.getBigHeight()-1)>>8)&0xFF),(byte)0x00);
            DC.low();
            DEVICE.write((byte)0x0C);//BOOSTER_SOFT_START_CONTROL
            DC.high();
            DEVICE.write((byte)0xD7,(byte)0xD6,(byte)0x9D);
            DC.low();
            DEVICE.write((byte)0x2C);//WRITE_VCOM_REGISTER
            DC.high();
            DEVICE.write((byte)0xA8);
            DC.low();
            DEVICE.write((byte)0x3A);//SET_DUMMY_LINE_PERIOD
            DC.high();
            DEVICE.write((byte)0x1A);
            DC.low();
            DEVICE.write((byte)0x3B);//SET_GATE_TIME
            DC.high();
            DEVICE.write((byte)0x08);
            DC.low();
            DEVICE.write((byte)0x11);//DATA_ENTRY_MODE_SETTING
            DC.high();
            DEVICE.write((byte)0x03);
            DC.low();
            DEVICE.write((byte)0x32);//WRITE_LUT_REGISTER
            DC.high();
            DEVICE.write(partialMode?epaperScreenSize.getPartialRefreshLookUpTable():epaperScreenSize.getTotalRefreshLookUpTable());
            DC.low();
            DEVICE.write((byte)0x44);//SET_RAM_X_ADDRESS_START_END_POSITION
            DC.high();
            DEVICE.write((byte)0x00,(byte)(((epaperScreenSize.getLittleWidth()-1)>>3)&0xFF));
            DC.low();
            DEVICE.write((byte)0x45);//SET_RAM_Y_ADDRESS_START_END_POSITION
            DC.high();
            DEVICE.write((byte)0x00,(byte)0x00,(byte)((epaperScreenSize.getBigHeight()-1)&0xFF),(byte)(((epaperScreenSize.getBigHeight()-1)>>8)&0xFF));
            byte b=(byte)0x00;
            for(int j=0;j<epaperScreenSize.getBigHeight();j++)
            {
                DC.low();
                DEVICE.write((byte)0x4E);//SET_RAM_X_ADDRESS_COUNTER
                DC.high();
                DEVICE.write((byte)0x00);
                DC.low();
                DEVICE.write((byte)0x4F);//SET_RAM_Y_ADDRESS_COUNTER
                DC.high();
                DEVICE.write((byte)(j&0xFF),(byte)((j>>8)&0xFF));
                while(BUSY.isHigh())
                    Thread.sleep(Time.get(10).millisecond());
                DC.low();
                DEVICE.write((byte)0x24);//WRITE_RAM
                DC.high();
                for(int i=0;i<epaperScreenSize.getLittleWidth();i++)
                {
                    if(pixels.isIOk(i)&&pixels.getPixel(i,j).getValue()==1)
                        b|=0x80>>(i%8);
                    if(i%8==7)
                    {
                        DEVICE.write(b);
                        b=(byte)0x00;
                    }
                }
            }
            DC.low();
            DEVICE.write((byte)0x22);//DISPLAY_UPDATE_CONTROL_2
            DC.high();
            DEVICE.write((byte)0xC4);
            DC.low();
            DEVICE.write((byte)0x20);//MASTER_ACTIVATION
            DEVICE.write((byte)0xFF);//TERMINATE_FRAME_READ_WRITE
            while(BUSY.isHigh())
                Thread.sleep(Time.get(10).millisecond());
        }
        catch(InterruptedException e)
        {
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Unable to initialize device or to send image to device ("+e.toString()+")");
        }
    }
}
