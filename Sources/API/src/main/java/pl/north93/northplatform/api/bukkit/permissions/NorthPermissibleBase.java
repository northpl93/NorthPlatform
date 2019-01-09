package pl.north93.northplatform.api.bukkit.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NorthPermissibleBase extends PermissibleBase
{
    private final PermissibleBase      original;
    private final Map<String, Boolean> cache;
    private final List<String>         asterisk;

    public NorthPermissibleBase(final PermissibleBase original, final ServerOperator player)
    {
        super(player);
        this.original = original;
        this.cache = new HashMap<>(16);
        this.asterisk = new ArrayList<>();
    }

    private boolean checkAsterisk(final String permission)
    {
        for (final String perm : this.asterisk)
        {
            if (permission.startsWith(perm))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPermissionSet(final String  permission)
    {
        return this.original.isPermissionSet(permission);
    }

    @Override
    public boolean isPermissionSet(final Permission permission)
    {
        return this.original.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(final String permission)
    {
        final String perm = FastToLowerCase.toLowerCase(permission);

        if (this.cache.containsKey(perm))
        {
            return this.cache.get(perm);
        }

        if (! this.original.hasPermission(permission))
        {
            final boolean has = this.checkAsterisk(perm);
            this.cache.put(perm, has);
            return has;
        }
        else
        {
            this.cache.put(perm, true);
            return true;
        }
    }

    @Override
    public boolean hasPermission(final Permission permission)
    {
        final String perm = FastToLowerCase.toLowerCase(permission.getName());

        if (this.cache.containsKey(perm))
        {
            return this.cache.get(perm);
        }

        if (! this.original.hasPermission(permission))
        {
            final boolean has = this.checkAsterisk(perm);
            this.cache.put(perm, has);
            return has;
        }
        else
        {
            this.cache.put(perm, true);
            return true;
        }
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final String s, final boolean b)
    {
        return this.original.addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin)
    {
        return this.original.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final String s, final boolean b, final int i)
    {
        return this.original.addAttachment(plugin, s, b, i);
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final int i)
    {
        return this.original.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(final PermissionAttachment permissionAttachment)
    {
        this.original.removeAttachment(permissionAttachment);
    }

    @Override
    public synchronized void recalculatePermissions()
    {
        if (this.original == null) // ignore self call
        {
            return;
        }
        this.original.recalculatePermissions();
        this.cache.clear();
        this.asterisk.clear();
        for (final PermissionAttachmentInfo permissionAttachmentInfo : this.getEffectivePermissions())
        {
            final String permission = permissionAttachmentInfo.getPermission();
            if (permission.endsWith("*"))
            {
                this.asterisk.add(permission.substring(0, permission.length() - 1));
            }
        }
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        return this.original.getEffectivePermissions();
    }

    @Override
    public boolean isOp()
    {
        return this.original.isOp();
    }

    @Override
    public void setOp(final boolean op)
    {
        this.original.setOp(op);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("original", this.original).toString();
    }
}