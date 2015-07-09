package au.com.zacher.popularmovies.activity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import au.com.zacher.popularmovies.ActivityInitialiser;
import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;
import au.com.zacher.popularmovies.adapter.DisplayItemGridAdapter;


public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.setContext(getApplicationContext());
        Logger.logActionCreate("MainActivity");
        super.onCreate(savedInstanceState);


        // setup the toolbar and contentView
        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = false;
        ActivityInitialiser.initActivity(options, savedInstanceState, this, R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView)this.findViewById(R.id.movie_grid);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.logMethodCall("onOptionsItemSelected(MenuItem)", "MainActivity");

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
    public boolean onMenuItemClick(MenuItem item) {
        return this.onOptionsItemSelected(item);
    }
}
