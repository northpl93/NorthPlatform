package pl.north93.northplatform.features.global.network;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.IComponentBundle;
import pl.north93.northplatform.api.global.component.IComponentManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class ListComponents extends NorthCommand
{
    @Inject
    private IComponentManager componentManager;

    public ListComponents()
    {
        super("listcomponents", "components");
        //this.setPermission("basefeatures.cmd.listcomponents");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        sender.sendMessage("&aCurrently loaded NorthPlatform components:");
        for (final IComponentBundle component : this.componentManager.getComponents())
        {
            sender.sendMessage(" &3" + component.getName() + (component.isBuiltinComponent() ? " (BUILTIN)" : ""));
            sender.sendMessage("  &c- Enabled: " + (component.getStatus().isEnabled() ? "&atrue" : "&cfalse"));
            sender.sendMessage("  &c- Description: " + component.getDescription().getDescription());
        }
    }
}
