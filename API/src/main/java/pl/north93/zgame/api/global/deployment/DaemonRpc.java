package pl.north93.zgame.api.global.deployment;

import java.util.UUID;

import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;

public interface DaemonRpc
{
    /**
     * Zmienia mozliwosc tworzenia nowych serwerow na tym daemonie.
     * Wartosc ta mozna pobrac z klucza w Redisie
     */
    @DoNotWaitForResponse
    void setAcceptingNewServers(Boolean isAcceptingNewServers);

    /**
     * Rozpoczyna na tym demonie proces instalowania i uruchamiania serwera.
     * @param serverUuid
     * @param templateName
     */
    @DoNotWaitForResponse
    void deployServer(UUID serverUuid, String templateName);

    /**
     * Wysyla do serwera polecenie wylaczenia.
     * @param serverUuid unikalny identyfikator serwera
     */
    @DoNotWaitForResponse
    void stopServer(UUID serverUuid);
}
