package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.setPlayerData;
import static pl.arieals.minigame.elytrarace.ElytraRaceMode.fromVariantId;


import javax.xml.bind.JAXB;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.minigame.elytrarace.ElytraRaceMode;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.arieals.minigame.elytrarace.arena.StartCountdown;
import pl.arieals.minigame.elytrarace.cfg.ArenaConfig;
import pl.arieals.minigame.elytrarace.shop.ElytraEffectTask;
import pl.arieals.minigame.elytrarace.shop.ElytraEffectsManager;
import pl.arieals.minigame.elytrarace.shop.effects.IElytraEffect;
import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ArenaStartListener implements Listener
{
    @Inject @Messages("ElytraRace")
    private MessagesBox          messages;
    @Inject
    private ElytraEffectsManager effectsManager;

    @EventHandler
    public void loadArenaData(final LobbyInitEvent event)
    {
        final LocalArena arena = event.getArena();

        final ElytraRaceMode elytraRaceMode = fromVariantId(arena.getMiniGame().getVariantId());
        final ElytraRaceArena arenaData = new ElytraRaceArena(this.loadConfig(arena), elytraRaceMode);

        arena.setArenaData(arenaData);
    }

    @EventHandler
    public void teleportToLobbyWhenPlayerJoin(final PlayerJoinArenaEvent event)
    {
        final LocalArena arena = event.getArena();
        final ElytraRaceArena arenaData = arena.getArenaData();

        final Location lobby = arenaData.getArenaConfig().getLobbyLocation().toBukkit(arena.getWorld().getCurrentWorld());

        event.getPlayer().teleport(lobby);
    }

    @EventHandler
    public void startGame(final GameStartEvent event)
    {
        final LocalArena arena = event.getArena();
        final ElytraRaceArena arenaData = arena.getArenaData();
        this.setupPlayers(arena, arenaData);

        final PlayersManager playersManager = arena.getPlayersManager();
        playersManager.broadcast(this.messages, "separator");
        playersManager.broadcast(this.messages, arenaData.getGameMode() == ElytraRaceMode.SCORE_MODE ? "score.welcome" : "race.welcome", MessageLayout.CENTER);
        playersManager.broadcast(this.messages, "separator");

        // odpalamy taska generujacego efekty lotu
        arena.getScheduler().runTaskTimer(new ElytraEffectTask(arena), 2, 2);

        // task odpalający arenę po 15 sekundach
        arena.getScheduler().runAbstractCountdown(new StartCountdown(15, arena), 20);
    }

    private ArenaConfig loadConfig(final LocalArena arena)
    {
        return JAXB.unmarshal(arena.getWorld().getResource("ElytraRaceArena.xml"), ArenaConfig.class);
    }

    private void setupPlayers(final LocalArena arena, final ElytraRaceArena elytraRaceArena)
    {
        final Iterator<XmlLocation> locations = elytraRaceArena.getArenaConfig().getStartLocations().iterator();

        for (final Player player : arena.getPlayersManager().getPlayers())
        {
            final Location startLoc = locations.next().toBukkit(arena.getWorld().getCurrentWorld());

            setPlayerData(player, new ElytraRacePlayer(this.getEffect(player), startLoc));
            if (elytraRaceArena.getGameMode() == ElytraRaceMode.SCORE_MODE)
            {
                // w trybie score ustawiamy graczowi dodatkowy obiekt
                // sledzacy ilosc punktów, combo itp.
                setPlayerData(player, new ElytraScorePlayer());
            }

            player.teleport(startLoc);
            player.getInventory().setChestplate(this.createElytra());

            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    private IElytraEffect getEffect(final Player player)
    {
        return this.effectsManager.getEffect(player);
    }

    private ItemStack createElytra()
    {
        final ItemStack elytra = new ItemStack(Material.ELYTRA);
        final ItemMeta itemMeta = elytra.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        elytra.setItemMeta(itemMeta);
        return elytra;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
