package pl.north93.northplatform.lobby.tutorial.cmd;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.lobby.tutorial.ITutorialManager;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class TutorialCommand extends NorthCommand
{
    @Inject @Messages("UserInterface")
    private MessagesBox      messages;
    @Inject
    private ITutorialManager tutorialManager;

    public TutorialCommand()
    {
        super("tutorial", "samouczek");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        if (this.tutorialManager.isInTutorial(player))
        {
            sender.sendMessage(this.messages, "tutorial.cmd.exited", label);
            this.tutorialManager.exitTutorial(player);
        }
        else if (this.tutorialManager.canStartTutorial(player))
        {
            sender.sendMessage(this.messages, "tutorial.cmd.started", label);
            this.tutorialManager.startTutorial(player);
        }
        else
        {
            sender.sendMessage(this.messages, "tutorial.cmd.no_tutorial");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
