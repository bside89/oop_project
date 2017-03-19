package com.bside89.poo.tp;

import java.util.Collection;
import java.util.Random;

/**
 * Representação abstrata de um item especial.
 *
 * Um Item Especial é definido como um objeto que pertence a uma arena, situado em
 * uma determinada posição, e que produz um efeito em um objeto robô no momento em
 * que este robô situa-se na mesma posição que o item especial.
 *
 * Um item especial é também uma entidade de arena ({@link ArenaEntity}),
 * isto é, ocupa uma posição ({@link Point3D}) em um objeto arena.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 *
 * @see Bomb
 * @see Virus
 * @see Weapon
 * @see Robot
 * @see Arena
 */
abstract class SpecialItem extends ArenaEntity {

    SpecialItem(Point3D position) {
        super(position);
    }

    SpecialItem() {
        super();
    }

    /**
     * Instancia um novo item especial aleatório.
     * Este novo item pode ser uma bomba, arma ou vírus.
     * Caso este seja uma arma, uma nova seleção aleatória de uma coleção de
     * armas deve ser
     *
     * A probabilidade é aproximadamente a mesma para cada item especial.
     *
     * @param weaponsVariety uma coleção de objetos armas
     *
     * @return a referência de um novo item especial aleatório
     */
    static SpecialItem random(Collection<? extends Weapon> weaponsVariety) {
        int n = new Random().nextInt(3);
        switch (n) {
            case 0:
                return new Bomb();
            case 1:
                return new Virus();
            default:
                return Weapons.random(weaponsVariety);
        }
    }

    /**
     * Método abstrato que implementa o efeito deste item especial sobre um robô.
     *
     * @param r o robô que sofrerá o efeito
     * @param a a arena onde este item (e o robô) estão situados
     */
    abstract void effect(Robot r, Arena a);

}
