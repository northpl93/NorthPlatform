package pl.north93.northplatform.api.minigame.server.shared.citizens;

import static pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper.toNmsEntity;


import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.destroystokyo.paper.utils.UnsafeUtils;
import com.mojang.authlib.properties.Property;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.DioriteReflectionUtils;
import org.diorite.commons.reflections.FieldAccessor;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.npc.profile.ProfileFetcher;
import net.citizensnpcs.npc.skin.Skin;
import net.citizensnpcs.npc.skin.SkinnableEntity;

/**
 * Trait sluzacy do ustawiania skina na podstawie wlasnych danych tekstur i cyfrowego podpisu
 */
public class SkinTrait extends Trait
{
    private final String texturesData;
    private final String texturesSign;
    private boolean isReSpawn;

    public SkinTrait(final String texturesData, final String texturesSign)
    {
        super("northpl93-skin");
        this.texturesData = texturesData;
        this.texturesSign = texturesSign;
    }

    @Override
    public void onSpawn()
    {
        final boolean shouldRespawn = ! this.isReSpawn;
        this.isReSpawn = true;
        SkinHelper.applySkin(this.npc, this.texturesData, this.texturesSign, shouldRespawn);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("texturesData", this.texturesData).append("texturesSign", this.texturesSign).toString();
    }
}

final class SkinHelper
{
    private static final FieldAccessor<Map> PENDING = DioriteReflectionUtils.getField(Skin.class, "pending");
    private static final FieldAccessor<String> SKIN_NAME = DioriteReflectionUtils.getField(Skin.class, "skinName");
    private static final FieldAccessor<Properties> SKIN_PROPERTIES = DioriteReflectionUtils.getField(Skin.class, "skinData");
    private static final FieldAccessor<UUID> SKIN_ID = DioriteReflectionUtils.getField(Skin.class, "skinId");

    public static void removeFromPending(final SkinnableEntity entity)
    {
        PENDING.get(entity.getSkinTracker().getSkin()).remove(entity);
        ProfileFetcher.reset();
    }

    public static void applySkin(final NPC npc, final String data, final String sign, final boolean respawn)
    {
        final SkinnableEntity skinnable = toNmsEntity(npc.getEntity());

        removeFromPending(skinnable);

        final Skin newSkin;
        try
        {
            newSkin = (Skin) UnsafeUtils.getUnsafe().allocateInstance(Skin.class);
        }
        catch (final InstantiationException e)
        {
            throw new RuntimeException(e);
        }

        SKIN_NAME.set(newSkin, String.valueOf(npc.getId()));

        final Property textures = new Property("textures", data, sign);
        SKIN_PROPERTIES.set(newSkin, textures);

        SKIN_ID.set(newSkin, skinnable.getProfile().getId());

        if (respawn)
        {
            newSkin.applyAndRespawn(skinnable);
        }
        else
        {
            newSkin.apply(skinnable);
        }
    }
}