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
public class WaterUE4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (InputStream is = WaterUE4.class.getResourceAsStream("/com/openitvn/test/water_template_ue4");
                FileInputStream fis = new FileInputStream("D:/Games/Grand Theft Auto III/data/water.dat");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr)) {
            // read template
            byte[] data = new byte[is.available()];
            is.read(data);
            String template = new String(data);
            System.out.println("Begin Map\n   Begin Level");
            // read planes
            String line;
            int id = 1;
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
                        System.out.printf(template, id, x, y, z, width, length);
//                        System.out.printf("(X=%.3f,Y=%.3f,Z=%.3f) | (X=%.3f,Y=%.3f,Z=1)\n", x, y, z, width, length);
                        id++;
                    }
                }
            }
            System.out.println("   End Level\nBegin Surface\nEnd Surface\nEnd Map");
        } catch (IOException ex) {
            Logger.getLogger(WaterUE4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
