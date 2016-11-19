package pl.north93.zgame.controller.cfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;

import pl.north93.zgame.api.global.deployment.ServerPattern;

@CfgComments({"Konfiguracja wzorów instancji serwerów"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class ServersPatternsConfig
{
    private static List<ServerPattern> getDefaultPatterns()
    {
        final Template<ServerPattern> template = TemplateCreator.getTemplate(ServerPattern.class, true);
        final ArrayList<ServerPattern> list = new ArrayList<>(2);

        {
            final ServerPattern pattern = template.fillDefaults(new ServerPattern());
            pattern.setPatternName("pattern_minigame_example");
            pattern.setEngineName("spigot-1.10.jar");
            pattern.setMaxMemory(1000);
            pattern.setStartMemory(1000);
            pattern.setComponents(new ArrayList<>(Arrays.asList("core", "minigame-example")));

            list.add(pattern);
        }

        return list;
    }

    @CfgDelegateDefault("getDefaultPatterns")
    private List<ServerPattern> patterns;

    public List<ServerPattern> getPatterns()
    {
        return this.patterns;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("patterns", this.patterns).toString();
    }
}
