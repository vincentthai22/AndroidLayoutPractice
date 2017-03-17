package com.example.vincent.babynursinglayouts;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 3/16/17.
 */

public class NewPumpingEntryActivity extends AppCompatActivity {


    private ListView newPumpingListView;
    private List<List<String>> pumpingListViewData;  //2-D list to organize headers, cell titles, and user input
    private PumpingArrayAdapter adapter;
    private int currentPosition; //store current position to determine which alertDialog will be called
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private String[] leftNumberPickerValues, rightNumberPickerValues, milliliterNumberPickerValues;
    private boolean isOunces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_new_pumping);

        newPumpingListView = (ListView) findViewById(R.id.newPumpingListView);
        pumpingListViewData = setupList();
        setupListViewListeners();
        setupAlertDialogs();
        adapter = new PumpingArrayAdapter(this, R.layout.list_view_new_pumping_entry_header_cell, pumpingListViewData);
        newPumpingListView.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }

    /**
     * @param  NONE
     *  setupList()
     *  Initializes list to place into the adapter to be displayed by the listView.
     *  List is organized in this manner as a 2-D list:
     *
     *              index[i][0]     index[i][1]
     *  index[0][j] Amount Pumped   someValue
     *  index[1][j] Left Breast     someValue
     *  ----------------------------------------
     *
     *  As shown above every first index of the 2-D List serves as a cell title and every second index
     *  holds the user input which is referred to as "someValue".
     *  This page is designed to have three headers and three cells per header giving us a total of
     *  twelve rows within this listView.  Every fourth row starting from zero is a section header.
     *
     */
    public List<List<String>> setupList(){
        List<List<String>> tempList = new ArrayList<>(); //Need list to fill in ListView
        AssetManager assetManager = getAssets();
        try{
            InputStream questionsAndOptionListStream = assetManager.open("newPumpingHeadersAndTitles.txt");  //retrieve list items from text file
            BufferedReader in = new BufferedReader(new InputStreamReader(questionsAndOptionListStream));
            String line="";
            while((line = in.readLine()) != null) {
                List<String> temp = new ArrayList<>();
                temp.add(line);
                temp.add(1,"");
                tempList.add(temp);
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_SHORT);
            toast.show();
        }
        Log.d("size of temp", tempList.size()+"");
        return tempList;
    }

    /**
     * @param NONE
     * setupListViewListeners()
     * sets up the onClickListener for the listView
     * Each varying section results in a different AlertDialog to appear.
     * Amount Pumped Section -- presents a number picker allowing the user to choose the amount pumped in ounces or milliliters
     * Date & Time -- presents a DatePicker and TimePicker
     * Additional Info -- TBD
     */
    public void setupListViewListeners(){


        newPumpingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.show();
                currentPosition = position;
            }
        });
    }
    
    public void setupAlertDialogs(){

        //--------------------------------------------------SET UP VIEWS--------------------------------------------------//
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_custom_view_new_pumping, null);

        final NumberPicker leftNP = (NumberPicker) view.findViewById(R.id.leftNumberPicker);
        final NumberPicker rightNP = (NumberPicker) view.findViewById(R.id.rightNumberPicker);
        final NumberPicker milliliterNP = new NumberPicker(this);

        TextView doneButton = (TextView) view.findViewById(R.id.doneTextView);
        TextView cancelButton = (TextView) view.findViewById(R.id.cancelTextView);
        final LinearLayout numberPickerLinearLayout = (LinearLayout) view.findViewById(R.id.numberPickerLinearLayout);
        Button ouncesButton = (Button) view.findViewById(R.id.ouncesButton);
        Button milliliterButton = (Button) view.findViewById(R.id.milliliterButton);

        isOunces = true;

        //----------------------------------------ON CLICK LISTENERS--------------------------------------------------//

        ouncesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPickerLinearLayout.removeAllViews();
                numberPickerLinearLayout.addView(leftNP);
                numberPickerLinearLayout.addView(rightNP);
                isOunces = true;
            }
        });
        milliliterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPickerLinearLayout.removeAllViews();
                numberPickerLinearLayout.addView(milliliterNP);
                isOunces = false;

            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result;

                if (isOunces)
                    result = leftNP.getDisplayedValues()[leftNP.getValue()] + rightNP.getDisplayedValues()[rightNP.getValue()];
                else
                    result = milliliterNP.getDisplayedValues()[milliliterNP.getValue()];

                pumpingListViewData.get(currentPosition).add(1,result);
                newPumpingListView.setAdapter(adapter); //reset adapter to update listview
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //-------------------------------------------END OF ON CLICK LISTENERS----------------------------------------//

        //number picker initializers
        setNumberPickerTextColor(leftNP, Color.BLACK);
        setNumberPickerTextColor(rightNP,Color.BLACK);
        setNumberPickerTextColor(milliliterNP, Color.BLACK);

        milliliterNumberPickerValues = new String[3000];
        leftNumberPickerValues = new String[3000];
        rightNumberPickerValues = new String[10];

        leftNP.setMinValue(0);
        leftNP.setMaxValue(leftNumberPickerValues.length-1);
        rightNP.setMinValue(0);
        rightNP.setMaxValue(rightNumberPickerValues.length-1);
        milliliterNP.setMinValue(0);
        milliliterNP.setMaxValue(milliliterNumberPickerValues.length-1);

        for(int i = 0 ; i < 3000; i++) {
            leftNumberPickerValues[i] = i + ".";
            milliliterNumberPickerValues[i] = i+" ml";
        }
        for(int i = 0 ; i < 10 ; i++)
            rightNumberPickerValues[i] = i + " oz";

        leftNP.setDisplayedValues(leftNumberPickerValues);
        rightNP.setDisplayedValues(rightNumberPickerValues);
        milliliterNP.setDisplayedValues(milliliterNumberPickerValues);

        //set alert dialog to custom view
        builder.setView(view);
        dialog = builder.create();
    }

    public boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.d("setNumberPickerTextColo", e+"");
                }
                catch(IllegalAccessException e){
                    Log.d("setNumberPickerTextColo", e+"");
                }
                catch(IllegalArgumentException e){
                    Log.d("setNumberPickerTextColo", e+"");
                }
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            return true;
        } else if (id == android.R.id.home){
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
        menu.findItem(R.id.action_add_pumping).setVisible(false);


        return true;
    }
}
