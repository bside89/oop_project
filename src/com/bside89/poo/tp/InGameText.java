package com.bside89.poo.tp;

import java.io.File;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Blue on 30/09/2016.
 *
 * @author Bruno Santos
 */
class InGameText {

    private static final String GAME_NAME = "Mecha Wars";

    static final String ERR_FILE_NOT_FOUND = "ERRO: Arquivo não encontrado.";
    static final String ERR_POS = "ERRO: Acesso a uma posição inválida.";
    static final String ERR_MOVE = "ERRO: Movimento maior do que o permitido.";
    static final String ERR_INVALID_COMMAND = "ERRO: Comando inválido. Tente novamente.";
    static final String ERR_INVALID_OPTION = "ERRO: Opção inválida. Tente novamente.";
    static final String ERR_INVALID_DIM = "Dimensão deve ser maior que " +
            GameConfigs.getArenaMinDimension() + ".";
    static final String ERR_GAME_CONFIG = "Erro durante carregamento de configurações do arquivo.";
    static final String USE_DEFAULT_CONFIGS = "As configurações padrão serão utilizadas.";

    static void printIntro() {
        System.out.println("********************");
        System.out.println(GAME_NAME);
        System.out.println("********************");
        System.out.println();
        System.out.println("Bem-vindo ao " + GAME_NAME + ".");
        System.out.println("Vamos às configurações iniciais de jogo.");
        System.out.println();
    }

    static void printConfigArena() {

        Random r = new Random();
        int min = GameConfigs.getArenaMinDimension();
        int x = min + r.nextInt(min);
        int y = min + r.nextInt(min);
        int z = min + r.nextInt(min);

        System.out.println("Digite as dimensões da arena, em números inteiros.");
        System.out.println("São, respectivamente, largura, comprimento e altura.");
        System.out.printf("Exemplo: %d %d %d\n", x, y, z);
    }

    static void printGameOptions(int turn, int actionNum, Player p) throws IllegalArgumentException {
        if (turn <= 0 || actionNum <= 0)
            throw new IllegalArgumentException();
        Robot r = p.getRobot();
        System.out.println("\n----------------------------------------------");
        System.out.printf("TURNO N# %d - AÇÃO N# %d (JOGADOR %d)\n", turn, actionNum, p.getID());
        System.out.println("PARA MOVER-SE:");
        System.out.println("-> Digite: \"move dx dy dz\" para se deslocar dx, dy e dz unidades na arena.");
        System.out.println("-> Exemplo: move 2 1 -1");
        System.out.printf("-> Max. de unidades de movimento p/ %s: %d\n", r.getName(), r.getMoveLimit());
        if (turn != 1) {
            System.out.println("PARA ATACAR O ADVERSÁRIO:");
            System.out.println("-> Digite: \"attack\"");
        }
        System.out.println("PARA ENCERRAR O JOGO: Digite \"exit\"");
        System.out.println("----------------------------------------------\n");
    }

    static void printGameStatus(Player p1, Player p2, Arena a) throws NullPointerException {

        Player players[] = new Player[]{p1, p2};
        System.out.println("----------------------------------------------");
        for (Player p : players) {
            Robot r = p.getRobot();
            System.out.println("STATUS - PLAYER " + p.getID());
            System.out.printf("%s c/ %s em P%s: <HP %.2f> <A %.2f>%s\n", r.getName(), r.getWeapon(),
                    r.getPosition(), r.getHp(), r.getArmor(), (r.isInfected() ? " (infectado)" : ""));
            System.out.println("----------------------------------------------");
        }
        System.out.println(a);
        System.out.println("----------------------------------------------");
    }

    static void printGameWinner(Player p) {
        System.out.println("----------------------------------------------");
        System.out.println("Jogo encerrado!");
        System.out.printf("O VENCEDOR É O JOGADOR %d.\n", p.getID());
        System.out.println("PARABÉNS!");
        System.out.println("----------------------------------------------");
    }

    static boolean ask(String question, char positiveAnswer) {
        Scanner in = new Scanner(System.in);
        String answer;
        System.out.println(question);
        System.out.printf("Digite '%c' caso 'SIM', caso 'NÃO' digite qualquer coisa.", positiveAnswer);
        try {
            answer = in.next();
            return answer.charAt(0) == positiveAnswer;
        } catch (StringIndexOutOfBoundsException | InputMismatchException e) {
            return false;
        }
    }

}
