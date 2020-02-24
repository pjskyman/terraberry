package sky.terraberry;

import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;
import sky.program.Duration;
import sky.terraberry.page.MainMenuPage;

public final class Terraberry
{
    private Terraberry()
    {
    }

    public static void main(String[] args)
    {
        Logger.LOGGER.info("Starting "+Terraberry.class.getSimpleName()+"...");
        try
        {
            MainMenuPage mainMenuPage=new MainMenuPage();
            Screen currentScreen=mainMenuPage.potentiallyUpdate().getScreen();
            AtomicInteger currentModificationCount=new AtomicInteger(currentScreen.getModificationCount());
            long lastCompleteRefresh=System.currentTimeMillis();
            EpaperScreenManager.display(currentScreen,RefreshType.TOTAL_REFRESH);
            SwitchManager.addSwitch1Listener(new SwitchListener()
            {
                public void switched()
                {
                    Logger.LOGGER.info("Switch has been pressed");
                    SwingUtilities.invokeLater(this::changePage);
                }

                private synchronized void changePage()
                {
                    mainMenuPage.rotated(RotationDirection.CLOCKWISE);
                    try
                    {
                        Thread.sleep(1000L);
                    }
                    catch(InterruptedException e)
                    {
                    }
                    mainMenuPage.clicked(false);
                    Logger.LOGGER.info("Page change has been completed");
                }
            });
            Logger.LOGGER.info(Terraberry.class.getSimpleName()+" is now ready!");
            new Thread("ledUpdater")
            {
                @Override
                public void run()
                {
                    try
                    {
                        LedManager.setLed(null);
                        Thread.sleep(Duration.of(2).second());
                        while(true)
                        {
                            double temperature=ThermometerManager.getTemperature();
                            if(temperature<20.5d)
                                LedManager.setLed(LedColor.BLUE);
                            else
                                if(temperature<21.5d)
                                    LedManager.setLed(LedColor.GREEN);
                                else
                                    if(temperature<22.5d)
                                        LedManager.setLed(LedColor.ORANGE);
                                    else
                                        LedManager.setLed(LedColor.RED);
                            Thread.sleep(Duration.of(2).secondPlus(479).millisecond());
                        }
                    }
                    catch(InterruptedException e)
                    {
                    }
                }
            }.start();
            new Thread("pageUpdater")
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(Duration.of(1).second());
                        while(true)
                        {
                            try
                            {
                                mainMenuPage.potentiallyUpdate();
                            }
                            catch(Throwable t)
                            {
                                Logger.LOGGER.error("Unmanaged error during refresh ("+t.toString()+")");
                                t.printStackTrace();
                            }
                            Thread.sleep(Duration.of(207).millisecond());
                        }
                    }
                    catch(InterruptedException e)
                    {
                    }
                }
            }.start();
            try
            {
                while(true)
                {
//                    Logger.LOGGER.info("Getting new pixels from page \""+mainMenuPage.getActivePageName()+"\"");
                    Screen newScreen=mainMenuPage.getScreen();
                    int newModificationCount=newScreen.getModificationCount();
//                    Logger.LOGGER.info("New pixels successfully got from page \""+mainMenuPage.getActivePageName()+"\"");
                    if(newScreen!=currentScreen||newModificationCount!=currentModificationCount.get())
                    {
                        long now=System.currentTimeMillis();
                        RefreshType realRefreshType=RefreshType.PARTIAL_REFRESH;
                        if(now-lastCompleteRefresh>Duration.of(10).minute())
                        {
                            realRefreshType=realRefreshType.combine(RefreshType.TOTAL_REFRESH);
                            lastCompleteRefresh=now;
                        }
                        Logger.LOGGER.info("Updating display content from page \""+mainMenuPage.getActivePageName()+"\" ("+realRefreshType.toString()+")");
                        EpaperScreenManager.display(newScreen,realRefreshType);
                        Logger.LOGGER.info("Display content successfully updated from page \""+mainMenuPage.getActivePageName()+"\" ("+realRefreshType.toString()+")");
                        currentScreen=newScreen;
                        currentModificationCount.set(newModificationCount);
                    }
                    Thread.sleep(Duration.of(48).millisecond());
                }
            }
            catch(InterruptedException e)
            {
            }
        }
        catch(Exception e)
        {
            Logger.LOGGER.error("Unknown error");
            e.printStackTrace();
        }
    }
}
