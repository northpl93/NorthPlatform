package pl.north93.northplatform.api.bukkit.utils.nms;

import static com.google.common.base.Preconditions.checkNotNull;


import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryMaterials;

import org.bukkit.entity.EntityType;

import org.diorite.commons.reflections.DioriteReflectionUtils;

public final class EntityUtils
{
    private static final RegistryMaterials<MinecraftKey, Class<? extends Entity>> registryMaterials;

    static
    {
        registryMaterials = DioriteReflectionUtils.<RegistryMaterials<MinecraftKey, Class<? extends Entity>>>getField(EntityTypes.class, "b").get(null);
        checkNotNull(registryMaterials, "Field b of EntityTypes is null");
    }

    public static void registerCustomEntity(final String name, final int networkId, final EntityType entityType, final Class<? extends Entity> clazz)
    {
        final MinecraftKey entityKey = new MinecraftKey(name);

        registryMaterials.a(networkId, entityKey, clazz);

        EntityTypes.clsToKeyMap.put(clazz, entityKey);
        EntityTypes.clsToTypeMap.put(clazz, entityType);
    }
}
