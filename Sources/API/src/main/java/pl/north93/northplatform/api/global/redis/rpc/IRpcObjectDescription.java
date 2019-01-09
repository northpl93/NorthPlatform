package pl.north93.northplatform.api.global.redis.rpc;

import java.lang.reflect.Method;

public interface IRpcObjectDescription
{
    Integer getClassId();

    Integer getMethodId(Method method);
}
