import lejos.robotics.Color;

/**
 * Created by robert on 31.05.16.
 */
public class ColorDetector 
{
  // classifier params
  int brightnesConeOffset = 22;
  double brightnesConeRadiusWhite = 70.0;
  double brightnesConeRadiusBlack = 3.0;

  // params for color segments
  
  /*
  int uvMaxLen = 362; // sqrt(u^2 + v^2)
  int colorCubes[][] = {
      {16, 7, 8, 100, 200, 45, 56}
  };

  int yuvBlackWhite[][] = {
      {7, 0, 50},
      {9, 100, 150},
      {7, 200, 255},
  };
*/
  /*
  static public void rgb2yuv(int rgb[], int yuv[]) 
  {
    double Y = 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2];
    int U = (int) Math.round((rgb[1] - Y) * 0.493);      //U=(B-Y)*0.493
    int V = (int) Math.round((rgb[0] - Y) * 0.877);      //U=(B-Y)*0.877

    yuv[0] = Y;
    yuv[1] = U;
    yuv[2] = V;
  }
  */
  
  static public void rgb2yuv(int rgb[], int yuv[]) 
  {
	yuv[0] = ((  66 * rgb[0] + 129 * rgb[1] +  25 * rgb[2] + 128) >> 8) +  16;
	yuv[1] = (( -38 * rgb[0] -  74 * rgb[1] + 112 * rgb[2] + 128) >> 8) + 128;
	yuv[2] = (( 112 * rgb[0] -  94 * rgb[1] -  18 * rgb[2] + 128) >> 8) + 128;
  }

  /*
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
  }
  */
  
  public int getColor(float[] rgb) 
  {
	  int R = (int)(rgb[0]*255+0.5);
	  int G = (int)(rgb[1]*255+0.5);
	  int B = (int)(rgb[2]*255+0.5);
	  int yuv[] = {0, 0, 0};
	  rgb2yuv(new int[]{R,G,B}, yuv);
	  
	  if(noColor(yuv)) 
	  {
      	if(yuv[0] < brightnesConeOffset) {
      		return Color.BLACK;
      	} else if(yuv[0] > brightnesConeOffset + 10) {
      		return Color.WHITE;
      	}
      } else 
      {
    	  double a = colorAngle(yuv);
    	  if(a > Math.PI-Math.PI/4) {
    		 return Color.YELLOW;
    	  } else if(a > -Math.PI && a < -Math.PI/2) {
          	return Color.GREEN;
          } else if(a > Math.PI/2 && a < Math.PI) {
        	  return Color.RED;
          } else if(a > 0 && a < Math.PI/2) {
        	  return Color.MAGENTA;
          } else if(a < 0 && a > -Math.PI/2) {
        	  return Color.BLUE;
          }
      }
	  
	  return Color.NONE;
  }
  
  private double colorAngle(int yuv[]) {
	  return Math.atan2(yuv[2] - 128.0, yuv[1] - 128.0);
  }
	
  // return true if the pixel doesn'r have enough chroma
  private boolean noColor(int yuv[]) {
	  return noColor(yuv[0],yuv[1],yuv[2]);
  }
  private boolean noColor(int y, int u, int v)
  {
    double brightnesAlpha = (brightnesConeRadiusWhite - brightnesConeRadiusBlack) / (double)(255 - brightnesConeOffset);
    double cromaThreshold = Math.max(brightnesConeRadiusBlack, brightnesConeRadiusBlack + brightnesAlpha * (double)(y-brightnesConeOffset));
    return Math.sqrt((u - 128)*(u - 128) + (v - 128)*(v - 128)) < cromaThreshold;
  }
}

