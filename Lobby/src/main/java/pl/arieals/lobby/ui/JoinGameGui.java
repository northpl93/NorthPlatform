package pl.arieals.lobby.ui;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.lobby.play.PlayGameController;
import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.event.GuiClickEvent;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.commands.annotation.QuickCommand;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class JoinGameGui extends Gui
{
    @Inject @Messages("UserInterface")
    private static MessagesBox        messages;
    @Inject
    private static PlayGameController playController;
    private final GameIdentity gameIdentity;
    private final boolean      isDynamic;

    public JoinGameGui(final GameIdentity gameIdentity, final boolean isDynamic)
    {
        super(messages, getGuiName(gameIdentity));
        this.gameIdentity = gameIdentity;
        this.isDynamic = isDynamic;
    }

    public static void openForPlayerAndGame(final Player player, final GameIdentity gameIdentity, final boolean isDynamicGame)
    {
        new JoinGameGui(gameIdentity, isDynamicGame).open(player);
    }

    @ClickHandler
    public void play(final GuiClickEvent event)
    {
        playController.playGame(event.getWhoClicked(), this.gameIdentity, this.isDynamic, null);
    }

    private static String getGuiName(final GameIdentity gameIdentity)
    {
        return "playflow/play_" + gameIdentity.getGameId() + "_" + gameIdentity.getVariantId();
    }

    @QuickCommand(name = "testbwsolo")
    public static void testCmd(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        new JoinGameGui(GameIdentity.create("bedwars", "solo"), false).open(player);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("gameIdentity", this.gameIdentity).toString();
    }
}
