package pl.north93.northplatform.minigame.goldhunter.gui;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class GoldHunterGui extends Gui
{
    @Inject
    @Messages("gh_gui")
    protected static MessagesBox messages;
    
    protected final GoldHunterPlayer player;
    
    protected GoldHunterGui(GoldHunterPlayer player, String layout)
    {
        super(messages, layout);
        this.player = player;
    }
    
    public GoldHunterPlayer getPlayer()
    {
        return player;
    }
    
    public final void open()
    {
        open(player.getPlayer());
    }
    
    @Override
    protected void onOpen(Player player)
    {
        Preconditions.checkState(this.player.getPlayer().equals(player));
    }
}
