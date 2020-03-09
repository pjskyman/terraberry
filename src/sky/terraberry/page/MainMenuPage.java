package sky.terraberry.page;

import sky.terraberry.RotationDirection;

public class MainMenuPage extends AbstractMenuPage
{
    public MainMenuPage()
    {
        super(null);
        subpages.add(new NextTrainPage(this).potentiallyUpdate());
        subpages.add(new WeatherPage(this).potentiallyUpdate());
        subpages.add(new SeineLevelPage(this).potentiallyUpdate());
        subpages.add(new MoonPage(this).potentiallyUpdate());
        currentPageRank=1;
    }

    public String getName()
    {
        return "Menu principal";
    }

    @Override
    public boolean clicked(boolean initial)
    {
        if(currentlySelectedPageRank==-1)
        {
            if(subpages.get(currentPageRank-1).clicked(false))
                currentlySelectedPageRank=currentPageRank;
            return false;
        }
        else
        {
            currentPageRank=currentlySelectedPageRank;
            currentlySelectedPageRank=-1;
            subpages.get(currentPageRank-1).clicked(true);
            return false;
        }
    }

    @Override
    public boolean rotated(RotationDirection rotationDirection)
    {
        if(subpages.get(currentPageRank-1).rotated(rotationDirection))
            return true;
        currentlySelectedPageRank=((currentlySelectedPageRank==-1?currentPageRank:currentlySelectedPageRank)-1+(rotationDirection==RotationDirection.CLOCKWISE?1:-1)+subpages.size())%subpages.size()+1;
        return true;
    }
}
