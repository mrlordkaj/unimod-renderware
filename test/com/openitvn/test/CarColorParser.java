/*
 * Copyright (C) 2020 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.test;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class CarColorParser {

    private enum ReadMode { None, Color, CarColor }
    
    private class CarColor {
        Color primaryColor;
        Color secondaryColor;
    }
    
    private ReadMode mode = ReadMode.None;
    
    private ArrayList<Color> colors;
    
    private HashMap<String, ArrayList<CarColor>> carColors;
    
    void parseFile(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr)) {
            colors = new ArrayList<>();
            carColors = new HashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\\s*#.*$", "");
                if (line.equals("col")) {
                    mode = ReadMode.Color;
                    continue;
                } else if (line.equals("car")) {
                    mode = ReadMode.CarColor;
                    continue;
                } else if (line.equals("end")) {
                    mode = ReadMode.None;
                    continue;
                }
                String[] params = line.split(",");
                switch (mode) {
                    case Color:
                        if (params.length == 3) {
                            int r = Integer.parseInt((params[0]));
                            int g = Integer.parseInt((params[1]));
                            int b = Integer.parseInt((params[2]));
                            colors.add(new Color(r, g, b));
                        }
                        break;
                        
                    case CarColor:
                        if (params.length >= 3) {
                            String name = params[0].trim();
                            ArrayList<CarColor> myColors = carColors.get(name);
                            if (myColors == null) {
                                myColors = new ArrayList<>();
                                carColors.put(name, myColors);
                            }
                            for (int i = 1; i < params.length; i+= 2) {
                                int primary = Integer.parseInt(params[i].trim());
                                int secondary = Integer.parseInt(params[i+1].trim());
                                CarColor myColor = new CarColor();
                                myColor.primaryColor = colors.get(primary);
                                myColor.secondaryColor = colors.get(secondary);
                                myColors.add(myColor);
                            }
                        }
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    void printUE4(String vehicleName) {
        ArrayList<CarColor> myColors = carColors.get(vehicleName);
        System.out.println("(");
        float f = 1.0f / 255.0f;
        for (CarColor myColor : myColors) {
            float r1 = myColor.primaryColor.getRed() * f;
            float g1 = myColor.primaryColor.getGreen()* f;
            float b1 = myColor.primaryColor.getBlue()* f;
            float r2 = myColor.secondaryColor.getRed() * f;
            float g2 = myColor.secondaryColor.getGreen()* f;
            float b2 = myColor.secondaryColor.getBlue()* f;
            System.out.printf("(primaryColor=(R=%.6f,G=%.6f,B=%.6f,A=1.000000),\n", r1, g1, b1 );
            System.out.printf("secondaryColor=(R=0.360784,G=0.360784,B=0.360784,A=1.000000)),\n", r2, g2, b2);
        }
        System.out.println(")");
    }
    
    public static void main(String[] args) {
        CarColorParser col = new CarColorParser();
        col.parseFile("D:/Games/Grand Theft Auto III/data/carcols.dat");
        col.printUE4("kuruma");
    }
}
