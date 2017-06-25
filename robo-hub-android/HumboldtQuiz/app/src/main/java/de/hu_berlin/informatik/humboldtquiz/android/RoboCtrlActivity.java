package de.hu_berlin.informatik.humboldtquiz.android;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import de.hu_berlin.informatik.app.connection.protocol.EV3Command;
import de.hu_berlin.informatik.app.connection.ConnectionInfo;
import de.hu_berlin.informatik.app.connection.SocketConnection;
import de.hu_berlin.informatik.humboldtquiz.R;

public class RoboCtrlActivity extends AppCompatActivity implements SensorEventListener {

    public static final int QUIZ_MODE_STOPPED = 0;
    public static final int QUIZ_MODE_RUNNING = 1;

    public static final int CONNECTION_MODE_DISCONNECTED = 0;
    public static final int CONNECTION_MODE_CONNECTED = 1;

    public static final int ROBOCTRL_MODE_GYRO = 0;
    public static final int ROBOCTRL_MODE_GYRO2 = 1;
    public static final int ROBOCTRL_MODE_TOUCH = 2;
    public static final int ROBOCTRL_MODE_LINE = 3;
    public static final int ROBOCTRL_MODE_DISTANCE = 4;

    public static final int APP_MODE_GAME = 1;
    public static final int APP_MODE_QUIZ = 2;
    public static final int APP_MODE_CONNECTION_CONFIG = 3;
    public static final int APP_MODE_ROBOCTRL_CONFIG = 4;
    public static final int APP_MODE_ABOUT = 5;

    int current_app_mode = APP_MODE_GAME;
    int current_app_view = R.id.activity_robo_ctrl;
    int current_app_layout = R.layout.activity_robo_ctrl;

    QuizViewManager qvm = null;

    int quizMode = QUIZ_MODE_STOPPED;
    int connectionMode = CONNECTION_MODE_DISCONNECTED;
    int roboctrlMode = ROBOCTRL_MODE_TOUCH;

    SocketConnection sc = null;
    ConnectionInfo ci = null;

    AppToRoboCommunicationControl app2roboComCtrl = null;
    AsyncTask commTask = null;

    Chronometer chronometer;
    boolean chronometer_running = false;
    long chronometerBase = 0;

    final int[] viewSize = new int[2];
    final int[] viewPos = new int[2];

    //the Sensor Manager
    private SensorManager sensorManager;
    private Sensor sensorGravity;
    private Sensor sensorCompass;
    private Sensor sensorAccel;

    float[] mGravity;
    float[] mGeomagnetic;
    float nullAzimut = -10;
    float nullPitch = -10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( qvm == null ) qvm = new QuizViewManager(this);

        ci = new ConnectionInfo("192.168.0.184",2000);//192.168.0.174", 2000); //Dev -> Robo
        sc = new SocketConnection(ci);
        app2roboComCtrl = new AppToRoboCommunicationControl(sc,this);

        setContentView(R.layout.activity_main);
        current_app_mode = APP_MODE_GAME;

        updateView();

