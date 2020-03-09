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
import sky.program.Duration;

public class EpaperScreenManager
{
    private static PixelMatrix[] pixelMatrices=new PixelMatrix[2];
    private static int pixelMatrixIndex=0;
    private static final SpiDevice DEVICE;
    private static final GpioPinDigitalOutput RESET;
    private static final GpioPinDigitalOutput DC;
    private static final GpioPinDigitalInput BUSY;
    public static final int LITTLE_WIDTH=128;
    public static final int BIG_HEIGHT=296;
    private static final byte[] TOTAL_REFRESH_LOOK_UP_TABLE=new byte[]
    {
        (byte)0x02,
        (byte)0x02,
        (byte)0x01,
        (byte)0x11,
        (byte)0x12,
        (byte)0x12,
        (byte)0x22,
        (byte)0x22,
        (byte)0x66,
        (byte)0x69,
        (byte)0x69,
        (byte)0x59,
        (byte)0x58,
        (byte)0x99,
        (byte)0x99,
        (byte)0x88,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0xF8,
        (byte)0xB4,
        (byte)0x13,
        (byte)0x51,
        (byte)0x35,
        (byte)0x51,
        (byte)0x51,
        (byte)0x19,
        (byte)0x01,
        (byte)0x00
    };
    private static final byte[] PARTIAL_REFRESH_LOOK_UP_TABLE=new byte[]
    {
        (byte)0x10,
        (byte)0x18,
        (byte)0x18,
        (byte)0x08,
        (byte)0x18,
        (byte)0x18,
        (byte)0x08,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x13,
        (byte)0x14,
        (byte)0x44,
        (byte)0x12,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00
    };

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
                e.printStackTrace();
                System.exit(1);
            }
            DEVICE=device;
            Logger.LOGGER.info("SPI device successfully initialized");
            RESET=GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_00,PinState.HIGH);
            DC=GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_06,PinState.LOW);//TODO parfois ces lignes merdouillent, il faut les sécuriser !
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
    }

    private EpaperScreenManager()
    {
    }

    public static synchronized void display(Screen screen,RefreshType refreshType)
    {
        long now=System.currentTimeMillis();
        PixelMatrix currentPixelMatrix=screen.getPixelMatrix();
        int iMin;
        int iMax;
        int jMin;
        int jMax;
        if(pixelMatrices[pixelMatrixIndex]==null)
        {
            iMin=0;
            iMax=LITTLE_WIDTH;
            jMin=0;
            jMax=BIG_HEIGHT;
        }
        else
        {
            iMin=Integer.MAX_VALUE;
            iMax=Integer.MIN_VALUE;
            jMin=Integer.MAX_VALUE;
            jMax=Integer.MIN_VALUE;
            int x;
            int y;
            for(int j=0;j<BIG_HEIGHT;j++)
                for(int i=0;i<LITTLE_WIDTH;i++)
                {
                    x=j;
                    y=LITTLE_WIDTH-1-i;
                    if(!currentPixelMatrix.arePixelsEqual(pixelMatrices[pixelMatrixIndex],x,y))
                    {
                        if(i<iMin)
                            iMin=i;
                        if(i>iMax)
                            iMax=i;
                        if(j<jMin)
                            jMin=j;
                        if(j>jMax)
                            jMax=j;
                    }
                }
            iMax++;
            jMax++;
        }
        iMin=iMin/8*8;
        iMax=((iMax-1)/8+1)*8;
        PixelState[][] pixelStates=new PixelState[jMax-jMin][iMax-iMin];
        int x;
        int y;
        for(int j=jMin;j<jMax;j++)
            for(int i=iMin;i<iMax;i++)
            {
                x=j;
                y=LITTLE_WIDTH-1-i;
                pixelStates[j-jMin][i-iMin]=currentPixelMatrix.getPixelState(x,y);
            }
        long now2=System.currentTimeMillis();
        displayImpl(pixelStates,iMin,jMin,refreshType);
        long subtime=System.currentTimeMillis()-now2;
        pixelMatrices[pixelMatrixIndex]=currentPixelMatrix;
        int tempPixelMatrixIndex=pixelMatrixIndex;
        pixelMatrixIndex=++pixelMatrixIndex%pixelMatrices.length;
        Logger.LOGGER.debug(refreshType.name()+" Ok iMin="+iMin+" iMax="+iMax+" jMin="+jMin+" jMax="+jMax+" cache="+tempPixelMatrixIndex+" time="+(System.currentTimeMillis()-now)+" ms ("+subtime+" ms)");
    }

    private static void displayImpl(PixelState[][] pixelStates,int i,int j,RefreshType refreshType)
    {
        int imageWidth=pixelStates[0].length;
        int imageHeight=pixelStates.length;
        i&=0xF8;
        imageWidth&=0xF8;
        int iEnd;
        if(i+imageWidth>=LITTLE_WIDTH)
            iEnd=LITTLE_WIDTH-1;
        else
            iEnd=i+imageWidth-1;
        int jEnd;
        if(j+imageHeight>=BIG_HEIGHT)
            jEnd=BIG_HEIGHT-1;
        else
            jEnd=j+imageHeight-1;
        try
        {
            RESET.low();
            Thread.sleep(Duration.of(150).millisecond());
            RESET.high();
            Thread.sleep(Duration.of(150).millisecond());
            DC.low();
            DEVICE.write((byte)0x01);//DRIVER_OUTPUT_CONTROL
            DC.high();
            DEVICE.write((byte)((BIG_HEIGHT-1)&0xFF),(byte)(((BIG_HEIGHT-1)>>8)&0xFF),(byte)0x00);
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
            DEVICE.write(refreshType.isPartialRefresh()?PARTIAL_REFRESH_LOOK_UP_TABLE:TOTAL_REFRESH_LOOK_UP_TABLE);
            DC.low();
            DEVICE.write((byte)0x44);//SET_RAM_X_ADDRESS_START_END_POSITION
            DC.high();
            DEVICE.write((byte)((i>>3)&0xFF),(byte)((iEnd>>3)&0xFF));
            DC.low();
            DEVICE.write((byte)0x45);//SET_RAM_Y_ADDRESS_START_END_POSITION
            DC.high();
            DEVICE.write((byte)(j&0xFF),(byte)((j>>8)&0xFF),(byte)(jEnd&0xFF),(byte)((jEnd>>8)&0xFF));
            DC.low();
            DEVICE.write((byte)0x4E);//SET_RAM_X_ADDRESS_COUNTER
            DC.high();
            DEVICE.write((byte)((i>>3)&0xFF));
            DC.low();
            DEVICE.write((byte)0x4F);//SET_RAM_Y_ADDRESS_COUNTER
            DC.high();
            DEVICE.write((byte)(j&0xFF),(byte)((j>>8)&0xFF));
            while(BUSY.isHigh())
                Thread.sleep(Duration.of(10).millisecond());
            DC.low();
            DEVICE.write((byte)0x24);//WRITE_RAM
            DC.high();
            byte b=(byte)0x00;
            for(int j_=0;j_<jEnd-j+1;j_++)
                for(int i_=0;i_<iEnd-i+1;i_++)
                {
                    if(pixelStates[j_][i_].getValue()==1)
                        b|=0x80>>(i_%8);
                    if(i_%8==7)
                    {
                        DEVICE.write(b);
                        b=(byte)0x00;
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
                Thread.sleep(Duration.of(10).millisecond());
        }
        catch(InterruptedException e)
        {
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Unable to initialize device or to send pixels to device ("+e.toString()+")");
            e.printStackTrace();
        }
    }
}
