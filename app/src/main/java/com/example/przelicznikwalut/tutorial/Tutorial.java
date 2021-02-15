package com.example.przelicznikwalut.tutorial;

import android.view.View;

import com.example.przelicznikwalut.MainActivity;
import com.example.przelicznikwalut.R;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.OnViewInflateListener;

public class Tutorial {

    MainActivity mainActivity;
    FancyShowCaseView mFancyShowCaseView;

    public Tutorial(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

    }

    public void startTutorial(){
        showFirstHint();
    }

    private void showFirstHint(){
        mFancyShowCaseView = new FancyShowCaseView.Builder(mainActivity)
                .focusOn(mainActivity.findViewById(R.id.fragment_container))
                .title("Wybór waluty")
                .focusCircleRadiusFactor(0.35)
                .delay(100)
                .roundRectRadius(100)
                .customView(R.layout.tutorial_1_layout, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(View view) {
                        view.findViewById(R.id.button1_1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFancyShowCaseView.removeView();
                                showSecondHint();
                            }
                        });
                    }
                }).closeOnTouch(false)
                .build();
        mFancyShowCaseView.show();
    }

    private void showSecondHint(){
        mFancyShowCaseView = new FancyShowCaseView.Builder(mainActivity)
                .focusOn(mainActivity.findViewById(R.id.fragment_container2))
                .focusCircleRadiusFactor(0.35)
                .delay(100)
                .customView(R.layout.tutorial_2_layout, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(View view) {
                        view.findViewById(R.id.button2_1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFancyShowCaseView.removeView();
                                showThirdHint();
                            }
                        });
                    }
                }).closeOnTouch(false)
                .build();
        mFancyShowCaseView.show();
    }

    private void showThirdHint(){
        mFancyShowCaseView = new FancyShowCaseView.Builder(mainActivity)
                .focusOn(mainActivity.findViewById(R.id.linear_layout_input))
                .focusCircleRadiusFactor(0.4)
                .delay(100)
                .customView(R.layout.tutorial_3_layout, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(View view) {
                        view.findViewById(R.id.button3_1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFancyShowCaseView.removeView();
                                showFourthHint();
                            }
                        });
                    }
                }).closeOnTouch(false)
                .build();
        mFancyShowCaseView.show();
    }

    private void showFourthHint(){
        mFancyShowCaseView = new FancyShowCaseView.Builder(mainActivity)
                .delay(100)
                .customView(R.layout.tutorial_4_layout, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(View view) {
                        view.findViewById(R.id.button4_1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFancyShowCaseView.removeView();
                            }
                        });
                    }
                }).closeOnTouch(false)
                .build();
        mFancyShowCaseView.show();
    }


}
