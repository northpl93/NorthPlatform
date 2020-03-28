package pl.north93.northplatform.api.global.redis.rpc;

import java.lang.reflect.Method;

public interface IRpcObjectDescription
{
    int getClassId();

    int getMethodId(Method method);
}
