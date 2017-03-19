package com.bside89.poo.tp;

import org.jetbrains.annotations.NotNull;

import static com.bside89.poo.tp.InGameText.*;

import java.io.Serializable;
import java.util.*;

/**
 * Representa uma ação executada por um jogador.
 *
 * Uma ação é entendida como uma jogada, que é caracterizada por
 * dois tipos:
 *  - Mover-se na arena (com o robô)
 *  - Atacar o robô adversário
 *
 *  Cada jogador só pode efetuar uma ação por turno.
 *  Uma ação que tenha sido consumada não pode ser desfeita.
 *  Cada objeto ação só poderá armazenar a ação de um único jogador
 *  em um dado turno. Quando é a vez do jogador adversário efetuar
 *  sua jogada, um novo objeto ação deve ser instanciado.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 *
 * @see Arena
 * @see Player
 * @see Robot
 */
class Action implements Serializable, Comparable<Action> {

    /**
     * String que armazena o registro de todos os eventos ao final de uma ação.
     * Uma fila de eventos armazenará temporariamente cada evento (texto).
     * Ao término da ação, todos os dados da fila serão concatenados nesta String.
     */
    private String eventsLog;

    /**
     * Fila que armazenará cada descrição de evento durante a ação.
     */
    private transient Queue<String> events;

    /**
     * String estática a ser referênciada pela String eventsLog
     * caso esta ação ainda não tenha sido consumada.
     */
    private static final String NO_ACTION = "Ação ainda não realizada.";

    /**
     * Os jogadores participantes desta ação.
     */
    private final Player P1, P2;

    /**
     * A arena na qual a batalha entre os robôs ocorre.
     */
    private final Arena A;

    /**
     * Representam, respectivamente, o número desta ação e o turno onde a mesma foi
     * executada, ambos fornecidos pelo controlador do jogo.
     */
    private final int NUM, TURN;

    /**
     * Constrói uma nova ação.
     *
     * @param   num     o número desta ação
     * @param   turn    o turno da batalha no qual esta ação ocorreu
     * @param   a       a arena onde ocorreu a ação
     * @param   p1      o primeiro jogador
     * @param   p2      o segundo jogador
     */
    Action(int turn, int num, Arena a, Player p1, Player p2) throws IllegalArgumentException {

        assert (a != null && p1 != null && p2 != null && num > 0 && turn > 0);
        this.events = new LinkedList<>();
        this.eventsLog = NO_ACTION;
        this.TURN = turn;
        this.A = a;
        this.P1 = p1;
        this.P2 = p2;
        this.NUM = num;
    }

    /**
     * Método principal da ação. Executa um comando, que pode ser: mover, atacar ou
     * encerrar o jogo.
     *
     * A String de comando deve ter a seguinte sintaxe:
     *
     * Para ataque:     attack
     * Para movimento:  move dx dy dz
     * Para encerrar:   exit
     *
     * Onde dx, dy e dz são os valores de deslocamento, respectivamente, no eixo x,
     * eixo y e eixo z do plano cartesiano da arena.
     *
     * Dependendo do comando do usuário, e não havendo divergência na sintaxe, este
     * método chamará um de seus dois sub-métodos: makeMove e makeAttack
     *
     * Qualquer string que não esteja no formato descrito acima resultará no
     * lançamento de {@code IllegalArgumentException} pelo método.
     *
     * @param command o comando, que deve ser digitado pelo jogador
     *
     * @return  <tt>true</tt> para indicar à classe (a que chamou o método) um "sinal de
     *          shutdown", ou seja, que a ação executada foi a ação de encerramento da
     *          aplicação. Retorna <tt>false</tt> em todos os demais casos.
     *
     * @throws IllegalArgumentException caso o comando informado não obedeça a
     *                                  sintaxe descrita acima.
     */
    boolean make(String command) throws IllegalArgumentException {

        assert (eventsLog.equals(NO_ACTION));

        StringTokenizer tok = new StringTokenizer(command);
        String act = "";

        events.add(String.format("Comando: %s\n", command));
        events.add(actionHeader(TURN, NUM));

        if (tok.hasMoreTokens()) act = tok.nextToken();

        Player a = (NUM % 2 == 0) ? P2 : P1; // Jogador ativo nesta ação.
        Player b = (NUM % 2 == 0) ? P1 : P2; // Jogador passivo nesta ação.
        switch (act) {

            case "attack":
                makeAttack(a, b);
                break;

            case "move":
                Queue<String> q = new ArrayDeque<>(3);
                while (tok.hasMoreTokens()) q.add(tok.nextToken());
                if (q.size() != 3)
                    throw new IllegalArgumentException(ERR_INVALID_COMMAND);
                int dx = Integer.parseInt(q.poll());
                int dy = Integer.parseInt(q.poll());
                int dz = Integer.parseInt(q.poll());
                try {
                    makeMove(a, dx, dy, dz);
                    // Se a posição movida já está ocupada pelo outro robô
                    if (a.getRobot().getPosition().equals(b.getRobot().getPosition()))
                        fixPosition(a);
                } catch (LimitMoveException | LimitArenaException e) {
                    System.err.println(e.getMessage());
                    foulPunish(a);
                }
                break;

            case "exit":
                events.add(actionExit());
                buildLog();
                return true; // Envia o sinal para o método que invocou esta ação

            default:
                throw new IllegalArgumentException(ERR_INVALID_COMMAND);
        }
        if (a.getRobot().isInfected()) {
            events.add(actionVirusDamaged(a.getRobot().receiveVirusDamage()));
        }
        buildLog();
        return false;
    }

