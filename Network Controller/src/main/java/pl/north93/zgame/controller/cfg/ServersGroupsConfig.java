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

import pl.north93.zgame.api.global.deployment.ServersAllocatorType;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.network.JoiningPolicy;

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
            exampleServersGroup.setServerPattern("pattern_minigame_example");
            exampleServersGroup.setAllocatorType(ServersAllocatorType.JOINING_POLICY);
            exampleServersGroup.setJoiningPolicy(JoiningPolicy.EVERYONE);
            exampleServersGroup.setMinServers(2);
            exampleServersGroup.setMaxServers(10);

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
