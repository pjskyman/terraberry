package sky.terraberry;

public interface Page
{
    public int getSerial();

    public String getName();

    public Page potentiallyUpdate();

    public Pixels getPixels();

    public boolean hasHighFrequency();
}