    /**
     * Método-filho de {@code make()}. Executa a ação de ataque entre um robô
     * atacante e um robô alvo.
     *
     * @param attacker o jogador (robô) atacante
     * @param target o jogador (robô) alvo do ataque
     */
    private void makeAttack(Player attacker, Player target) {

        Robot a = attacker.getRobot(), b = target.getRobot();
        double d = a.attack(b);

        events.add(actionAttack(attacker, target, d));
    }

    /**
     * Método-filho de {@code make()}. Executa a ação de movimento de um robô.
     *
     * Antes da realização do movimento propriamente dito, o método chama
     * a arena e verifica se há um item especial naquela posição.
     * Se houver, este item produzirá um efeito no robô. Este efeito é
     * descrito melhor na descrição do método
     *
     * @param   p   O jogador que realizará o movimento
     * @param   dx  O deslocamento no eixo x
     * @param   dy  O deslocamento no eixo y
     * @param   dz  O deslocamento no eixo z
     *
     * @throws  LimitArenaException Se o robô tentar se mover para um ponto
     *                              fora dos limites da arena
     *
     * @throws  LimitMoveException  Se o robô tentar se mover além da sua
     *                              capacidade física
     */
    private void makeMove(Player p, int dx, int dy, int dz) throws LimitArenaException, LimitMoveException {

        Robot r = p.getRobot();
        if (Math.abs(dx + dy + dz) > r.getMoveLimit())
            throw new LimitMoveException(ERR_MOVE);

        int x = r.getPosition().getX();
        int y = r.getPosition().getY();
        int z = r.getPosition().getZ();
        Point3D newPosition = new Point3D(x + dx, y + dy, z + dz);
        SpecialItem item;
        try {
            item = A.at(newPosition);
        } catch (IllegalArgumentException e) {
            throw new LimitArenaException(ERR_POS);
        }
        if (item != null) getSpecialEffect(item, p);
        r.setPosition(newPosition);

        events.add(actionMove(p, newPosition));
    }

    /**
     * Método chamado quando o robô tenta se mover para uma posição
     * ocupada por um item especial.
     * Este item especial deve ser passado como parâmetro para
     * produzir um efeito no robô.
     *
     * @param item  O item especial produtor do efeito
     * @param p     O player (robô) que receberá o efeito do item
     */
    private void getSpecialEffect(SpecialItem item, Player p) {

        Robot r = p.getRobot();
        double hpBefore = r.getHp();

        // 'itemAfter' é o ítem obtido após o seu efeito no robô 'r'.
        //
        // O polimorfismo de 'item.effect(r)' produz três possíveis resultados:
        // 1. Se 'item' é uma bomba, o robô 'r' sofre um dano;
        // 2. Se 'item' é um vírus, o robô 'r' é infectado;
        // 3. Se 'item' é uma arma, o robô pega esta nova arma.
        //
        item.effect(r, A);

        if (item instanceof Weapon) {
            Weapon w = (Weapon) item;
            String question = String.format("Arma %s foi encontrada. Deseja pegá-la?\n", w);
            boolean answer = InGameText.ask(question, 'y');
            // Se robô não quer pegar a nova arma:
            if (!answer) item.effect(r, A); // Desfaz a troca de arma do efeito 3, descrito acima.
            events.add(actionWeaponFound(w, answer));
        } else {

            if (item instanceof Bomb)
                events.add(actionBOOM(hpBefore - r.getHp()));
            else if (item instanceof Virus)
                events.add(actionInfected());

            A.removeItem(item.getPosition()); // Remove da arena este 'item'.
        }
    }

