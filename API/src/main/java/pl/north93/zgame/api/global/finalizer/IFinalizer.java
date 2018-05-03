package pl.north93.zgame.api.global.finalizer;

/**
 * Prosty komponent zapewniajacy alternatywe dla metody {@link Object#finalize()}.
 */
public interface IFinalizer
{
    /**
     * Rozpoczyna nasluchiwanie na zlecenie usuniecia obiektu przez GC.
     *
     * @param object Obiekt ktorego usuniecie wywola akcje.
     * @param cleanup Akcja do wykonania po usunieciu obiektu przez GC.
     */
    void register(Object object, Runnable cleanup);

    /**
     * Zwraca liczbe zyjacych obiektow sledzonych przez finalizera.
     *
     * @return Liczba sledzonych obiektow przez finalizer.
     */
    int countAliveObjects();
}
