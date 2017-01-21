package pl.north93.zgame.api.defaultcommands;

import java.util.stream.Collectors;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.IComponentManager;

public class ListComponents extends NorthCommand
{
    private ApiCore apiCore;

    public ListComponents()
    {
        super("listcomponents", "components");
        //this.setPermission("api.command.listcomponents");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        sender.sendMessage("&aAktualnie załadowane moduły API:");
        final IComponentManager componentManager = this.apiCore.getComponentManager();
        for (final IComponentBundle component : componentManager.getComponents())
        {
            final String prettyPackages = component.getBasePackages().stream().collect(Collectors.joining(", "));
            sender.sendMessage(" &3" + component.getName() + " [" + prettyPackages + "]");
            sender.sendMessage("  &c- Builtin: " + (component.isBuiltinComponent() ? "&atrue" : "&cfalse"));
            sender.sendMessage("  &c- Enabled: " + (component.getStatus().isEnabled() ? "&atrue" : "&cfalse"));
            sender.sendMessage("  &c- Description: " + component.getDescription().getDescription());
        }
    }
}