    /**
     * Método chamado quando ocorre uma violação de espaço
     * (tentar se mover para fora da arena) ou de físico (tentar
     * se mover mais posições do que a restrição física do robô permite).
     * Nestes casos, é caracterizada uma infração por parte do jogador,
     * que deve ser punido recebendo um dano D dado por:
     *
     * D = 2^(n)
     *
     * Onde n é a quantidade de vezes que o jogador provocou uma infração.
     * A punição é gradativa e exponencial.
     *
     * @param p o jogador alvo da punição
     */
    private void foulPunish(Player p) {
        p.addFoul(); // Acrescenta a contagem de infrações do jogador
        final double damage = Math.pow(2, p.getFouls());
        p.getRobot().receiveDamage(damage); // Jogador recebe dano devido à infração.
        events.add(actionFoul(damage));
    }

    /**
     * Corrige o problema de um jogador tentar se mover para a posição na qual
     * o robô adversário já está situado.
     * Neste caso o algoritmo abaixo irá deslocar o robô uma unidade adjacente
     * a ele, sendo esta unidade escolhida aleatoriamente.
     *
     * O método garante que a nova posição do robô estará dentro dos limites
     * da arena.
     *
     * @param p o jogador (robô) que deve ter sua posição corrigida
     */
    private void fixPosition(Player p) {
        boolean lock = true;
        Random r = new Random();
        Point3D q, o = p.getRobot().getPosition();
        do {
            try {
                int i = r.nextInt(3); // Seleciona um dos 3 casos do switch.
                int n = -1 + r.nextInt(3); // Seleciona -1, 0 ou 1.
                int x = o.getX(), y = o.getY(), z = o.getZ();
                switch (i) {
                    case 0:
                        q = new Point3D(x + n, y, z);
                        break;
                    case 1:
                        q = new Point3D(x, y + n, z);
                        break;
                    case 2:
                        q = new Point3D(x, y, z + n);
                        break;
                    default: return;
                }
                A.at(q);
                lock = false;
            } catch (IllegalArgumentException e) {
                q = null;
            }
        } while (lock);
        p.getRobot().setPosition(q);
        events.add(actionRepeatedMove(o, q));
    }

    /**
     * Gera a String de LOG final desta ação, consumando-a.
     */
    private void buildLog() {
        StringBuilder e = new StringBuilder();
        while (!events.isEmpty())
            e.append(events.poll());
        eventsLog = e.toString();
        events = null;
    }

    private static String actionAttack(Player attacker, Player target, double damage) {
        return String.format("Player %d atacou Player %d. Dano: %.1f.\n",
                attacker.getID(), target.getID(), damage);
    }

    private static String actionMove(Player p, Point3D newPosition) {
        return String.format("Player %d moveu-se para a posição %s.\n", p.getID(), newPosition);
    }

    private static String actionRepeatedMove(Point3D old, Point3D nu) {
        return String.format("Posição já ocupada. Deslocado de %s para %s.\n", old, nu);
    }

    private static String actionBOOM(double damage) {
        return String.format("Bomba encontrada! Recebeu %.1f de dano.\n", damage);
    }

    private static String actionInfected() {
        return "Virus encontrado! Robô foi infectado.\n";
    }

    private static String actionVirusDamaged(double damage) {
        return String.format("Recebeu %.1f de dano devido ao vírus.\n", damage);
    }

    private static String actionWeaponFound(Weapon w, boolean switchWeapon) {
        return String.format("Robô encontrou arma %s %s a pegou.\n", w, switchWeapon ? "e" : "mas não");
    }

    private static String actionFoul(double damage) {
        return String.format("Movimento imprudente anulado. Recebeu %.0f de dano.\n", damage);
    }

    private static String actionHeader(int turn, int actionNum) {
        return String.format("TURN %d - ACTION %d\n", turn, actionNum);
    }

    private static String actionExit() {
        return "O jogo foi encerrado.\n";
    }

    /**
     * Compara duas ações diferentes, onde o primeiro critério é o
     * turno na qual esta ação ocorreu e o segundo critério é o
     * número correspondente desta ação.
     *
     * @param o a outra ação a ser comparada com esta
     *
     * @return -1 caso esta ação seja menor do que a outra
     *          1 caso esta ação seja maior do que a outra
     *          0 caos ambas as ações sejam iguais
     */
    @Override
    public int compareTo(@NotNull Action o) {
        if (TURN > o.TURN)
            return 1;
        if (TURN < o.TURN)
            return -1;
        if (NUM > o.NUM)
            return 1;
        if (NUM < o.NUM)
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;
        return NUM == action.NUM && TURN == action.TURN;
    }

    @Override
    public int hashCode() {
        int result = NUM;
        result = 31 * result + TURN;
        return result;
    }

    @Override
    public String toString() {
        return eventsLog;
    }

}
