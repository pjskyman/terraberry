package sky.terraberry.page;

import java.awt.Font;
import java.awt.Graphics2D;
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
import sky.program.Duration;
import sky.terraberry.Logger;

public class NextTrainPage extends AbstractSinglePage
{
    private final List<Train> nextTrainsR;
    private final List<Train> nextTrainsC;
    private static final boolean INTERNET_ACTIVE=true;

    public NextTrainPage(Page parentPage)
    {
        super(parentPage);
        nextTrainsR=new ArrayList<>();
        nextTrainsC=new ArrayList<>();
    }

    public String getName()
    {
        return "Lignes C & R";
    }

    protected long getMinimalRefreshDelay()
    {
        return Duration.of(1).minuteMinus(1).secondMinus(300).millisecond();
    }

    protected void populateImage(Graphics2D g2d) throws VetoException,Exception
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

        if(errorC)
            for(int x=0;x<146;x++)
                for(int y=0;y<128;y++)
                    if((x+(y-1)/2)%2==0)
                        g2d.drawLine(x,y,x,y);
        if(errorR)
            for(int x=146;x<250;x++)
                for(int y=0;y<128;y++)
                    if((x+(y-1)/2)%2==0)
                        g2d.drawLine(x,y,x,y);

        Font timeFont=FREDOKA_ONE_FONT.deriveFont(32f);
        Font missionFont=FREDOKA_ONE_FONT.deriveFont(20f);
        if(nextTrainsC.stream().anyMatch(train->train.getMission().startsWith("E")))
        {
            List<Train> list=new ArrayList<Train>();
            nextTrainsC.stream()
                    .filter(train->train.getMission().startsWith("E"))
                    .forEach(list::add);
            nextTrainsC.clear();
            nextTrainsC.addAll(list);
        }
        drawTrainList(g2d,nextTrainsC,0,timeFont,missionFont);
        drawTrainList(g2d,nextTrainsR,146,timeFont,missionFont);
    }

    private static void drawTrainList(Graphics2D g2d,List<Train> trains,int baseX,Font timeFont,Font missionFont)
    {
        for(int i=0;i<trains.size();i++)
        {
            int y=(i+1)*26-2;
            Train train=trains.get(i);
            g2d.setFont(timeFont);
            String time=train.getTime()+(train.getAdditionalMessage().toLowerCase().contains("retar")?"*":"");
            int timeWidth=(int)Math.ceil(g2d.getFont().getStringBounds(time,g2d.getFontRenderContext()).getWidth());
            g2d.drawString(time,baseX+1,y);
            g2d.setFont(missionFont);
            String mission=train.getMission();
            int missionWidth=(int)Math.ceil(g2d.getFont().getStringBounds(mission,g2d.getFontRenderContext()).getWidth());
            g2d.drawString(mission,baseX+1+timeWidth+3,y-3);
            if(train.getAdditionalMessage().toLowerCase().contains("suppr"))
            {
                g2d.drawLine(baseX,y-11,baseX+timeWidth+2+missionWidth+3,y-11);
                g2d.drawLine(baseX,y-10,baseX+timeWidth+2+missionWidth+3,y-10);
                g2d.drawLine(baseX,y-9,baseX+timeWidth+2+missionWidth+3,y-9);
            }
        }
    }

    protected String getDebugImageFileName()
    {
        return "next_train.png";
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

    public static void main(String[] args)
    {
        new NextTrainPage(null).potentiallyUpdate();
    }
}
