package pl.north93.zgame.api.global.redis.rpc.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import pl.north93.zgame.api.global.redis.rpc.IRpcObjectDescription;

class RpcObjectDescription implements IRpcObjectDescription
{
    private final Class<?>                            classInterface;
    private final Integer                             classId;
    private final Map<Method, RpcMethodDescription>   methodToDescription;
    private final Int2ObjectMap<RpcMethodDescription> methodDesc;

    public RpcObjectDescription(final Class<?> classInterface)
    {
        this.classInterface = classInterface;
        this.classId = this.classInterface.getName().hashCode();
        this.methodToDescription = new HashMap<>();
        this.methodDesc = new Int2ObjectArrayMap<>();
        this.populateMethodList();
    }

    private void populateMethodList()
    {
        final Method[] methods = ReflectUtil.getDeclaredMethodsInOrder(this.classInterface);
        for (int methodId = 0; methodId < methods.length; methodId++)
        {
            final Method method = methods[methodId];
            final RpcMethodDescription rpcMethodDescription = new RpcMethodDescription(methodId, method);

            this.methodToDescription.put(method, rpcMethodDescription);
            this.methodDesc.put(methodId, rpcMethodDescription);
        }
    }

    @Override
    public Integer getClassId()
    {
        return this.classId;
    }

    @Override
    public Integer getMethodId(final Method method)
    {
        return this.methodToDescription.get(method).getId();
    }

    public RpcMethodDescription getMethodDescription(final int methodId)
    {
        return this.methodDesc.get(methodId);
    }

    public RpcMethodDescription getMethodDescription(final Method method)
    {
        return this.methodToDescription.get(method);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("classInterface", this.classInterface).append("classId", this.classId).append("methodToDescription", this.methodToDescription).append("methodDesc", this.methodDesc).toString();
    }
}
