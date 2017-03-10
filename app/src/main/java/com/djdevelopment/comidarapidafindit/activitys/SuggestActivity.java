package com.djdevelopment.comidarapidafindit.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djdevelopment.comidarapidafindit.R;
import com.djdevelopment.comidarapidafindit.data.MenuService;
import com.djdevelopment.comidarapidafindit.tools.UtilUI;

import java.util.ArrayList;

public class SuggestActivity extends AppCompatActivity {

    private ArrayList<MenuService> menuServices;
    LayoutInflater inflaterItem = null;
    private Typeface custom_font2 = null;
    LinearLayout cardViewServices = null;
    private ArrayList<String> telephones = null;
    private ArrayList<String> creditCards = null;
    private ArrayList<Image> imagesList = null;
    LinearLayout cardViewServicesTelephons = null;
    LinearLayout cardViewCreditCards = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);


        telephones = new ArrayList<String>();
        creditCards = new ArrayList<String>();
        imagesList = new ArrayList<Image>();


        cardViewServices =  (LinearLayout)findViewById(R.id.cardViewServicesPrices);
        cardViewServicesTelephons =  (LinearLayout) findViewById(R.id.cardViewServicesTelephons);
        cardViewCreditCards =  (LinearLayout)findViewById(R.id.cardViewCreditCards);

        menuServices = new ArrayList<>();
        custom_font2 = Typeface.createFromAsset(SuggestActivity.this.getAssets(), "Roboto-Thin.ttf");

        inflaterItem = (LayoutInflater) SuggestActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        cardViewServices =  (LinearLayout)findViewById(R.id.cardViewServicesPrices);

        initComponents();
    }

    public void initComponents(){
        addNewFastFood();
        addNewServiceModel();
        suggestCreditCards();
        suggestTelephones();
        suggetImage();


    }

    private void addNewFastFood(){

        ((Button)findViewById(R.id.btnAddNewFastFood)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.requestFocus();
                Intent intent =  new Intent( SuggestActivity.this, LocationPickerActivity.class);
                startActivityForResult(intent, 11);

            }
        });
    }

    private void addNewServiceModel(){
        ((Button)findViewById(R.id.btnAddNewServiceModel)).setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.requestFocus();
                UtilUI.getMenuServiceDialog(SuggestActivity.this, new Runnable() {

                    @Override
                    public void run() {

                        final MenuService menuService =UtilUI.menuService;
                        menuServices.add(menuService);


                        View viewPrices = inflaterItem.inflate(R.layout.item_layout_edit, null, true);
                        TextView serviceName = (TextView)viewPrices.findViewById(R.id.lblTimeOftheLastVote);
                        ImageView iconImage  = (ImageView) viewPrices.findViewById(R.id.iconService);
                        final TextView servicePrice =(TextView)viewPrices.findViewById(R.id.service_value);

                        serviceName.setText(menuService.getMenuName());
                        String currency = String.valueOf(menuService.getCurrencyType());
                        if(currency.equals("0")){
                            currency = "RD";
                        }

                        servicePrice.setText(String.format(currency+"$%,3.2f",menuService.getPrice()));
                        serviceName.setTypeface(custom_font2);
                        servicePrice.setTypeface(custom_font2);

                        cardViewServices.addView(viewPrices);


                        servicePrice.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                RelativeLayout r = (RelativeLayout)v.getParent();
                                int index  = cardViewServices.indexOfChild(r);
                                cardViewServices.removeViewAt(index);
                                menuServices.remove(index);
                            }
                        });
                        UtilUI.menuService = null;
                        UtilUI.hideSoftKeyBoard(SuggestActivity.this);
                    }
                });

                UtilUI.hideSoftKeyBoard(SuggestActivity.this);
            }
        });

    }

    private void suggestCreditCards(){

        ((Button)findViewById(R.id.btnSuggestCreditCards)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.requestFocus();
                final ArrayList<String> creditCard = new ArrayList<String>();
                UtilUI.getCreditCardsDialog(SuggestActivity.this, creditCard, new Runnable() {

                    @Override
                    public void run() {
                        for(String creditCardValue: creditCard){
                            if(creditCards.indexOf(creditCardValue) == -1){
                                creditCards.add(creditCardValue);
                                View viewCreditCards = inflaterItem.inflate(R.layout.item_layout_edit, null, true);
                                TextView txtCreditCard = (TextView) viewCreditCards.findViewById(R.id.lblTimeOftheLastVote);
                                TextView txtValue = (TextView) viewCreditCards.findViewById(R.id.service_value);
                                ImageView iconImage  = (ImageView) viewCreditCards.findViewById(R.id.iconService);
                                iconImage.setImageResource(R.drawable.icons_10);

                                txtCreditCard.setText(creditCardValue);
                                txtCreditCard.setTypeface(custom_font2);

                                txtValue.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        RelativeLayout r = (RelativeLayout)v.getParent();
                                        int index  = cardViewCreditCards.indexOfChild(r);
                                        cardViewCreditCards.removeViewAt(index);
                                        creditCards.remove(index);
                                    }
                                });


                                cardViewCreditCards.addView(viewCreditCards);
                            }

                        }
                    }
                });

            }
        });
    }

    private void suggestTelephones(){
        ((Button)findViewById(R.id.btnSuggestedTelephons)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UtilUI.getTelephoneDialog(SuggestActivity.this, new Runnable() {

                    @Override
                    public void run() {

                        UtilUI.hideSoftKeyBoard(SuggestActivity.this);
                        String phone = UtilUI.telephone;
                        telephones.add(phone);
                        View viewPhones = inflaterItem.inflate(R.layout.item_layout_edit, null, true);
                        TextView txtPhone = (TextView) viewPhones.findViewById(R.id.lblTimeOftheLastVote);
                        TextView txtValue = (TextView) viewPhones.findViewById(R.id.service_value);
                        ImageView iconImage  = (ImageView) viewPhones.findViewById(R.id.iconService);
                        iconImage.setImageResource(R.drawable.icons_07);


                        txtPhone.setText(phone);
                        txtPhone.setTypeface(custom_font2);




                        txtValue.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                RelativeLayout r = (RelativeLayout)v.getParent();
                                int index  = cardViewServicesTelephons.indexOfChild(r);
                                cardViewServicesTelephons.removeViewAt(index);
                                telephones.remove(index);
                            }
                        });


                        cardViewServicesTelephons.addView(viewPhones);


                        UtilUI.telephone = null;

                    }
                });
            }

        });
    }

    private void suggetImage(){
        ((Button)findViewById(R.id.btnAddImage)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.requestFocus();
                Intent intent;

                if (Build.VERSION.SDK_INT < 19){
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                }
                else
                {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }


                intent.setType("image/*");//image/jpeg
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
                    //for API Level 11+
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                }else{
                    intent.putExtra("android.intent.extra.LOCAL_ONLY", true);
                }

                startActivityForResult(intent, 10);


            }
        });
    }
}
