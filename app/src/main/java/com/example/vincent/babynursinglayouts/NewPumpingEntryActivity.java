package com.example.vincent.babynursinglayouts;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by vincent on 3/16/17.
 */

public class NewPumpingEntryActivity extends AppCompatActivity {

    private ListView newPumpingListView;
    private List<List<String>> pumpingListViewData;  //2-D list to organize headers, cell titles, and user input
    private AlertDialog.Builder builder;
    private View amountPumpedView, dateAndTimeView, additionalInfoView;

    private PumpingArrayAdapter adapter;

    private AlertDialog amountPumpedDialog, dateAndTimeDialogue;
    private String[] leftNumberPickerValues, rightNumberPickerValues, milliliterNumberPickerValues;
    private boolean isOunces;
    private int currentPosition,
            amountPumpedSection, dateAndTimeSection, additionalInfoSection, //store current position to determine which alertDialog will be called
            currentSelectedYear, currentSelectedMonth, days, minutes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_new_pumping);

        amountPumpedSection = 0;
        dateAndTimeSection = 4;
        additionalInfoSection = 8;
        newPumpingListView = (ListView) findViewById(R.id.newPumpingListView);
        pumpingListViewData = setupList();
        setupListViewListeners();
        setupAlertDialogs();
        adapter = new PumpingArrayAdapter(this, R.layout.list_view_new_pumping_entry_header_cell, pumpingListViewData);
        newPumpingListView.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }

    /**
     * @param NONE setupList()
     *             Initializes list to place into the adapter to be displayed by the listView.
     *             List is organized in this manner as a 2-D list:
     *             <p>
     *             index[i][0]     index[i][1]
     *             index[0][j] Amount Pumped   someValue
     *             index[1][j] Left Breast     someValue
     *             ----------------------------------------
     *             <p>
     *             As shown above every first index of the 2-D List serves as a cell title and every second index
     *             holds the user input which is referred to as "someValue".
     *             This page is designed to have three headers and three cells per header giving us a total of
     *             twelve rows within this listView.  Every fourth row starting from zero is a section header.
     *             index 0 = Amount Pumped section header.
     *             index 4 = Date & Time section header.
     *             index 8 = Additional Information section header.
     */
    public List<List<String>> setupList() {
        List<List<String>> tempList = new ArrayList<>(); //Need list to fill in ListView
        AssetManager assetManager = getAssets();
        try {
            InputStream questionsAndOptionListStream = assetManager.open("newPumpingHeadersAndTitles.txt");  //retrieve list items from text file
            BufferedReader in = new BufferedReader(new InputStreamReader(questionsAndOptionListStream));
            String line = "";
            while ((line = in.readLine()) != null) {
                List<String> temp = new ArrayList<>();
                temp.add(line);
                temp.add(1, "");
                tempList.add(temp);
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_SHORT);
            toast.show();
        }

        //default values
        tempList.get(dateAndTimeSection+1).set(1, new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(new Date()));
        tempList.get(dateAndTimeSection+2).set(1, new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(new Date()));
        return tempList;
    }


    /**
     * @param NONE setupListViewListeners()
     *             sets up the onClickListener for the listView
     *             Each varying section results in a different AlertDialog to appear.
     *             Amount Pumped Section -- presents a number picker allowing the user to choose the amount pumped in ounces or milliliters
     *             Date & Time -- presents a DatePicker and TimePicker
     *             Additional Info -- TBD
     */
    public void setupListViewListeners() {


        newPumpingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < dateAndTimeSection) {
                    amountPumpedDialog.show();
                    setDialogueWindow(amountPumpedDialog);
                } else if (position < additionalInfoSection) {
                    dateAndTimeDialogue.show();
                    setDialogueWindow(dateAndTimeDialogue);
                } else {
                    builder.setView(amountPumpedView);
                }
                    currentPosition = position;
                }
            });
        }


    public void setupAlertDialogs() {

        builder = new AlertDialog.Builder(this);
        amountPumpedView = setupAlertDialogForAmountPumpedSection();
        dateAndTimeView = setupAlertDialogForDateAndTimeSection();
        //set alert amountPumpedDialog to custom view
        builder.setView(amountPumpedView);
        amountPumpedDialog = builder.create();
        builder.setView(dateAndTimeView);
        dateAndTimeDialogue = builder.create();

    }

    public void setDialogueWindow(AlertDialog dialog) {
        LinearLayout parentView = (LinearLayout) findViewById(R.id.parentLinearLayout);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
        windowLayoutParams.gravity = Gravity.BOTTOM;
        windowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        windowLayoutParams.width = parentView.getWidth();
        window.setAttributes(windowLayoutParams);
    }

    public View setupAlertDialogForDateAndTimeSection() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_date_time_view_new_pumping, null);
        final Button timeButton = (Button) view.findViewById(R.id.timeButton);
        final Button dateButton = (Button) view.findViewById(R.id.dateButton);
        final LinearLayout dateAndTimeLinearLayout = (LinearLayout) view.findViewById(R.id.dateAndTimePickerLinearLayout);
        TextView cancelTextView = (TextView) view.findViewById(R.id.cancelTextView);
        TextView doneTextView = (TextView) view.findViewById(R.id.doneTextView);
        String[] monthsList = new DateFormatSymbols().getMonths();
        String[] amPmList = new DateFormatSymbols().getAmPmStrings();

        final NumberPicker leftNumberPicker, middleNumberPicker, rightNumberPicker, monthsNumberPicker, amPmNumberPicker;

        leftNumberPicker = (NumberPicker) view.findViewById(R.id.leftNumberPicker);
        middleNumberPicker = (NumberPicker) view.findViewById(R.id.middleNumberPicker);
        rightNumberPicker = (NumberPicker) view.findViewById(R.id.rightNumberPicker);
        monthsNumberPicker = new NumberPicker(this);
        amPmNumberPicker = new NumberPicker(this);

        monthsNumberPicker.setDisplayedValues(monthsList);
        amPmNumberPicker.setDisplayedValues(amPmList);

        //Set number pickers by default to start on the Date Section
        final int currentYear, currentMonth;
        final Date now = new Date();
        currentSelectedMonth = currentMonth = Integer.parseInt(new SimpleDateFormat("MM").format(now));
        currentSelectedYear = currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(now));
        minutes = days = 0;

        //hours
        leftNumberPicker.setMinValue(0);
        leftNumberPicker.setMaxValue(12);
        leftNumberPicker.setValue(Integer.parseInt(new SimpleDateFormat("hh").format(now)));

        //minutes & days
        middleNumberPicker.setMinValue(1);
        middleNumberPicker.setMaxValue(getDaysInMonth(currentMonth, currentYear));
        middleNumberPicker.setValue(Integer.parseInt(new SimpleDateFormat("dd").format(now)));

        //year
        rightNumberPicker.setMinValue(currentYear - 1);
        rightNumberPicker.setMaxValue(currentYear);
        rightNumberPicker.setValue(currentYear);

        //month
        monthsNumberPicker.setMinValue(0);
        monthsNumberPicker.setMaxValue(monthsList.length - 1);
        monthsNumberPicker.setValue(currentMonth-1);

        //am/pm
        amPmNumberPicker.setMinValue(0);
        amPmNumberPicker.setMaxValue(amPmList.length - 1);
        if(new SimpleDateFormat("a").format(new Date()).toUpperCase().trim().equals("AM"))
            amPmNumberPicker.setValue(0);
        else
            amPmNumberPicker.setValue(1);

        dateAndTimeLinearLayout.removeViewAt(0);
        dateAndTimeLinearLayout.addView(monthsNumberPicker, 0);

        setNumberPickerTextColor(leftNumberPicker, Color.BLACK);
        setNumberPickerTextColor(middleNumberPicker, Color.BLACK);
        setNumberPickerTextColor(rightNumberPicker, Color.BLACK);
        setNumberPickerTextColor(monthsNumberPicker,Color.BLACK);
        setNumberPickerTextColor(amPmNumberPicker, Color.BLACK);

        monthsNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentSelectedMonth = newVal;
                middleNumberPicker.setMaxValue(getDaysInMonth(newVal, currentSelectedYear));
            }
        });

        rightNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentSelectedYear = newVal;
                middleNumberPicker.setMaxValue(getDaysInMonth(currentSelectedMonth, newVal));
            }
        });

        middleNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateAndTimeLinearLayout.removeViewAt(0);
                dateAndTimeLinearLayout.addView(monthsNumberPicker, 0);
                dateAndTimeLinearLayout.removeViewAt(2);
                dateAndTimeLinearLayout.addView(rightNumberPicker);
                monthsNumberPicker.setValue(currentMonth-1);
                dateButton.setBackgroundResource(R.drawable.blue_in_white_out_button_border);
                dateButton.setTextColor(Color.WHITE);
                timeButton.setBackgroundResource(R.drawable.white_in_blue_out_button_border);
                timeButton.setTextColor(Color.BLUE);
                minutes = middleNumberPicker.getValue();
                middleNumberPicker.setMinValue(1);
                middleNumberPicker.setMaxValue(getDaysInMonth(currentMonth,currentYear));
                if (days == 0)
                    middleNumberPicker.setValue(Integer.parseInt(new SimpleDateFormat("dd").format(now)));
                else
                    middleNumberPicker.setValue(days);

            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateAndTimeLinearLayout.removeViewAt(0);
                dateAndTimeLinearLayout.addView(leftNumberPicker, 0);
                dateAndTimeLinearLayout.removeViewAt(2);
                dateAndTimeLinearLayout.addView(amPmNumberPicker);
                timeButton.setBackgroundResource(R.drawable.blue_in_white_out_button_border);
                timeButton.setTextColor(Color.WHITE);
                dateButton.setBackgroundResource(R.drawable.white_in_blue_out_button_border);
                dateButton.setTextColor(Color.BLUE);
                Log.d("ampm", new SimpleDateFormat("a").format(new Date()));
                if(new SimpleDateFormat("a").format(new Date()).toUpperCase().trim().equals("AM"))
                    amPmNumberPicker.setValue(0);
                else
                    amPmNumberPicker.setValue(1);
                days = middleNumberPicker.getValue();
                middleNumberPicker.setMinValue(0);
                middleNumberPicker.setMaxValue(60);
                if (minutes == 0)
                    middleNumberPicker.setValue(Integer.parseInt(new SimpleDateFormat("mm").format(now)));
                else
                    middleNumberPicker.setValue(minutes);

            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateAndTimeDialogue.dismiss();
                minutes = days = 0;
            }
        });

        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateString;
                dateString = monthsNumberPicker.getValue()+1 + "/" + days + "/" + rightNumberPicker.getValue() +
                        " " + leftNumberPicker.getValue() + ":" + minutes +
                        " " + amPmNumberPicker.getDisplayedValues()[amPmNumberPicker.getValue()];
                pumpingListViewData.get(currentPosition).set(1, dateString);
                dateAndTimeDialogue.dismiss();
                newPumpingListView.setAdapter(adapter);
                minutes = days = 0;
            }
        });


        return view;

    }

    public int getDaysInMonth(int month, int year) {
        Calendar myCalendar = new GregorianCalendar(year, month, 1);
        return myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param NONE setupAlertDialogsForAmountPumpedSection()
     *             <p>
     *             sets up the entirety of the alertDialogs which includes:
     *             custom views
     *             instantiation of all number pickers
     *             customization of all number pickers
     *             onClick handlers for all clickables within the alertDialog
     */
    public View setupAlertDialogForAmountPumpedSection() {

        final int amountPumpedPosition = 1;
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_custom_view_new_pumping, null);

        //number picker initializers
        final NumberPicker leftNP = (NumberPicker) view.findViewById(R.id.leftNumberPicker);
        final NumberPicker rightNP = (NumberPicker) view.findViewById(R.id.rightNumberPicker);
        final NumberPicker milliliterNP = new NumberPicker(this);

        TextView doneButton = (TextView) view.findViewById(R.id.doneTextView);
        TextView cancelButton = (TextView) view.findViewById(R.id.cancelTextView);
        final LinearLayout numberPickerLinearLayout = (LinearLayout) view.findViewById(R.id.numberPickerLinearLayout);
        final Button ouncesButton = (Button) view.findViewById(R.id.ouncesButton);
        final Button milliliterButton = (Button) view.findViewById(R.id.milliliterButton);

        isOunces = true;

        //----------------------------------------ON CLICK LISTENERS--------------------------------------------------//

        ouncesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPickerLinearLayout.removeAllViews();
                numberPickerLinearLayout.addView(leftNP);
                numberPickerLinearLayout.addView(rightNP);
                ouncesButton.setBackgroundResource(R.drawable.orange_in_white_out_button_border);
                ouncesButton.setTextColor(Color.WHITE);
                milliliterButton.setBackgroundResource(R.drawable.white_in_orange_out_button_border);
                milliliterButton.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.pumpingHeaderCellBackground));
                isOunces = true;
            }
        });
        milliliterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPickerLinearLayout.removeAllViews();
                numberPickerLinearLayout.addView(milliliterNP);
                isOunces = false;
                milliliterButton.setBackgroundResource(R.drawable.orange_in_white_out_button_border);
                milliliterButton.setTextColor(Color.WHITE);
                ouncesButton.setBackgroundResource(R.drawable.white_in_orange_out_button_border);
                ouncesButton.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.pumpingHeaderCellBackground));

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
                pumpingListViewData.get(currentPosition).set(1, result);

                String leftBreast = pumpingListViewData.get(amountPumpedPosition + 1).get(1),
                        rightBreast = pumpingListViewData.get(amountPumpedPosition + 2).get(1);
                float total;

                if (leftBreast.length() > 0)
                    leftBreast = leftBreast.substring(0, leftBreast.length() - 3);  //remove " oz" from String.
                else
                    leftBreast = "0";
                if (rightBreast.length() > 0)
                    rightBreast = rightBreast.substring(0, rightBreast.length() - 3);//rightBreast = "10 oz" - 3 = "10"
                else
                    rightBreast = "0";
                total = Float.parseFloat(leftBreast) + Float.parseFloat(rightBreast);

                pumpingListViewData.get(amountPumpedPosition).set(1, total + " oz");
                newPumpingListView.setAdapter(adapter); //reset adapter to update listview
                amountPumpedDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountPumpedDialog.dismiss();
            }
        });

        //-------------------------------------------END OF ON CLICK LISTENERS----------------------------------------//

        //number picker customizers
        setNumberPickerTextColor(leftNP, Color.BLACK);
        setNumberPickerTextColor(rightNP, Color.BLACK);
        setNumberPickerTextColor(milliliterNP, Color.BLACK);

        milliliterNumberPickerValues = new String[3000];
        leftNumberPickerValues = new String[3000];
        rightNumberPickerValues = new String[10];

        leftNP.setMinValue(0);
        leftNP.setMaxValue(leftNumberPickerValues.length - 1);
        rightNP.setMinValue(0);
        rightNP.setMaxValue(rightNumberPickerValues.length - 1);
        milliliterNP.setMinValue(0);
        milliliterNP.setMaxValue(milliliterNumberPickerValues.length - 1);

        for (int i = 0; i < 3000; i++) {
            leftNumberPickerValues[i] = i + ".";
            milliliterNumberPickerValues[i] = i + " ml";
        }
        for (int i = 0; i < 10; i++)
            rightNumberPickerValues[i] = i + " oz";

        leftNP.setDisplayedValues(leftNumberPickerValues);
        rightNP.setDisplayedValues(rightNumberPickerValues);
        milliliterNP.setDisplayedValues(milliliterNumberPickerValues);

        return view;
    }


    public boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    Log.d("setNumberPickerTextColo", e + "");
                } catch (IllegalAccessException e) {
                    Log.d("setNumberPickerTextColo", e + "");
                } catch (IllegalArgumentException e) {
                    Log.d("setNumberPickerTextColo", e + "");
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
        } else if (id == android.R.id.home) {
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
