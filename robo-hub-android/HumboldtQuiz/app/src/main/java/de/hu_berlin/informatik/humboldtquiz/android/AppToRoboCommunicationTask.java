package de.hu_berlin.informatik.humboldtquiz.android;

import android.os.AsyncTask;

import de.hu_berlin.informatik.app.connection.protocol.EV3Command;
import de.hu_berlin.informatik.app.connection.SocketConnection;

/**
 * Created by robert on 25.05.16.
 */
class AppToRoboCommunicationTask extends AsyncTask<AppToRoboCommunicationControl, Integer, AppToRoboCommunicationControl> {

    private Exception exception;

    AppToRoboCommunicationControl app2roboComCtrl = null;

    protected AppToRoboCommunicationControl doInBackground(AppToRoboCommunicationControl ... a2rCommCtrls) {
        try {
            app2roboComCtrl = a2rCommCtrls[0];
            SocketConnection ap2roboConn = app2roboComCtrl.ap2roboConn;

            ap2roboConn.init();

            while ( ! app2roboComCtrl.closeIt ) {
                if (ap2roboConn.rxAvailable()) {
                    int rx = ap2roboConn.receiveInt();
                    System.out.println("RX: " + rx);
                    publishProgress(rx);
                } else {

                    int txCommand = app2roboComCtrl.getNextSendCommand();
                    if ( txCommand != -1 ) {
                        if (ap2roboConn.isClosed()) {
                            System.out.println("Socket is Closed");
                        } else {
                            System.out.println("Send move cmd:" + txCommand);
                            ap2roboConn.sendInt(txCommand);
                        }
                    }

                    Thread.sleep(20,0);
                }
            }

            //send the rest
            int txCommand = app2roboComCtrl.getNextSendCommand();
            while (txCommand != -1) {
                ap2roboConn.sendInt(txCommand);
                txCommand = app2roboComCtrl.getNextSendCommand();
            }

            app2roboComCtrl.reset();
            ap2roboConn.close();

            System.out.println("ConnTaskFin");
            return app2roboComCtrl;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }


    protected void onProgressUpdate(Integer... rxTxInt) {
        System.out.println("RX comand: " + rxTxInt[0]);

        int params[] = { 0,0,0 };
        int command = EV3Command.decode(rxTxInt[0], params);

        switch ( command ) {
            case EV3Command.COMMAND_QUESTION: {
                app2roboComCtrl.reset();
                app2roboComCtrl.appCompatActivity.qvm.nextQuestion();
                break;
            }
            case EV3Command.COMMAND_STOP: {
                app2roboComCtrl.reset();
                app2roboComCtrl.appCompatActivity.stopQuiz(false);
                break;
            }
        }
    }

    protected void onPostExecute(SocketConnection sc) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

