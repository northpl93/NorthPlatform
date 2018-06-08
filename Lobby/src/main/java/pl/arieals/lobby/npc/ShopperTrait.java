package pl.arieals.lobby.npc;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import pl.arieals.lobby.gui.bedwars.BwShopMain;
import pl.arieals.lobby.gui.elytrarace.ElytraShopMain;
import pl.arieals.lobby.gui.goldhunter.GoldHunterShopGui;

@TraitName("hubShopper")
public class ShopperTrait extends Trait
{
    private final Shop shopType;

    public ShopperTrait(final Shop shopType)
    {
        super("hubShopper");
        this.shopType = shopType;
    }

    public Shop getShopType()
    {
        return this.shopType;
    }

    @EventHandler
    public void rightClick(final PlayerInteractAtEntityEvent event)
    {
        if (event.getRightClicked() != this.getNPC().getEntity())
        {
            return;
        }

        this.shopType.openForPlayer(event.getPlayer());
    }

    @EventHandler
    public void leftClick(final NPCLeftClickEvent event)
    {
        if (event.getNPC() != this.getNPC())
        {
            return;
        }

        this.shopType.openForPlayer(event.getClicker());
    }

    public static Shop getById(final String id)
    {
        for (final Shop shop : Shop.values())
        {
            if (shop.name().equalsIgnoreCase(id))
            {
                return shop;
            }
        }
        throw new IllegalArgumentException(id);
    }

    public enum Shop
    {
        GOLD_HUNTER("goldhunter", GoldHunterShopGui::openMainGui),
        BED_WARS("bedwars", player -> new BwShopMain(player).open(player)),
        ELYTRA("elytra", player -> new ElytraShopMain(player).open(player));

        private final String gameId;
        private final Consumer<Player> openFunction;

        Shop(final String gameId, final Consumer<Player> openFunction)
        {
            this.gameId = gameId;
            this.openFunction = openFunction;
        }

        public String getGameId()
        {
            return this.gameId;
        }

        public void openForPlayer(final Player player)
        {
            this.openFunction.accept(player);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("shopType", this.shopType).toString();
    }
}
