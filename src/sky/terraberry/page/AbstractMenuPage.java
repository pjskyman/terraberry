package sky.terraberry.page;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import sky.terraberry.Logger;
import sky.terraberry.RotationDirection;
import sky.terraberry.Screen;

/**
 * Squelette de menu. Un menu est avant tout une page, mais contient une
 * arborescence avec des sous-pages. Cette arborescence est théoriquement de
 * profondeur infinie.
 * @author PJ Skyman
 */
public abstract class AbstractMenuPage extends AbstractPage
{
    /**
     * Liste des [n] sous-pages contenues dans ce menu.
     */
    protected final List<Page> subpages;
    /**
     * Rang de la sous-page actuelle.
     * <li>Vaut -1 pour indiquer qu'on est pas encore rentré dans le menu.
     * <li>Vaut un rang [1-n] valide de sous-page pour indiquer qu'on affiche une
     * sous-page réelle.
     */
    protected int currentPageRank;
    /**
     * Rang de la sous-page actuellement sélectionnée.
     * <li>Vaut -1 pour indiquer qu'on ne sélectionne rien.
     * <li>Vaut 0 pour indiquer qu'on désire sortir du menu.
     * <li>Vaut un rang [1-n] valide de sous-page pour indiquer qu'on a réellement
     * sélectionné une sous-page.
     */
    protected int currentlySelectedPageRank;
    /**
     * Rang de la dernière sous-page prise en compte pour l'affichage.
     */
    private int cachedCurrentPageRank;
    /**
     * Rang de la dernière sous-page sélectionnée prise en compte pour
     * l'affichage.
     */
    private int cachedCurrentlySelectedPageRank;
    /**
     * Dernier contenu de la sous-page pris en compte pour l'affichage.
     */
    private Screen cachedPageScreen;
    /**
     * Dernier compteur de modifications du contenu de la sous-page pris en
     * compte pour l'affichage.
     */
    private int cachedPageScreenModificationCount;
    /**
     * Dernier contenu de l'incrustation pris en compte pour l'affichage.
     */
    private final Screen cachedSelectionIncrustScreen;
    /**
     * Contenu vierge initialisé à blanc.
     */
    private static final Screen BLANK_SCREEN=new Screen().initializeBlank();

    protected AbstractMenuPage(Page parentPage)
    {
        super(parentPage);
        subpages=new ArrayList<>();
        currentPageRank=-1;
        currentlySelectedPageRank=-1;
        cachedCurrentPageRank=-2;//initialisé à -2 pour provoquer une mise à jour dans potentiallyUpdate
        cachedCurrentlySelectedPageRank=-2;//initialisé à -2 pour provoquer une mise à jour dans potentiallyUpdate
        cachedPageScreen=BLANK_SCREEN;
        cachedPageScreenModificationCount=-1;
        cachedSelectionIncrustScreen=new Screen().initializeTransparent();
    }

    public String getActivePageName()
    {
        if(currentPageRank==-1)
            return getName();
        else
            return subpages.get(currentPageRank-1).getActivePageName();
    }

    public int getRankOf(Page subpage)
    {
        if(subpages.contains(subpage))
            return subpages.indexOf(subpage)+1;
        else
            return -1;
    }

    public int getPageCount()
    {
        return subpages.size();
    }

    public synchronized Page potentiallyUpdate()
    {
        //TODO la ligne suivante pourrait-elle se faire en stream parallèle ?
        subpages.forEach(Page::potentiallyUpdate);//avant tout le reste, comme ça on pourra récupérer leurs nouveaux pixels le cas échéant
        Screen tempPageScreen=currentPageRank==-1?BLANK_SCREEN:subpages.get(currentPageRank-1).getScreen();
        int tempModificationCount=tempPageScreen.getModificationCount();
        if(currentPageRank!=cachedCurrentPageRank//si on a changé de sous-page
           ||currentlySelectedPageRank!=cachedCurrentlySelectedPageRank//si on a changé de sélection de sous-page
           ||tempPageScreen!=cachedPageScreen//si le contenu de la sous-page a changé (objet différent, mais compte-tenu du fonctionnement actuel des pages, c'est peu probable)
           ||tempModificationCount!=cachedPageScreenModificationCount)//si le contenu de la sous-page a changé (compteur de modifications différent)
        {
            Logger.LOGGER.info("Menu \""+getName()+"\" needs to be updated");
            try
            {
                cachedCurrentPageRank=currentPageRank;
                if(currentlySelectedPageRank!=cachedCurrentlySelectedPageRank)
                {
                    if(currentlySelectedPageRank==-1)
                        cachedSelectionIncrustScreen.setTransparent();
                    else
                        if(currentlySelectedPageRank==0)
                            cachedSelectionIncrustScreen.setImage(generateOutMessageIncrustImage());
                        else//>0
                            cachedSelectionIncrustScreen.setImage(generateSelectedPageIncrustImage(subpages.get(currentlySelectedPageRank-1)));
                    cachedCurrentlySelectedPageRank=currentlySelectedPageRank;
                }
                cachedPageScreen=tempPageScreen;
                cachedPageScreenModificationCount=tempModificationCount;
                screen.setContentWithIncrust(cachedPageScreen,cachedSelectionIncrustScreen);
                Logger.LOGGER.info("Menu \""+getName()+"\" updated successfully");
            }
            catch(Exception e)
            {
                Logger.LOGGER.error("Unknown error when updating menu \""+getName()+"\"");
                e.printStackTrace();
            }
        }
        return this;
    }

