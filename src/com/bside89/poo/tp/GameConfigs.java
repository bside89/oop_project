package com.bside89.poo.tp;

import static com.bside89.poo.tp.InGameText.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * Created by Blue on 30/09/2016.
 *
 * @author Bruno Santos
 */
public final class GameConfigs {

    // Valores padrão:
    private static final int VALUES = 3; // Qtd. de atributos da classe
    private static int arenaMinDimension = 30;
    private static double arenaFillCoefficient = 0.2;
    private static String fileSaveStateName = "datafiles" + File.separator + "saves.ser";

    static void loadConfigs(String filePath) {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            Queue<String> q = new LinkedList<>();

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.equals("") || line.charAt(0) == '#')
                    continue;
                for (StringTokenizer tok = new StringTokenizer(line); tok.hasMoreTokens(); )
                    q.add(tok.nextToken());
            }
            if (q.size() != VALUES)
                throw new IOException(ERR_GAME_CONFIG);

            arenaMinDimension = Integer.parseInt(q.poll());
            arenaFillCoefficient = Double.parseDouble(q.poll());
            fileSaveStateName = "datafiles" + File.separator + q.poll();

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println(USE_DEFAULT_CONFIGS);
        }
    }

    static int getArenaMinDimension() {
        return arenaMinDimension;
    }

    static double getArenaFillCoefficient() {
        return arenaFillCoefficient;
    }

    static String getFileSaveStateName() {
        return fileSaveStateName;
    }

}
