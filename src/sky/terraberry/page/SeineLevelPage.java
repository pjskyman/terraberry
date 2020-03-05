package sky.terraberry.page;

import java.awt.Font;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import sky.program.Duration;

public class SeineLevelPage extends AbstractSinglePage
{
    private double level;
    private static final boolean INTERNET_ACTIVE=true;

    public SeineLevelPage(Page parentPage)
    {
        super(parentPage);
        level=0d;
    }

    public String getName()
    {
        return "Niveau de la Seine";
    }

    protected long getMinimalRefreshDelay()
    {
        return Duration.of(10).minutePlus(5).secondMinus(300).millisecond();
    }

    protected void populateImage(Graphics2D g2d) throws VetoException,Exception
    {
        if(INTERNET_ACTIVE)
        {
            HttpURLConnection connection=(HttpURLConnection)new URL("http://hubeau.eaufrance.fr/api/v1/hydrometrie/observations_tr.xml?code_entite=F700000103&size=1").openConnection();
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
            String rawContent=stringBuilder.toString();
            if(!rawContent.isEmpty())
            {
                Element hydrometrieElement=new SAXBuilder().build(new StringReader(rawContent)).getRootElement();
                Namespace namespace=hydrometrieElement.getNamespace();
                Element DonneesElement=hydrometrieElement.getChild("Donnees",namespace);
                Element SeriesElement=DonneesElement.getChild("Series",namespace);
                Element SerieElement=SeriesElement.getChild("Serie",namespace);
                Element ObssHydroElement=SerieElement.getChild("ObssHydro",namespace);
                Element ObsHydroElement=ObssHydroElement.getChild("ObsHydro",namespace);
                Element ResObsHydroElement=ObsHydroElement.getChild("ResObsHydro",namespace);
                String levelString=ResObsHydroElement.getText();
                level=Double.parseDouble(levelString)/1000d;
            }
            else
                level=0d;
        }
        else
        {
            level=0d;
        }

        Font font=FREDOKA_ONE_FONT.deriveFont(72f);
        g2d.setFont(font);
        String levelString=String.valueOf(level)+" m";
        int levelStringWidth=(int)Math.ceil(g2d.getFont().getStringBounds(levelString,g2d.getFontRenderContext()).getWidth());
        g2d.drawString(levelString,148-levelStringWidth/2,90);
    }

    protected String getDebugImageFileName()
    {
        return "seine.png";
    }

    public static void main(String[] args)
    {
        new SeineLevelPage(null).potentiallyUpdate();
    }
}
