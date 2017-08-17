package pl.north93.zgame.api.bukkit.utils.xml.itemstack;

import static org.diorite.utils.reflections.DioriteReflectionUtils.getCanonicalClass;
import static org.diorite.utils.reflections.DioriteReflectionUtils.getField;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.reflections.FieldAccessor;

@XmlRootElement(name = "skullMeta")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlSkullMeta extends XmlItemMeta
{
    private static final Class<?>                   CRAFT_META_SKULL   = getCanonicalClass("org.bukkit.craftbukkit.v1_10_R1.inventory.CraftMetaSkull");
    private static final FieldAccessor<GameProfile> SKULL_GAME_PROFILE = getField(CRAFT_META_SKULL, "profile");

    @XmlElement
    private String owner;
    @XmlElement
    private String textures;
    @XmlElement
    private String sign;

    @Override
    public void apply(final ItemMeta itemMeta)
    {
        super.apply(itemMeta);
        final SkullMeta skullMeta = (SkullMeta) itemMeta;

        if (this.textures != null && this.sign != null)
        {
            this.applyCustomGameProfile(skullMeta, this.textures, this.sign);
        }
        else if (this.owner != null)
        {
            skullMeta.setOwner(this.owner);
        }
    }

    private void applyCustomGameProfile(final SkullMeta skullMeta, final String textures, final String sign)
    {
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "NorthPL93");
        gameProfile.getProperties().put("textures", new Property("textures", textures, sign));

        SKULL_GAME_PROFILE.set(skullMeta, gameProfile);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("owner", this.owner).append("textures", this.textures).append("sign", this.sign).toString();
    }
}
