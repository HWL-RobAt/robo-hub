package de.hu_berlin.informatik.humboldtquiz.android;

import java.util.ArrayList;
import java.util.List;

import de.hu_berlin.informatik.app.connection.protocol.EV3Command;
import de.hu_berlin.informatik.app.connection.SocketConnection;

/**
 * Created by robert on 25.05.16.
 */
public class AppToRoboCommunicationControl {
    SocketConnection ap2roboConn = null;
    RoboCtrlActivity appCompatActivity = null;

    List<Integer> miscCommands;

    int moveCommand = -1;

    public AppToRoboCommunicationControl(SocketConnection ap2roboConn, RoboCtrlActivity appCompatActivity) {
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
            if ( command == EV3Command.COMMAND_MOVE ) {
                moveCommand = EV3Command.encode(command, p0, p1, p2);
                System.out.println("Add move cmd:" + moveCommand);
            } else {
                miscCommands.add(EV3Command.encode(command, p0, p1, p2));
            }
        }

        return ret;
    }

}
