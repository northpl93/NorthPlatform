package pl.north93.zgame.api.bukkit.utils.dmgtracker;

import javax.annotation.Nonnull;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingDeque;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EvictingDeque<E> extends ForwardingDeque<E> // copy of EvictingQueue, but with exposed deque API
{
    private final int      maxSize;
    private final Deque<E> deque;

    public EvictingDeque(final int maxSize)
    {
        this.maxSize = maxSize;
        this.deque = new ArrayDeque<>(maxSize);
    }

    public int remainingCapacity()
    {
        return this.maxSize - this.size();
    }

    @Override
    protected Deque<E> delegate()
    {
        return this.deque;
    }

    @Override
    public boolean offer(final @Nonnull E e)
    {
        return this.add(e);
    }

    @Override
    public boolean add(final @Nonnull E e)
    {
        Preconditions.checkNotNull(e);
        if (this.maxSize == 0)
        {
            return true;
        }
        else
        {
            if (this.size() == this.maxSize)
            {
                this.deque.remove();
            }

            this.deque.add(e);
            return true;
        }
    }

    @Override
    public boolean addAll(final @Nonnull Collection<? extends E> collection)
    {
        return this.standardAddAll(collection);
    }

    @Override
    public boolean contains(final Object object)
    {
        return this.delegate().contains(Preconditions.checkNotNull(object));
    }

    @Override
    public boolean remove(@Nonnull final Object object)
    {
        return this.delegate().remove(Preconditions.checkNotNull(object));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("maxSize", this.maxSize).append("deque", this.deque).toString();
    }
}
