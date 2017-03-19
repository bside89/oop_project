import com.bside89.poo.tp.MechaWars;

import java.io.File;

/**
 * PROGRAMA PRINCIPAL - ESTRADASPHERE
 *
 * Projeto iniciado em 14/07/2016.
 */

public class Main {

    public static void main(String[] args) {

        String path1 = String.format("datafiles%srobots.txt", File.separator);
        String path2 = String.format("datafiles%sweapons.txt", File.separator);
        String path3 = String.format("datafiles%sconfig.txt", File.separator);

        MechaWars gameInstance = new MechaWars(path1, path2, path3);
        gameInstance.runGame();

    }
}
