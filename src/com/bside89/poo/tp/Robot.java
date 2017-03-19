package com.bside89.poo.tp;

/**
 * Created by Blue on 02/09/2016.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 */
class Robot extends ArenaEntity implements Cloneable, Nameable {

    private final String NAME;
    private final double BASE_HP, BASE_ARMOR;
    private double hp, armor;
    private Weapon weapon;
    private Virus virus;
    private final int MOVE_LIMIT;

    /**
     * Constrói este robô, desarmado e fora da arena (situado no OUTLIER_POINT).
     *
     * @param   name    o nome deste robô
     * @param   hp      os pontos de vida
     * @param   armor   o valor de armadura
     */
    Robot(String name, double hp, double armor) {
        this(name, hp, armor, null, OUTLIER_POINT);
    }

    /**
     * Constrói este robô.
     *
     * @param   name        o nome deste robô
     * @param   hp          seus pontos de vida
     * @param   armor       seu valor de armadura
     * @param   weapon      sua arma
     * @param   position    sua posição
     */
    Robot(String name, double hp, double armor, Weapon weapon, Point3D position) {

        assert (name != null && hp > 0 && armor > 0);
        this.NAME = name;
        this.hp = BASE_HP = hp;
        this.armor = BASE_ARMOR = armor;
        this.weapon = weapon;
        this.position = position;
        this.MOVE_LIMIT = calcMoveLimit(hp, armor);
    }

    /**
     * Obtém o valor máximo de unidades na qual um robô pode deslocar-se em uma arena.
     * O cálculo do limite L de um robô R é feito da seguinte forma:
     *
     * L(R) = (9000 / HP) + (600 / A)
     *
     * Onde HP e A são, respectivamente, valores de HP e armadura do robô.
     *
     * @param hp o valor de hp do robô
     * @param armor o valor de armadura do robô
     *
     * @return o valor do cálculo descrito acima
     */
    private int calcMoveLimit(double hp, double armor) {
        return (int) (9000/hp + 600/armor);
    }

    /**
     * Verifica se este robô está morto, ou seja, hp = 0
     *
     * @return <tt>true</tt> caso o robô esteja morto
     */
    boolean isDead() {
        return hp == 0;
    }

    /**
     * Verifica se este robô está infectado por um vírus.
     *
     * @return <tt>true</tt> caso este robô esteja infectado
     */
    boolean isInfected() {
        return virus != null;
    }

    /**
     * Verifica se este robô possui uma arma.
     *
     * @return <tt>true</tt> caso este robô tenha uma arma
     */
    boolean isArmed() {
        return weapon != null;
    }

    /**
     * Executa a ação de atacar do robô.
     * O cálculo do dano D de um robô r1 em um robô r2 é dado por:
     *
     * D(r1, r2) = (S(r1)/dist(r1, r2))*random(0, 1) - (A(r2))*random(0, 1)
     *
     * Onde:
     * S(r1) é o coeficiente de dano da arma do robô atacante (r1);
     * dist(r1, r2) é a distância entre os dois robôs;
     * A(r2) é o valor de armadura do robô alvo (r2).
     *
     * Note que nenhm ataque será executado caso este robô esteja desarmado ou
     * caso ao menos um dos robôs esteja fora da arena.
     *
     * @param target o robô alvo do ataque
     *
     * @return o dano do ataque, cujo cálculo foi descrito acima
     */
    double attack(Robot target) {

        if (!isArmed())
            return 0;
        if (position.equals(OUTLIER_POINT) || target.position.equals(OUTLIER_POINT))
            return 0;

        double sigma = weapon.getSigma();
        double dist = position.distance(target.position);
        double rand1 = Math.random(), rand2 = Math.random();
        double defense = target.armor;

        double damage = (sigma/dist)*rand1 - defense*rand2;

        if (damage < 0)
            damage = 0; // Não deve existir número negativo de dano.
        else
            target.receiveDamage(damage);

        return damage;
    }

    /**
     * Infecta este robô com um vírus
     *
     * @param virus o vírus a ser atachado a este robô
     */
    void getInfectionBy(Virus virus) {
        this.virus = virus;
        this.virus.setPosition(this.position); // Carrega o vírus junto
    }

    /**
     * Recebe um valor de dano, descontado do HP deste robô.
     *
     * @param damage o valor do dano a ser descontado
     */
    void receiveDamage(double damage) {
        hp -= damage;
        if (hp < 0) hp = 0;
    }


    /**
     * Zera os pontos de vida do robõ, deixando-o no estado "morto".
     */
    void kill() {
        hp = 0;
    }

    /**
     * Recebe um valor de dano do vírus atachado a este robô, caso exista.
     *
     * @return o valor do dano recebido
     */
    double receiveVirusDamage() {

        if (isInfected()) {
            double damage = virus.calcDamage();
            receiveDamage(damage);
            virus.reduce();
            if (virus.isDead()) virus = null; // Desinfecta o robô.
            return damage;
        }
        return 0;
    }

    /**
     * Desloca este robô, e todos os itens carregados por ele, para uma nova posição.
     *
     * @param position a nova posição deste robô
     */
    void setPosition(Point3D position) {
        this.position = position;
        if (isArmed())
            weapon.setPosition(position); // Carrega a arma junto.
        if (isInfected())
            virus.setPosition(position); // Carrega o vírus junto.
    }

    /**
     * Equipa este robô com uma nova arma.
     *
     * @param weapon sua nova arma
     */
    void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        // Coloca a arma na mesma posição do robô.
        if (!position.equals(this.weapon.getPosition()))
            this.weapon.setPosition(this.position);
    }

    @Override
    public String getName() {
        return NAME;
    }

    double getHp() {
        return hp;
    }

    double getArmor() {
        return armor;
    }

    Weapon getWeapon() {
        return weapon;
    }

    Point3D getPosition() {
        return position;
    }

    int getMoveLimit() {
        return MOVE_LIMIT;
    }

    /**
     * Obtém uma deep copy deste robô.
     *
     * Note que a cópia terá os atributos padrão do robô, como em uma nova
     * instância de objeto.
     * Caso este robô possua uma arma, esta também será clonada.
     *
     * @return uma cópia deste robô
     */
    @Override
    public final Object clone() {
        try {
            Robot o = (Robot) super.clone();
            if (weapon != null) o.weapon = (Weapon) weapon.clone();
            o.setPosition(OUTLIER_POINT);
            return o;
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
        Robot other = (Robot) o;
        return NAME.equals(other.NAME) && BASE_HP == other.BASE_HP && BASE_ARMOR == other.BASE_ARMOR;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = NAME.hashCode();
        temp = Double.doubleToLongBits(BASE_HP);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(BASE_ARMOR);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s: <HP %.2f> <Armor %.2f>", NAME,
                weapon != null ? "c/ " + weapon : "", BASE_HP, BASE_ARMOR);
    }

}
