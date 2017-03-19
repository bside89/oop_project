package com.bside89.poo.tp;

/**
 * Classe que modela um item <tt>Bomb</tt> (Bomba) no jogo.
 *
 * Um <tt>Bomb</tt> é um objeto que, uma vez encontrado por um <tt>Robot</tt> em
 * uma <tt>Arena</tt>, irá "explodir" e decrementar pontos de vida do <tt>Robot</tt>.
 * Após o dano, o objeto deixa de existir na <tt>Arena</tt> (a posição ficará vaga (nula)).
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 *
 * @see Arena
 * @see Robot
 */
class Bomb extends SpecialItem {

    /**
     * Coeficiente de dano estático de qualquer objeto bomba.
     */
    private static final double SIGMA = 300;

    Bomb() {
        super();
    }

    Bomb(Point3D position) {
        super(position);
    }

    /**
     * Implementa o método abstrato de <tt>SpecialItem</tt>.
     *
     * Afeta um robô causando um dano neste, proporcional
     * ao coeficiente de dano e a um número aleatório de 0 a 1.
     * Uma vez que a bomba "explode" e deixa de existir, a função devolve
     * <tt>null</tt>, que é a posição que ficará vaga na <tt>Arena</tt>.
     *
     * @param r o robô que sofrerá o efeito deste <tt>SpecialItem</tt>
     */
    @Override
    void effect(Robot r, Arena a) {
        r.receiveDamage(SIGMA * Math.random());
        a.removeItem(this.position); // A bomba deixa de existir na arena.
        this.position = OUTLIER_POINT;
    }

    @Override
    void setPosition(Point3D position) {
        this.position = position;
    }

    @Override
    Point3D getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "/!\\ " + super.toString() + " /!\\";
    }

}
