package pl.north93.northplatform.api.global.network.proxy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProxyDto
{
    private String  id;
    private String  hostname;
    private Integer onlinePlayers;
    private Boolean antiDdosState;
}
