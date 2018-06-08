package pl.arieals.lobby.npc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.trait.Trait;
import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.lobby.ui.JoinGameGui;

public class PlayGameTrait extends Trait
{
    private final GameIdentity gameIdentity;
    private final boolean      isDynamic;

    public PlayGameTrait(final GameIdentity gameIdentity, final boolean isDynamic)
    {
        super("playGame");
        this.gameIdentity = gameIdentity;
        this.isDynamic = isDynamic;
    }

    public GameIdentity getGameIdentity()
    {
        return this.gameIdentity;
    }

    public boolean isDynamic()
    {
        return this.isDynamic;
    }

    @EventHandler
    public void rightClick(final PlayerInteractAtEntityEvent event)
    {
        if (event.getRightClicked() != this.getNPC().getEntity())
        {
            return;
        }

        JoinGameGui.openForPlayerAndGame(event.getPlayer(), this.gameIdentity, this.isDynamic);
    }

    @EventHandler
    public void leftClick(final NPCLeftClickEvent event)
    {
        if (event.getNPC() != this.getNPC())
        {
            return;
        }

        JoinGameGui.openForPlayerAndGame(event.getClicker(), this.gameIdentity, this.isDynamic);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("gameIdentity", this.gameIdentity).toString();
    }
}
