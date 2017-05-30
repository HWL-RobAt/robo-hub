package de.hu_berlin.informatik.robokart.robo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by robert on 24.06.16.
 */
public class RoboKartConfig {

  boolean simMode = false;

  int frequency = 50;

  int defaultHighSpeed = 300;
  int defaultLowSpeed = 200;
  int defaultStartSpeed = defaultHighSpeed;

  int[] lineColorLeft = null;
  int[] lineColorRight = null;

  String colorClfConfig = null;

  int[] markerColor = null;

  public RoboKartConfig() {

  }

  public RoboKartConfig(String propertiesFile) {
    this();
    loadConfigfile(propertiesFile);
  }

  public void loadConfigfile(String propertiesFile) {
    Properties p = new Properties();

    try {
      p.load(new FileReader(propertiesFile));
    } catch ( FileNotFoundException e) {
      System.out.println("File " + propertiesFile + " not found");
    } catch ( IOException ioe ) {
      System.out.println("IO-Error! File: " + propertiesFile + "");
    }

    readProperties(p);

  }

  private void readProperties(Properties p) {
    simMode = Boolean.parseBoolean(p.getProperty("Sim", "false"));
    colorClfConfig = p.getProperty("ColorClfConfig","");
    defaultHighSpeed = Integer.parseInt(p.getProperty("DefaultHighSpeed","300"));
    defaultLowSpeed = Integer.parseInt(p.getProperty("DefaultLowSpeed","200"));
    defaultStartSpeed = Integer.parseInt(p.getProperty("DefaultStartSpeed",""+defaultHighSpeed));
    frequency = Integer.parseInt(p.getProperty("Frequency","50"));


    String lcl = p.getProperty("LineColorLeft","-1");

    String lclList[] = lcl.replace(" ","").split(",");
    lineColorLeft = new int[lclList.length];
    for ( int i = 0; i < lclList.length; i++)
      lineColorLeft[i] = Integer.parseInt(lclList[i]);


    String lcr = p.getProperty("LineColorRight","-1");

    String lcrList[] = lcr.replace(" ","").split(",");
    lineColorRight = new int[lcrList.length];
    for ( int i = 0; i < lcrList.length; i++)
      lineColorRight[i] = Integer.parseInt(lcrList[i]);

    String mc = p.getProperty("LineColorRight","-1");

    String mcList[] = mc.replace(" ","").split(",");
    markerColor = new int[mcList.length];
    for ( int i = 0; i < mcList.length; i++)
      markerColor[i] = Integer.parseInt(mcList[i]);

  }

}
