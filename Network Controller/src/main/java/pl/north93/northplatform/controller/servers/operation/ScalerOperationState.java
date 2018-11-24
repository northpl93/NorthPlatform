package pl.north93.northplatform.controller.servers.operation;

public enum ScalerOperationState
{
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED;

    public boolean isEnded()
    {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
}
