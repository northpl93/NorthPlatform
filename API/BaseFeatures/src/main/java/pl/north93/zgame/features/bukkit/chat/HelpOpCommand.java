package pl.north93.zgame.features.bukkit.chat;

import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.parseLegacyText;


import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.features.bukkit.chat.admin.AdminChatService;

public class HelpOpCommand extends NorthCommand
{
    @Inject
    private AdminChatService adminChatService;
    @Inject @Messages("BaseFeatures")
    private MessagesBox      messages;

    public HelpOpCommand()
    {
        super("helpop");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            sender.sendMessage(this.messages, "command.helpop.no_msg");
            return;
        }

        final String message = args.asText(0);
        sender.sendMessage(this.messages, "command.helpop.message", message);

        final BaseComponent adminMessage = parseLegacyText("&a[HELPOP] &7{0}: {1}", sender.getName(), message);
        this.adminChatService.broadcast(adminMessage);
    }
}
