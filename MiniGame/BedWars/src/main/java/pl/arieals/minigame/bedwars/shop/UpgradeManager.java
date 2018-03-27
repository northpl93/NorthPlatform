package pl.arieals.minigame.bedwars.shop;

import static java.text.MessageFormat.format;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BwShopConfig;
import pl.arieals.minigame.bedwars.event.UpgradeInstallEvent;
import pl.arieals.minigame.bedwars.shop.gui.UpgradesGui;
import pl.arieals.minigame.bedwars.shop.upgrade.IUpgrade;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.impl.GuiTracker;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.PluralForm;
import pl.north93.zgame.api.global.uri.UriHandler;

public class UpgradeManager
{
    @Inject
    private Logger        logger;
    @Inject
    private BukkitApiCore apiCore;
    @Inject
    private GuiTracker    guiTracker;
    @Inject
    private BwShopConfig  config;
    @Inject @Messages("BedWarsShop")
    private MessagesBox   shopMessages;
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

    @UriHandler("/minigame/bedwars/upgrade/:name/:playerId/buy")
    public boolean upgradeUri(final String calledUri, final Map<String, String> parameters)
    {
        final IUpgrade upgrade = this.getUpgrade(parameters);
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));

        final LocalArena arena = getArena(player);
        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null || playerData.getTeam() == null)
        {
            this.logger.log(SEVERE, "PlayerData or team is null in upgradeUri {0} on arena {1}", new Object[]{playerData, arena.getId()});
            return false;
        }
        final Team team = playerData.getTeam();
        final int actualLevel = team.getUpgrades().getUpgradeLevel(upgrade);

        final UpgradeInstallEvent event = this.apiCore.callEvent(new UpgradeInstallEvent(arena, team, player, upgrade, actualLevel + 1, true));
        if (event.isCancelled())
        {
            this.logger.log(INFO, "Upgrade {0} installing cancelled for team {1} on arena {2}", new Object[]{upgrade.getName(), team.getName(), arena.getId()});
            return false;
        }

        this.logger.log(INFO, "Installing upgrade {0} for team {1} in arena {2}", new Object[]{upgrade.getName(), team.getName(), arena.getId()});
        team.getUpgrades().installUpgrade(upgrade);
        this.refreshGui(player);
        return true;
    }

    @UriHandler("/minigame/bedwars/upgrade/:name/:playerId/getNameColor")
    public String getNameColor(final String calledUri, final Map<String, String> parameters)
    {
        final IUpgrade upgrade = this.getUpgrade(parameters);
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));

        final LocalArena arena = getArena(player);
        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null || playerData.getTeam() == null)
        {
            this.logger.log(SEVERE, "PlayerData or team is null in getNameColor {0} on arena {1}", new Object[]{playerData, arena.getId()});
            return ChatUtils.COLOR_CHAR + "c";
        }
        final Team team = playerData.getTeam();
        final int actualLevel = team.getUpgrades().getUpgradeLevel(upgrade);

        final UpgradeInstallEvent event = this.apiCore.callEvent(new UpgradeInstallEvent(arena, team, player, upgrade, actualLevel + 1, false));
        if (event.isCancelled())
        {
            return ChatUtils.COLOR_CHAR + "c";
        }

        return ChatUtils.COLOR_CHAR + "a";
    }

    @UriHandler("/minigame/bedwars/upgrade/:name/:playerId/composeLore")
    public String composeLore(final String calledUri, final Map<String, String> parameters)
    {
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));
        final IUpgrade upgrade = this.getUpgrade(parameters);

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        final Team team = playerData.getTeam();
        final int actualLevel = team.getUpgrades().getUpgradeLevel(upgrade);
        final Integer price = upgrade.getPrice(this.config, team);

        final String description = upgrade.getLoreDescription(this.shopMessages, team, player);

        if (actualLevel >= upgrade.maxLevel())
        {
            return this.shopMessages.getLegacyMessage(player.getLocale(),
                    "gui.upgrade_lore.max_level",
                    description);
        }
        else
        {
            final String diamondsWord = this.shopMessages.getLegacyMessage(player.getLocale(), PluralForm.transformKey("currency.diamond", price), price);
            if (player.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), price))
            {
                return this.shopMessages.getLegacyMessage(player.getLocale(),
                        "gui.upgrade_lore.available",
                        description,
                        diamondsWord,
                        actualLevel + 1);
            }
            else
            {
                return this.shopMessages.getLegacyMessage(player.getLocale(),
                        "gui.upgrade_lore.no_diamonds",
                        description,
                        diamondsWord);
            }
        }
    }

    public @Nullable IUpgrade getUpgradeByName(final String upgradeName)
    {
        return this.upgrades.get(upgradeName);
    }

    private IUpgrade getUpgrade(final Map<String, String> parameters)
    {
        final IUpgrade upgrade = this.upgrades.get(parameters.get("name"));
        if (upgrade == null)
        {
            throw new IllegalArgumentException(format("Not found upgrade with name {0}", parameters.get("name")));
        }
        return upgrade;
    }

    private void refreshGui(final Player player)
    {
        final Gui currentGui = this.guiTracker.getCurrentGui(player);
        if (currentGui instanceof UpgradesGui)
        {
            currentGui.markDirty();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("upgrades", this.upgrades).toString();
    }
}

