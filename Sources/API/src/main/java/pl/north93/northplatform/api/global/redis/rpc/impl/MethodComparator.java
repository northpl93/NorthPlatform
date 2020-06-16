package pl.north93.northplatform.api.global.redis.rpc.impl;

import java.lang.reflect.Method;
import java.util.Comparator;

class MethodComparator implements Comparator<Method>
{
    @Override
    public int compare(final Method m1, final Method m2)
    {
        final int methodNameCompare = m1.getName().compareTo(m2.getName());
        if (methodNameCompare != 0)
        {
            return methodNameCompare;
        }

        final Class<?>[] parameters1 = m1.getParameterTypes();
        final Class<?>[] parameters2 = m2.getParameterTypes();

        if (parameters1.length != parameters2.length)
        {
            return parameters1.length - parameters2.length;
        }

        for (int i = 0; i < parameters1.length; i++)
        {
            final String parameterType1 = parameters1[i].getName();
            final String parameterType2 = parameters2[i].getName();

            final int parameterTypesCompare = parameterType1.compareTo(parameterType2);
            if (parameterTypesCompare != 0)
            {
                return parameterTypesCompare;
            }
        }

        return 0;
    }
}
