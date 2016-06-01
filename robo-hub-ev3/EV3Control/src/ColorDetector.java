import lejos.robotics.Color;

/**
 * Created by robert on 31.05.16.
 */
public class ColorDetector {

  /*
  public static final int RED = ;
  public static final int GREEN = 1;
 	public static final int BLUE = 2;
 	public static final int YELLOW = 3;
 	public static final int MAGENTA = 4;
 	public static final int ORANGE = 5;
 	public static final int WHITE = 6;
  public static final int BLACK = 7;
  public static final int PINK = 8;
  public static final int GRAY = 9;
  public static final int LIGHT_GRAY = 10;
  public static final int DARK_GRAY = 11;
  public static final int CYAN = 12;
  public static final int BROWN = 13;
  */


  int uvMaxLen = 362; // sqrt(u^2 + v^2)
  int colorCubes[][] = {
      {16, 7, 8, 100, 200, 45, 56}
  };


  int yuvBlackWhite[][] = {
      {7, 0, 50},
      {9, 100, 150},
      {7, 200, 255},
  };

  static public void rgb2yuv(int rgb[], int yuv[]) {
    double Y = 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2];
    int U = (int) Math.round((rgb[1] - Y) * 0.493);      //U=(B-Y)*0.493
    int V = (int) Math.round((rgb[0] - Y) * 0.877);      //U=(B-Y)*0.877

    rgb[0] = (int) Math.round(Y);
    rgb[1] = U;
    rgb[2] = V;
  }

  public int YUVDetector(int rgb[]) {
    int yuv[] = {0, 0, 0};

    rgb2yuv(rgb, yuv);

    int uvLen = (int) Math.round(Math.sqrt(yuv[1] * yuv[0] + yuv[1] * yuv[0]));

    //uvLen/y = uvMaxLen/256
    // -->
    //(uvMaxLen * y) / 256 = uvLen

    boolean isBlackWhite = (uvLen < ((uvMaxLen * yuv[0]) / 256));

    int color = Color.NONE;

    if (isBlackWhite) {
      color = getColorFromCubeList(yuvBlackWhite);
    }

    return color;
  }

  public int getColorFromCubeList(int yuvBlackWhite[][]){
    return 0;
  };
}

