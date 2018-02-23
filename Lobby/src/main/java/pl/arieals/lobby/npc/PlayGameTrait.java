package pl.arieals.lobby.npc;

import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.lobby.ui.JoinGameGui;

public class PlayGameTrait extends Trait
{
    private final GameIdentity gameIdentity;

    public PlayGameTrait(final GameIdentity gameIdentity)
    {
        super("playGame");
        this.gameIdentity = gameIdentity;
    }

    public GameIdentity getGameIdentity()
    {
        return this.gameIdentity;
    }

    @EventHandler
    public void rightClick(final NPCRightClickEvent event)
    {
        if (event.getNPC() != this.getNPC())
        {
            return;
        }

        JoinGameGui.openForPlayerAndGame(event.getClicker(), this.gameIdentity);
    }

    @EventHandler
    public void leftClick(final NPCLeftClickEvent event)
    {
        if (event.getNPC() != this.getNPC())
        {
            return;
        }

        JoinGameGui.openForPlayerAndGame(event.getClicker(), this.gameIdentity);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("gameIdentity", this.gameIdentity).toString();
    }
}
