/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitvn.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thinh
 */
public class Water {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("D:/Games/Grand Theft Auto III/data/water.dat");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(";")) {
                    String[] params = line.split("[\\,]?\\t+");
                    if (params.length == 5) {
                        float level = Float.parseFloat(params[0]);
                        float xLeft = Float.parseFloat(params[1]);
                        float yBottom = Float.parseFloat(params[2]);
                        float xRight = Float.parseFloat(params[3]);
                        float yTop = Float.parseFloat(params[4]);
                        float x = (xRight + xLeft) / 2 * 100;
                        float y = -(yBottom + yTop) / 2 * 100;
                        float z = level * 100;
                        float width = Math.abs(xRight - xLeft);
                        float length = Math.abs(yBottom - yTop);
                        System.out.printf("(X=%.3f,Y=%.3f,Z=%.3f) | (X=%.3f,Y=%.3f,Z=1)\n", x, y, z, width, length);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Water.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
