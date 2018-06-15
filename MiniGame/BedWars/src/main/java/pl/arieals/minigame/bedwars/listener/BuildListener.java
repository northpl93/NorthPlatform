package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.north93.zgame.api.global.utils.math.MathUtils.distanceSquared;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.material.Bed;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorController;
import pl.arieals.minigame.bedwars.event.BedDestroyedEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class BuildListener implements Listener
{
    @Inject
    private BukkitApiCore apiCore;
    @Inject @Messages("BedWars")
    private MessagesBox   messages;

    @EventHandler
    public void onCrafting(final CraftItemEvent event) // blokujemy wszelki crafting zeby sami nie zrobili sobie blokow z irona
    {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event)
    {
        final Block block = event.getBlock();
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());

        final LocalArena arena = getArena(block.getWorld());
        if (arena == null || arena.getGamePhase() != GamePhase.STARTED)
        {
            // gdy gra nie wystartowala to nic nie mozna robic
            event.setCancelled(true);
            return;
        }

        final BedWarsArena arenaData = arena.getArenaData();
        if (this.checkGenerator(arenaData, block) || this.checkSecureRegion(arenaData, block))
        {
            player.sendActionBar(this.messages.getMessage(player.getMyLocale(), "no_permissions"));
            event.setCancelled(true);
            return;
        }

        arenaData.getPlayerBlocks().add(block);
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockDestroy(final BlockBreakEvent event)
    {
        final Block block = event.getBlock();
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());

        final LocalArena arena = getArena(block.getWorld());
        if (arena == null || arena.getGamePhase() != GamePhase.STARTED)
        {
            // gdy gra nie wystartowala to nic nie mozna robic
            event.setCancelled(true);
            return;
        }

        final BedWarsArena arenaData = arena.getArenaData();
        final BedWarsPlayer playerData = getPlayerData(event.getPlayer(), BedWarsPlayer.class);
        if (this.checkSecureRegion(arenaData, block))
        {
            player.sendActionBar(this.messages.getMessage(player.getMyLocale(), "no_permissions"));
            event.setCancelled(true);
            return;
        }

        if (this.handleBedBreak(event, arenaData, playerData))
        {
            return;
        }

        if (arenaData.getPlayerBlocks().remove(block))
        {
            return; // ok, block byl na liscie i go usunelismy
        }

        event.setCancelled(true);
        player.sendActionBar(this.messages.getMessage(player.getMyLocale(), "no_permissions"));
    }

    // zwraca true jesli gracz niszczy lozko - wtedy nie wykonujemy reszty kodu w evencie
    private boolean handleBedBreak(final BlockBreakEvent event, final BedWarsArena arenaData, final BedWarsPlayer playerData)
    {
        final Block block = event.getBlock();
        if (block.getType() != Material.BED_BLOCK)
        {
            return false; // nie lozko - nie returnujemy reszty kodu w evencie
        }

        // anulujemy event, blok zniszczymy recznie pozniej
        event.setCancelled(true);
        final Team teamAt = arenaData.getTeamAt(block);
        if (teamAt == playerData.getTeam())
        {
            return true; // gracz nie moze zniszczyc lozka swojej druzyny
        }

        // usuwamy lozko. Gdy gracz zniszczy gorna czesc lozka to dolna dropnie item
        // dlatego sprawdzamy to i najpierw recznie usuwamy dolna czesc
        final Bed bedData = (Bed) block.getState().getData();
        if (bedData.isHeadOfBed())
        {
            final Block lowerPart = block.getRelative(bedData.getFacing().getOppositeFace());
            lowerPart.setType(Material.AIR, false);
        }
        else
        {
            final Block upperPart = block.getRelative(bedData.getFacing());
            upperPart.setType(Material.AIR, false);
        }
        block.setType(Material.AIR, false);

        if (! teamAt.isBedAlive())
        {
            return true; // lozko mozna zniszczyc tylko raz, zabezpieczenie przed bugami Bukkita
        }
        teamAt.setBedAlive(false); // oznaczamy, ze lozko druzyny zostalo zniszczone

        // wywolujemy event zniszczenia lozka
        this.apiCore.callEvent(new BedDestroyedEvent(arenaData.getArena(), playerData.getBukkitPlayer(), block, teamAt));

        return true;
    }

    private boolean checkGenerator(final BedWarsArena arenaData, final Block block)
    {
        for (final GeneratorController generator : arenaData.getGenerators())
        {
            final Location gen = generator.getLocation();
            if (distanceSquared(gen.getBlockX(), gen.getBlockY(), gen.getBlockZ(), block.getX(), block.getY(), block.getZ()) <= 3)
            {
                return true;
            }
        }
        return false;
    }

    // zwraca true jesli blok jest w strefie niemodyfikowalnej
    private boolean checkSecureRegion(final BedWarsArena arenaData, final Block block)
    {
        for (final Team team : arenaData.getTeams())
        {
            if (team.getHealArena().contains(block))
            {
                return true;
            }
        }

        for (final Cuboid cuboid : arenaData.getSecureRegions())
        {
            if (cuboid.contains(block))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
