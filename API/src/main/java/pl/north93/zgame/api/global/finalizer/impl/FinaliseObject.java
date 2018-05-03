package pl.north93.zgame.api.global.finalizer.impl;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/*default*/ class FinaliseObject extends PhantomReference<Object>
{
    private final Runnable finalize;

    public FinaliseObject(final Object referent, final ReferenceQueue<? super Object> q, final Runnable finalize)
    {
        super(referent, q);
        this.finalize = finalize;
    }

    public void runFinalization()
    {
        this.finalize.run();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
