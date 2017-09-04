package pl.north93.zgame.controller.servers.scaler.value;

import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;

/**
 * Reprezentuje wartosc wedlug ktorej mozna skonfigurowac skalowanie serwerow.
 */
public interface IScalingValue
{
    /**
     * Unikalny identyfikator wartosci uzywany w konfiguracji.
     *
     * @return unikalny identyfikator wartosci.
     */
    String getId();

    /**
     * Oblicza wartosc dla danej grupy serwerow i ja zwraca.
     *
     * @param managedServersGroup Grupa serwerow dla ktorej obliczamy wartosc.
     * @return Obliczona wartosc.
     */
    double calculate(LocalManagedServersGroup managedServersGroup);
}
