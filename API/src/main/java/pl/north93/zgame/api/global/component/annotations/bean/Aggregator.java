package pl.north93.zgame.api.global.component.annotations.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * System agregacji umozliwia szukanie instancji podanej klasy lub
 * metod z konkretna adnotacja.
 * <p>
 *     Gdy agregujemy metody oznaczone adnotacja dostepne sa
 *     nastepujace beany w kontekscie tymczasowym:
 *     <ul>
 *         <li>Instancja adnotacji
 *         <li>Method nazwa:Target - metoda nad ktora znalezlismy adnotacje
 *         <li>Object nazwa:MethodOwner - instancja klasy do ktorej nalezy adnotacja (bean leniwy)
 * <p>
 *     Gdy agregujemy instancje danej klasy dostepne sa
 *     nastepujace beany w kontekscie tymczasowym:
 *     <ul>
 *         <li>Instancja szukanej klasy (bez nazwy)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Aggregator
{
    Class<?> value();
}