    private BufferedImage generateOutMessageIncrustImage()
    {
        BufferedImage image=new BufferedImage(296,128,BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g2d=image.createGraphics();
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0,0,296,128);
        g2d.setColor(Color.BLACK);
        Font baseFont=AbstractPage.BALOO_FONT.deriveFont(40f);
        Font descriptionFont=baseFont.deriveFont(20f);
        g2d.setFont(baseFont);
        String string1="Sortir de";
        int string1Width=(int)Math.ceil(baseFont.getStringBounds(string1,g2d.getFontRenderContext()).getWidth());
        int string1Height=(int)Math.ceil(baseFont.getStringBounds(string1,g2d.getFontRenderContext()).getHeight());
        g2d.setFont(descriptionFont);
        String string2=getName()+" ?";
        int string2Width=(int)Math.ceil(descriptionFont.getStringBounds(string2,g2d.getFontRenderContext()).getWidth());
        int string2Height=(int)Math.ceil(descriptionFont.getStringBounds(string2,g2d.getFontRenderContext()).getHeight());
        int maxStringWidth=Math.max(string1Width,string2Width)+20;
        int totalStringHeight=string1Height+string2Height+10;
        g2d.fillRoundRect(148-maxStringWidth/2,64-totalStringHeight/2,maxStringWidth,totalStringHeight,40,40);
        g2d.setColor(Color.WHITE);
        g2d.setFont(baseFont);
        g2d.drawString(string1,148-string1Width/2,64-totalStringHeight/2+string1Height-7);
        g2d.setFont(descriptionFont);
        g2d.drawString(string2,148-string2Width/2,64-totalStringHeight/2+string1Height-3+string2Height);
        g2d.dispose();
        return image;
    }

    private BufferedImage generateSelectedPageIncrustImage(Page page)
    {
        BufferedImage image=new BufferedImage(296,128,BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g2d=image.createGraphics();
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0,0,296,128);
        g2d.setColor(Color.BLACK);
        Font baseFont=AbstractPage.BALOO_FONT.deriveFont(40f);
        Font descriptionFont=baseFont.deriveFont(20f);
        g2d.setFont(baseFont);
        Page aParentPage=page.getParentPage();
        String string1=page.getPageCount()==-1?"Page":"Menu";
        if(aParentPage!=null)
            string1="["+aParentPage.getRankOf(page)+"] "+string1;
        int string1Width=(int)Math.ceil(baseFont.getStringBounds(string1,g2d.getFontRenderContext()).getWidth());
        int string1Height=(int)Math.ceil(baseFont.getStringBounds(string1,g2d.getFontRenderContext()).getHeight());
        g2d.setFont(descriptionFont);
        String string2=page.getName();
        int string2Width=(int)Math.ceil(descriptionFont.getStringBounds(string2,g2d.getFontRenderContext()).getWidth());
        int string2Height=(int)Math.ceil(descriptionFont.getStringBounds(string2,g2d.getFontRenderContext()).getHeight());
        int maxStringWidth=Math.max(string1Width,string2Width)+20;
        int totalStringHeight=string1Height+string2Height+10;
        g2d.fillRoundRect(148-maxStringWidth/2,64-totalStringHeight/2,maxStringWidth,totalStringHeight,40,40);
        g2d.setColor(Color.WHITE);
        g2d.setFont(baseFont);
        g2d.drawString(string1,148-string1Width/2,64-totalStringHeight/2+string1Height-7);
        g2d.setFont(descriptionFont);
        g2d.drawString(string2,148-string2Width/2,64-totalStringHeight/2+string1Height-3+string2Height);
        g2d.dispose();
        return image;
    }

    public boolean clicked(boolean initial)
    {
        if(currentPageRank==-1)
        {
            currentPageRank=1;
            currentlySelectedPageRank=subpages.get(0).getPageCount()==-1?-1:1;
            return false;
        }
        else
            if(currentlySelectedPageRank==-1)
            {
                if(subpages.get(currentPageRank-1).clicked(false))
                    currentlySelectedPageRank=currentPageRank;
                return false;
            }
            else
                if(currentlySelectedPageRank==0)
                {
                    currentPageRank=-1;
                    currentlySelectedPageRank=-1;
                    return true;
                }
                else
                {
                    currentPageRank=currentlySelectedPageRank;
                    currentlySelectedPageRank=-1;
                    subpages.get(currentPageRank-1).clicked(true);
                    return false;
                }
    }

    public boolean rotated(RotationDirection rotationDirection)
    {
        if(currentPageRank==-1)
            return false;
        if(currentPageRank!=-1&&subpages.get(currentPageRank-1).rotated(rotationDirection))
            return true;
        currentlySelectedPageRank=((currentlySelectedPageRank==-1?currentPageRank:currentlySelectedPageRank)+(rotationDirection==RotationDirection.CLOCKWISE?1:-1)+subpages.size()+1)%(subpages.size()+1);
        return true;
    }
}
