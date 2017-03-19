package com.bside89.poo.tp;

import org.jetbrains.annotations.NotNull;

/**
 * Classe que modela um item <tt>Virus</tt> no jogo.
 *
 * Um <tt>Virus</tt> é um objeto que, uma vez encontrado por um <tt>Robot</tt> em
 * uma <tt>Arena</tt>, irá infectar este objeto <tt>Robot</tt>, drenando pontos
 * de vida dele durante uma quantidade de turnos definida aleatoriamente.
 * Após infectar o <tt>Robot</tt>, o <tt>Virus</tt> deixa de ocupar a posição
 * na <tt>Arena</tt>.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 */
class Virus extends SpecialItem implements Comparable<Virus> {

    /**
     * Coeficiente de dano de um <tt>Virus</tt>.
     */
    private static final double M = 100;

    /**
     * Coeficiente de quantidade de turnos, a ser multiplicado por um
     * número aleatório entre 0 e 1.
     */
    private static final int N = 5;

    /**
     * Quantidade de turnos ativos de um <tt>Virus</tt>, baseado no cálculo
     * descrito acima.
     */
    private int life;

    Virus() {
        super();
    }

    Virus(Point3D position) {
        super(position);
        life = (int) Math.floor(N * Math.random());
    }

    /**
     * Diminui em uma unidade a quantidade de vida (turnos)
     * deste <tt>Virus</tt>.
     */
    void reduce() {
        life--;
    }

    /**
     * Retorna <tt>true</tt> se este <tt>Virus</tt> já não tem mais turnos ativos,
     * isto é, está morto e não provocará mais nenhum efeito em um <tt>Robot</tt>.
     *
     * @return <tt>true</tt> se este <tt>Virus</tt> já não tem mais turnos ativos
     */
    boolean isDead() {
        return life == 0;
    }

    /**
     * Calcula uma quantidade de dano, baseado na constante M proporcionalmente a
     * um número aleatório de 0 a 1, e devolve seu valor.
     *
     * @return o valor do cálculo descrito acima
     */
    double calcDamage() {
        return M * Math.random();
    }

    /**
     * Implementa o método abstrato de <tt>SpecialItem</tt>.
     * Infecta o robô do parâmetro com este <tt>Virus</tt> e devolve
     * <tt>null</tt>, que equivale a uma posição vaga na <tt>Arena</tt> que
     * chamar este método.
     *
     * Em outras palavras, este <tt>Virus</tt> sai da <tt>Arena</tt> e fica
     * atachado em um <tt>Robot</tt>, assim, a posição na <tt>Arena</tt> na
     * qual o virus ocupava deverá ficar vazia (<tt>null</tt>).
     *
     * @param r o robô que sofrerá o efeito deste <tt>SpecialItem</tt>
     */
    @Override
    void effect(Robot r, Arena a) {
        r.getInfectionBy(this);
        a.removeItem(this.position); // Vírus deixa de existir na arena
    }

    @Override
    void setPosition(Point3D position) {
        this.position = position;
    }

    @Override
    Point3D getPosition() {
        return position;
    }

    /**
     * Implementa a comparação da interface Comparable.
     *
     * A comparação é feita baseada na quantidade de turnos restantes de
     * um <tt>Virus</tt>.
     *
     * @param o o outro objeto <tt>Virus</tt> a ser comparado com este
     *
     * @return  1, se este <tt>Virus</tt> tem mais turnos restantes do que o outro;
     *          -1, se este tem menos turnos restantes;
     *          0, se ambos possuem a mesma quantidade de turnos
     */
    @Override
    public int compareTo(@NotNull Virus o) {
        if (life > o.life)
            return 1;
        if (life < o.life)
            return -1;
        return 0;
    }

    @Override
    public String toString() {
        return String.format("/!\\ %s /!\\ (%d turnos)", super.toString(), life);
    }

}
