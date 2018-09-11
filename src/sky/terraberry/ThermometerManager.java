package sky.terraberry;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import java.io.IOException;

public class ThermometerManager
{
    private static final I2CBus BUS;
    private static final int ADDRESS=0x18;
    private static final I2CDevice DEVICE;

    static
    {
        I2CBus bus=null;
        try
        {
            bus=I2CFactory.getInstance(I2CBus.BUS_1);
        }
        catch(IOException|UnsupportedBusNumberException e)
        {
            Logger.LOGGER.error("Unable to get I2C bus ("+e.toString()+")");
            System.exit(1);
        }
        BUS=bus;
        I2CDevice device=null;
        try
        {
            device=BUS.getDevice(ADDRESS);
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Unable to get device for thermometer ("+e.toString()+")");
            System.exit(1);
        }
        DEVICE=device;
        try
        {
            DEVICE.write(0x01,new byte[2]);//Continuous conversion mode, Power-up default
            DEVICE.write(0x08,(byte)0x03);//résolution maximale, c'est-à-dire 0.0625°C par incrément
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Unable to configure device for thermometer ("+e.toString()+")");
            System.exit(1);
        }
    }

    private ThermometerManager()
    {
    }

    public static synchronized double getTemperature()
    {
        try
        {
            byte[] data=new byte[2];
            DEVICE.read(0x05,data,0,data.length);
            int temperature=((data[0]&0x1F)*256+(data[1]&0xFF));
            if(temperature>4095)
                temperature-=8192;
//            Logger.LOGGER.info("Temperature is "+temperature*.0625d+"°C");
            double rawTemp=(double)temperature*.0625d;
            return -.024681*rawTemp*rawTemp+1.950705d*rawTemp-11.131971d;//correction applied according to a referential analog thermometer
            //Our device seems to have a problem since measured temperatures are biased by about 2.5°C.
            //Please adapt the correction if your device works properly!
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Unable to get temperature ("+e.toString()+")");
            return Double.NaN;
        }
    }
}
