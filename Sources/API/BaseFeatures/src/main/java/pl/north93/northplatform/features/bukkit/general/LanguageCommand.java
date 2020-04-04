package pl.north93.northplatform.features.bukkit.general;

import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.INetworkManager;

@Slf4j
public class LanguageCommand extends NorthCommand
{
    @Inject @Messages("BaseFeatures")
    private MessagesBox     messages;
    @Inject
    private INetworkManager networkManager;

    public LanguageCommand()
    {
        super("language", "jezyk");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final INorthPlayer player = INorthPlayer.wrap(sender);

        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "command.language.help");
            return;
        }

        final Locale locale;
        switch (args.asString(0).toLowerCase())
        {
            case "pl":
            case "polski":
                locale = Locale.forLanguageTag("pl-PL");
                break;
            case "en":
            case "english":
                locale = Locale.forLanguageTag("en-GB");
                break;
            default:
                locale = Locale.forLanguageTag("pl-PL");
        }

        player.updateLocale(locale);
        sender.sendMessage(this.messages, "command.language.changed");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
