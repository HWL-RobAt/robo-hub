package de.hu_berlin.informatik.roboshadowfunction;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_connection) {

            View C = findViewById(R.id.content_main);
            if ( C != null ) {
              ViewGroup parent = (ViewGroup) C.getParent();
              int index = parent.indexOfChild(C);
              parent.removeView(C);
              C = getLayoutInflater().inflate(R.layout.connection_setup_main, parent, false);
              parent.addView(C, index);
            }

            ImageView img = (ImageView)findViewById(R.id.imageView);
            img.setImageResource(R.drawable.wifi_robo_conn_on);
            return true;
        }

        if (id == R.id.action_settings) {

            View C = findViewById(R.id.connection_setup_main);
            if ( C != null ) {
                ViewGroup parent = (ViewGroup) C.getParent();
                int index = parent.indexOfChild(C);
                parent.removeView(C);
                C = getLayoutInflater().inflate(R.layout.content_main, parent, false);
                parent.addView(C, index);
            }

            ImageView img = (ImageView)findViewById(R.id.imageView);
            img.setImageResource(R.drawable.wifi_robo_conn_on);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
