package com.bside89.poo.tp;

import java.io.*;
import java.util.*;

import static com.bside89.poo.tp.GameConfigs.*;
import static com.bside89.poo.tp.InGameText.*;

/**
 * Classe que implementa o jogo Mecha Wars.
 * Esta é a classe-pai de todas as outras classes componentes deste software.
 * Realiza inteiramente a comunicação entre usuário e a aplicação,
 * encapsulando as demais informações do mundo externo.
 *
 * Documento descrevendo a jogabilidade constam no pacote que acompanha este
 * código-fonte.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 */
public final class MechaWars {

    private Queue<Action> actions;
    private int turn;
    private Arena arena;
    private Player p1, p2;
    private Set<Robot> robots;
    private Set<Weapon> weapons;
    private Scanner stdin;

    public MechaWars(String filePathRobots, String filePathWeapons, String filePathConfigs) throws
            IllegalArgumentException {

        // Inicializa todos os objetos com valores padrão
        actions = new LinkedList<>();
        stdin = new Scanner(System.in);
        p1 = new Player(1);
        p2 = new Player(2);

        // Ordena a coleção de robôs e armas por nome
        Comparator<Nameable> nameComp = (o1, o2) -> o1.getName().compareTo(o2.getName());
        robots = new TreeSet<>(nameComp);
        weapons = new TreeSet<>(nameComp);

        try {
            loadConfigs(filePathConfigs);
            fromFileToCollection('r', filePathRobots);
            fromFileToCollection('w', filePathWeapons);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(ERR_FILE_NOT_FOUND, e);
        }
    }

    private void setup() {
        printIntro();
        configArena();
        configPlayer(p1);
        configPlayer(p2);
    }

    private void configArena() {

        boolean lock = true;
        Scanner in = new Scanner(System.in);
        printConfigArena();
        do {
            try {
                int dim1 = in.nextInt();
                int dim2 = in.nextInt();
                int dim3 = in.nextInt();
                arena = new Arena(dim1, dim2, dim3, weapons);
                lock = false;
            } catch (IllegalArgumentException e) {
                System.err.println(ERR_INVALID_DIM);
            }
        } while (lock);
    }

    private void configPlayer(Player p) {

        boolean lock = true;
        rollConfigOptions(p);
        do {
            try {
                int choice1 = stdin.nextInt() - 1;
                int choice2 = stdin.nextInt() - 1;
                Robot r = (choice1 == -1) ? Robots.random(robots) : Robots.get(robots, choice1);
                Weapon w = (choice2 == -1) ? Weapons.random(weapons) : Weapons.get(weapons, choice2);
                r.setWeapon(w);
                r.setPosition(arena.randomPoint());
                p.setRobot(r);
                robots.remove(r);
                lock = false;
            } catch (IndexOutOfBoundsException e) {
                System.err.println(ERR_INVALID_OPTION);
            }
        } while (lock);
        System.out.printf("Jogador %d configurado com sucesso.\n", p.getID());
    }

    /**
     * Obtém registros dos arquivos de listagem de robôs e armas e constrói um
     * coleção (set) contendo todos estes registros.
     *
     * Um caractere indicador é enviado como parâmetro para indicar se a coleção
     * construída será a de robôs ou a de armas.
     *
     * @param indic     um caractere indicador que aponta para o método qual
     *                  das duas coleções (robôs ou armas) este método
     *                  irá construir
     *
     * @param filePath  sting contendo o caminho do arquivo contendo
     *                  os registros
     *
     * @throws FileNotFoundException    se o arquivo cujo caminho é filePath
     *                                  não for encontrado.
     */
    private void fromFileToCollection(char indic, String filePath) throws FileNotFoundException {

        assert (indic == 'r' || indic == 'w');
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            for (String line = br.readLine(); line != null; line = br.readLine()) {

                if (line.equals("") || line.charAt(0) == '#')
                    continue;
                Scanner in = new Scanner(line);
                String name = in.next();
                switch (indic) {
                    case 'r':
                        robots.add(new Robot(name, in.nextInt(), in.nextInt()));
                        break;
                    case 'w':
                        weapons.add(new Weapon(name, in.nextInt()));
                        break;
                }
            }
        } catch (NoSuchElementException e) {
            // Isto ocorrerá caso haja alterações incorretas dos
            // arquivos de coleções
            throw new BadConfigError(e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void rollConfigOptions(Player p) {

        System.out.printf("Escolha para o jogador %d, respectivamente, robô e arma.\n", p.getID());
        System.out.println("Exemplo: 1 2\n");
        System.out.println("---");
        int i = 0, j = 0;
        System.out.println("0: Robô aleatório.");
        for (Robot e : robots)
            System.out.format("%d: %-20s <HP %5.0f> <Armor %5.0f>\n", ++i,
                    e.getName(), e.getHp(), e.getArmor());
        System.out.println("---");
        System.out.println("0: Arma aleatória.");
        for (Weapon e : weapons)
            System.out.format("%d: %-20s <Damage %5.0f>\n", ++j, e.getName(), e.getSigma());
        System.out.println("---");
    }

    /**
     * Método-pai da classe. Executa toda a configuração inicial a partir dos
     * dados informados pelo usuario e executa todas as funcionalidades do jogo.
     *
     * A interação com os usuarios externos é feita através das streams de entrada
     * e saída padrão do sistema, ou seja, teclado e tela do computador,
     * respectivamente.
     */
    public void runGame() {
        setup();
        int actionNum = 0;

        while (!p1.isDefeated() && !p2.isDefeated()) {

            boolean lock = true;
            if (actionNum % 2 == 0) turn++;
            actionNum++;
            if (actionNum > 2) actionNum = 1;

            Player activePlayer = actionNum == 1 ? p1 : p2;
            printGameStatus(p1, p2, arena);
            printGameOptions(turn, actionNum, activePlayer);
            do {
                try {
                    Action action = new Action(turn, actionNum, arena, p1, p2);
                    String command;
                    do command = stdin.nextLine(); while (command.equals(""));
                    boolean signal = action.make(command);
                    if (signal) {
                        // Jogador que chamou "exit" deve perder o jogo.
                        activePlayer.getRobot().kill();
                    }
                    actions.add(action);
                    System.out.println("----------------------------------------------");
                    System.out.print(action);
                    System.out.println("----------------------------------------------");
                    lock = false;
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
            } while (lock);
        }
        printGameWinner((p1.isDefeated()) ? p2 : p1);
        saveState();
        System.out.println("Pressione qualquer tecla p/ continuar...");
        stdin.nextLine();
    }

    /**
     * Salva o estado de todas as ações em um arquivo binário.
     * O nome deste arquivo pode ser alterado nas constantes em InGameText
     * (procurar pela constante FILE_SAVE_STATE)
     */
    private void saveState() {
        try {
            FileOutputStream saveFile = new FileOutputStream(getFileSaveStateName());
            ObjectOutputStream saveObj = new ObjectOutputStream(saveFile);
            saveObj.reset(); // Apaga os dados antigos contidos no arquivo.
            for (Action e : actions) {
                saveObj.writeObject(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
