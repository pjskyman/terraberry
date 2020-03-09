package sky.terraberry.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
            HttpURLConnection connection=(HttpURLConnection)new URL("https://hubeau.eaufrance.fr/api/v1/hydrometrie/observations_tr?code_entite=F700000103&grandeur_hydro=H&size=1").openConnection();
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
//            System.out.println(rawContent);
            if(!rawContent.isEmpty())
                try
                {
                    JsonObject element=new JsonParser().parse(rawContent).getAsJsonObject();
                    JsonArray dataArray=element.getAsJsonArray("data");
                    JsonObject data0Object=dataArray.get(0).getAsJsonObject();
                    JsonPrimitive resultat_obsPrimitive=data0Object.get("resultat_obs").getAsJsonPrimitive();
                    level=resultat_obsPrimitive.getAsDouble()/1000d;
                }
                catch(JsonSyntaxException e)
                {
                    e.printStackTrace();
                    level=0d;
                }
            else
                level=0d;
        }
        else
            level=0d;

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
