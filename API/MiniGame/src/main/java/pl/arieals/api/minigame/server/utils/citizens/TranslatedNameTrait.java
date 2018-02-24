package pl.arieals.api.minigame.server.utils.citizens;

import net.minecraft.server.v1_12_R1.EntityPlayer;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import net.citizensnpcs.api.trait.Trait;
import pl.north93.zgame.api.bukkit.hologui.hologram.IHologram;
import pl.north93.zgame.api.bukkit.hologui.hologram.TranslatableStringLine;
import pl.north93.zgame.api.bukkit.utils.nms.EntityTrackerHelper;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class TranslatedNameTrait extends Trait
{
    private final TranslatableString[] nameLines;
    private IHologram hologram;

    public TranslatedNameTrait(final TranslatableString... nameLines)
    {
        super("translatedName");
        this.nameLines = nameLines;
    }

    @Override
    public void onAttach()
    {
        if (this.npc.isSpawned())
        {
            this.setup();
        }
    }

    @Override
    public void onSpawn()
    {
        this.setup();
    }

    @Override
    public void onDespawn()
    {
        this.destroy();
    }

    @Override
    public void onRemove()
    {
        this.destroy();
    }

    private void setup()
    {
        if (this.hologram != null)
        {
            return;
        }

        final Entity entity = this.getNPC().getEntity();
        if (entity.getType() == EntityType.PLAYER)
        {
            final ObservableMap<EntityPlayer, Boolean> observeTracker = EntityTrackerHelper.observeTracker(((CraftEntity) entity).getHandle());
            observeTracker.addListener(this::playerStartedTracking);
        }
        else
        {
            entity.setCustomNameVisible(false);
        }

        this.hologram = IHologram.createWithLowerLocation(entity.getLocation().add(0, entity.getHeight(), 0));
        for (int i = 0; i < this.nameLines.length; i++)
        {
            final int messageId = this.nameLines.length - i - 1;
            this.hologram.setLine(i, new TranslatableStringLine(this.nameLines[messageId]));
        }
    }

    private void destroy()
    {
        if (this.hologram == null)
        {
            return;
        }

        this.hologram.remove();
        this.hologram = null;
    }

    private void playerStartedTracking(final MapChangeListener.Change<? extends EntityPlayer, ? extends Boolean> change)
    {
        final CraftPlayer player = change.getKey().getBukkitEntity();
        final Scoreboard scoreboard = player.getScoreboard();

        if (change.wasRemoved() && change.getValueRemoved() == false)
        {
            return;
        }

        final Team entryTeam = scoreboard.getEntryTeam(this.npc.getName());
        if (entryTeam != null)
        {
            // ten npc juz jest dodany do scoreboardu gracza, nic nie musimy robic
            return;
        }

        final Team team = scoreboard.registerNewTeam(RandomStringUtils.randomAlphanumeric(16));
        team.addEntry(this.npc.getName());
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("nameLines", this.nameLines).toString();
    }
}
