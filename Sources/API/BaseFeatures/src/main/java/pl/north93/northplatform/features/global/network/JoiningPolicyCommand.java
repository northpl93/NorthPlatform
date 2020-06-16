package pl.north93.northplatform.features.global.network;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.JoiningPolicy;

public class JoiningPolicyCommand extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;

    public JoiningPolicyCommand()
    {
        super("joiningpolicy", "joinpolicy");
        this.setPermission("basefeatures.cmd.joiningpolicy");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            final String values = Arrays.stream(JoiningPolicy.values()).map(Enum::name).collect(Collectors.joining(", "));
            sender.sendMessage("&eDostępne wartości: " + values);
            sender.sendMessage("&eAktualnie wybrana opcja: " + this.networkManager.getNetworkConfig().get().joiningPolicy);
        }
        else if (args.length() == 1)
        {
            final JoiningPolicy newJoinPolicy;
            try
            {
                newJoinPolicy = JoiningPolicy.valueOf(args.asString(0).toUpperCase());
            }
            catch (final IllegalArgumentException e)
            {
                sender.sendMessage("&cNiepoprawne argumenty");
                return;
            }

            this.networkManager.getNetworkConfig().update(meta ->
            {
                meta.joiningPolicy = newJoinPolicy;
            });
            sender.sendMessage("&aJoining policy zmienione na " + newJoinPolicy.name());
        }
        else
        {
            sender.sendMessage("&cNiepoprawne argumenty");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
