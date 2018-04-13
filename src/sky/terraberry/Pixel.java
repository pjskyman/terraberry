package sky.terraberry;

public enum Pixel
{
    BLACK
    {
        public int getValue()
        {
            return 0;
        }
    },
    WHITE
    {
        public int getValue()
        {
            return 1;
        }
    },
    TRANSPARENT
    {
        public int getValue()
        {
            return 1;
        }
    },
    ;

    public abstract int getValue();
}
