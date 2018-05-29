package pl.north93.zgame.features.global.network;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.client.IConfigClient;

public class ReloadConfigCommand extends NorthCommand
{
    @Inject
    private IConfigClient configClient;

    public ReloadConfigCommand()
    {
        super("reloadconfig", "configreload");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 1)
        {
            final String configName = args.asString(0);
            this.configClient.reloadConfig(configName);

            sender.sendMessage("&aWyslano polecenie przeladowania configu {0}", configName);
        }
        else
        {
            sender.sendMessage("&c/reloadconfig <nazwa> - przeladowuje config z kontrolera sieci");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
