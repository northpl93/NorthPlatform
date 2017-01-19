package pl.north93.zgame.restful.models;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaStore;

public final class PlayerModel
{
    private UUID                uuid;
    private String              nick;
    private boolean             isOnline;
    private String              group;
    private Map<String, Object> metadata;

    public PlayerModel(final UUID uuid, final String nick, final boolean isOnline, final String group, final MetaStore metaStore)
    {
        this.uuid = uuid;
        this.nick = nick;
        this.isOnline = isOnline;
        this.group = group;
        this.metadata = metaStore.getInternalMap().entrySet().stream()
                                 .collect(Collectors.toMap(e -> e.getKey().getKey(), Map.Entry::getValue));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("nick", this.nick).append("isOnline", this.isOnline).append("group", this.group).append("metadata", this.metadata).toString();
    }
}
