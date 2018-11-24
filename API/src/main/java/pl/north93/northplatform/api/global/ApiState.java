package pl.north93.northplatform.api.global;

public enum ApiState
{
    CONSTRUCTED,
    INITIALISED,
    ENABLED,
    DISABLED;

    public boolean isEnabled()
    {
        return this == ENABLED;
    }

    public boolean isDisabled()
    {
        return this == DISABLED;
    }
}
