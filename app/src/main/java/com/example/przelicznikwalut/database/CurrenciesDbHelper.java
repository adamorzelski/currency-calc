package com.example.przelicznikwalut.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.przelicznikwalut.model.Currency;

import java.util.LinkedList;
import java.util.List;

import static com.example.przelicznikwalut.database.CurrenciesContract.CurrenciesEntry.TABLE_NAME;

public class CurrenciesDbHelper extends SQLiteOpenHelper {

    Context context;

    public static final String DATABASE_NAME = "currency_db";
    public static final int DATABASE_VERSION = 1;

    public static final String CREATE_TABLE = "create table if not exists "
            + TABLE_NAME + " ("
            + CurrenciesContract.CurrenciesEntry.SYMBOL_ID + " varchar(10) primary key, "
            + CurrenciesContract.CurrenciesEntry.DESCRITPTION + " varchar(100) not null, "
            + CurrenciesContract.CurrenciesEntry.VALUE + " varchar(30) not null, "
            + CurrenciesContract.CurrenciesEntry.DATE + " varchar(30) not null, "
            + CurrenciesContract.CurrenciesEntry.FAVOURITE + " integer not null);";

    public static final String DROP_TABLE = "drop table if exists "
            + TABLE_NAME;

    public CurrenciesDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void insertCurrencies(List<Currency> currencies, SQLiteDatabase db) {

        for(int i = 0; i < currencies.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CurrenciesContract.CurrenciesEntry.SYMBOL_ID, currencies.get(i).getSymbol());
            contentValues.put(CurrenciesContract.CurrenciesEntry.DESCRITPTION, currencies.get(i).getDescription());
            contentValues.put(CurrenciesContract.CurrenciesEntry.VALUE, currencies.get(i).getValue());
            contentValues.put(CurrenciesContract.CurrenciesEntry.DATE, currencies.get(i).getDate());
            contentValues.put(CurrenciesContract.CurrenciesEntry.FAVOURITE, currencies.get(i).getFavourite());

            db.insert(TABLE_NAME, null, contentValues);

        }

    }

    public Currency getCurrency(String symbol_id, SQLiteDatabase db){

        String[] projections = {CurrenciesContract.CurrenciesEntry.SYMBOL_ID,
                CurrenciesContract.CurrenciesEntry.DESCRITPTION,
                CurrenciesContract.CurrenciesEntry.VALUE,
                CurrenciesContract.CurrenciesEntry.DATE,
                CurrenciesContract.CurrenciesEntry.FAVOURITE};


        String having = CurrenciesContract.CurrenciesEntry.SYMBOL_ID + "= '" + symbol_id +"'";
        String groupBy = CurrenciesContract.CurrenciesEntry.SYMBOL_ID;

        Cursor cursor = db.query(TABLE_NAME,
                projections,
                null,
                null,
                groupBy,
                having,
                null);

        Currency result = null;

        while(cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.SYMBOL_ID));
            String description = cursor.getString(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.DESCRITPTION));
            String value = cursor.getString(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.VALUE));
            String date = cursor.getString(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.DATE));
            int favourite = cursor.getInt(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.FAVOURITE));
            result = new Currency(id, description, value, date, favourite);
        }

        return result;
    }

    public void updateCurrencies(LinkedList<Currency> currencies, SQLiteDatabase db) {

        for(int i = 0; i < currencies.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(CurrenciesContract.CurrenciesEntry.DATE, currencies.get(i).getDate());
            contentValues.put(CurrenciesContract.CurrenciesEntry.VALUE, currencies.get(i).getValue());

            String whereClause = CurrenciesContract.CurrenciesEntry.SYMBOL_ID + "=" + "'" + currencies.get(i).getSymbol() + "'";
            db.update(TABLE_NAME, contentValues, whereClause, null );
        }
        Toast.makeText(context, "Zaaktualizowano kursy", Toast.LENGTH_SHORT).show();
    }

    public void toggleFavourite(String symbol_id, int isFavourite, SQLiteDatabase db){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CurrenciesContract.CurrenciesEntry.FAVOURITE, isFavourite);

        String whereClause = CurrenciesContract.CurrenciesEntry.SYMBOL_ID + "=" + "'" + symbol_id + "'";

        db.update(TABLE_NAME, contentValues, whereClause, null );
    }



    public LinkedList<Currency> getCurrencies(SQLiteDatabase db){

        String[] projections = {CurrenciesContract.CurrenciesEntry.SYMBOL_ID,
                CurrenciesContract.CurrenciesEntry.DESCRITPTION,
                CurrenciesContract.CurrenciesEntry.VALUE,
                CurrenciesContract.CurrenciesEntry.DATE,
                CurrenciesContract.CurrenciesEntry.FAVOURITE};

        Cursor cursor = db.query(TABLE_NAME,
                projections,
                null,
                null,
                null,
                null,
                CurrenciesContract.CurrenciesEntry.FAVOURITE + " desc");

        return convertCursorToLinkedList(cursor);
    }

    private LinkedList<Currency> convertCursorToLinkedList(Cursor cursor){

        LinkedList<Currency> resultList = new LinkedList<>();

        while(cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.SYMBOL_ID));
            String description = cursor.getString(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.DESCRITPTION));
            String value = cursor.getString(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.VALUE));
            String date = cursor.getString(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.DATE));
            int favourite = cursor.getInt(cursor.getColumnIndex(CurrenciesContract.CurrenciesEntry.FAVOURITE));

            resultList.add(new Currency(id, description, value, date, favourite));
        }
        return  resultList;
    }

    public void deleteAllCurrencies(SQLiteDatabase db)
    {
        db.delete(TABLE_NAME, null, null);
    }

    public int countRows(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select count(*) from " + TABLE_NAME, null);
        int result = 0;
        while(cursor.moveToNext())
            result = cursor.getInt(0);

        return result;
    }
}
