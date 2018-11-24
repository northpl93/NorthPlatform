package pl.north93.northplatform.features.bukkit.chat;

import static pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils.parseLegacyText;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.features.bukkit.chat.admin.AdminChatService;

public class HelpOpCommand extends NorthCommand
{
    @Inject
    private AdminChatService adminChatService;
    @Inject @Messages("BaseFeatures")
    private MessagesBox      messages;

    public HelpOpCommand()
    {
        super("helpop", "report", "modreq");
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

        final BaseComponent adminMessage = prepareMessage(sender, message);
        this.adminChatService.broadcast(adminMessage);
    }
    
    private BaseComponent prepareMessage(NorthCommandSender sender, String message)
    {
    	TextComponent result = new TextComponent("[HP] ");
    	result.setColor(ChatColor.GREEN);
    	result.setBold(true);
    	
    	TextComponent senderName = new TextComponent(sender.getName());
    	senderName.setColor(ChatColor.GRAY);
    	senderName.setBold(false);
    	
    	senderName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/hr " + sender.getName() + " "));
    	
    	TextComponent msg = new TextComponent(parseLegacyText("§7: §a" + message));
    	
    	result.addExtra(senderName);
    	result.addExtra(msg);
    	
    	return result;
    }
}
