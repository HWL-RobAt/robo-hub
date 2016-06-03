import lejos.hardware.*;
import lejos.hardware.Button;
import lejos.robotics.Color;
import sun.plugin2.gluegen.runtime.BufferFactory;

import java.awt.*;
import java.io.*;
import java.util.Properties;

/**
 * Created by Heinrich Mellmann on 31.05.16.
 */
public class ColorClassifierYUV extends ColorClassifier {
  // classifier params
  int brightnesConeOffset = 25;
  double brightnesConeRadiusWhite = 70.0;
  double brightnesConeRadiusBlack = 2.0;

  //Angle, Y, UVLen
  double colorAngleUV[][] = { {  0,  90,  -1,  0,  0,  0},
                              { 90, 109,  -1,  0,  0,  0},
                              {110, 122,   0, 30,  0,  0},
                              {110, 122,  31, 80,  0,  0},
                              {122, 150,  -1,  0,  0,  0},
                              {150, 180,  -1,  0,  0,  0},
                              {180, 240,  -1,  0,  0,  0},
                              {240, 280,  -1,  0,  0,  0},
                              {280, 360,  -1,  0,  0,  0}};

  int colorIdUV[] = {Color.MAGENTA, Color.PINK, Color.RED, Color.ORANGE, Color.BROWN, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.NONE};

  double noColorRangesY[][] = { {  0, 25 },
                                { 26, 35 },
                                { 36,255 }};

  int noColorIdY[] = {Color.BLACK, Color.GRAY, Color.WHITE, Color.NONE};

  private boolean debugYUV = false;
  private PrintWriter debugWriter = null;

  public Color getColor(int rgb[]) {
    return new Color(rgb[0], rgb[1], rgb[2], getColorID(rgb));
  }

  public int getColorID(int rgb[]) {
    int color = Color.NONE;

    int yuv[] = {0, 0, 0};
    rgb2yuv(rgb, yuv);

    int angleInt = (int)((Math.round((180.0 * colorAngle(yuv))/Math.PI) + 360) % 360);

    if (noColor(yuv)) {
      for (int c = 0; noColorIdY[c] != Color.NONE; c++) {
        if ((noColorRangesY[c][0] <= yuv[0]) && ((yuv[0] <= noColorRangesY[c][1])))
          color = noColorIdY[c];
      }
    } else {
      for (int c = 0; colorIdUV[c] != Color.NONE; c++) {
        if ((colorAngleUV[c][0] <= angleInt) && ((angleInt <= colorAngleUV[c][1]))) {
          if ( colorAngleUV[c][2] == -1) color = colorIdUV[c];
          else {
            if ((colorAngleUV[c][2] <= yuv[0]) && ((yuv[0] <= colorAngleUV[c][3])))
              color = colorIdUV[c];
          }
        }
      }
    }

    if ( debugYUV ) {
      int uvLen = (yuv[1] - 128) * (yuv[1] - 128) + (yuv[2] - 128) * (yuv[2] - 128);
      debugWriter.println(color + "," + yuv[0] + "," + angleInt + "," + Math.round(Math.sqrt(uvLen)));
    }

    return color;
  }

  private double colorAngle(int yuv[]) {
	  return Math.atan2(yuv[2] - 128.0, yuv[1] - 128.0);
  }

  static public void rgb2yuv(int rgb[], int yuv[]) {
	  yuv[0] = ((  66 * rgb[0] + 129 * rgb[1] +  25 * rgb[2] + 128) >> 8) +  16;
	  yuv[1] = (( -38 * rgb[0] -  74 * rgb[1] + 112 * rgb[2] + 128) >> 8) + 128;
	  yuv[2] = (( 112 * rgb[0] -  94 * rgb[1] -  18 * rgb[2] + 128) >> 8) + 128;
  }

  // return true if the pixel doesn't have enough chroma
  private boolean noColor(int yuv[]) {
	  return noColor(yuv[0],yuv[1],yuv[2]);
  }

  private boolean noColor(int y, int u, int v)
  {
    double brightnesAlpha = (brightnesConeRadiusWhite - brightnesConeRadiusBlack) / (double)(255 - brightnesConeOffset);
    double cromaThreshold = Math.max(brightnesConeRadiusBlack, brightnesConeRadiusBlack + brightnesAlpha * (double)(y-brightnesConeOffset));
    int uvLen = (u - 128)*(u - 128) + (v - 128)*(v - 128);
    return uvLen < (cromaThreshold * cromaThreshold); // sqrt(Y) < X --> y < (X*X)
  }


  public void loadConfig(String filename) {
    Properties p = new Properties();

    try {
      p.load(new FileReader(filename));
    } catch ( FileNotFoundException e) {
      System.out.println("File " + filename + " not found");
      try {
        Thread.sleep(20000);
      }catch (InterruptedException ie) {}
    } catch ( IOException ioe ) {
      System.out.println("File(io) " + filename + " not found");
      try {
        Thread.sleep(20000);
      }catch (InterruptedException ie) {}
    }

    brightnesConeOffset = Integer.parseInt(p.getProperty("brightnesConeOffset"));
    brightnesConeRadiusWhite = Double.parseDouble(p.getProperty("brightnesConeRadiusWhite"));
    brightnesConeRadiusBlack = Double.parseDouble(p.getProperty("brightnesConeRadiusBlack"));

    String colorDataTable =  p.getProperty("colorDataTable");
    noColorRangesY = new double[10][2];
    noColorIdY = new int[10];
    colorAngleUV = new double[20][6];
    colorIdUV = new int[20];

    int noColorIndex = 0;
    int colorIndex = 0;

    try {
      FileReader fr = new FileReader(colorDataTable);
      BufferedReader bufR = new BufferedReader(fr);

      String line = null;
      while (null != (line = bufR.readLine())) {
        String[] data = line.replace(" ","").split(",");

        if (data.length < 7) continue;

        if ( Double.parseDouble(data[1]) == -1) { //no angle so black/white
          noColorRangesY[noColorIndex][0] = Double.parseDouble(data[3]);
          noColorRangesY[noColorIndex][1] = Double.parseDouble(data[4]);
          noColorIdY[noColorIndex] = Integer.parseInt(data[0]);
          noColorIndex++;
        } else {
          for (int i = 0; i < 6; i++) colorAngleUV[colorIndex][i] = Double.parseDouble(data[i+1]);
          colorIdUV[colorIndex] = Integer.parseInt(data[0]);
          colorIndex++;
        }
      }

      fr.close();
    } catch (FileNotFoundException fnfE) {
      System.out.println("File " + colorDataTable + " not found");
      try {
        Thread.sleep(20000);
      }catch (InterruptedException ie) {}
      throw new RuntimeException();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }

    noColorIdY[noColorIndex] = Color.NONE;
    colorIdUV[colorIndex] = Color.NONE;
  }

  public void startDebug(String filename) {
    try {
      debugWriter = new PrintWriter(filename);
      debugWriter.println("C,Y,Angle,UVLen");
      debugYUV = true;
    } catch (IOException ioe) {
      System.out.println("File " + filename + " could not be created");
      throw new RuntimeException();
    }
  }

  public void stopDebug() {
    if ( debugWriter != null ) {
      debugWriter.close();
      debugWriter = null;
      debugYUV = false;
    }
  }
}

