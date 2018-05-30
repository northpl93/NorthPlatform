package pl.mcpiraci.world.properties;

public enum Weather
{
    SUNNY(false, false),
    RAIN(true, false),
    THUNDERING(false, true),
    STORM(true, true);
    ;
    
    private final boolean rain;
    private final boolean thundering;
    
    private Weather(boolean rain, boolean thundering)
    {
        this.rain = rain;
        this.thundering = thundering;
    }
    
    public boolean hasRain()
    {
        return rain;
    }
    
    public boolean hasThundering()
    {
        return thundering;
    }
}
