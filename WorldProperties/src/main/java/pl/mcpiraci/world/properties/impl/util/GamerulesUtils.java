package pl.mcpiraci.world.properties.impl.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import net.minecraft.server.v1_12_R1.GameRules;

import pl.north93.zgame.api.global.utils.lang.CatchException;
import pl.north93.zgame.api.global.utils.lang.MethodHandlesUtils;

public final class GamerulesUtils
{
    private static final MethodHandle GAMERULES_MAP_GETTER = MethodHandlesUtils.unreflectGetter(GameRules.class, "a").asType(MethodType.methodType(Object.class, Object.class));
    private static final MethodHandle GAMERULES_MAP_SETTER = MethodHandlesUtils.unreflectSetter(GameRules.class, "a").asType(MethodType.methodType(Object.class, Object.class, Object.class));
    
    public static void resetGamerules(World world)
    {
        CraftWorld cworld = (CraftWorld) world;
        
        GameRules rules = cworld.getHandle().getGameRules();
        GameRules defaultRules = new GameRules();
        
        CatchException.sneaky(() -> GAMERULES_MAP_SETTER.invoke(rules, GAMERULES_MAP_GETTER.invoke(defaultRules)));
    }
    
    public static Map<String, String> getGameruleValues(World world)
    {
        Map<String, String> result = new HashMap<>();
        
        for ( String gamerule : world.getGameRules() )
        {
            result.put(gamerule, world.getGameRuleValue(gamerule));
        }
        
        return result;
    }
}
