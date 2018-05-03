package pl.north93.zgame.api.global.finalizer.impl;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Set;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.sets.ConcurrentSet;

import pl.north93.zgame.api.global.PlatformConnector;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.finalizer.IFinalizer;

public class FinalizerImpl extends Component implements IFinalizer
{
    private static final int QUEUE_CLEANUP_TIME = 10;
    private final ReferenceQueue<Object> queue      = new ReferenceQueue<>();
    private final Set<FinaliseObject>    references = new ConcurrentSet<>(100);

    @Override
    protected void enableComponent()
    {
        final PlatformConnector connector = this.getApiCore().getPlatformConnector();

        // odpalamy zadanie co okreslony czas w itackach
        connector.runTaskAsynchronously(this::handleCleanup, QUEUE_CLEANUP_TIME);
    }

    @Override
    protected void disableComponent()
    {
        // wykonujemy ostateczne czyszczenie po zakonczeniu pracy
        this.handleCleanup();
    }

    @Override
    public void register(final Object object, final Runnable cleanup)
    {
        Preconditions.checkNotNull(object);

        this.references.add(new FinaliseObject(object, this.queue, cleanup));
    }

    @Override
    public int countAliveObjects()
    {
        return this.references.size();
    }

    private void handleCleanup()
    {
        for (Reference reference = this.queue.poll();; reference = this.queue.poll())
        {
            if (reference == null)
            {
                break;
            }

            final FinaliseObject finaliseObject = (FinaliseObject) reference;
            this.references.remove(finaliseObject);

            finaliseObject.runFinalization();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("queue", this.queue).toString();
    }
}
