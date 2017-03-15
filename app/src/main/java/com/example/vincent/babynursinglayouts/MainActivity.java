package com.example.vincent.babynursinglayouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.example.vincent.babynursinglayouts.models.PumpingEntry;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView pumpingListView = (ListView) findViewById(R.id.pumpingListView);

        List<PumpingEntry> someList = new ArrayList<>();
        someList.add(new PumpingEntry(2, 0.3f, 0.6f));
        someList.add(new PumpingEntry(2, 0.3f, 0.6f));
        View header = (View)getLayoutInflater().inflate(R.layout.list_view_cell_pumping, null);
        pumpingListView.addHeaderView(header);
        PumpingArrayAdapter adapter = new PumpingArrayAdapter(this, R.layout.list_view_header_cell_pumping, someList);
        pumpingListView.setAdapter(adapter);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        ExpandableListAdapter eAdapter = new PumpingExpandableListAdapter(this, someList);
        expandableListView.setAdapter(eAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_pumping) {
            return true;
        } else if (id == android.R.id.home){
            Intent intent = getIntent();
            setResult(RESULT_OK,intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        return true;
    }
}
