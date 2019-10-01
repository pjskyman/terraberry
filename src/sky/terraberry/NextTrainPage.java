package sky.terraberry;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class NextTrainPage extends AbstractPage
{
    private long lastRefreshTime;
    private final List<Train> nextTrainsR;
    private final List<Train> nextTrainsC;
    private static final boolean INTERNET_ACTIVE=true;

    public NextTrainPage()
    {
        lastRefreshTime=0L;
        nextTrainsR=new ArrayList<>();
        nextTrainsC=new ArrayList<>();
    }

    public int getSerial()
    {
        return 1;
    }

    public String getName()
    {
        return "Lignes C & R";
    }

    public synchronized Page potentiallyUpdate()
    {
        long now=System.currentTimeMillis();
        if(now-lastRefreshTime>Duration.of(1).minuteMinus(1).secondMinus(300).millisecond())
        {
            lastRefreshTime=now;
            try
            {
                String login="";
                String password="";
                try(BufferedReader reader=new BufferedReader(new FileReader(new File("transilien.ini"))))
                {
                    login=reader.readLine();
                    password=reader.readLine();
                }
                catch(IOException e)
                {
                    Logger.LOGGER.error("Unable to read Transilien access informations from the config file ("+e.toString()+")");
                }
                String departC="";
                String arriveeC="";
                String departR="";
                String arriveeR="";
                try(BufferedReader reader=new BufferedReader(new FileReader(new File("transilien_config.ini"))))
                {
                    departC=reader.readLine();
                    arriveeC=reader.readLine();
                    departR=reader.readLine();
                    arriveeR=reader.readLine();
                }
                catch(IOException e)
                {
                    Logger.LOGGER.error("Unable to read Transilien configuration from the config file ("+e.toString()+")");
                }

                boolean errorC;
                boolean errorR;
                if(INTERNET_ACTIVE)
                {
                    errorC=!updateTrains(nextTrainsC,departC,arriveeC,login,password);
                    errorR=!updateTrains(nextTrainsR,departR,arriveeR,login,password);
                }
                else
                {
                    errorC=false;
                    errorR=false;

                    nextTrainsC.add(new Train("18:01","ELAO","Coucou","Retardé"));
                    nextTrainsC.add(new Train("18:18","ELAO","Coucou","Supprimé"));
                    nextTrainsC.add(new Train("18:31","ELAO","Coucou",""));
                    nextTrainsC.add(new Train("18:46","ZEGO","Coucou","Retardé"));
                    nextTrainsC.add(new Train("19:01","ZOBA","Coucou","Supprimé"));
                    nextTrainsC.add(new Train("19:16","ELBA","Coucou",""));

                    nextTrainsR.add(new Train("18:07","GAMA","Coucou",""));
                    nextTrainsR.add(new Train("18:13","LARO","Coucou",""));
                    nextTrainsR.add(new Train("18:32","GAME","Coucou",""));
                    nextTrainsR.add(new Train("18:46","KUMO","Coucou",""));
                    nextTrainsR.add(new Train("19:07","GAMA","Coucou",""));
                    nextTrainsR.add(new Train("19:16","KUMO","Coucou",""));
                    nextTrainsR.add(new Train("19:32","GAME","Coucou",""));
                }

                BufferedImage sourceImage=new BufferedImage(250,128,BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics2D g2d=sourceImage.createGraphics();
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0,0,250,128);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0,6,250,122);
                g2d.setColor(Color.BLACK);
//                g2d.drawLine(123,6,123,128);
//                g2d.drawLine(124,6,124,128);
//                g2d.drawLine(125,6,125,128);
//                g2d.drawLine(126,6,126,128);

                if(errorC)
                    for(int x=0;x<122;x++)
                        for(int y=0;y<128;y++)
                            if((x+(y-1)/2)%2==0)
                                g2d.drawLine(x,y,x,y);
                if(errorR)
                    for(int x=122;x<250;x++)
                        for(int y=0;y<128;y++)
                            if((x+(y-1)/2)%2==0)
                                g2d.drawLine(x,y,x,y);

                Font bigTimeFont=Terraberry.FONT.deriveFont(30f);
                Font mediumTimeFont=Terraberry.FONT.deriveFont(13f).deriveFont(AffineTransform.getScaleInstance(1.1d,.95d));
                Font bigMissionFont=Terraberry.FONT.deriveFont(20f).deriveFont(AffineTransform.getScaleInstance(.85d,1d));
                Font mediumMissionFont=Terraberry.FONT.deriveFont(13f).deriveFont(AffineTransform.getScaleInstance(1.1d,.95d));
                int currentY=5;
                if(nextTrainsC.stream().anyMatch(train->train.getMission().startsWith("E")))
                {
                    List<Train> list=new ArrayList<Train>();
                    nextTrainsC.stream()
                            .filter(train->train.getMission().startsWith("E"))
                            .forEach(list::add);
                    nextTrainsC.clear();
                    nextTrainsC.addAll(list);
                }
                for(int i=0;i<nextTrainsC.size();i++)
                {
                    Train trainC=nextTrainsC.get(i);
                    g2d.setFont(bigTimeFont);
                    String time=trainC.getTime()+(trainC.getAdditionalMessage().toLowerCase().contains("retar")?"*":"");
                    int timeWidth=(int)Math.ceil(bigTimeFont.getStringBounds(time,g2d.getFontRenderContext()).getWidth());
                    g2d.drawString(time,1,currentY+20);
                    if(trainC.getAdditionalMessage().toLowerCase().contains("suppr"))
                    {
                        g2d.drawLine(0,currentY+20-11,timeWidth+2,currentY+20-11);
                        g2d.drawLine(0,currentY+20-10,timeWidth+2,currentY+20-10);
                        g2d.drawLine(0,currentY+20-9,timeWidth+2,currentY+20-9);
                    }
                    g2d.setFont(bigMissionFont);
                    g2d.drawString(trainC.getMission(),1+timeWidth+3,currentY+20-3);
                    currentY+=20;
                }
                for(int i=0;i<nextTrainsR.size();i++)
                {
                    Train trainR=nextTrainsR.get(i);
                    g2d.setFont(bigTimeFont);
                    String time=trainR.getTime()+(trainR.getAdditionalMessage().toLowerCase().contains("retar")?"*":"");
                    int timeWidth=(int)Math.ceil(bigTimeFont.getStringBounds(time,g2d.getFontRenderContext()).getWidth());
                    g2d.drawString(time,123,20*(i+1)+5);
                    if(trainR.getAdditionalMessage().toLowerCase().contains("suppr"))
                    {
                        g2d.drawLine(122,20*(i+1)+5-11,122+timeWidth+2,20*(i+1)+5-11);
                        g2d.drawLine(122,20*(i+1)+5-10,122+timeWidth+2,20*(i+1)+5-10);
                        g2d.drawLine(122,20*(i+1)+5-9,122+timeWidth+2,20*(i+1)+5-9);
                    }
                    g2d.setFont(bigMissionFont);
                    g2d.drawString(trainR.getMission(),123+timeWidth+3,20*(i+1)+2);
                }

                g2d.dispose();
//                try(OutputStream outputStream=new FileOutputStream(new File("next_train.png")))
//                {
//                    ImageIO.write(sourceImage,"png",outputStream);
//                }
                pixels=new Pixels().writeImage(sourceImage);
                Logger.LOGGER.info("Page "+getSerial()+" updated successfully");
            }
            catch(Exception e)
            {
                Logger.LOGGER.error("Unknown error ("+e.toString()+")");
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private boolean updateTrains(List<Train> list,String departure,String arrival,String login,String password)
    {
        try
        {
            HttpURLConnection connection=(HttpURLConnection)new URL("https://api.transilien.com/gare/"+departure+"/depart/"+arrival).openConnection();
            connection.setRequestProperty("Authorization","Basic "+Base64.getEncoder().encodeToString((login+":"+password).getBytes()));
            connection.setRequestProperty("Accept","application/vnd.sncf.transilien.od.depart+xml;vers=1");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setAllowUserInteraction(false);
            connection.setDoOutput(true);
            StringBuilder stringBuilder=new StringBuilder();
            try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(connection.getInputStream())))
            {
                String line;
                while((line=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
            }
            connection.disconnect();
            connection=null;
            if(stringBuilder.toString().isEmpty())
                return false;
    //        Logger.LOGGER.info("requestResponse="+stringBuilder.toString());
            Element passagesElement=new SAXBuilder().build(new StringReader(stringBuilder.toString())).getRootElement();
            list.clear();
            ((List<Element>)passagesElement.getChildren("train")).forEach(trainElement->
            {
                String date=trainElement.getChild("date").getText();
                date=date.substring(date.length()-5,date.length());
                String state=trainElement.getChild("etat")!=null?trainElement.getChild("etat").getText():"";
                list.add(new Train(date,trainElement.getChild("miss").getText(),trainElement.getChild("term").getText(),state));
            });
            return true;
        }
        catch(IOException|JDOMException e)
        {
            return false;
        }
    }

    public Pixels getPixels()
    {
        return pixels;
    }

    public boolean hasHighFrequency()
    {
        return false;
    }

    public static void main(String[] args)
    {
        new NextTrainPage().potentiallyUpdate();
    }
}
