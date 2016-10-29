package pl.north93.zgame.api.standalone;

public abstract class StandaloneApp
{
    /**
     * @return unique identifier of this instance
     */
    public abstract String getId();

    public abstract void start(StandaloneApiCore apiCore);

    public abstract void stop();
}
