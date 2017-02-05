package pl.north93.zgame.skyplayerexp.server.gui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.windows.ClickHandler;
import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.IslandRole;
import pl.north93.zgame.skyblock.api.NorthBiome;
import pl.north93.zgame.skyblock.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyplayerexp.server.ExperienceServer;

public class IslandOptions extends Window
{
    private static final List<String> LORE_LOADING = lore("&7Trwa wczytywanie");
    private static final ItemStack PLACEHOLDER;
    static
    {
        PLACEHOLDER = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        final ItemMeta itemMeta = PLACEHOLDER.getItemMeta();
        itemMeta.setDisplayName(" ");
        PLACEHOLDER.setItemMeta(itemMeta);
    }
    private final ExperienceServer experience;
    private final Player           player;
    // items
    private ItemStack islandInfo;
    private ItemStack isInvitesAllowed;
    private ItemStack isRankingEnabled;

    public IslandOptions(final ExperienceServer experience, final Player player)
    {
        super("WYSPA", 3 * 9);
        this.experience = experience;
        this.player = player;
    }

    @Override
    protected void onShow()
    {
        {
            this.islandInfo = new ItemStack(Material.SIGN);
            final ItemMeta itemMeta = this.islandInfo.getItemMeta();
            itemMeta.setDisplayName(color("&6Wczytywanie"));
            itemMeta.setLore(LORE_LOADING);
            this.islandInfo.setItemMeta(itemMeta);
            this.addElement(10, this.islandInfo);
        }

        {
            this.isInvitesAllowed = new ItemStack(Material.WOOL, 1, (short) 15);
            final ItemMeta itemMeta = this.isInvitesAllowed.getItemMeta();
            itemMeta.setDisplayName(color("&6Odwiedzanie przez innych graczy"));
            itemMeta.setLore(LORE_LOADING);
            this.isInvitesAllowed.setItemMeta(itemMeta);
            this.addElement(12, this.isInvitesAllowed);
        }

        {
            this.isRankingEnabled = new ItemStack(Material.WOOL, 1, (short) 15);
            final ItemMeta itemMeta = this.isRankingEnabled.getItemMeta();
            itemMeta.setDisplayName(color("&6Pokazywanie wyspy w rankingu"));
            itemMeta.setLore(LORE_LOADING);
            this.isRankingEnabled.setItemMeta(itemMeta);
            this.addElement(14, this.isRankingEnabled);
        }

        {
            final ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(color("&6Menu serwera"));
            itemStack.setItemMeta(itemMeta);
            this.addElement(18, itemStack, event -> this.experience.getServerGuiManager().openServerMenu(this.player));
        }

        this.fillEmpty(PLACEHOLDER);
    }

    public void loadAsyncData(final Value<IOnlinePlayer> player)
    {
        final SkyPlayer skyPlayer = SkyPlayer.get(player);
        if (! skyPlayer.hasIsland())
        {
            this.close();
            return;
        }

        final IslandData data = this.experience.getSkyBlock().getIslandDao().getIsland(skyPlayer.getIslandId());
        final IslandConfig islandType = this.experience.getSkyBlock().getSkyBlockConfig().getIslandType(data.getIslandType());
        final int size = islandType.getRadius() * 2;
        final long islandRankingPos = this.experience.getSkyBlock().getIslandsRanking().getPosition(data.getIslandId());

        {
            final ItemMeta itemMeta = this.islandInfo.getItemMeta();
            itemMeta.setDisplayName(color("&6" + data.getName()));
            itemMeta.setLore(lore("&7Rozmiar: &6" + size + "x" + size,
                                  "&7Biom: &6" + this.translateBiomeName(data.getBiome()),
                                  "&7Punkty: &6" + data.getPoints().intValue() + " &7(miejsce &6" + (islandRankingPos + 1) + "&7)"));
            this.islandInfo.setItemMeta(itemMeta);
            this.addElement(10, this.islandInfo);
        }

        {
            final ItemMeta itemMeta = this.isInvitesAllowed.getItemMeta();
            final ClickHandler action;
            if (data.getAcceptingVisits())
            {
                this.isInvitesAllowed.setDurability((short) 5);
                itemMeta.setLore(lore("&aWlaczone"));
                action = ev -> this.acceptingVisits(skyPlayer, false);
            }
            else
            {
                this.isInvitesAllowed.setDurability((short) 14);
                itemMeta.setLore(lore("&cWylaczone"));
                action = ev -> this.acceptingVisits(skyPlayer, true);
            }
            this.isInvitesAllowed.setItemMeta(itemMeta);
            this.addElement(12, this.isInvitesAllowed, action);
        }

        {
            final ItemMeta itemMeta = this.isRankingEnabled.getItemMeta();
            final ClickHandler action;
            if (data.getShowInRanking())
            {
                this.isRankingEnabled.setDurability((short) 5);
                itemMeta.setLore(lore("&aWlaczone"));
                action = ev -> this.rankingSwitch(skyPlayer, false);
            }
            else
            {
                this.isRankingEnabled.setDurability((short) 14);
                itemMeta.setLore(lore("&cWylaczone"));
                action = ev -> this.rankingSwitch(skyPlayer, true);
            }
            this.isRankingEnabled.setItemMeta(itemMeta);
            this.addElement(14, this.isRankingEnabled, action);
        }
    }

    private void acceptingVisits(final SkyPlayer player, final boolean newValue)
    {
        if (player.getIslandRole() == IslandRole.MEMBER)
        {
            return;
        }
        this.experience.getSkyBlock().getIslandDao().modifyIsland(player.getIslandId(), islandData ->
        {
            islandData.setAcceptingVisits(newValue);
        });
        this.experience.getServerGuiManager().openIslandOptions(this.player);
    }

    private void rankingSwitch(final SkyPlayer player, final boolean newValue)
    {
        if (player.getIslandRole() == IslandRole.MEMBER)
        {
            return;
        }
        this.experience.getSkyBlock().getSkyBlockManager().setShowInRanking(player.getIslandId(), newValue);
        this.experience.getServerGuiManager().openIslandOptions(this.player);
    }

    private String translateBiomeName(final NorthBiome northBiome)
    {
        switch (northBiome)
        {
            case NETHER:
                return "Pieklo (Nether)";
            case THE_END:
                return "Kres (The End)";
            case OVERWORLD:
                return "Normalny";
        }
        throw new AssertionError();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
