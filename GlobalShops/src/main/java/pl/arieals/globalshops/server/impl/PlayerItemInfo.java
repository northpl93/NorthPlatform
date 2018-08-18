package pl.arieals.globalshops.server.impl;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = {"groupId", "itemId"})
/*default*/ class PlayerItemInfo
{
    private String  groupId;
    private String  itemId;
    @Setter
    private Integer boughtLevel;
    @Setter
    private Integer shards;

    public boolean matches(final String groupId, final String itemId)
    {
        return this.groupId.equals(groupId) && this.itemId.equals(itemId);
    }

    public boolean isBought()
    {
        return this.boughtLevel > 0;
    }
}
