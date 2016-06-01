package de.hu_berlin.informatik.humboldtquiz;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class RoboCtrlActivity extends AppCompatActivity implements SensorEventListener {

    public static final int QUIZ_MODE_STOPPED = 0;
    public static final int QUIZ_MODE_RUNNING = 1;

    public static final int CONNECTION_MODE_DISCONNECTED = 0;
    public static final int CONNECTION_MODE_CONNECTED = 1;

    public static final int ROBOCTRL_MODE_GYRO = 0;
    public static final int ROBOCTRL_MODE_TOUCH = 1;
    public static final int ROBOCTRL_MODE_LINE = 2;
    public static final int ROBOCTRL_MODE_DISTANCE = 3;

    QuizViewManager qvm = null;

    int quizMode = QUIZ_MODE_STOPPED;
    int connectionMode = CONNECTION_MODE_DISCONNECTED;
    int roboctrlMode = 0;

    SocketConnector sc = null;
    ConnectionInfo ci = null;

    AppToRoboCommunicationControl app2roboComCtrl = null;

    Chronometer chronometer;
    long chronometerBase = 0;

    final int[] viewSize = new int[2];
    final int[] viewPos = new int[2];

    //the Sensor Manager
    private SensorManager sensorManager;

    TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( qvm == null ) qvm = new QuizViewManager(this);

        ci = new ConnectionInfo("192.168.0.174", 2000); //Dev -> Robo
        sc = new SocketConnector(ci);
        app2roboComCtrl = new AppToRoboCommunicationControl(sc,this);

        roboctrlMode = 0;

        updateView(true);

        //get a hook to the sensor service
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    public void onClickConnect(View view) {
        if ( connectionMode == CONNECTION_MODE_DISCONNECTED ) {
            EditText etIP = (EditText)findViewById(R.id.editText_addr);
            String ipAddr = etIP.getText().toString();

            if ( !ipAddr.equals("127.0.0.1")) ci.nameOrIP = ipAddr;

            AsyncTask commTask = new AppToRoboCommunicationTask().execute(app2roboComCtrl);

            System.out.println("Connect");

            connectionMode = CONNECTION_MODE_CONNECTED;
        } else {
            app2roboComCtrl.sendCommand(AppCommand.COMMAND_DISCONNECT);
            app2roboComCtrl.ap2roboConn.close();

            System.out.println("Disconnect");

            connectionMode = CONNECTION_MODE_DISCONNECTED;
        }

        updateView(false);
    }

    public void onClickStart(View view) {
        if ( quizMode == QUIZ_MODE_STOPPED) {
            final Spinner spinner_level = (Spinner) findViewById(R.id.spinner_level);
            qvm.setLevel(spinner_level.getSelectedItemPosition());

            final Spinner spinner_roboctrl = (Spinner) findViewById(R.id.spinner_roboctrl);
            roboctrlMode = spinner_roboctrl.getSelectedItemPosition();

            System.out.println("Mode: " + roboctrlMode);
            app2roboComCtrl.sendCommand(AppCommand.COMMAND_START, roboctrlMode, 0, 0);

            startChronometer(false);

            quizMode = QUIZ_MODE_RUNNING;

        } else {
            app2roboComCtrl.sendCommand(AppCommand.COMMAND_STOP);

            stopChronometer(false);

            quizMode = QUIZ_MODE_STOPPED;
        }

        updateView(false);
    }

    public void setSpinnerItems() {
        Spinner spinner_level = (Spinner) findViewById(R.id.spinner_level);
        ArrayAdapter<CharSequence> adapter_level = ArrayAdapter.createFromResource(this, R.array.level_items, android.R.layout.simple_spinner_item);

        Spinner spinner_roboctrl = (Spinner) findViewById(R.id.spinner_roboctrl);
        ArrayAdapter<CharSequence> adapter_roboctrl = ArrayAdapter.createFromResource(this, R.array.roboctrl_items, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter_level.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_roboctrl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner_level.setAdapter(adapter_level);
        if ( qvm.qc.currentLevel >= 0 ) spinner_level.setSelection(qvm.qc.currentLevel);

        spinner_roboctrl.setAdapter(adapter_roboctrl);
        spinner_roboctrl.setSelection(roboctrlMode);

    }

    public void updateView(boolean switchContentView) {
        if ( switchContentView ) {
            setContentView(R.layout.activity_robo_ctrl);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            if ( ! ci.nameOrIP.equals("127.0.0.1")) {
                EditText etIP = (EditText) findViewById(R.id.editText_addr);
                etIP.setText(ci.nameOrIP);
            }

            setSpinnerItems();

            chronometer = (Chronometer)findViewById(R.id.chronometer);

            tv = (TextView)findViewById(R.id.textView_debug);
            if ( quizMode == QUIZ_MODE_RUNNING ) startChronometer(true);
        }

        if ( connectionMode == CONNECTION_MODE_DISCONNECTED ) {
            final Button buttonConn = (Button) findViewById(R.id.button_connect);
            buttonConn.setText("Connect");
            final Button buttonStart = (Button) findViewById(R.id.button_start);
            buttonStart.setEnabled(false);
        } else {
            final Button buttonConn = (Button) findViewById(R.id.button_connect);
            buttonConn.setText("Disconnect");

            final Button buttonStart = (Button) findViewById(R.id.button_start);
            buttonStart.setEnabled(true);
        }

        if ( quizMode == QUIZ_MODE_STOPPED) {
            final Spinner spinner_level = (Spinner) findViewById(R.id.spinner_level);
            spinner_level.setEnabled(true);
            final Spinner spinner_roboctrl = (Spinner) findViewById(R.id.spinner_roboctrl);
            spinner_roboctrl.setEnabled(true);
            final Button buttonConn = (Button) findViewById(R.id.button_connect);
            buttonConn.setEnabled(true);
            final Button buttonStart = (Button) findViewById(R.id.button_start);
            buttonStart.setText("Start");
        } else {
            final Spinner spinner_level = (Spinner) findViewById(R.id.spinner_level);
            spinner_level.setEnabled(false);

            final Spinner spinner_roboctrl = (Spinner) findViewById(R.id.spinner_roboctrl);
            spinner_roboctrl.setEnabled(false);

            final Button buttonConn = (Button) findViewById(R.id.button_connect);
            buttonConn.setEnabled(false);

            final Button buttonStart = (Button) findViewById(R.id.button_start);
            buttonStart.setText("Stop");
        }

        if ((roboctrlMode == ROBOCTRL_MODE_TOUCH) && (quizMode == QUIZ_MODE_RUNNING)) {
            final ImageView imageView = (ImageView) findViewById(R.id.imageView_roboctrl_touch);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // have same code as onTouchEvent() (for the Activity) above
                    int action = event.getActionMasked();
                    int x = Math.max(Math.min(Math.round(100 * ((event.getX() /*- viewPos[0]*/) / viewSize[0])), 100), 0);
                    int y = Math.max(Math.min(Math.round(100 * ((event.getY() /*- viewPos[1]*/) / viewSize[1])), 100), 0);

                    Log.i("Touch", String.valueOf(action));
                    Log.i("x: ", String.valueOf(x));
                    Log.i("y: ", String.valueOf(y));

                    if (connectionMode == CONNECTION_MODE_CONNECTED) {
                        if (action == 2) app2roboComCtrl.sendCommand(AppCommand.COMMAND_MOVE, x, y, 1);
                        else             app2roboComCtrl.sendCommand(AppCommand.COMMAND_MOVE, 0, 0, 0);
                    }

                    return true;
                }
            });
        }


    }

    public void stopChronometer(boolean justSwitchView) {
        if ( justSwitchView ) chronometerBase  = chronometer.getBase();
        else                  chronometer.stop();
    }

    public void startChronometer(boolean justSwitchView) {
        chronometer.start();
        if ( justSwitchView ) chronometer.setBase(chronometerBase);
        else {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometerBase  = chronometer.getBase();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView_roboctrl_touch);

        imageView.getLocationOnScreen(viewPos);
        viewSize[0] = imageView.getWidth();
        viewSize[1] = imageView.getHeight();

        System.out.println("S: " + imageView.getHeight() + " / " + imageView.getWidth());
        System.out.println("P: " + viewPos[0] + " / " + viewPos[1]);
    }

    //Go back from question to RoboCtrlView
    public void onClickBack(View view) {
        System.out.println("back");

        app2roboComCtrl.reset(); //delete old moves
        app2roboComCtrl.sendCommand(AppCommand.COMMAND_ANSWER,qvm.lastAnswerCorrect?1:2,0,0);

        updateView(true);
    }

    public void onClickQuestion(View view) {
        app2roboComCtrl.reset();
        stopChronometer(true);
        qvm.nextQuestion();
    }

    public void onClickA(View view) {
        qvm.onClickAnswerButton(0);
    }
    public void onClickB(View view) {
        qvm.onClickAnswerButton(1);
    }
    public void onClickC(View view) {
        qvm.onClickAnswerButton(2);
    }
    public void onClickD(View view) {
        qvm.onClickAnswerButton(3);
    }


    //when this Activity starts
    @Override
    protected void onResume()
    {
        super.onResume();
        System.out.println("On Resume");
        /*register the sensor listener to listen to the gyroscope sensor, use the
        callbacks defined in this class, and gather the sensor information as quick
        as possible*/
        System.out.println("No Gravity: " + sensorManager.getSensorList(Sensor.TYPE_GRAVITY).size());
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_UI);
    }

  //When this Activity isn't visible anymore
    @Override
    protected void onStop()
    {
        //unregister the sensor listener
        sensorManager.unregisterListener(this);
        System.out.println("On Stop");
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) { /* Do nothing.*/ }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            //System.out.println("SensorManager.SENSOR_STATUS_UNRELIABLE");
            //return;
        }

        //else it will output the Roll, Pitch and Yawn values
        //tv.setText("Orientation X (Roll) :" + Float.toString(event.values[2]) + "\n" +
        //           "Orientation Y (Pitch) :" + Float.toString(event.values[1]) + "\n" +
        //           "Orientation Z (Yaw) :"+ Float.toString(event.values[0]));

        if ((roboctrlMode == ROBOCTRL_MODE_GYRO) && (quizMode == QUIZ_MODE_RUNNING)) {
            //y : rechts -  links +
            // z: vorn + hinten -
            int x = (int) Math.max(Math.min(50 + Math.round(-12.5 * event.values[1]), 100), 0);
            int y = (int) Math.max(Math.min(50 + Math.round(-12.5 * event.values[0]), 100), 0);

            //else it will output the Roll, Pitch and Yawn values
            tv.setText("Orientation X : " + Integer.toString(x) + "\n" +
                       "Orientation Y : " + Integer.toString(y));

            app2roboComCtrl.sendCommand(AppCommand.COMMAND_MOVE, x, y, 1);
        }

    }
}
