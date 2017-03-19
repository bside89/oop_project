package com.bside89.poo.tp;

import java.util.Collection;
import java.util.Random;

/**
 * Created by Blue on 27/09/2016.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 *
 * @see Weapon
 */
class Weapons {

    // Suppresses default constructor, ensuring non-instantiability.
    private Weapons(){}

    static Weapon random(Collection<? extends Weapon> weaponsVariety) {
        int index = new Random().nextInt(weaponsVariety.size());
        return get(weaponsVariety, index);
    }

    static Weapon get(Collection<? extends Weapon> weapons, int index) {
        Weapon[] w = weapons.toArray(new Weapon[0]);
        return (Weapon) w[index].clone();
    }

}
