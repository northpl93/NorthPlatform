package pl.north93.zgame.controller.servers.operation;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class AutoScalerOperation
{
    private ScalerOperationState state = ScalerOperationState.NOT_STARTED;

    public final void start()
    {
        Preconditions.checkArgument(this.state == ScalerOperationState.NOT_STARTED, "Operation already started.");

        this.state = ScalerOperationState.IN_PROGRESS;
        this.startOperation();
    }

    protected abstract void startOperation();

    /**
     * Zwraca zcachowany stan tej operacji.
     *
     * @return stan operacji.
     */
    public final ScalerOperationState getCachedState()
    {
        return this.state;
    }

    /**
     * Wymusza sprawdzenie czy operacja osiagnela zadany efekt i zwraca nowy stan.
     *
     * @return stan operacji.
     */
    public final ScalerOperationState refreshState()
    {
        if (this.state.isEnded())
        {
            // jesli zadanie sie zakonczyla (w jakikolwiek sposob) to juz nie aktualizujemy stanu.
            return this.state;
        }

        return this.state = this.checkState();
    }

    protected abstract ScalerOperationState checkState();

    public final boolean tryCancel()
    {
        Preconditions.checkArgument(this.state == ScalerOperationState.IN_PROGRESS, "Operation may be cancelled only when it is in progress!");

        final boolean cancelResult = this.cancel();
        if (cancelResult)
        {
            this.state = ScalerOperationState.CANCELLED;
            return true;
        }

        return false;
    }

    protected abstract boolean cancel();

    /**
     * Sprawdza czy ta operacja jest przeciwna do podanej w argumencie.
     *
     * @param operation Operacja do sprawdzenia.
     * @return Czy jest to operacja przeciwna do tej z argumentu (tzn czy ma odwrotne dzialanie).
     */
    protected abstract boolean isOpposite(AutoScalerOperation operation);

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("state", this.state).toString();
    }
}
