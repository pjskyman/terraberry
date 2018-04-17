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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class NextTrainPage extends AbstractPage
{
    private long lastRefreshTime;

    public NextTrainPage()
    {
        lastRefreshTime=0L;
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
        if(now-lastRefreshTime>Time.get(1).minute())
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

                List<Train> nextTrainsR=getTrains(departR,arriveeR,login,password);
                List<Train> nextTrainsC=getTrains(departC,arriveeC,login,password);
//                nextTrainsC.sort((o1,o2)->o1.getTime().compareTo(o2.getTime()));
//                nextTrainsR.add(new Train("18:07","GAMA","Coucou",""));
//                nextTrainsR.add(new Train("18:13","LARO","Coucou",""));
//                nextTrainsR.add(new Train("18:32","GAME","Coucou",""));
//                nextTrainsR.add(new Train("18:46","KUMO","Coucou",""));
//                nextTrainsR.add(new Train("19:07","GAMA","Coucou",""));
//                nextTrainsR.add(new Train("19:16","KUMO","Coucou",""));
//                nextTrainsR.add(new Train("19:32","GAME","Coucou",""));
//                nextTrainsC.add(new Train("18:01","ELAO","Coucou","Retardé"));
//                nextTrainsC.add(new Train("18:18","ELAO","Coucou","Supprimé"));
//                nextTrainsC.add(new Train("18:31","ELAO","Coucou",""));
//                nextTrainsC.add(new Train("18:46","ELAO","Coucou",""));
//                nextTrainsC.add(new Train("19:01","ELBA","Coucou",""));
//                nextTrainsC.add(new Train("19:16","ELBA","Coucou",""));

                BufferedImage sourceImage=new BufferedImage(250,128,BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics2D g2d=sourceImage.createGraphics();
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0,0,250,128);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0,6,250,122);
                g2d.setColor(Color.BLACK);
                g2d.drawLine(123,6,123,128);
                g2d.drawLine(124,6,124,128);
                g2d.drawLine(125,6,125,128);
                g2d.drawLine(126,6,126,128);
                Font bigTimeFont=Terraberry.FONT.deriveFont(30f);
                Font mediumTimeFont=Terraberry.FONT.deriveFont(20f);
                Font missionFont=Terraberry.FONT.deriveFont(20f).deriveFont(AffineTransform.getScaleInstance(.85d,1d));
                Font infoFont=Terraberry.FONT.deriveFont(12f).deriveFont(AffineTransform.getScaleInstance(1d,1.1d));
                for(int i=0;i<nextTrainsR.size();i++)
                {
                    Train trainR=nextTrainsR.get(i);
                    g2d.setFont(bigTimeFont);
                    String time=trainR.getTime()+(trainR.getAdditionalMessage().toLowerCase().contains("retar")?"*":"");
                    int timeWidth=(int)Math.ceil(bigTimeFont.getStringBounds(time,g2d.getFontRenderContext()).getWidth());
                    g2d.drawString(time,1,20*(i+1)+5);
                    if(trainR.getAdditionalMessage().toLowerCase().contains("suppr"))
                    {
                        g2d.drawLine(0,20*(i+1)+5-11,timeWidth+2,20*(i+1)+5-11);
                        g2d.drawLine(0,20*(i+1)+5-10,timeWidth+2,20*(i+1)+5-10);
                        g2d.drawLine(0,20*(i+1)+5-9,timeWidth+2,20*(i+1)+5-9);
                    }
                    g2d.setFont(missionFont);
                    g2d.drawString(trainR.getMission(),1+timeWidth+3,20*(i+1)+2);
                }
                int currentY=5;
                for(int i=0;i<nextTrainsC.size();i++)
                {
                    Train trainC=nextTrainsC.get(i);
                    if(trainC.getMission().startsWith("E"))
                    {
                        g2d.setFont(bigTimeFont);
                        String time=trainC.getTime()+(trainC.getAdditionalMessage().toLowerCase().contains("retar")?"*":"");
                        int timeWidth=(int)Math.ceil(bigTimeFont.getStringBounds(time,g2d.getFontRenderContext()).getWidth());
                        g2d.drawString(time,128,currentY+20);
                        if(trainC.getAdditionalMessage().toLowerCase().contains("suppr"))
                        {
                            g2d.drawLine(127,currentY+20-11,127+timeWidth+2,currentY+20-11);
                            g2d.drawLine(127,currentY+20-10,127+timeWidth+2,currentY+20-10);
                            g2d.drawLine(127,currentY+20-9,127+timeWidth+2,currentY+20-9);
                        }
                        g2d.setFont(missionFont);
                        g2d.drawString(trainC.getMission(),128+timeWidth+3,currentY+20-3);
                        currentY+=20;
                    }
                    else
                    {
                        g2d.setFont(mediumTimeFont);
                        String time=trainC.getTime()+(trainC.getAdditionalMessage().toLowerCase().contains("retar")?"*":"");
                        int timeWidth=(int)Math.ceil(mediumTimeFont.getStringBounds(time,g2d.getFontRenderContext()).getWidth());
                        g2d.drawString(time,128,currentY+14);
                        if(trainC.getAdditionalMessage().toLowerCase().contains("suppr"))
                        {
                            g2d.drawLine(127,currentY+14-6,127+timeWidth+2,currentY+14-6);
                            g2d.drawLine(127,currentY+14-5,127+timeWidth+2,currentY+14-5);
                        }
                        g2d.setFont(missionFont);
                        g2d.drawString(trainC.getMission(),128+timeWidth+4,currentY+14);
                        currentY+=14;
                    }
                }
                g2d.setFont(infoFont);
                String ip="";
                mainLoop:for(NetworkInterface networkInterface:Collections.list(NetworkInterface.getNetworkInterfaces()))
                    for(InetAddress inetAddress:Collections.list(networkInterface.getInetAddresses()))
                        if(inetAddress.getHostAddress().contains("192.168"))
                        {
                            ip=inetAddress.getHostAddress();
                            break mainLoop;
                        }
                if(!ip.isEmpty())
                    ip=ip.substring(ip.lastIndexOf(".")+1);
                int ipWidth=(int)Math.ceil(infoFont.getStringBounds(ip,g2d.getFontRenderContext()).getWidth());
                g2d.fillRect(250-ipWidth-2,128-11,ipWidth+3,13);
                g2d.setColor(Color.WHITE);
                g2d.drawString(ip,250-ipWidth-1,128-1);
                g2d.setColor(Color.BLACK);
                String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
                int timeWidth=(int)Math.ceil(infoFont.getStringBounds(time,g2d.getFontRenderContext()).getWidth());
                g2d.fillRect(123-timeWidth-2,128-11,timeWidth+3,13);
                g2d.setColor(Color.WHITE);
                g2d.drawString(time,123-timeWidth-1,128-1);
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
    private List<Train> getTrains(String departure,String arrival,String login,String password) throws IOException,JDOMException
    {
        HttpURLConnection connection=(HttpURLConnection)new URL("http://api.transilien.com/gare/"+departure+"/depart/"+arrival).openConnection();
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
            return new ArrayList<>(0);
//        Logger.LOGGER.info("requestResponse="+stringBuilder.toString());
        Element passagesElement=new SAXBuilder().build(new StringReader(stringBuilder.toString())).getRootElement();
        List<Train> trains=new ArrayList<>();
        ((List<Element>)passagesElement.getChildren("train")).forEach(trainElement->
        {
            String date=trainElement.getChild("date").getText();
            date=date.substring(date.length()-5,date.length());
            String state=trainElement.getChild("etat")!=null?trainElement.getChild("etat").getText():"";
            trains.add(new Train(date,trainElement.getChild("miss").getText(),trainElement.getChild("term").getText(),state));
        });
        return trains;
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