package de.hu_berlin.informatik.humboldtquiz;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 25.05.16.
 */
public class AppToRoboCommunicationControl {
    SocketConnector ap2roboConn = null;
    RoboCtrlActivity appCompatActivity = null;

    List<Integer> miscCommands;

    int moveCommand = -1;

    public AppToRoboCommunicationControl(SocketConnector ap2roboConn, RoboCtrlActivity appCompatActivity) {
        this.ap2roboConn = ap2roboConn;
        this.appCompatActivity = appCompatActivity;
        miscCommands = new ArrayList<Integer>();
    }

    public void sendCommand(int command, int p0, int p1, int p2) {
        nextCommendSetGet(false, command, p0, p1, p2);
    }

    public void sendCommand(int command) {
        nextCommendSetGet(false, command, 0, 0, 0);
    }

    public int getNextSendCommand() {
        return nextCommendSetGet(true , 0, 0, 0, 0);
    }

    public void reset() {
        moveCommand = -1;
        miscCommands.clear();
    }

    synchronized private int nextCommendSetGet(boolean get, int command, int p0, int p1, int p2) {
        int ret = -1;
        if ( get ) {
            if ( miscCommands.size() > 0 ) {
                ret = miscCommands.get(0);
                miscCommands.remove(0);
            } else {
                if ( moveCommand != -1 ) {
                    ret = moveCommand;
                    moveCommand = -1;
                }
            }
        } else {
            if ( command == AppCommand.COMMAND_MOVE ) {
                moveCommand = AppCommand.encode(command, p0, p1, p2);
            } else {
                miscCommands.add(AppCommand.encode(command, p0, p1, p2));
            }
        }

        return ret;
    }

}
