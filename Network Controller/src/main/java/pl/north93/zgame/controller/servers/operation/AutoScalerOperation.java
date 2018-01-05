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
        try
        {
            if (! this.startOperation())
            {
                this.state = ScalerOperationState.FAILED;
            }
        }
        catch (final Exception e)
        {
            // cos nieprzewidzianego podczas startowania zadania; logujemy i oznaczamy jako FAILED
            e.printStackTrace();
            this.state = ScalerOperationState.FAILED;
        }
    }

    /**
     * Próbuje rozpoczac akcje auto-skalera.
     * W razie niepowodzenia zwraca false, wtedy cala akcja zostaje uznana za nieudana.
     *
     * @return Czy udalo sie rozpoczac akcje autoscalera.
     */
    protected abstract boolean startOperation();

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
     * Aktualizuje stan operacji i go zwraca.
     * W przypadku gdy osiagnieto juz jakikolwiek zakonczony stan to nie zostanie
     * wykonane sprawdzenie.
     *
     * @return stan operacji.
     */
    public final ScalerOperationState refreshState()
    {
        if (this.state.isEnded())
        {
            // jesli zadanie sie zakonczylo (w jakikolwiek sposob) to juz nie aktualizujemy stanu.
            return this.state;
        }

        return this.state = this.checkState();
    }

    /**
     * Uruchamia logikę sprawdzającą stan tej operacji.
     * Metoda będzie wykonywana ciągle w {@link #refreshState()}, aż nie zostanie zwrócony jakikolwiek zakończony stan.
     *
     * @see ScalerOperationState#isEnded()
     * @return Stan operacji auto-skalera.
     */
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
