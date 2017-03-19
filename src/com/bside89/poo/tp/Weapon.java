package com.bside89.poo.tp;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Blue on 03/09/2016.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 */
class Weapon extends SpecialItem implements Comparable<Weapon>, Cloneable, Nameable {

    private final String NAME;
    private final double SIGMA;

    Weapon(String name, double sigma) {
        this(name, sigma, OUTLIER_POINT);
    }

    Weapon(String name, double sigma, Point3D position) {
        super(position);
        assert (name != null && sigma > 0);
        this.NAME = name;
        this.SIGMA = sigma;
    }

    @Override
    void effect(Robot r, Arena a) {
        Weapon old = r.getWeapon();
        r.setWeapon(this);
        a.removeItem(this.position);
        a.putItem(old);
    }

    @Override
    void setPosition(Point3D position) {
        this.position = position;
    }

    @Override
    public String getName() {
        return NAME;
    }

    double getSigma() {
        return SIGMA;
    }

    @Override
    Point3D getPosition() {
        return position;
    }

    @Override
    public int compareTo(@NotNull Weapon o) {
        if (SIGMA > o.SIGMA)
            return 1;
        if (SIGMA < o.SIGMA)
            return -1;
        return 0;
    }

    @Override
    public final Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // Isto não deve acontecer, já que implementamos Cloneable
            throw new InternalError(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Weapon other = (Weapon) o;
        return NAME.equals(other.NAME) && SIGMA == other.SIGMA;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = NAME.hashCode();
        temp = Double.doubleToLongBits(SIGMA);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f)", NAME, SIGMA);
    }

}
