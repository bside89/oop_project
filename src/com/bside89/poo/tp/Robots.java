package com.bside89.poo.tp;

import java.util.Collection;
import java.util.Random;

/**
 * Created by Blue on 27/09/2016.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 *
 * @see Robot
 */
class Robots {

    // Suppresses default constructor, ensuring non-instantiability.
    private Robots(){}

    static Robot random(Collection<? extends Robot> robotsVariety) {
        int index = new Random().nextInt(robotsVariety.size());
        return get(robotsVariety, index);
    }

    static Robot get(Collection<? extends Robot> robots, int index) {
        Robot[] r = robots.toArray(new Robot[0]);
        return (Robot) r[index].clone();
    }

}
