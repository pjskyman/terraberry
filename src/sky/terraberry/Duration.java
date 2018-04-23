package sky.terraberry;

public class Duration
{
    private final int value;
    private final long previousValue;

    private Duration(int value)
    {
        this(value,0L);
    }

    private Duration(int value,long previousValue)
    {
        this.value=value;
        this.previousValue=previousValue;
    }

    private long millisecondImpl()
    {
        return (long)value;
    }

    public long millisecond()
    {
        return millisecondImpl()+previousValue;
    }

    public Duration millisecondPlus(int value)
    {
        return new Duration(value,millisecond());
    }

    public Duration millisecondMinus(int value)
    {
        return new Duration(-value,millisecond());
    }

    private long secondImpl()
    {
        return 1000L*millisecondImpl();
    }

    public long second()
    {
        return secondImpl()+previousValue;
    }

    public Duration secondPlus(int value)
    {
        return new Duration(value,second());
    }

    public Duration secondMinus(int value)
    {
        return new Duration(-value,second());
    }

    private long minuteImpl()
    {
        return 60L*secondImpl();
    }

    public long minute()
    {
        return minuteImpl()+previousValue;
    }

    public Duration minutePlus(int value)
    {
        return new Duration(value,minute());
    }

    public Duration minuteMinus(int value)
    {
        return new Duration(-value,minute());
    }

    private long hourImpl()
    {
        return 60L*minuteImpl();
    }

    public long hour()
    {
        return hourImpl()+previousValue;
    }

    public Duration hourPlus(int value)
    {
        return new Duration(value,hour());
    }

    public Duration hourMinus(int value)
    {
        return new Duration(-value,hour());
    }

    private long dayImpl()
    {
        return 24L*hourImpl();
    }

    public long day()
    {
        return dayImpl()+previousValue;
    }

    public Duration dayPlus(int value)
    {
        return new Duration(value,day());
    }

    public Duration dayMinus(int value)
    {
        return new Duration(-value,day());
    }

    private long weekImpl()
    {
        return 7L*dayImpl();
    }

    public long week()
    {
        return weekImpl()+previousValue;
    }

    public Duration weekPlus(int value)
    {
        return new Duration(value,week());
    }

    public Duration weekMinus(int value)
    {
        return new Duration(-value,week());
    }

    public static Duration of(int value)
    {
        return new Duration(value);
    }

    public static void main(String[] args)
    {
        System.out.println(Duration.of(1).millisecond());
        System.out.println(Duration.of(1).second());
        System.out.println(Duration.of(1).minute());
        System.out.println(Duration.of(1).hour());
        System.out.println(Duration.of(1).day());
        System.out.println(Duration.of(1).week());
        System.out.println(Duration.of(1).weekPlus(2).minutePlus(3).secondMinus(7).millisecond());
    }
}
