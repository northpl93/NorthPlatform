package pl.north93.zgame.skyplayerexp.server.gui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.api.global.network.PrivateMessages;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyplayerexp.server.ExperienceServer;
import pl.north93.zgame.skyplayerexp.server.compass.CompassConnector;

public class PlayerOptions extends Window
{
    private static final List<String> LORE_LOADING = lore("&7Trwa wczytywanie");
    private static final ItemStack    PLACEHOLDER;
    static
    {
        PLACEHOLDER = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        final ItemMeta itemMeta = PLACEHOLDER.getItemMeta();
        itemMeta.setDisplayName(" ");
        PLACEHOLDER.setItemMeta(itemMeta);
    }

    private final ExperienceServer experience;
    private final Player           player;
    // OPTIONS
    private ItemStack enableCompass;
    private ItemStack enablePrivateMessages;

    public PlayerOptions(final ExperienceServer experience, final Player player)
    {
        super("OPCJE SERWERA", 3 * 9);
        this.experience = experience;
        this.player = player;
    }

    @Override
    protected void onShow()
    {
        {
            this.enableCompass = new ItemStack(Material.WOOL, 1, (short) 15);
            final ItemMeta itemMeta = this.enableCompass.getItemMeta();
            itemMeta.setDisplayName(color("&6Kompas w lobby"));
            itemMeta.setLore(LORE_LOADING);
            this.enableCompass.setItemMeta(itemMeta);
            this.addElement(10, this.enableCompass);
        }

        {
            this.enablePrivateMessages = new ItemStack(Material.WOOL, 1, (short) 15);
            final ItemMeta itemMeta = this.enablePrivateMessages.getItemMeta();
            itemMeta.setDisplayName(color("&6Otrzymywanie prywatnych wiadomosci"));
            itemMeta.setLore(LORE_LOADING);
            this.enablePrivateMessages.setItemMeta(itemMeta);
            this.addElement(12, this.enablePrivateMessages);
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
        final IOnlinePlayer cached = player.get();
        final CompassConnector compassConnector = this.experience.getCompassManager().getCompassConnector();
        final boolean isCompassEnabled = compassConnector.compassEnabledInConfig(cached.getNick());

        {
            if (isCompassEnabled)
            {
                this.enableCompass.setDurability((short) 5);
                final ItemMeta itemMeta = this.enableCompass.getItemMeta();
                itemMeta.setLore(lore("&aWlaczone"));
                this.enableCompass.setItemMeta(itemMeta);
                this.addElement(10, this.enableCompass, ev -> this.switchCompassState(this.player, false));
            }
            else
            {
                this.enableCompass.setDurability((short) 14);
                final ItemMeta itemMeta = this.enableCompass.getItemMeta();
                itemMeta.setLore(lore("&cWylaczone"));
                this.enableCompass.setItemMeta(itemMeta);
                this.addElement(10, this.enableCompass, ev -> this.switchCompassState(this.player, true));
            }
        }

        {
            if (cached.privateMessagesPolicy() == PrivateMessages.ENABLED)
            {
                this.enablePrivateMessages.setDurability((short) 5);
                final ItemMeta itemMeta = this.enablePrivateMessages.getItemMeta();
                itemMeta.setLore(lore("&aWlaczone"));
                this.enablePrivateMessages.setItemMeta(itemMeta);
                this.addElement(12, this.enablePrivateMessages, ev -> this.setPrivateMessagesState(player, PrivateMessages.DISABLED));
            }
            else if (cached.privateMessagesPolicy() == PrivateMessages.DISABLED)
            {
                this.enablePrivateMessages.setDurability((short) 14);
                final ItemMeta itemMeta = this.enablePrivateMessages.getItemMeta();
                itemMeta.setLore(lore("&cWylaczone"));
                this.enablePrivateMessages.setItemMeta(itemMeta);
                this.addElement(12, this.enablePrivateMessages, ev -> this.setPrivateMessagesState(player, PrivateMessages.ENABLED));
            }
        }
    }

    private void switchCompassState(final Player player, final boolean newState)
    {
        final CompassConnector compassConnector = this.experience.getCompassManager().getCompassConnector();
        compassConnector.switchCompassStateInConfig(player.getName(), newState);
        if (compassConnector.isLobby())
        {
            this.experience.getCompassManager().switchCompassState(player, newState);
        }
        this.experience.getServerGuiManager().openServerOptions(this.player);
    }

    private void setPrivateMessagesState(final Value<IOnlinePlayer> player, final PrivateMessages newState)
    {
        player.update(value ->
        {
            value.setPrivateMessagesPolicy(newState);
        });
        this.experience.getServerGuiManager().openServerOptions(this.player);
    }
}
