package com.bside89.poo.tp;

/**
 * É lançada para indicar que um movimento para um
 * ponto (x, y, z) não está no "range" de Arena.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 */
class LimitMoveException extends Exception {

    LimitMoveException() {
        super();
    }

    LimitMoveException(String s) {
        super(s);
    }

    LimitMoveException(String s, Throwable cause) {
        super(s, cause);
    }

    LimitMoveException(Throwable cause) {
        super(cause);
    }

}
