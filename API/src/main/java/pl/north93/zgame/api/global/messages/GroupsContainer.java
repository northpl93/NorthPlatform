package pl.north93.zgame.api.global.messages;

import static org.diorite.cfg.annotations.CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgCollectionStyle;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;
import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;

/**
 * Używane do przechowywania informacji o rangach w Redisie i w konfiguracji;
 */
@CfgComments({"Konfiguracja grup i uprawnień"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class GroupsContainer
{
    private static List<GroupEntry> getDefaultGroups()
    {
        final Template<GroupEntry> template = TemplateCreator.getTemplate(GroupEntry.class, true);
        final ArrayList<GroupEntry> list = new ArrayList<>(2);

        {
            final GroupEntry groupDefault = template.fillDefaults(new GroupEntry());
            groupDefault.inheritance = new ArrayList<>();
            groupDefault.permissions = new ArrayList<>();
            groupDefault.permissions.add("permission1");
            groupDefault.permissions.add("permission2");
            list.add(groupDefault);
        }

        return list;
    }

    @CfgComment("Domyślna grupa dla graczy bez ustawionej grupy")
    @CfgStringDefault("default")
    public String defaultGroup;

    @CfgComment("Konfiguracja wszystkich grup i ich uprawnień")
    @CfgDelegateDefault("getDefaultGroups")
    public ArrayList<GroupEntry> groups;

    public static class GroupEntry
    {
        @CfgComment("Nazwa grupy. Nie może być zmieniana, ponieważ doprowadzi to do utracenia rangi przez graczy i powrót do domyślnej.")
        @CfgStringDefault("default")
        public String            name;
        @CfgComment("Wiadomość która pojawi się gdy gracz wejdzie na serwer. Puste pole oznacza brak wiadomości.")
        @CfgStringDefault("")
        public String            joinMessage;
        @CfgComment("Format wyświetlanych wiadomości na czacie")
        @CfgStringDefault("[Gracz] %s: %s")
        public String            chatFormat;
        @CfgComment("Lista uprawnień danej grupy")
        @CfgCollectionStyle(ALWAYS_NEW_LINE)
        public ArrayList<String> permissions;
        @CfgComment("Lista grup po której ta dziedziczy uprawnienia")
        @CfgCollectionStyle(ALWAYS_NEW_LINE)
        public ArrayList<String> inheritance;

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("joinMessage", this.joinMessage).append("chatFormat", this.chatFormat).append("permissions", this.permissions).append("inheritance", this.inheritance).toString();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("defaultGroup", this.defaultGroup).append("groups", this.groups).toString();
    }
}
