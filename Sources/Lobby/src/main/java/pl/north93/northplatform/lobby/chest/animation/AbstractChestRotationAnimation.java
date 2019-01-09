package pl.north93.northplatform.lobby.chest.animation;

import net.minecraft.server.v1_12_R1.Vector3f;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;

import io.netty.buffer.ByteBuf;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.nms.EntityMetaPacketHelper;

abstract class AbstractChestRotationAnimation extends ChestAnimation
{
    public AbstractChestRotationAnimation(final AnimationInstance instance)
    {
        super(instance);
    }

    protected final void setRotation(final float newRotation)
    {
        final ArmorStand armorStand = this.getArmorStand();

        // bytebuf zostanie zwolniony w metodzie write
        final ByteBuf packet = this.createPacket(armorStand.getEntityId(), newRotation);

        final CraftPlayer craftPlayer = INorthPlayer.asCraftPlayer(this.getPlayer());
        craftPlayer.getHandle().playerConnection.networkManager.channel.writeAndFlush(packet);
    }

    private ByteBuf createPacket(final int entityId, final float newRotation)
    {
        final EntityMetaPacketHelper packetHelper = new EntityMetaPacketHelper(entityId);
        packetHelper.addMeta(12, EntityMetaPacketHelper.MetaType.VECTOR, new Vector3f(0, newRotation, 0));
        return packetHelper.complete();
    }
}
