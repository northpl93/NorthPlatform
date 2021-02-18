package pl.north93.northplatform.minigame.elytrarace.listener;


import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.xml.XmlLocation;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.JaxbUtils;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.player.ArenaChatManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.north93.northplatform.minigame.elytrarace.ElytraRaceMode;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRaceArena;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.north93.northplatform.minigame.elytrarace.arena.StartCountdown;
import pl.north93.northplatform.minigame.elytrarace.cfg.ArenaConfig;
import pl.north93.northplatform.minigame.elytrarace.shop.ElytraEffectTask;
import pl.north93.northplatform.minigame.elytrarace.shop.ElytraEffectsManager;
import pl.north93.northplatform.minigame.elytrarace.shop.effects.IElytraEffect;

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

        final ElytraRaceMode elytraRaceMode = ElytraRaceMode.fromVariantId(arena.getMiniGame().getVariantId());
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

        final ArenaChatManager chatManager = arena.getChatManager();
        chatManager.broadcast(this.messages, "separator");
        chatManager.broadcast(this.messages, this.getWelcomeMessageKey(arenaData), MessageLayout.CENTER);
        chatManager.broadcast(this.messages, "separator");

        // odpalamy taska generujacego efekty lotu
        arena.getScheduler().runTaskTimer(new ElytraEffectTask(arena), 2, 2);

        // task odpalający arenę po 15 sekundach
        arena.getScheduler().runAbstractCountdown(new StartCountdown(15, arena), 20);
    }

    private String getWelcomeMessageKey(final ElytraRaceArena arenaData)
    {
        return arenaData.getGameMode() == ElytraRaceMode.SCORE_MODE ? "score.welcome" : "race.welcome";
    }

    private ArenaConfig loadConfig(final LocalArena arena)
    {
        return JaxbUtils.unmarshal(arena.getWorld().getResource("ElytraRaceArena.xml"), ArenaConfig.class);
    }

    private void setupPlayers(final LocalArena arena, final ElytraRaceArena elytraRaceArena)
    {
        final Iterator<XmlLocation> locations = elytraRaceArena.getArenaConfig().getStartLocations().iterator();

        final Set<ElytraRacePlayer> elytraPlayers = elytraRaceArena.getPlayers();
        for (final INorthPlayer player : arena.getPlayersManager().getPlayers())
        {
            final IElytraEffect elytraEffect = this.getEffect(player);
            final Location startLoc = locations.next().toBukkit(arena.getWorld().getCurrentWorld());

            final ElytraRacePlayer elytraRacePlayer;
            if (elytraRaceArena.getGameMode() == ElytraRaceMode.SCORE_MODE)
            {
                // w trybie score ustawiamy graczowi dodatkowy rozszerzony obiekt
                // sledzacy ilosc punktów, combo itp.
                elytraRacePlayer = new ElytraScorePlayer(player, elytraEffect, startLoc);
            }
            else
            {
                // w trybie race ustawiamy standardowy obiekt
                elytraRacePlayer = new ElytraRacePlayer(player, elytraEffect, startLoc);
            }

            elytraPlayers.add(elytraRacePlayer);
            player.setPlayerData(elytraRacePlayer);

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
        itemMeta.setUnbreakable(true);
        elytra.setItemMeta(itemMeta);
        return elytra;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
