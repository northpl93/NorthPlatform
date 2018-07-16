package pl.north93.zgame.api.bukkit.protocol.impl;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diorite.commons.reflections.DioriteReflectionUtils;

import io.netty.channel.Channel;

import net.minecraft.server.v1_12_R1.ServerConnection;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

public class ServerConnectionPatcher
{
    private static final Logger logger = LogManager.getLogger();
    
    @Inject
    private static ApiCore apiCore;
    @Inject
    private static ProtocolManagerComponent protocolManager;
    
    public static void patch() throws Exception
    {
        ClassPool classPool = new ClassPool();
        classPool.appendSystemPath();
        classPool.appendClassPath(new LoaderClassPath(ServerConnectionPatcher.class.getClassLoader()));
        
        loadHelperClassViaSystemClassloader(classPool);
        doServerConnectionPatch(classPool);
        
        logger.info("Successfully patched ServerConnection class for ProtocolManagerComponent");
    }
    
    private static void loadHelperClassViaSystemClassloader(ClassPool classPool) throws Exception
    {
        CtClass helperClass = classPool.get(ServerConnectionPatcher.class.getName() + "$InitChannelHelper");
        Class<?> cls = helperClass.toClass(ClassLoader.getSystemClassLoader(), ServerConnectionPatcher.class.getProtectionDomain());
        
        Consumer<Channel> action = ServerConnectionPatcher::onInitializeChannel;
        DioriteReflectionUtils.getField(cls, "action").set(null, action);
    }
    
    private static void doServerConnectionPatch(ClassPool classPool) throws Exception
    {
        CtClass ctClass = classPool.get(ServerConnection.class.getName() + "$4");
        CtMethod initChannel = ctClass.getMethod("initChannel", "(L" + Channel.class.getName().replace('.', '/') + ";)V");
        
        initChannel.insertAfter(InitChannelHelper.class.getName() + ".onInitializeChannel($1);");
        
        apiCore.getInstrumentationClient().redefineClass(ctClass.getName(), ctClass.toBytecode());
    }
    
    public static void onInitializeChannel(Channel channel)
    {
        // this method will be called at the end of channel initializer
        try
        {
            logger.debug("Called onInitializeChannel for {}", channel);
            protocolManager.initChannel(channel);
        }
        catch ( Throwable e )
        {
            logger.error("An error occured whilst initialize channel {}", channel, e);
        }
    }
    
    // we cannot refer any class that isn't loaded with system classloader
    public static class InitChannelHelper
    {
        private static Consumer<Channel> action;
        
        public static void onInitializeChannel(Channel channel)
        {
            action.accept(channel);
        }
        
        static
        {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$ Initialize with " + ( InitChannelHelper.class.getClassLoader() == ClassLoader.getSystemClassLoader() ));
        }
    }
}

