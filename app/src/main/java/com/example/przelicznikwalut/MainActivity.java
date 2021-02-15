package com.example.przelicznikwalut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przelicznikwalut.database.CurrenciesDbHelper;
import com.example.przelicznikwalut.fragments.CurrencyListFragment;
import com.example.przelicznikwalut.model.Currency;
import com.example.przelicznikwalut.tutorial.Tutorial;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    public static final String FRAGMENT_UP_TAG = "fragment_up";
    public static final String FRAGMENT_DOWN_TAG = "fragment_down";

    public static final String SHARED_PREFS = "CurrencyCalcApp";
    public static final String SHARED_PREFS_Tutorial = "tutorial";

    private FragmentManager fragmentManager;
    private CurrencyListFragment currencyListFragmentUp;
    private CurrencyListFragment currencyListFragmentDown;

    private Currency currencyUp;
    private Currency currencyDown;
    private LinkedList<Currency> currencies;

    private ImageButton imageButton;
    private TextView textViewLegend;
    private TextView textViewResult;
    private EditText editTextAmount;
    private Button buttonClear;

    private Toolbar toolbar;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setDefaultCurrencies();
        setFragments(savedInstanceState);

        imageButton = findViewById(R.id.imageButtonReverse);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Currency tmpCurrencyUp = currencyUp;
                currencyUp = currencyDown;
                currencyDown = tmpCurrencyUp;

                currencyListFragmentUp.setCurrentCurrency(currencyUp);
                currencyListFragmentDown.setCurrentCurrency(currencyDown);

                currencyListFragmentUp.updateView(currencyUp);
                currencyListFragmentDown.updateView(currencyDown);

                setTextViewLegend();
                setTextViewResult();
            }
        });

        textViewLegend = findViewById(R.id.textViewLegend);
        setTextViewLegend();

        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        editTextAmount = findViewById(R.id.editTextAmount);
        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTextViewResult();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setTextViewResult();

        buttonClear = findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!editTextAmount.getText().toString().equals("")){
                    editTextAmount.setText("");
                }
                if(!textViewResult.getText().toString().equals("")){
                    setTextViewResult();
                }
            }
        });

        toolbar = findViewById(R.id.ToolBar);
        setSupportActionBar(toolbar);

        //dev
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrenciesDbHelper currenciesDbHelper = new CurrenciesDbHelper(getApplicationContext());
                SQLiteDatabase database = currenciesDbHelper.getReadableDatabase();
                currenciesDbHelper.deleteAllCurrencies(database);
                currenciesDbHelper.close();
            }
        });

        checkIfShowTutorial();

    }

    private void setDefaultCurrencies(){
        CurrenciesDbHelper currenciesDbHelper = new CurrenciesDbHelper(getApplicationContext());
        SQLiteDatabase database = currenciesDbHelper.getReadableDatabase();
        currencyUp = currenciesDbHelper.getCurrency("EUR", database);
        currencyDown = currenciesDbHelper.getCurrency("PLN", database);
        currenciesDbHelper.close();
    }

    private void setFragments(Bundle savedInstanceState){
        fragmentManager = getSupportFragmentManager();
        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            currencyListFragmentUp = new CurrencyListFragment(currencyUp, this);
            fragmentTransaction.add(R.id.fragment_container, currencyListFragmentUp, FRAGMENT_UP_TAG);
            fragmentTransaction.commit();
        }

        if(findViewById(R.id.fragment_container2) != null){
            if(savedInstanceState != null){
                return;
            }
            FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
            currencyListFragmentDown = new CurrencyListFragment(currencyDown, this);
            fragmentTransaction1.add(R.id.fragment_container2, currencyListFragmentDown, FRAGMENT_DOWN_TAG);
            fragmentTransaction1.commit();
        }
    }

    private void setTextViewLegend(){

        Double currencyUpValue = Double.parseDouble(currencyUp.getValue());
        Double currencyDownValue = Double.parseDouble(currencyDown.getValue());
        Double result = calculateValue(1.0, currencyUpValue, currencyDownValue);

        String roundedResult = roundDouble(result);
        String text = "1 " + currencyUp.getSymbol() + " = " + roundedResult + " " + currencyDown.getSymbol();
        System.out.println("WYNIK: " + text);
        textViewLegend.setText(text);
    }

    private Double calculateValue(Double amount, Double firstValue, Double secondValue) {
        return  (amount * (firstValue/secondValue));
    }

    private String roundDouble(Double toRound){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String roundedResult = df.format(toRound);

        return roundedResult;
    }

    private void setTextViewResult(){

        Double currencyUpValue = Double.parseDouble(currencyUp.getValue());
        Double currencyDownValue = Double.parseDouble(currencyDown.getValue());

        String amountString = editTextAmount.getText().toString();
        Double amount = 0.0;
        if(!amountString.equals("")){
            amount = Double.parseDouble(amountString);
        } else {
            amount = 0.0;
            amountString = "0.0";
        }

        Double result = calculateValue(amount, currencyUpValue, currencyDownValue);
        String roundedResult = roundDouble(result);

        String text = amountString + " " + currencyUp.getSymbol() + " = " + roundedResult + " " + currencyDown.getSymbol();

        textViewResult.setText(text);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    /**
     * Related to Menu in ToolBar
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settingsInfo:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Informacje")
                        .setMessage("Kursy aktualizowane są przy włączaniu aplikacji. " +
                                "\nNowe kursy na dany dzień są publikowane przez NBP między godziną 11:45-12:15")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;

            case R.id.action_settingsTutorial:
                Tutorial tutorial = new Tutorial(MainActivity.this);
                tutorial.startTutorial();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkIfShowTutorial(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String result = sharedPreferences.getString(SHARED_PREFS_Tutorial, "default");
        if(result.equals("true")){
            showTutorial();
            saveTutorialShouldNotBeShownNextTime();
        }
    }

    private void showTutorial(){
        Tutorial tutorial = new Tutorial(MainActivity.this);
        tutorial.startTutorial();
    }

    private void saveTutorialShouldNotBeShownNextTime(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putString(SHARED_PREFS_Tutorial, "false")
                .apply();
    }

    /**
     * Method call from activity_main.xml
     */
    public void performClick(View view){

        CurrenciesDbHelper currenciesDbHelper = new CurrenciesDbHelper(getApplicationContext());
        SQLiteDatabase database = currenciesDbHelper.getReadableDatabase();
        currencies =  currenciesDbHelper.getCurrencies(database);
        currenciesDbHelper.close();

        if(view.getId() == R.id.fragment_container){
            currencyListFragmentUp.createCurrenciesListDialog(currencies).show();
        } else if(view.getId() == R.id.fragment_container2){
            currencyListFragmentDown.createCurrenciesListDialog(currencies).show();
        }

    }

    /**
     * Method call from activity_main.xml
     */
    public void performClick2(View view){
        showKeyboard();
    }

    private void showKeyboard(){
        editTextAmount.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextAmount, InputMethodManager.SHOW_IMPLICIT);
    }

    public void setCurrencyUp(Currency currencyUp) {
        this.currencyUp = currencyUp;
        setTextViewLegend();
        setTextViewResult();
    }

    public void setCurrencyDown(Currency currencyDown) {
        this.currencyDown = currencyDown;
        setTextViewLegend();
        setTextViewResult();
    }
}
