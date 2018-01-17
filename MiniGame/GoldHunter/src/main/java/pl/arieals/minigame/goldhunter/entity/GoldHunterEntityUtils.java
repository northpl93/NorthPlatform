package pl.arieals.minigame.goldhunter.entity;

import java.util.Map;

import org.diorite.utils.reflections.DioriteReflectionUtils;
import org.diorite.utils.reflections.FieldAccessor;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityTypes;

public class GoldHunterEntityUtils
{
    public static void registerGoldHunterEntities()
    {
        registerCustomEntity(PoisonArrow.class, 10);
    }
    
    private static void registerCustomEntity(Class<? extends Entity> entityClass, int typeId)
    {
        FieldAccessor<Map<Class<? extends Entity>, String>> d = DioriteReflectionUtils.getField(EntityTypes.class, "d");
        FieldAccessor<Map<Class<? extends Entity>, Integer>> f = DioriteReflectionUtils.getField(EntityTypes.class, "f");
        
        d.get(null).put(entityClass, entityClass.getSimpleName());
        f.get(null).put(entityClass, typeId);
    }
}
