package pl.north93.zgame.api.global.repo;

import java.util.UUID;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackNullable;

public class RepoFile
{
    private UUID   fileUuid;
    @MsgPackNullable
    private String fileLabel;

}
