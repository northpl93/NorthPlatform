package pl.north93.northplatform.api.global.network.proxy;

import lombok.Data;

@Data
public class ProxyDto
{
    private String id;
    private String hostname;
    private Integer onlinePlayers;
    private Boolean antiDdosState;
}
