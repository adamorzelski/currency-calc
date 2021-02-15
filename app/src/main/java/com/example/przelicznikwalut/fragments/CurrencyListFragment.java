package com.example.przelicznikwalut.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.przelicznikwalut.MainActivity;
import com.example.przelicznikwalut.R;
import com.example.przelicznikwalut.adapters.MyDialogAdapter;
import com.example.przelicznikwalut.model.Currency;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrencyListFragment extends Fragment {

    private MainActivity parentActivity;
    private TextView textView1;
    private TextView textView2;
    private ImageView imageView;
    private CurrencyListFragment thisInstance;
    private Currency currentCurrency;

    public CurrencyListFragment() {
        // Required empty public constructor
    }

    public CurrencyListFragment(Currency currentCurrency, MainActivity parent) {
        this.currentCurrency = currentCurrency;
        this.parentActivity = parent;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thisInstance = this;

        View view = inflater.inflate(R.layout.fragment_lista_linear, container, false);

        textView1 = view.findViewById(R.id.spinner_textView6);
        textView2 = view.findViewById(R.id.spinner_textView7);
        imageView = view.findViewById(R.id.spinner_imageView);

        updateView(this.currentCurrency);

        setRetainInstance(true);
        return view;
    }


    public void updateView(Currency currency){
        textView1.setText(currency.getSymbol());
        textView2.setText(currency.getDescription());
        String nameOfFlag = currentCurrency.getSymbol().toLowerCase();
        if(nameOfFlag.equals("try")){
            nameOfFlag = "tryf";
        }

        imageView.setImageResource(getImageId(getContext(), nameOfFlag));
    }

    public Dialog createCurrenciesListDialog(final LinkedList<Currency> currencies){

        final Dialog dialog1 = new Dialog(getContext());
        dialog1.setContentView(R.layout.dialog_currencies_list);
        dialog1.setTitle("Wybierz walutę");

        final ListView listViewDialog = dialog1.findViewById(R.id.listViewDialog);
        MyDialogAdapter myDialogAdapter = new MyDialogAdapter(getContext(), currencies);
        listViewDialog.setAdapter(myDialogAdapter);

        listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setCurrentCurrency(currencies.get(position));
                thisInstance.updateView(currencies.get(position));

                if(thisInstance.getTag().equals(MainActivity.FRAGMENT_UP_TAG)){
                    parentActivity.setCurrencyUp(thisInstance.currentCurrency);
                } else if(thisInstance.getTag().equals(MainActivity.FRAGMENT_DOWN_TAG)){
                    parentActivity.setCurrencyDown(thisInstance.currentCurrency);
                }

                dialog1.dismiss();
            }
        });

        return dialog1;
    }

    public void setCurrentCurrency(Currency currentCurrency) {
        this.currentCurrency = currentCurrency;
    }

    private int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

}
