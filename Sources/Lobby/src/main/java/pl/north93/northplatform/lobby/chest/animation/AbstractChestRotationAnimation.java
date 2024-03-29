package pl.north93.northplatform.lobby.chest.animation;

import static pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper.toNmsPlayer;


import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.Vector3f;

import org.bukkit.entity.ArmorStand;

import io.netty.buffer.ByteBuf;
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

        final EntityPlayer entityPlayer = toNmsPlayer(this.getPlayer());
        entityPlayer.playerConnection.networkManager.channel.writeAndFlush(packet);
    }

    private ByteBuf createPacket(final int entityId, final float newRotation)
    {
        final EntityMetaPacketHelper packetHelper = new EntityMetaPacketHelper(entityId);
        packetHelper.addMeta(12, EntityMetaPacketHelper.MetaType.VECTOR, new Vector3f(0, newRotation, 0));
        return packetHelper.complete();
    }
}
