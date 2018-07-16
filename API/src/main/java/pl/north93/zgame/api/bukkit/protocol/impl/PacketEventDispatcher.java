package pl.north93.zgame.api.bukkit.protocol.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.Packet;

import pl.north93.zgame.api.bukkit.protocol.HandlerPriority;
import pl.north93.zgame.api.bukkit.protocol.PacketEvent;
import pl.north93.zgame.api.global.utils.lang.ListUtils;
import pl.north93.zgame.api.global.utils.lang.SneakyThrow;

public class PacketEventDispatcher
{
    private final Map<Class<? extends Packet<?>>, List<PacketEventHandler>> handlersByPacketType = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    
    PacketEventDispatcher()
    {
    }
    
    public void addMethod(Method method, Object instance, HandlerPriority priority)
    {
        lock.writeLock().lock();
        try
        {
            Class<? extends Packet<?>> packetType = findPacketType(method);
            
            method.setAccessible(true);
            MethodHandle methodHandle = SneakyThrow.sneaky(() -> MethodHandles.lookup().unreflect(method));
            
            PacketEventHandler packetEventHandler = new PacketEventHandler(methodHandle, instance, priority);
            
            List<PacketEventHandler> handlers = handlersByPacketType.computeIfAbsent(packetType, p -> new CopyOnWriteArrayList<>());
            ListUtils.insertSorted(handlers, packetEventHandler);
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }
    
    @SuppressWarnings("unchecked")
    private Class<? extends Packet<?>> findPacketType(Method method)
    {
        Preconditions.checkArgument(method.getReturnType() == void.class, "PacketHandler method has invalid return type - must be void");
        
        Class<?>[] argTypes = method.getParameterTypes();
        if ( argTypes.length != 1 || !PacketEvent.class.isAssignableFrom(argTypes[0]) )
        {
            throw new IllegalArgumentException("Invalid packet type method signature - must be one argument with type PacketEvent");
        }
        
        Type argType = method.getGenericParameterTypes()[0];
        Preconditions.checkArgument(argType instanceof ParameterizedType, "Packet handler argument must be parameterized type with packet class");
        
        Type typeArgument = ((ParameterizedType) argType).getActualTypeArguments()[0];
        Preconditions.checkArgument(typeArgument instanceof Class, "Packet handler argument must be parameterized type with packet class");
        
        Class<? extends Packet<?>> packetType = (Class<? extends Packet<?>>) typeArgument;
        Preconditions.checkState(Packet.class.isAssignableFrom(packetType));
        
        return packetType;
    }
    
    @SuppressWarnings("rawtypes")
    public void dispatch(Class<? extends Packet> packetType, PacketEvent<? extends Packet<?>> event)
    {
        lock.readLock().lock();
        try
        {
            List<PacketEventHandler> handlers = handlersByPacketType.get(packetType);
            
            if ( handlers != null )
            {
                handlers.forEach(handler -> handler.dispatch(event));
            }
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
}

class PacketEventHandler implements Comparable<PacketEventHandler>
{
    private static final Logger logger = LogManager.getLogger();
    
    private final MethodHandle method;
    private final Object instance;
    private final HandlerPriority priority;
    
    PacketEventHandler(MethodHandle method, Object instance, HandlerPriority priority)
    {
        this.method = method;
        this.instance = instance;
        this.priority = priority;
    }
    
    void dispatch(PacketEvent<?> event)
    {
        try
        {
            if ( instance != null )
            {
                method.invoke(instance, event);
            }
            else
            {
                method.invoke(event);
            }
        }
        catch ( Throwable e )
        {
            logger.error("An exception was thrown when executing packet handler:", e);
        }
    }
    
    @Override
    public int compareTo(PacketEventHandler o)
    {
        return Integer.compare(this.priority.ordinal(), o.priority.ordinal());
    }
}
