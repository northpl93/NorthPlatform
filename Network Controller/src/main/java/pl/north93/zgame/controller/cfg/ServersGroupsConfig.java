package pl.north93.zgame.controller.cfg;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;

import pl.north93.zgame.api.global.deployment.AllocationConfiguration;
import pl.north93.zgame.api.global.deployment.ServersAllocatorType;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.ServerType;

@CfgComments({"Konfiguracja grup serwerów"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class ServersGroupsConfig
{
    private static List<ServersGroup> getDefaultGroups()
    {
        final Template<ServersGroup> template = TemplateCreator.getTemplate(ServersGroup.class, true);
        final ArrayList<ServersGroup> list = new ArrayList<>(2);

        {
            final ServersGroup exampleServersGroup = template.fillDefaults(new ServersGroup());
            exampleServersGroup.setName("minigame_example");
            exampleServersGroup.setServersType(ServerType.MINIGAME);
            exampleServersGroup.setServerPattern("pattern_minigame_example");
            exampleServersGroup.setJoiningPolicy(JoiningPolicy.EVERYONE);
            exampleServersGroup.setAllocatorConfiguration(new AllocationConfiguration(ServersAllocatorType.STATIC, false, 1, 1));

            list.add(exampleServersGroup);
        }

        return list;
    }

    @CfgDelegateDefault("getDefaultGroups")
    private List<ServersGroup> groups;

    public List<ServersGroup> getGroups()
    {
        return this.groups;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("groups", this.groups).toString();
    }
}
