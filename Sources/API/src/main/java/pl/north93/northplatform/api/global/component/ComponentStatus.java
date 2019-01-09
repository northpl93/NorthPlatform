package pl.north93.northplatform.api.global.component;

public enum ComponentStatus
{
    ENABLED,
    DISABLED,
    ERROR;

    public boolean isEnabled()
    {
        return this == ENABLED;
    }

    public boolean isDisabled()
    {
        return ! this.isEnabled();
    }
}
