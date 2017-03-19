package com.bside89.poo.tp;

import java.io.Serializable;

/**
 * Esta classe provém um modelo de objeto de arena do jogo.
 * Um objeto de arena é qualquer objeto (robô ou item especial)
 * que ocupa uma posição P(x, y, z) na arena.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 */
abstract class ArenaEntity implements Serializable {

    static final Point3D OUTLIER_POINT = new Point3D(-1, -1, -1);
    Point3D position;

    ArenaEntity(Point3D position) {
        this.position = position;
    }

    ArenaEntity() {
        this.position = OUTLIER_POINT;
    }

    abstract void setPosition(Point3D position);

    abstract Point3D getPosition();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
