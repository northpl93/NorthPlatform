package pl.arieals.minigame.bedwars.arena.generator;

import static pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper.getTrackerEntry;


import java.util.ArrayList;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;
import net.minecraft.server.v1_12_R1.Vector3f;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.netty.buffer.ByteBuf;
import pl.arieals.api.minigame.server.gamehost.arena.player.PlayersManager;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.cfg.BwGeneratorItemConfig;
import pl.arieals.minigame.bedwars.cfg.BwGeneratorType;
import pl.north93.zgame.api.bukkit.utils.nms.EntityMetaPacketHelper;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

class GeneratorHudHandlerImpl implements IGeneratorHudHandler
{
    @Inject @Messages("BedWars")
    private       MessagesBox         messages;
    private final GeneratorController generator;
    private final GeneratorHologram   hologram;
    private final ArmorStand          item;
    private       float               itemPose;

    public GeneratorHudHandlerImpl(final GeneratorController generator)
    {
        this.generator = generator;
        this.hologram = new GeneratorHologram(generator);

        final Location location = generator.getLocation();

        // ArmorStand nie zespawnuje sie gdy arena nie ma skonfigurowanej listy chunków.
        this.item = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 1.3, 0), EntityType.ARMOR_STAND);
        this.item.setVisible(false);
        this.item.setGravity(false);
        this.item.setHelmet(new ItemStack(generator.getGeneratorType().getHudItem()));
        this.item.setMarker(true);

        final BwGeneratorItemConfig current = this.generator.getEntries().get(0).getCurrent();
        if (current != null)
        {
            // wylaczamy wywalanie powiadomien o  generatorach wlaczonych od
            // poczatku
            this.generator.getArenaData().getAnnouncedItems().add(current);
        }
    }

    @Override
    public void tick(final BwGeneratorItemConfig currentItem, final int timer)
    {
        this.hologram.update(currentItem, timer);
        this.checkAnnouncement(currentItem);
    }

    @Override
    public void handleItemRotation()
    {
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

    @Override
    public void markOverload()
    {
        this.hologram.overload();
        this.checkAnnouncement(null);
    }

    // Wysyla komunikaty o nowym itemie w generatorze
    private void checkAnnouncement(final BwGeneratorItemConfig item)
    {
        final BwGeneratorItemConfig current;
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

        final BedWarsArena arenaData = this.generator.getArenaData();
        if (arenaData.getAnnouncedItems().contains(current))
        {
            return;
        }
        arenaData.getAnnouncedItems().add(current);

        final PlayersManager playersManager = this.generator.getArena().getPlayersManager();
        for (final Player player : playersManager.getPlayers())
        {
            final BwGeneratorType type = this.generator.getGeneratorType();
            final String generatorName = this.messages.getString(player.getLocale(), "generator.type.genitive." + type.getName());

            final long generatorsCount = this.countSameGenerators();
            final String msgName = generatorsCount == 1 ? "generator.upgrade.singular" : "generator.upgrade.plural";

            this.messages.sendMessage(player, msgName, MessageLayout.SEPARATED, generatorName, current.getName());
        }
    }

    private long countSameGenerators()
    {
        final BwGeneratorType type = this.generator.getGeneratorType();
        final BedWarsArena arenaData = this.generator.getArena().getArenaData();
        return arenaData.getGenerators().stream().filter(gen -> gen.getGeneratorType().equals(type)).count();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("hologram", this.hologram).toString();
    }
}