        chronometer = (Chronometer)findViewById(R.id.chronometer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get a hook to the sensor service
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorCompass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void setConnectionMode(int newConnectionMode) {
        final Button buttonConn = (Button) findViewById(R.id.button_connect);
        ImageView img = (ImageView) findViewById(R.id.imageView);
        connectionMode = newConnectionMode;

        if ( connectionMode == CONNECTION_MODE_CONNECTED ) {
            buttonConn.setText("Disconnect");
            img.setImageResource(R.drawable.wifi_robo_conn_on);
        } else {
            buttonConn.setText("Connect");
            img.setImageResource(R.drawable.wifi_robo_conn_off);
        }
    }

    private void setIPToForm() {
        EditText etIP = (EditText)findViewById(R.id.conn_setup_ip);
        EditText etPort = (EditText)findViewById(R.id.conn_setup_port);

        String ipAddr = ci.getNameOrIP().toString();
        etIP.setText(ipAddr);

        etPort.setText("" + ci.getPort());
    }

    private void saveIPFromForm() {
        EditText etIP = (EditText)findViewById(R.id.conn_setup_ip);
        EditText etPort = (EditText)findViewById(R.id.conn_setup_port);
        String ipAddr = etIP.getText().toString();
        int port = Integer.parseInt(etPort.getText().toString());

        if ( !ipAddr.equals("127.0.0.1")) ci.setNameOrIP(ipAddr);
        ci.setPort(port);
    }

    public void onClickConnect(View view) {
        if ( connectionMode == CONNECTION_MODE_DISCONNECTED ) {
            System.out.println("Connect");

            saveIPFromForm();

            commTask = new AppToRoboCommunicationTask().execute(app2roboComCtrl);

            setConnectionMode(CONNECTION_MODE_CONNECTED);
        } else {
            System.out.println("Disconnect");

            app2roboComCtrl.sendCommand(EV3Command.COMMAND_DISCONNECT);
            app2roboComCtrl.closeConnection();

            setConnectionMode(CONNECTION_MODE_DISCONNECTED);
        }
    }

    public void onClickStart(View view) {
        if ( quizMode == QUIZ_MODE_STOPPED) {

            assert(connectionMode == CONNECTION_MODE_CONNECTED );

            System.out.println("Mode: " + roboctrlMode);

            if (roboctrlMode >= ROBOCTRL_MODE_GYRO2)
                app2roboComCtrl.sendCommand(EV3Command.COMMAND_START, roboctrlMode - 1, 0, 0);
            else
                app2roboComCtrl.sendCommand(EV3Command.COMMAND_START, roboctrlMode, 0, 0);

            quizMode = QUIZ_MODE_RUNNING;

            startChronometer();

            TextView tvInfo = (TextView)findViewById(R.id.editText_result);;
            tvInfo.setText("");

            nullAzimut = -10;
            nullPitch = -10;
        } else {
            app2roboComCtrl.sendCommand(EV3Command.COMMAND_STOP);
            stopQuiz(true);
        }

        updateView();
    }

    public void stopQuiz(boolean stoppedByUser) {
        quizMode = QUIZ_MODE_STOPPED;

        stopChronometer();

        app2roboComCtrl.reset();

        qvm.resetQuestions();

        if (!stoppedByUser) {
            updateView();

            chronometer = (Chronometer)findViewById(R.id.chronometer);

            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            long minutes = (elapsedMillis/1000)/60;
            long sec = (elapsedMillis/1000)%60;

            String timeString = "" + minutes + " Minute" + ((minutes==1)?" und ":"n und ") + sec + " Sekunde" + ((sec==1)?"":"n");

            TextView tvInfo = (TextView)findViewById(R.id.editText_result);;
            tvInfo.setText("Herzlichen Glückwunsch!\nDu hast die Reise in " + timeString + " geschafft!");
        }
    }

    public void getSpinnerItems() {
        Spinner spinner_level = (Spinner) findViewById(R.id.spinner_level);
        Spinner spinner_roboctrl = (Spinner) findViewById(R.id.spinner_roboctrl);

        // Apply the adapter to the spinner
        qvm.setLevel(spinner_level.getSelectedItemPosition());
        roboctrlMode = spinner_roboctrl.getSelectedItemPosition();
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
        if ( qvm.getLevel() >= 0 ) spinner_level.setSelection(qvm.getLevel());

        spinner_roboctrl.setAdapter(adapter_roboctrl);
        spinner_roboctrl.setSelection(roboctrlMode);
    }

    public void updateView() {

        if ( current_app_mode == APP_MODE_GAME ) {

            final Button buttonStart = (Button) findViewById(R.id.button_start);

            if (quizMode == QUIZ_MODE_STOPPED) {
                buttonStart.setText("Start");
            } else {
                buttonStart.setText("Stop");
            }

            if (connectionMode == CONNECTION_MODE_DISCONNECTED) {
                buttonStart.setEnabled(false);
            } else {
                buttonStart.setEnabled(true);
            }

            setTouchPanel((roboctrlMode == ROBOCTRL_MODE_TOUCH) && (quizMode == QUIZ_MODE_RUNNING));

        }
    }

    private void setTouchPanel(boolean setListener) {
        final ImageView imageView = (ImageView) findViewById(R.id.imageView_roboctrl_touch);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        System.out.println("H: " + height + " W: " + width);
        imageView.getLayoutParams().height = (width-50);
        imageView.getLayoutParams().width = (width-50);


        if ( setListener ) {
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // have same code as onTouchEvent() (for the Activity) above
                    int action = event.getActionMasked();
                    int x = Math.max(Math.min(Math.round(100 * ((event.getX() /*- viewPos[0]*/) / viewSize[0])), 100), 0);
                    int y = Math.max(Math.min(Math.round(100 * ((event.getY() /*- viewPos[1]*/) / viewSize[1])), 100), 0);

                    Log.i("Touch: ", "Action " + String.valueOf(action));
                    Log.i("Touch: ", "x = " + String.valueOf(x));
                    Log.i("Touch: ", "y = " + String.valueOf(y));

                    if (connectionMode == CONNECTION_MODE_CONNECTED) {
                        if (action == 2)
                            app2roboComCtrl.sendCommand(EV3Command.COMMAND_MOVE, x, y, 1);
                        else
                            app2roboComCtrl.sendCommand(EV3Command.COMMAND_MOVE, 0, 0, 0);
                    }

                    return true;
                }
            });
        }
    }

    public void startChronometer() {
        System.out.println("Start Chronometer");
        if (chronometer_running) {
            System.out.println("Chronometer startet twice");
            //chronometer.setBase(chronometerBase);
        } else {
            chronometer = (Chronometer)findViewById(R.id.chronometer);
            chronometer.start();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer_running = true;
        }

        //disable button
    }

    public void continueChronometer() {
        System.out.println("Cont Chronometer");
        if (chronometer_running) {
            chronometer = (Chronometer)findViewById(R.id.chronometer);
            chronometer.start();
            chronometer.setBase(chronometerBase);
        }
    }

    public void breakChronometer() {
        System.out.println("Break Chronometer");
        if (chronometer_running) {
            chronometer = (Chronometer)findViewById(R.id.chronometer);
            chronometer.stop();
            chronometerBase  = chronometer.getBase();
        }
    }

    public void stopChronometer() {
        System.out.println("Stop Chronometer");
        if (chronometer_running) {
            chronometer = (Chronometer)findViewById(R.id.chronometer);
            chronometer.stop();
            chronometer_running = false;
        }
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView_roboctrl_touch);

        if ( imageView != null ) {
            imageView.getLocationOnScreen(viewPos);
            viewSize[0] = imageView.getWidth();
            viewSize[1] = imageView.getHeight();

            Log.i("ImageView", "S: " + imageView.getHeight() + " / " + imageView.getWidth());
            Log.i("ImageView", "P: " + viewPos[0] + " / " + viewPos[1]);
        }
    }

    //Go back from question to RoboCtrlView
    public void onClickBack(View view) {
        Log.i("ClickAction", "back");

        app2roboComCtrl.reset(); //delete old moves
        app2roboComCtrl.sendCommand(EV3Command.COMMAND_ANSWER,qvm.lastAnswerCorrect?1:2,0,0);

        switchToView(R.id.activity_robo_ctrl, R.layout.activity_robo_ctrl, APP_MODE_GAME);

        continueChronometer();

        updateView();
    }

    public void onClickQuestion(View view) {
        nextQuestion();
    }

    public void nextQuestion() {
        app2roboComCtrl.reset();
        breakChronometer();
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
        Log.i("GUI", "onResume");
        /*register the sensor listener to listen to the gyroscope sensor, use the
        callbacks defined in this class, and gather the sensor information as quick
        as possible*/
        Log.i("Sensor:", "No Gravity: " + sensorManager.getSensorList(Sensor.TYPE_GRAVITY).size());
        Log.i("Sensor:", "No Compass: " + sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size());
        Log.i("Sensor:", "No Acc: " + sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size());

        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorCompass, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_UI);
    }

  //When this Activity isn't visible anymore
    @Override
    protected void onStop()
    {
        Log.i("GUI", "On Stop");
        //unregister the sensor listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) { /* Do nothing.*/ }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (quizMode == QUIZ_MODE_RUNNING) {
            //if sensor is unreliable, return void

            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                //System.out.println("SensorManager.SENSOR_STATUS_UNRELIABLE");
                //return;
            }

            switch (roboctrlMode) {
                case ROBOCTRL_MODE_GYRO:
                    //System.out.println("SenChanged: " + event.sensor.getType());
                    if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                        //y : rechts -  links +
                        // z: vorn + hinten -
                        int x = (int) Math.max(Math.min(50 + Math.round(-12.5 * event.values[1]), 100), 0);
                        int y = (int) Math.max(Math.min(50 + Math.round(-12.5 * event.values[0]), 100), 0);

                        app2roboComCtrl.sendCommand(EV3Command.COMMAND_MOVE, x, y, 1);
                    }
                    break;
            }

            if (roboctrlMode == ROBOCTRL_MODE_GYRO2) {

                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    mGeomagnetic = event.values;

                    if (mGravity != null && mGeomagnetic != null) {
                        float R[] = new float[9];
                        float I[] = new float[9];
                        boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                        if (success) {
                            float orientation[] = new float[3];
                            SensorManager.getOrientation(R, orientation);
                            float azimut = orientation[0]; // orientation contains: azimut: Compass
                            float pitch = orientation[1];  // gas
                            //float roll = orientation[2];   // rotation

                            if (nullAzimut == -10) {
                                nullAzimut = azimut;
                                nullPitch = pitch;
                            } else {
                                float diffAzimut = nullAzimut - azimut;
                                if (diffAzimut > Math.PI) diffAzimut -= Math.PI;
                                if (diffAzimut < -Math.PI) diffAzimut += 2*Math.PI;

                                float diffPitch = nullPitch - pitch;

                                int x = (int) Math.max(Math.min(50 + Math.round(-100 * diffAzimut), 100), 0);
                                int y = (int) Math.max(Math.min(50 + Math.round(100 * diffPitch), 100), 0);

                                app2roboComCtrl.sendCommand(EV3Command.COMMAND_MOVE, x, y, 1);
                                //System.out.println("Ori: " + azimut + " , " + nullAzimut + " , " + diffAzimut);
                                //System.out.println("X: " + x + " Y:" + y);
                            }

                        }
                    }
                }

                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) mGravity = event.values;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    public void switchToView(int new_view_id, int new_layout_id, int new_app_mode) {
        View C = findViewById(current_app_view);
        assert(C != null);

        current_app_view = new_view_id;
        current_app_layout = new_layout_id;
        current_app_mode = new_app_mode;

        //ViewGroup parent = (ViewGroup)C.getParent();
        ViewGroup parent = (ViewGroup)findViewById(R.id.activity_main);
        int index = parent.indexOfChild(C);
        parent.removeView(C);

        C = getLayoutInflater().inflate(new_layout_id, parent, false);

        parent.addView(C, index);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //save everything of current view
        switch (current_app_mode) {
            case APP_MODE_GAME:
                break;
            case APP_MODE_ROBOCTRL_CONFIG:
                getSpinnerItems();
                break;
            case APP_MODE_CONNECTION_CONFIG:
                saveIPFromForm();
                break;
            case APP_MODE_ABOUT:
                break;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int new_view_id = R.id.activity_robo_ctrl;
        int new_layout_id = R.layout.activity_robo_ctrl;
        int new_app_mode = APP_MODE_GAME;

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_control:
            case R.id.action_quit:
                new_app_mode = APP_MODE_GAME;
                new_view_id = R.id.activity_robo_ctrl;
                new_layout_id = R.layout.activity_robo_ctrl;
                break;
            case R.id.action_connection:
                new_app_mode = APP_MODE_CONNECTION_CONFIG;
                new_view_id = R.id.connection_setup_main;
                new_layout_id = R.layout.connection_setup_main;
                break;
            case R.id.action_settings:
                new_app_mode = APP_MODE_ROBOCTRL_CONFIG;
                new_view_id = R.id.activity_robo_ctrl_config;
                new_layout_id = R.layout.activity_robo_ctrl_config;
                break;
            case R.id.action_about:
                new_app_mode = APP_MODE_ABOUT;
                new_view_id = R.id.activity_about;
                new_layout_id = R.layout.activity_about;
                break;
        }

        switchToView(new_view_id, new_layout_id, new_app_mode);

        // post view/layout switch
        switch (id) {
            case R.id.action_control:
                updateView();
                break;
            case R.id.action_connection:
                setConnectionMode(connectionMode); //just to set buttons and images
                setIPToForm();
                break;
            case R.id.action_settings:
                setSpinnerItems();
                break;
            case R.id.action_about:
                break;
            case R.id.action_quit:
                app2roboComCtrl.sendCommand(EV3Command.COMMAND_DISCONNECT);
                app2roboComCtrl.closeConnection();
                closeApp();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void closeApp() {
        //android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}
