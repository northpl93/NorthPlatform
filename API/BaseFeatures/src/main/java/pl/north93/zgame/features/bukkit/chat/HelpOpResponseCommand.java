package pl.north93.zgame.features.bukkit.chat;

import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.parseLegacyText;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.features.bukkit.chat.admin.AdminChatService;

public class HelpOpResponseCommand extends NorthCommand
{
	@Inject
    private INetworkManager networkManager;
    @Inject
    private AdminChatService adminChatService;
    @Inject
    @Messages("BaseFeatures")
    private MessagesBox messages;

    public HelpOpResponseCommand()
    {
        super("hr");
        setPermission("api.command.hr");
        setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if ( args.length() < 2 )
        {
            sender.sendMessage(this.messages, "command.hr.usage");
            return;
        }

        Value<IOnlinePlayer> receiverValue = this.networkManager.getPlayers().unsafe().getOnlineValue(args.asString(0));
        if ( !receiverValue.isPreset() )
        {
        	sender.sendMessage(this.messages, "command.hr.no_player");
        	return;
        }
        
        IOnlinePlayer receiver = receiverValue.get();
        
        String message = args.asText(1);
        
        receiver.sendMessage(prepareReceiverMessage(sender, receiver, message));
        
        BaseComponent adminMessage = prepareAdminMessage(sender, receiver.getNick(), message);
        this.adminChatService.broadcast(adminMessage);
    }
    
    private BaseComponent prepareReceiverMessage(NorthCommandSender sender, IOnlinePlayer receiver, String message)
    {
    	TextComponent result = new TextComponent("[HP] ");
    	result.setColor(ChatColor.GREEN);
    	result.setBold(true);
    	
    	TextComponent senderName = new TextComponent(sender.getName());
    	senderName.setColor(ChatColor.DARK_RED);
    	senderName.setBold(true);
    	
    	result.addExtra(senderName);
    	result.addExtra(" ");
    	
    	TextComponent line = new TextComponent("--");
    	line.setStrikethrough(true);
    	
    	result.addExtra(line);
    	result.addExtra("> ");
    	
    	TextComponent msg = new TextComponent(parseLegacyText("§a" + message));
    	
    	result.addExtra(msg);
    	
    	BaseComponent[] hoverText = new BaseComponent[] { messages.getMessage(receiver.getMyLocale(), "command.helpop.click_to_reply", new Object[0]) };
    	result.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
    	result.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/helpop "));
    	
    	return result;
    }
    
    private BaseComponent prepareAdminMessage(NorthCommandSender sender, String receiver, String message)
    {
    	TextComponent result = new TextComponent("[HP] ");
    	result.setColor(ChatColor.GREEN);
    	result.setBold(true);
    	
    	TextComponent senderName = new TextComponent(sender.getName());
    	senderName.setColor(ChatColor.DARK_RED);
    	senderName.setBold(true);
    	
    	result.addExtra(senderName);
    	result.addExtra(" ");
    	
    	TextComponent line = new TextComponent("--");
    	line.setStrikethrough(true);
    	
    	result.addExtra(line);
    	result.addExtra("> ");
    	
    	TextComponent receiverName = new TextComponent(receiver);
    	receiverName.setBold(false);
    	receiverName.setColor(ChatColor.GRAY);
    	
    	receiverName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/hr " + receiverName + " "));
    	
    	result.addExtra(receiverName);
    	
    	TextComponent msg = new TextComponent(parseLegacyText("§7: §a" + message));
    	
    	result.addExtra(msg);
    	
    	return result;
    }
}
