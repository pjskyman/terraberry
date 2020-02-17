package sky.terraberry;

public enum PixelState
{
    BLACK
    {
        public int getValue()
        {
            return 0;
        }

        public byte getByte()
        {
            return (byte)0;
        }
    },
    WHITE
    {
        public int getValue()
        {
            return 1;
        }

        public byte getByte()
        {
            return (byte)1;
        }
    },
    TRANSPARENT
    {
        public int getValue()
        {
            return 1;
        }

        public byte getByte()
        {
            return (byte)2;
        }
    },
    ;

    public abstract int getValue();

    public abstract byte getByte();
}
