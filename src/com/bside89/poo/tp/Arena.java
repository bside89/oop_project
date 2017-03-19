package com.bside89.poo.tp;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Representação de um objeto Arena, utilizado no jogo.
 *
 * Uma Arena é um objeto que pode ser modelado como um espaço tridimensional.
 * Cada posição P(x, y, z) em uma Arena, representado por um {@link Point3D},
 * é representada por números inteiros postivos (incluindo o zero).
 *
 * Uma Arena também possui nenhum ou vários {@link SpecialItem} espalhados
 * por seu espaço, cada um ocupando uma posição distinta.
 *
 * Uma Arena não é redimensionável, isto é, seus atributos de largura, comprimento
 * e altura não podem ser alterados uma vez que o objeto é instanciado.
 * No entanto, seu mapa de itens especiais suporta operações de inserção
 * e remoção de itens.
 *
 * @author Bruno Santos
 * @author Thais Hurtado
 *
 * @see Point3D
 * @see ArenaEntity
 * @see SpecialItem
 */
class Arena implements Serializable {

    private Map<Point3D, SpecialItem> itemsMap;
    private final int WIDTH, LENGTH, HEIGHT;

    Arena(int width, int length, int height, Collection<? extends Weapon> weaponsVariety) throws
            IllegalArgumentException {

        assert (weaponsVariety != null);
        final int MIN_DIM = GameConfigs.getArenaMinDimension();
        if (width < MIN_DIM || length < MIN_DIM || height < MIN_DIM)
            throw new IllegalArgumentException();
        this.WIDTH = width;
        this.LENGTH = length;
        this.HEIGHT = height;
        this.itemsMap = new HashMap<>();
        fill(weaponsVariety);
    }

    private void fill(Collection<? extends Weapon> weaponsVariety) {

        assert (weaponsVariety != null && itemsMap != null);

        final double QTY_COEF = GameConfigs.getArenaFillCoefficient();
        int n = (int) (WIDTH * LENGTH * HEIGHT * QTY_COEF * Math.random());

        while (n-- > 0) {
            SpecialItem item = SpecialItem.random(weaponsVariety);
            Point3D p = randomPoint(); // 'p' sempre será um ponto vago na arena.
            item.setPosition(p);
            itemsMap.put(p, item);
        }
    }

    private boolean containsPoint(int i, int j, int k) {
        return i >= 0 && j >= 0 && k >= 0 && i < WIDTH && j < LENGTH && k < HEIGHT;
    }

    private boolean containsPoint(Point3D point) {
        return containsPoint(point.getX(), point.getY(), point.getZ());
    }

    Point3D randomPoint() {
        Random r = new Random();
        Point3D p;
        do {
            int i = r.nextInt(WIDTH);
            int j = r.nextInt(LENGTH);
            int k = r.nextInt(HEIGHT);
            p = new Point3D(i, j, k);
        } while (itemsMap.containsKey(p)); // Garante uma posição vaga.
        return p;
    }

    SpecialItem at(Point3D p) throws IllegalArgumentException {
        if (!containsPoint(p))
            throw new IllegalArgumentException();
        return itemsMap.get(p);
    }

    void putItem(SpecialItem item) {
        itemsMap.put(item.getPosition(), item);
    }

    SpecialItem removeItem(Point3D inPosition) {
        return itemsMap.remove(inPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Arena other = (Arena) o;
        return WIDTH == other.WIDTH && LENGTH == other.LENGTH
                && HEIGHT == other.HEIGHT && itemsMap.equals(other.itemsMap);
    }

    @Override
    public int hashCode() {
        int result = itemsMap.hashCode();
        result = 31 * result + WIDTH;
        result = 31 * result + LENGTH;
        result = 31 * result + HEIGHT;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s: <Dim %d x %d x %d> <Itens %d>", getClass().getSimpleName(),
                WIDTH, LENGTH, HEIGHT, itemsMap.size());
    }

}
