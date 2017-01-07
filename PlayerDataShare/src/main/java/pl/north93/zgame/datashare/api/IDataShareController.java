package pl.north93.zgame.datashare.api;

import java.util.UUID;

public interface IDataShareController
{
    DataSharingGroup getMyGroup(UUID serverId);
}
