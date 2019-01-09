package pl.north93.northplatform.api.global.utils;

import java.util.function.Consumer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ObservableValue<T>
{
    private T value;
    private Listener head = new Listener((value) -> {});
    private Listener latest = this.head;

    public T get()
    {
        return this.value;
    }

    public void set(final T value)
    {
        this.value = value;
        this.head.fire(value);
    }

    public void addListener(final Consumer<T> listener)
    {
        final Listener linkedListener = new Listener(listener);
        this.latest.next = linkedListener;
        this.latest = linkedListener;
    }

    private final class Listener
    {
        private final Consumer<T> listener;
        private Listener          next;

        private Listener(final Consumer<T> listener)
        {
            this.listener = listener;
        }

        private void fire(final T newValue)
        {
            this.listener.accept(newValue);
            if (this.next != null)
            {
                this.next.fire(newValue);
            }
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("listener", this.listener).append("next", this.next).toString();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("value", this.value).append("head", this.head).append("latest", this.latest).toString();
    }
}
