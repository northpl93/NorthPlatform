package pl.north93.zgame.api.global.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a permissions group.
 */
public class Group
{
    private String name;
    private String chatFormat;
    private String joinMessage;
    private List<String> permissions;
    private List<Group>  inheritance;

    public Group(final String name)
    {
        this.name = name;
        this.permissions = new ArrayList<>(10);
        this.inheritance = new ArrayList<>(2);
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getChatFormat()
    {
        return this.chatFormat;
    }

    public void setChatFormat(final String chatFormat)
    {
        this.chatFormat = chatFormat;
    }

    public String getJoinMessage()
    {
        return this.joinMessage;
    }

    public void setJoinMessage(final String joinMessage)
    {
        this.joinMessage = joinMessage;
    }

    public Collection<String> getPermissions()
    {
        return Collections.unmodifiableCollection(this.permissions);
    }

    public void addPermission(final String permission)
    {
        this.permissions.add(permission);
    }

    public void removePermission(final String permission)
    {
        this.permissions.remove(permission);
    }

    public Collection<Group> getInheritance()
    {
        return Collections.unmodifiableCollection(this.inheritance);
    }

    public void addInheritGroup(final Group group)
    {
        this.inheritance.add(group);
    }

    public void removeInheritGroup(final Group group)
    {
        this.inheritance.remove(group);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final Group group = (Group) o;

        if (! this.name.equals(group.name))
        {
            return false;
        }
        if (! this.chatFormat.equals(group.chatFormat))
        {
            return false;
        }
        if (! this.joinMessage.equals(group.joinMessage))
        {
            return false;
        }
        if (! this.permissions.equals(group.permissions))
        {
            return false;
        }
        return this.inheritance.equals(group.inheritance);

    }

    @Override
    public int hashCode()
    {
        int result = this.name.hashCode();
        result = 31 * result + this.chatFormat.hashCode();
        result = 31 * result + this.joinMessage.hashCode();
        result = 31 * result + this.permissions.hashCode();
        result = 31 * result + this.inheritance.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("chatFormat", this.chatFormat).append("joinMessage", this.joinMessage).append("permissions", this.permissions).append("inheritance", this.inheritance).toString();
    }
}
