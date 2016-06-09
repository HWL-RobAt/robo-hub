package de.hu_berlin.informatik.ev3.ai;

import de.hu_berlin.informatik.ev3.data.BlackBoard;
import de.hu_berlin.informatik.ev3.data.BlackBoardUser;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 07.04.16
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public abstract class Braitenberg implements BlackBoardUser {

  public static final int BRAITENBERG_MOVE_NONE = 0;
  public static final int BRAITENBERG_MOVE_RIGHT = 1;
  public static final int BRAITENBERG_MOVE_LEFT = 2;

  protected String blackBoardInputName = null;

  protected String blackBoardName = null;
  protected int blackBoardID = -1;

  int dataInput[] = null;
  int dataOutput[] = null;

  public Braitenberg() {
    dataOutput = new int[1];
    dataOutput[0] = BRAITENBERG_MOVE_NONE;

    blackBoardInputName = "LineDetector";
    blackBoardName = "Braitenberg";
    blackBoardID = -1;
  }

  public void update(BlackBoard blackBoard) {
    dataInput = (int[])(blackBoard.getDataList(blackBoardInputName).get(0));

    if (blackBoardID == -1) blackBoardID = blackBoard.setData(blackBoardName, dataOutput);
  }
}
