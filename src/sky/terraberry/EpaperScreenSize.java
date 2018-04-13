package sky.terraberry;

public enum EpaperScreenSize
{
    _2_9
    {
        public int getLittleWidth()
        {
            return 128;
        }

        public int getBigHeight()
        {
            return 296;
        }

        public byte[] getTotalRefreshLookUpTable()
        {
            return new byte[]
            {
                (byte)0x02,
                (byte)0x02,
                (byte)0x01,
                (byte)0x11,
                (byte)0x12,
                (byte)0x12,
                (byte)0x22,
                (byte)0x22,
                (byte)0x66,
                (byte)0x69,
                (byte)0x69,
                (byte)0x59,
                (byte)0x58,
                (byte)0x99,
                (byte)0x99,
                (byte)0x88,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0xF8,
                (byte)0xB4,
                (byte)0x13,
                (byte)0x51,
                (byte)0x35,
                (byte)0x51,
                (byte)0x51,
                (byte)0x19,
                (byte)0x01,
                (byte)0x00
            };
        }

        public byte[] getPartialRefreshLookUpTable()
        {
            return new byte[]
            {
                (byte)0x10,
                (byte)0x18,
                (byte)0x18,
                (byte)0x08,
                (byte)0x18,
                (byte)0x18,
                (byte)0x08,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x13,
                (byte)0x14,
                (byte)0x44,
                (byte)0x12,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00
            };
        }
    },
    _2_13
    {
        public int getLittleWidth()
        {
            return 128;//122 en réel mais 128 en logique
        }

        public int getBigHeight()
        {
            return 250;
        }

        public byte[] getTotalRefreshLookUpTable()
        {
            return new byte[]
            {
                (byte)0x22,
                (byte)0x55,
                (byte)0xAA,
                (byte)0x55,
                (byte)0xAA,
                (byte)0x55,
                (byte)0xAA,
                (byte)0x11,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x1E,
                (byte)0x1E,
                (byte)0x1E,
                (byte)0x1E,
                (byte)0x1E,
                (byte)0x1E,
                (byte)0x1E,
                (byte)0x1E,
                (byte)0x01,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00
            };
        }

        public byte[] getPartialRefreshLookUpTable()
        {
            return new byte[]
            {
                (byte)0x18,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x0F,
                (byte)0x01,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00
            };
        }
    },
    ;

    public abstract int getLittleWidth();

    public abstract int getBigHeight();

    public abstract byte[] getTotalRefreshLookUpTable();

    public abstract byte[] getPartialRefreshLookUpTable();
}
