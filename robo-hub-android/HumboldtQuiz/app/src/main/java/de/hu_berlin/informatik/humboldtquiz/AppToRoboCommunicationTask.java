package de.hu_berlin.informatik.humboldtquiz;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by robert on 25.05.16.
 */
class AppToRoboCommunicationTask extends AsyncTask<AppToRoboCommunicationControl, Integer, AppToRoboCommunicationControl> {

    private Exception exception;

    AppToRoboCommunicationControl app2roboComCtrl = null;

    protected AppToRoboCommunicationControl doInBackground(AppToRoboCommunicationControl ... a2rCommCtrls) {
        try {
            app2roboComCtrl = a2rCommCtrls[0];
            SocketConnector ap2roboConn = app2roboComCtrl.ap2roboConn;

            ap2roboConn.init();

            while ( ! ap2roboConn.isClosed() ) {
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
                            ap2roboConn.sendInt(txCommand);
                        }
                    }

                    Thread.sleep(10,0);
                }
            }

            app2roboComCtrl.reset();

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
        int command = AppCommand.decode(rxTxInt[0], params);

        if ( command == AppCommand.COMMAND_QUESTION ) {
            app2roboComCtrl.reset();
            app2roboComCtrl.appCompatActivity.qvm.nextQuestion();
        }
    }

    protected void onPostExecute(SocketConnector sc) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

