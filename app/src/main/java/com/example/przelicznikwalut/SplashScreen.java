package com.example.przelicznikwalut;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przelicznikwalut.database.CurrenciesDbHelper;
import com.example.przelicznikwalut.model.Currency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import static com.example.przelicznikwalut.MainActivity.SHARED_PREFS;
import static com.example.przelicznikwalut.MainActivity.SHARED_PREFS_Tutorial;

public class SplashScreen extends AppCompatActivity {

    private static final int CZAS = 1000;
    private final String CURRENCIES_URL = "https://api.nbp.pl/api/exchangerates/tables/a/?format=json";

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        textView = findViewById(R.id.textView2);
        startAnim();
        createTutorialSharedPreferences();

        if(isNetworkAvailable()){
            if(isDatabaseFilled()){
                //update currencies
                getAndSaveCurrencies("UPDATE");
                startOpeningMainActivity();

            } else {
                //get currencies and save them
                getAndSaveCurrencies("CREATE");
                startOpeningMainActivity();
            }
        } else if(isDatabaseFilled()){
            //use currences from database
            startOpeningMainActivity();

        } else {
            //Internet need - dialog
            createNoInternetAndNoDataDialog();
        }

    }

    private void startAnim(){
        Animation alpha = new AlphaAnimation(0f, 1f);
        alpha.setDuration(1000);
        alpha.setRepeatCount(0);

        textView.startAnimation(alpha);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isDatabaseFilled(){
        CurrenciesDbHelper currenciesDbHelper = new CurrenciesDbHelper(this);
        SQLiteDatabase database = currenciesDbHelper.getReadableDatabase();
        int result = currenciesDbHelper.countRows(database);
        currenciesDbHelper.close();

        return result > 0;
    }

    private void startOpeningMainActivity() {
        ActivityStarter starter = new ActivityStarter();
        starter.start();
    }

    private class ActivityStarter extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep(CZAS);
            } catch (Exception e) {
                Log.e("SplashScreen", e.getMessage());
            }

            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            SplashScreen.this.startActivity(intent);
            SplashScreen.this.finish();
        }
    }

    private void createTutorialSharedPreferences(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String result = sharedPreferences.getString(SHARED_PREFS_Tutorial, "default");
        if(result.equals("") || result.equals("default")){
            saveTutorialShouldBeShown();
        }
    }

    private void saveTutorialShouldBeShown(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putString(SHARED_PREFS_Tutorial, "true")
                .apply();
    }


    /**
     * @param action "UPDATE" or "CREATE"
     */
    private void getAndSaveCurrencies(String action){
        new JsonTask().execute(CURRENCIES_URL, action);
    }

    private void createNoInternetAndNoDataDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
        builder.setTitle("Brak połączenia z internetem.")
                .setMessage("Włącz internet w celu pobrania kursów walut.")
                .setPositiveButton("Ponów", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Wyjdź", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        String action;

        public JsonTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            action = params[1];

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                //getting json
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                String date = jsonObject1.getString("effectiveDate");
                JSONArray jsonArray2 = jsonObject1.getJSONArray("rates");

                LinkedList<Currency> currenciesList = new LinkedList<>();

                int favourite = 0;
                for(int i = 0; i < jsonArray2.length(); i++){
                    JSONObject jsonObjectTMP = jsonArray2.getJSONObject(i); //get one currency data

                    if(     jsonObjectTMP.get("code").toString().equals("USD") ||
                            jsonObjectTMP.get("code").toString().equals("EUR") ||
                            jsonObjectTMP.get("code").toString().equals("GBP")){

                        favourite = 1;
                    } else {
                        favourite = 0;
                    }

                    currenciesList.add(new Currency(
                            jsonObjectTMP.get("code").toString(),
                            jsonObjectTMP.get("currency").toString(),
                            jsonObjectTMP.get("mid").toString(),
                            date,
                            favourite)
                    );
                }

                currenciesList.add(new Currency("PLN",
                        "polski złoty",
                        "1.0",
                        currenciesList.get(0).getDate(),
                        1));


                if(action.equals("CREATE")){
                    if(currenciesList.size() != 0){
                        CurrenciesDbHelper currenciesDbHelper = new CurrenciesDbHelper(getApplicationContext());
                        SQLiteDatabase database = currenciesDbHelper.getWritableDatabase();
                        currenciesDbHelper.insertCurrencies(currenciesList, database);
                        currenciesDbHelper.close();
                    } else {
                        Toast.makeText(SplashScreen.this, "Insert rows failed", Toast.LENGTH_SHORT).show();
                        throw new Exception("Insert rows failed. Empty list from JSON");
                    }
                } else if(action.equals("UPDATE")){
                    if(currenciesList.size() != 0){
                        CurrenciesDbHelper currenciesDbHelper = new CurrenciesDbHelper(getApplicationContext());
                        SQLiteDatabase database = currenciesDbHelper.getWritableDatabase();
                        currenciesDbHelper.updateCurrencies(currenciesList, database);
                        currenciesDbHelper.close();
                    } else {
                        Toast.makeText(SplashScreen.this, "Insert rows failed", Toast.LENGTH_SHORT).show();
                        throw new Exception("Update rows failed. Empty list from JSON");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
