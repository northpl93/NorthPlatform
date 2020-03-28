package pl.north93.northplatform.minigame.bedwars.shop;

import static java.text.MessageFormat.format;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsPlayer;
import pl.north93.northplatform.minigame.bedwars.arena.Team;
import pl.north93.northplatform.minigame.bedwars.cfg.BwShopConfig;
import pl.north93.northplatform.minigame.bedwars.event.UpgradeInstallEvent;
import pl.north93.northplatform.minigame.bedwars.shop.gui.UpgradesGui;
import pl.north93.northplatform.minigame.bedwars.shop.upgrade.IUpgrade;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.bukkit.gui.impl.GuiTracker;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.LegacyMessage;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.PluralForm;
import pl.north93.northplatform.api.global.uri.UriHandler;
import pl.north93.northplatform.api.global.uri.UriInvocationContext;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

@Slf4j
public class UpgradeManager
{
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
    public boolean upgradeUri(final UriInvocationContext context)
    {
        final IUpgrade upgrade = this.getUpgrade(context);
        final INorthPlayer player = INorthPlayer.get(context.asUuid("playerId"));

        final LocalArena arena = getArena(player);
        if (arena == null)
        {
            // shouldn't happen, but don't throw exception in case
            return false;
        }

        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (playerData == null || playerData.getTeam() == null)
        {
            log.error("PlayerData or team is null in upgradeUri {} on arena {}", playerData, arena.getId());
            return false;
        }

        final Team team = playerData.getTeam();
        final int actualLevel = team.getUpgrades().getUpgradeLevel(upgrade);

        final UpgradeInstallEvent event = this.apiCore.callEvent(new UpgradeInstallEvent(arena, team, player, upgrade, actualLevel + 1, true));
        if (event.isCancelled())
        {
            log.info("Upgrade {} installing cancelled for team {} on arena {}", upgrade.getName(), team.getName(), arena.getId());
            return false;
        }

        log.info("Installing upgrade {} for team {} in arena {}", upgrade.getName(), team.getName(), arena.getId());
        team.getUpgrades().installUpgrade(upgrade);
        this.refreshGui(player);
        return true;
    }

    @UriHandler("/minigame/bedwars/upgrade/:name/:playerId/getNameColor")
    public String getNameColor(final UriInvocationContext context)
    {
        final IUpgrade upgrade = this.getUpgrade(context);
        final INorthPlayer player = INorthPlayer.get(context.asUuid("playerId"));

        final LocalArena arena = getArena(player);
        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (playerData == null || playerData.getTeam() == null)
        {
            log.error("PlayerData or team is null in getNameColor {} on arena {}", playerData, arena.getId());
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
    public LegacyMessage composeLore(final UriInvocationContext context)
    {
        final IUpgrade upgrade = this.getUpgrade(context);
        final INorthPlayer player = INorthPlayer.get(context.asUuid("playerId"));

        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (playerData == null)
        {
            // shouldn't happen, but don't throw exception in case
            return LegacyMessage.EMPTY;
        }

        final Team team = playerData.getTeam();
        final int actualLevel = team.getUpgrades().getUpgradeLevel(upgrade);
        final Integer price = upgrade.getPrice(this.config, team);

        final String description = upgrade.getLoreDescription(this.shopMessages, team, player);

        if (actualLevel >= upgrade.maxLevel())
        {
            return this.shopMessages.getLegacy(player.getLocale(),
                    "gui.upgrade_lore.max_level",
                    description);
        }
        else
        {
            final LegacyMessage diamondsWord = this.shopMessages.getLegacy(player.getLocale(), PluralForm.transformKey("currency.diamond", price), price);
            if (player.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), price))
            {
                return this.shopMessages.getLegacy(player.getLocale(),
                        "gui.upgrade_lore.available",
                        description,
                        diamondsWord,
                        actualLevel + 1);
            }
            else
            {
                return this.shopMessages.getLegacy(player.getLocale(),
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

    private IUpgrade getUpgrade(final UriInvocationContext context)
    {
        final IUpgrade upgrade = this.upgrades.get(context.asString("name"));
        if (upgrade == null)
        {
            throw new IllegalArgumentException(format("Not found upgrade with name {0}", context.asString("name")));
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

