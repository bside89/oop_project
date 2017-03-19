package com.bside89.poo.tp;

/**
 * Erro lançado quando há alguma configuração errada dentro do jogo, como
 * arquivos de configurações (txt) mal formatados etc.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 */
class BadConfigError extends Error {

    BadConfigError() {
        super();
    }

    BadConfigError(String s) {
        super(s);
    }

    BadConfigError(String s, Throwable cause) {
        super(s, cause);
    }

    BadConfigError(Throwable cause) {
        super(cause);
    }

}
