package com.example.przelicznikwalut.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.przelicznikwalut.R;
import com.example.przelicznikwalut.database.CurrenciesDbHelper;
import com.example.przelicznikwalut.model.Currency;

import java.util.LinkedList;

public class MyDialogAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<Currency> currenciesList;
    private LayoutInflater layoutInflater;

    public MyDialogAdapter(Context context, LinkedList<Currency> currenciesList) {
        this.context = context;
        this.currenciesList = currenciesList;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return currenciesList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = layoutInflater.inflate(R.layout.element_dialog, parent, false);

        TextView text = view.findViewById(R.id.element_dialog_textView3);
        TextView text2 = view.findViewById(R.id.element_dialog_extView4);
        ImageView image = view.findViewById(R.id.element_dialog_imageView);

        String nameOfFlag = currenciesList.get(position).getSymbol().toLowerCase();
        if(nameOfFlag.equals("try")){
            nameOfFlag = "tryf";
        }
        image.setImageResource(getImageId(context, nameOfFlag));


        final ImageButton imageButton = view.findViewById(R.id.element_dialog_imageButton2);
        final ImageButton imageButton2 = view.findViewById(R.id.element_dialog_imageButton3);
        imageButton.setFocusable(false);
        imageButton2.setFocusable(false);
        text.setText(currenciesList.get(position).getSymbol());
        text2.setText(currenciesList.get(position).getDescription());

        if(currenciesList.get(position).getFavourite() > 0){
            imageButton.setVisibility(View.GONE);
            imageButton2.setVisibility(View.VISIBLE);
        } else {
            imageButton2.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
        }

        final int currentPosition = position;
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currenciesList.get(currentPosition).setFavourite(1);
                imageButton.setVisibility(View.GONE);
                imageButton2.setVisibility(View.VISIBLE);

                CurrenciesDbHelper currenciesDbHelper = new CurrenciesDbHelper(context);
                SQLiteDatabase database = currenciesDbHelper.getWritableDatabase();
                currenciesDbHelper.toggleFavourite(currenciesList.get(currentPosition).getSymbol(), 1, database);
                currenciesDbHelper.close();
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currenciesList.get(currentPosition).setFavourite(0);
                imageButton2.setVisibility(View.GONE);
                imageButton.setVisibility(View.VISIBLE);

                CurrenciesDbHelper currenciesDbHelper = new CurrenciesDbHelper(context);
                SQLiteDatabase database = currenciesDbHelper.getWritableDatabase();
                currenciesDbHelper.toggleFavourite(currenciesList.get(currentPosition).getSymbol(), 0, database);
                currenciesDbHelper.close();
            }
        });

        return view;
    }

    private int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}
