package pl.north93.zgame.controller.servers.groups;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.daemon.config.ManagedServersGroupConfig;
import pl.north93.zgame.api.global.network.server.group.ServersGroupDto;
import pl.north93.zgame.controller.servers.operation.AutoScalerOperation;
import pl.north93.zgame.controller.servers.operation.OperationCommitter;
import pl.north93.zgame.controller.servers.operation.ScalerOperationState;

public class LocalManagedServersGroup extends AbstractLocalServersGroup<ManagedServersGroupConfig>
{
    private static final int HICCUP_PREVENT_TIME = 15;
    private Instant lastOperation = Instant.now();
    private boolean autoEnabled = true;
    private Set<AutoScalerOperation> operations = new HashSet<>();

    public LocalManagedServersGroup(final ServersGroupDto dto, final ManagedServersGroupConfig config)
    {
        super(dto, config);
    }

    /**
     * Zwraca KOPIE liste operacji bedacych w trakcie w tej grupie serwerow.
     *
     * Zsynchronizowane na this poniewaz operuje na wewnetrznej liscie operacji.
     *
     * @return operacje bedace w trakcie.
     */
    public Set<AutoScalerOperation> getOperations()
    {
        return Collections.unmodifiableSet(this.getOperations0());
    }

    // aktualizuje liste operacji i ja zwraca
    private synchronized Set<AutoScalerOperation> getOperations0()
    {
        this.operations.removeIf(operation -> operation.refreshState().isEnded());
        return this.operations;
    }

    public void setAutoOperationsEnabled(final boolean enabled)
    {
        this.autoEnabled = enabled;
    }

    /**
     * Sprawdza czy ta grupa serwerow powinna zostac obsluzona automatycznie.
     *
     * Jesli nie, to zadne automatyczne akcje (utworzenie/usuniecie) serwera nie
     * powinny zostac podjete. Ciagle mozliwe jest reczne sterowanie.
     *
     * @return czy wlaczone jest automatyczne zarzadzanie serwerami.
     */
    public boolean shouldBeTicked()
    {
        return this.autoEnabled && Duration.between(this.lastOperation, Instant.now()).get(ChronoUnit.SECONDS) > HICCUP_PREVENT_TIME;
    }

    /**
     * Dodaje nowa operacja do tej grupy serwerow.
     *
     * Zsynchronizowane na this poniewaz operuje na wewnetrznej liscie operacji.
     *
     * @param operation Operacja do przeprowadzenia.
     * @return Czy udalo sie rozpoczac akcje.
     */
    public synchronized boolean commitOperation(final AutoScalerOperation operation)
    {
        Preconditions.checkArgument(operation.getCachedState() == ScalerOperationState.NOT_STARTED);

        final Set<AutoScalerOperation> operations = this.getOperations0();
        final OperationCommitter committer = new OperationCommitter(operations);

        final boolean success = committer.commit(operation);
        if (success)
        {
            this.lastOperation = Instant.now();
        }

        return success;
    }

    @Override
    public void init()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("lastOperation", this.lastOperation).append("autoEnabled", this.autoEnabled).append("operations", this.operations).toString();
    }
}
