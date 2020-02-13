/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitvn.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thinh
 */
public class WaterMax {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("D:/Games/Grand Theft Auto III/data/water.dat");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr)) {
            // read planes
            String line;
            int id = 1, vId = 1;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(";")) {
                    String[] params = line.split("[\\,]?\\t+");
                    if (params.length == 5) {
                        float level = Float.parseFloat(params[0]);
                        float xLeft = Float.parseFloat(params[1]);
                        float yBottom = Float.parseFloat(params[2]);
                        float xRight = Float.parseFloat(params[3]);
                        float yTop = Float.parseFloat(params[4]);
                        System.out.printf("v %f, %f, %f\n", xLeft, level, -yTop);
                        System.out.printf("v %f, %f, %f\n", xRight, level, -yTop);
                        System.out.printf("v %f, %f, %f\n", xRight, level, -yBottom);
                        System.out.printf("v %f, %f, %f\n", xLeft, level, -yBottom);
                        System.out.printf("o Water%d\n", id);
                        System.out.printf("f %d %d %d %d\n", vId, vId+1, vId+2, vId+3);
                        id++;
                        vId += 4;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(WaterMax.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
