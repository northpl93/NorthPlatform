package pl.arieals.minigame.bedwars.arena.generator;

import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;


import java.util.ArrayList;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.EntityTrackerEntry;
import net.minecraft.server.v1_10_R1.Vector3f;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.netty.buffer.ByteBuf;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorItemConfig;
import pl.north93.zgame.api.bukkit.utils.hologram.IHologram;
import pl.north93.zgame.api.bukkit.utils.hologram.TranslatedLine;
import pl.north93.zgame.api.bukkit.utils.nms.EntityMetaPacketHelper;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

class GeneratorHudHandler
{
    @Inject @Messages("BedWars")
    private MessagesBox               messages;
    private final GeneratorController generator;
    private final boolean             enabled;
    private final IHologram           hologram;
    private final ArmorStand          item;
    private       float               itemPose;

    public GeneratorHudHandler(final GeneratorController generator, final boolean enabled)
    {
        this.generator = generator;
        this.enabled = enabled;
        if (enabled)
        {
            final Location location = generator.getLocation();

            this.hologram = IHologram.create(location.clone().add(0, 3, 0));
            this.item = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 1.3, 0), EntityType.ARMOR_STAND);
            this.item.setVisible(false);
            this.item.setGravity(false);
            this.item.setHelmet(new ItemStack(generator.getGeneratorType().getHudItem()));
            this.item.setCollidable(false);

            final BedWarsGeneratorItemConfig current = this.generator.getEntries().get(0).getCurrent();
            if (current != null)
            {
                // wylaczamy wywalanie powiadomien o  generatorach wlaczonych od
                // poczatku
                current.setAnnounced(true);
            }
        }
        else
        {
            this.hologram = null;
            this.item = null;
        }
    }

    public void tick(final BedWarsGeneratorItemConfig currentItem, final int timer)
    {
        if (! this.enabled)
        {
            return;
        }

        this.checkAnnouncement(currentItem);

        final MessagesBox messages = this.messages;
        this.hologram.setLine(0, new TranslatedLine(messages, "generator.tier", currentItem.getName()));
        this.hologram.setLine(1, new TranslatedLine(messages, "generator.type.nominative." + this.generator.getGeneratorType().getName()));

        if (currentItem.getStartAt() > this.generator.getGameTime())
        {
            this.hologram.setLine(2, new TranslatedLine(messages, "generator.disabled"));
        }
        else
        {
            final int timeTo = (currentItem.getEvery() - timer) / 20;
            this.hologram.setLine(2, new TranslatedLine(messages, "generator.next_item_in", timeTo));
        }
    }

    public void handleItemRotation()
    {
        if (! this.enabled)
        {
            return;
        }

        final EntityTrackerEntry tracker = getTrackerEntry(((CraftArmorStand) this.item).getHandle());
        if (this.itemPose >= 360)
        {
            this.itemPose = 0;
        }
        this.itemPose += 0.5;

        final ByteBuf packet = this.createPacket(this.item.getEntityId(), this.itemPose);
        for (final EntityPlayer trackedPlayer : new ArrayList<>(tracker.trackedPlayers))
        {
            // kopiujemy bufor poniewaz jest on zamykany. (copy)
            trackedPlayer.playerConnection.networkManager.channel.writeAndFlush(packet.copy());
        }
        packet.release(); // zwracamy nasz wzorcowy bytebuf
    }

    private ByteBuf createPacket(final int entityId, final float newRotation)
    {
        final EntityMetaPacketHelper packetHelper = new EntityMetaPacketHelper(entityId);
        packetHelper.addMeta(12, EntityMetaPacketHelper.MetaType.VECTOR, new Vector3f(0, newRotation, 0));
        return packetHelper.complete();
    }

    public void markOverload()
    {
        if (! this.enabled)
        {
            return;
        }

        this.hologram.setLine(2, new TranslatedLine(this.messages, "generator.overload"));
        this.checkAnnouncement(null);
    }

    // Wysyla komunikaty o nowym itemie w generatorze
    private void checkAnnouncement(final BedWarsGeneratorItemConfig item)
    {
        if (! this.enabled)
        {
            return;
        }

        final BedWarsGeneratorItemConfig current;
        if (item == null)
        {
            final GeneratorController.ItemGeneratorEntry itemGeneratorEntry = this.generator.getEntries().get(0);
            current = itemGeneratorEntry.getCurrent();
        }
        else
        {
            current = item;
        }
        if (current.getStartAt() > this.generator.getGameTime())
        {
            return; // nie wywalamy komunikatu jesli generator w ogole nie wystartowal (lapis)
        }

        if (current.isAnnounced())
        {
            return;
        }
        current.setAnnounced(true);

        final PlayersManager playersManager = this.generator.getArena().getPlayersManager();
        for (final Player player : playersManager.getPlayers())
        {
            final String generatorName = this.messages.getMessage(player.spigot().getLocale(), "generator.type.genitive." + this.generator.getGeneratorType().getName());
            this.messages.sendMessage(player, "generator.upgrade", MessageLayout.SEPARATED, generatorName, current.getName());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enabled", this.enabled).append("hologram", this.hologram).toString();
    }
}
