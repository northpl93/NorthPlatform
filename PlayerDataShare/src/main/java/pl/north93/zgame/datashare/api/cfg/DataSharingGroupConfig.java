package pl.north93.zgame.datashare.api.cfg;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

public class DataSharingGroupConfig
{
    @CfgComment("Wewnętrzna nazwa tej grupy")
    private String       name;
    @CfgComment("Grupy serwerów należące do tej grupy udostępniania danych")
    private List<String> serversGroups;
    @CfgComment("Serwery należące do tej grupy udostępniania danych")
    private List<String> servers;
    @CfgComment("Czy w tej grupie współdzielić czat?")
    private boolean      shareChat;
    @CfgComment("Lista danych które należy synchronizować")
    private List<String> dataUnits;

    public String getName()
    {
        return this.name;
    }

    public List<String> getServersGroups()
    {
        return this.serversGroups;
    }

    public List<String> getServers()
    {
        return this.servers;
    }

    public boolean isShareChat()
    {
        return this.shareChat;
    }

    public List<String> getDataUnits()
    {
        return this.dataUnits;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("serversGroups", this.serversGroups).append("servers", this.servers).append("shareChat", this.shareChat).append("dataUnits", this.dataUnits).toString();
    }
}
