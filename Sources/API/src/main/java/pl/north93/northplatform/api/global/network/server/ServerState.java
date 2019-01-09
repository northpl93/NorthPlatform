package pl.north93.northplatform.api.global.network.server;

public enum ServerState
{
    CREATING, // serwer jest tworzony, instalowany
    STARTING, // serwer się uruchamia
    WORKING,  // serwer uruchomiony
    STOPPING, // serwer jest zatrzymywany
    STOPPED   // serwer jest wyłączony
}
