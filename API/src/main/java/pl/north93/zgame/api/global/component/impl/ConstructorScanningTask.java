package pl.north93.zgame.api.global.component.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtClass;
import javassist.CtConstructor;

class ConstructorScanningTask extends AbstractScanningTask
{
    private final Set<CtConstructor> constructors;

    public ConstructorScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, ctClass, beanContext);
        this.constructors = new HashSet<>(Arrays.asList(this.ctClass.getConstructors()));
    }

    @Override
    boolean tryComplete()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("constructors", this.constructors).toString();
    }
}
