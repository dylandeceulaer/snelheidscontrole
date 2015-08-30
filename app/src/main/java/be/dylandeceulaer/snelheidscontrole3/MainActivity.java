package be.dylandeceulaer.snelheidscontrole3;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import be.dylandeceulaer.loader.snelheidscontrolesLoader;


public class MainActivity extends AppCompatActivity implements DrawerFragment.onItemPosListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isTablet(this)){
            if(savedInstanceState == null){
                getFragmentManager().beginTransaction().add(R.id.container,new googleMapFragment(),"map").commit();
            }
        }




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Controles");

        Spinner spinner = (Spinner) findViewById(R.id.spinnerFilter);
        spinner.setAdapter(new ArrayAdapter<snelheidscontrolesLoader.MAAND>(this,android.R.layout.simple_list_item_1, snelheidscontrolesLoader.MAAND.values()));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((DrawerFragment) getFragmentManager().findFragmentById(R.id.drawer_fragment)).FilterByMonth((snelheidscontrolesLoader.MAAND)parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemPosClick(float x, float y, String titel, String sub) {
        ((googleMapFragment) getFragmentManager().findFragmentByTag("map")).PanMap(x, y,titel,sub);
    }
}
