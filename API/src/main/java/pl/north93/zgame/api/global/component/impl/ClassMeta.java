package pl.north93.zgame.api.global.component.impl;

import pl.north93.zgame.api.global.component.annotations.SingleInstance;

class ClassMeta
{
    private final Class<?> classRef;
    private final boolean  isSingleton;
    private       boolean  isCreated;

    public ClassMeta(final Class<?> classRef)
    {
        this.classRef = classRef;
        this.isSingleton = classRef.isAnnotationPresent(SingleInstance.class);
    }

    public Class<?> getClassRef()
    {
        return this.classRef;
    }

    public boolean isSingleton()
    {
        return this.isSingleton;
    }

    public boolean isCreated()
    {
        return this.isCreated;
    }

    public void setCreated(final boolean created)
    {
        this.isCreated = created;
    }
}
