package pl.north93.zgame.features.bukkit.general;

import java.util.Locale;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.player.impl.LanguageKeeper;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;

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
        final Player player = (Player) sender.unwrapped();

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

        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(Identity.of(player)))
        {
            t.getPlayer().setLocale(locale);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        LanguageKeeper.updateLocale(player, locale);
        sender.sendMessage(this.messages, "command.language.changed");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
