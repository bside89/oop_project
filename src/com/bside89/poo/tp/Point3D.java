package com.bside89.poo.tp;

import java.io.Serializable;

/**
 * Created by Blue on 28/09/2016.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 */
class Point3D implements Cloneable, Serializable {

    private final int X, Y, Z;

    Point3D(int x, int y, int z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    double distance(Point3D other) {
        double dx = X - other.X;
        double dy = Y - other.Y;
        double dz = Z - other.Z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    int getX() {
        return X;
    }

    int getY() {
        return Y;
    }

    int getZ() {
        return Z;
    }

    @Override
    public final Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Point3D other = (Point3D) o;
        return X == other.X && Y == other.Y && Z == other.Z;
    }

    @Override
    public int hashCode() {
        int result = X;
        result = 31 * result + Y;
        result = 31 * result + Z;
        return result;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d)", X, Y, Z);
    }

}
