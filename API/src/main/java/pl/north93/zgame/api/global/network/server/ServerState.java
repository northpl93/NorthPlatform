package pl.north93.zgame.api.global.network.server;

public enum ServerState
{
    ALLOCATING, // wybieranie/czekanie na wolnego demona
    INSTALLING, // demon przygotowuje serwer
    STARTING,   // serwer się uruchamia
    WORKING,    // serwer uruchomiony
    STOPPING,   // serwer jest zatrzymywany
    STOPPED,    // serwer jest wyłączony
    ERROR       // serwer jest wyłączony/nie został włączony ponieważ wystąpił błąd
}
