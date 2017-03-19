package com.bside89.poo.tp;

/**
 * É lançada para indicar que uma posição (x, y, z) não está
 * no "range" de Arena.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 */
class LimitArenaException extends Exception {

    LimitArenaException() {
        super();
    }

    LimitArenaException(String s) {
        super(s);
    }

    LimitArenaException(String s, Throwable cause) {
        super(s, cause);
    }

    LimitArenaException(Throwable cause) {
        super(cause);
    }

}
