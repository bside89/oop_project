package com.bside89.poo.tp;

/**
 * Interface utilizar para referenciar objetos que possuem um atributo
 * de nome. As classes que implementam esta interface devem fornecer
 * uma representação literal (String) como um nome, um rótulo etc.
 *
 * Assim, é fácil utilizar objetos que implementam esta interface em
 * algoritmos de ordenação por nome, ou em coleções ordenadas como
 * {@link java.util.TreeSet}, por exemplo.
 * Utilizando-se de {@link java.util.Comparator} é fácil utilizar esta
 * interface para ordenar objetos pelo seu atributo de nome.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 *
 * @see java.util.Comparator
 * @see java.util.TreeSet
 * @see java.util.TreeMap
 */
interface Nameable {

    /**
     * Devolve uma String que representa o rótulo, ou nome,
     * de um objeto Nameable.
     *
     * @return uma String que representa o rótulo, ou nome,
     * de um objeto Nameable
     */
    String getName();

}