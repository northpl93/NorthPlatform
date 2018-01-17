package pl.north93.zgame.api.global.permissions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Używane do przechowywania informacji o rangach w Redisie i w konfiguracji;
 */
@XmlRootElement(name = "groups")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupsContainer
{
    @XmlElement
    public String defaultGroup;

    @XmlElement(name = "group")
    public ArrayList<GroupEntry> groups;

    public static class GroupEntry
    {
        @XmlElement
        public String            name;
        @XmlElement
        public String            joinMessage;
        @XmlElement
        public String            chatFormat;
        @XmlElementWrapper(name = "permissions")
        @XmlElement(name = "permission")
        public ArrayList<String> permissions;
        @XmlElementWrapper(name = "inheritances")
        @XmlElement(name = "inheritance")
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
