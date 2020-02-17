package sky.terraberry;

public enum RefreshType
{
    PARTIAL_REFRESH
    {
        public RefreshType combine(RefreshType refreshType)
        {
            return refreshType;
        }

        @Override
        public String toString()
        {
            return "partial refresh";
        }
    },
    TOTAL_REFRESH
    {
        public RefreshType combine(RefreshType refreshType)
        {
            return TOTAL_REFRESH;
        }

        @Override
        public String toString()
        {
            return "total refresh";
        }
    },
    ;

    public abstract RefreshType combine(RefreshType refreshType);

    public boolean isPartialRefresh()
    {
        return !isTotalRefresh();
    }

    public boolean isTotalRefresh()
    {
        return this==TOTAL_REFRESH;
    }
}
