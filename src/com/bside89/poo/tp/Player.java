package com.bside89.poo.tp;

import java.io.Serializable;

/**
 * Created by Blue on 28/09/2016.
 *
 * @author Bruno Santos
 */
class Player implements Serializable {

    private final int ID;
    private Robot robot;
    private int fouls;

    Player(int ID, Robot robot) throws IllegalArgumentException {
        if (ID <= 0)
            throw new IllegalArgumentException();
        this.ID = ID;
        this.robot = robot;
        this.fouls = 0;
    }

    Player(int ID) throws IllegalArgumentException {
        this(ID, null);
    }

    boolean isDefeated() {
        return robot.isDead();
    }

    void setRobot(Robot robot) {
        if (this.robot == null) this.robot = robot;
    }

    void addFoul() {
        fouls++;
    }

    int getID() {
        return ID;
    }

    Robot getRobot() {
        return robot;
    }

    int getFouls() {
        return fouls;
    }

    @Override
    public String toString() {
        return String.format("%s %d: %s", getClass().getSimpleName(), ID, robot);
    }

}
