package sky.terraberry;

public class Train
{
    private final String time;
    private final String mission;
    private final String destination;
    private final String additionalMessage;

    public Train(String time,String mission,String destination,String additionalMessage)
    {
        this.time=time;
        this.mission=mission;
        this.destination=destination;
        this.additionalMessage=additionalMessage;
    }

    public String getTime()
    {
        return time;
    }

    public String getMission()
    {
        return mission;
    }

    public String getDestination()
    {
        return destination;
    }

    public String getAdditionalMessage()
    {
        return additionalMessage;
    }
}
