package pl.north93.zgame.controller.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;

import pl.north93.zgame.api.global.deployment.AllocationConfiguration;
import pl.north93.zgame.api.global.deployment.ServersAllocatorType;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.deployment.serversgroup.ManagedServersGroup;
import pl.north93.zgame.api.global.deployment.serversgroup.ServersGroupsContainer;
import pl.north93.zgame.api.global.deployment.serversgroup.UnManagedServer;
import pl.north93.zgame.api.global.deployment.serversgroup.UnManagedServersGroup;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.ServerType;

@CfgComments({"Konfiguracja grup serwerów"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class ServersGroupsConfig
{
    private static List<ManagedServersGroup> getDefaultManagedGroups()
    {
        final Template<ManagedServersGroup> template = TemplateCreator.getTemplate(ManagedServersGroup.class, true);
        final ArrayList<ManagedServersGroup> list = new ArrayList<>(2);

        {
            final ManagedServersGroup exampleServersGroup = template.fillDefaults(new ManagedServersGroup());
            exampleServersGroup.setName("minigame_example");
            exampleServersGroup.setServersType(ServerType.MINIGAME);
            exampleServersGroup.setServerPattern("pattern_minigame_example");
            exampleServersGroup.setJoiningPolicy(JoiningPolicy.EVERYONE);
            exampleServersGroup.setAllocatorConfiguration(new AllocationConfiguration(ServersAllocatorType.STATIC, false, 1, 1));

            list.add(exampleServersGroup);
        }

        return list;
    }

    private static List<UnManagedServersGroup> getDefaultUnManagedGroups()
    {
        final Template<UnManagedServersGroup> template = TemplateCreator.getTemplate(UnManagedServersGroup.class, true);
        final ArrayList<UnManagedServersGroup> list = new ArrayList<>(2);

        {
            final List<UnManagedServer> servers = new ArrayList<>(2);
            servers.add(new UnManagedServer(UUID.randomUUID().toString(), "localhost", 25566));

            final UnManagedServersGroup exampleServersGroup = template.fillDefaults(new UnManagedServersGroup());
            exampleServersGroup.setName("default");
            exampleServersGroup.setServersType(ServerType.NORMAL);
            exampleServersGroup.setJoiningPolicy(JoiningPolicy.EVERYONE);
            exampleServersGroup.setServers(servers);

            list.add(exampleServersGroup);
        }

        return list;
    }

    @CfgDelegateDefault("getDefaultManagedGroups")
    private List<ManagedServersGroup> managedGroups;

    @CfgDelegateDefault("getDefaultUnManagedGroups")
    private List<UnManagedServersGroup> unManagedGroups;

    public List<ManagedServersGroup> getManagedGroups()
    {
        return this.managedGroups;
    }

    public List<UnManagedServersGroup> getUnManagedGroups()
    {
        return this.unManagedGroups;
    }

    public ServersGroupsContainer toContainer()
    {
        final List<IServersGroup> groups = new ArrayList<>();
        groups.addAll(this.managedGroups);
        groups.addAll(this.unManagedGroups);
        return new ServersGroupsContainer(groups);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("managedGroups", this.managedGroups).append("unManagedGroups", this.unManagedGroups).toString();
    }
}
