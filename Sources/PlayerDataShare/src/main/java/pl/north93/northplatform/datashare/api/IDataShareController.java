package pl.north93.northplatform.datashare.api;

import java.util.UUID;

public interface IDataShareController
{
    DataSharingGroup getMyGroup(UUID serverId);
}
