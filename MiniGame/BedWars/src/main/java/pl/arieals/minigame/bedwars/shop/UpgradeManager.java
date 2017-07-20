package pl.arieals.minigame.bedwars.shop;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.shop.upgrade.IUpgrade;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.arieals.minigame.bedwars.event.UpgradeInstallEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.uri.UriHandler;

public class UpgradeManager
{
    @Inject
    private Logger        logger;
    @Inject
    private BukkitApiCore apiCore;
    @Inject
    private BwConfig      config;
    private Map<String, IUpgrade> upgrades = new HashMap<>();

    @Bean
    private UpgradeManager()
    {
    }

    @Aggregator(IUpgrade.class)
    private void collectUpgrades(final IUpgrade upgrade)
    {
        this.upgrades.put(upgrade.getClass().getSimpleName(), upgrade);
    }

    @UriHandler("/minigame/bedwars/upgrade/:name/:playerId")
    public boolean upgradeUri(final String calledUri, final Map<String, String> parameters)
    {
        final IUpgrade upgrade = this.upgrades.get(parameters.get("name"));
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));

        if (upgrade == null)
        {
            this.logger.log(Level.SEVERE, "Not found upgrade with name {0}", parameters.get("name"));
            return false;
        }

        final LocalArena arena = getArena(player);
        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null || playerData.getTeam() == null)
        {
            this.logger.log(Level.WARNING, "PlayerData or team is null in upgradeUri {0}", playerData);
            return false;
        }
        final Team team = playerData.getTeam();
        final int actualLevel = team.getUpgrades().getUpgradeLevel(upgrade);

        final UpgradeInstallEvent event = this.apiCore.callEvent(new UpgradeInstallEvent(arena, team, player, upgrade, actualLevel + 1));
        if (event.isCancelled())
        {
            return false;
        }

        this.logger.log(Level.INFO, "Installing upgrade {0} for team {1} in arena {2}", new Object[]{upgrade.getName(), team.getName(), arena.getId()});
        team.getUpgrades().installUpgrade(upgrade);
        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("upgrades", this.upgrades).toString();
    }
}
