package com.djdevelopment.comidarapidafindit.activitys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.djdevelopment.comidarapidafindit.R;
import com.djdevelopment.comidarapidafindit.data.Image;
import com.djdevelopment.comidarapidafindit.data.MenuService;
import com.djdevelopment.comidarapidafindit.tools.UtilUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SuggestActivity extends AppCompatActivity {

    final Context context = this;
    private ArrayList<MenuService> menuServices;
    LayoutInflater inflaterItem = null;
    private Typeface custom_font2 = null;
    LinearLayout cardViewServices = null;
    private ArrayList<String> telephones = null;
    private EditText txtName  = null;
    private ArrayList<String> creditCards = null;
    private ArrayList<Image> imagesList = null;
    private LatLng selectMotelLatLng = null;
    LinearLayout cardViewImages = null;
    LinearLayout cardViewServicesTelephons = null;
    LinearLayout cardViewCreditCards = null;
    LinearLayout cardViewLocation = null;
    private DatabaseReference mDatabase;

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
        cardViewLocation =  (LinearLayout)findViewById(R.id.cardViewLocation);

        menuServices = new ArrayList<>();
        custom_font2 = Typeface.createFromAsset(SuggestActivity.this.getAssets(), "Roboto-Thin.ttf");

        inflaterItem = (LayoutInflater) SuggestActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        cardViewServices =  (LinearLayout)findViewById(R.id.cardViewServicesPrices);

        initComponents();


        txtName =  (EditText) findViewById(R.id.txtName);
        final Button btnSend = (Button)findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Nombre de restaurante
                String restName = "";
                String creditCars = "";
                String telephonesArray = "";
                restName = txtName.getText().toString();

                //Localizacion de restaurante
                String LatLngCoord = "";
                if(selectMotelLatLng != null){
                    LatLngCoord = selectMotelLatLng.toString();
                }
                if(LatLngCoord.isEmpty()){
                    UtilUI.showAlertDialog(SuggestActivity.this, getString(R.string.location_info),getString(R.string.select_location_info) ,R.string.iGotIt,null);
                    (SuggestActivity.this.findViewById(R.id.lblLocation)).requestFocus();;
                    ScrollView sv = (ScrollView) SuggestActivity.this.findViewById(R.id.ScrollViewSujectMotel);
                    sv.scrollTo(0, sv.getTop());
                    return ;
                }
                //Menu de restaurante
                if(!UtilUI.validateEmptyFields(SuggestActivity.this,txtName)
                        && UtilUI.validateInternetConnetion(SuggestActivity.this,null)){

                    ArrayList<String> resMenuArray = new ArrayList<>();


                    for(MenuService menu: menuServices){
                        resMenuArray.add("{name: '"+ menu.getMenuName() + "', price: '" + menu.getPrice() +"', currency: '" + menu.getCurrencyType() + "'}");
                    }
                    creditCars = TextUtils.join(", ", creditCards.toArray());
                    telephonesArray = TextUtils.join(", ", telephones.toArray());
                    UtilUI.showAlertDialog(SuggestActivity.this, SuggestActivity.this.getString(R.string.thanksContibution),SuggestActivity.this.getString(R.string.thanksForSubmitInfo), R.string.iGotIt, new Runnable() {

                        @Override
                        public void run() {
                            SuggestActivity.this.finish();

                        }
                    });

                    mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://comidarapida-cae88.firebaseio.com/");
                    mDatabase.setValue("hola mundo");
                }

            }
        });
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
                startActivityForResult(intent,11);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 10  && resultCode == RESULT_OK) {


            if(data!=null && data.getData() !=null){

                showInputDialogAndAddElement(data);


            }
        }

        if(requestCode == 11  && resultCode == RESULT_OK){
            if(data!=null && data.getExtras().containsKey("userPosition")){



                cardViewLocation.removeAllViews();
                selectMotelLatLng = null;

                LatLng selectMotelPosition = (LatLng) data.getExtras().get("userPosition");
                selectMotelLatLng = selectMotelPosition;

                View viewPrices = inflaterItem.inflate(R.layout.item_layout_edit, null, true);
                TextView serviceName = (TextView)viewPrices.findViewById(R.id.lblTimeOftheLastVote);
                ImageView iconImage  = (ImageView) viewPrices.findViewById(R.id.iconService);
                String 	address = (String) data.getExtras().get("address");
                final TextView servicePrice =(TextView)viewPrices.findViewById(R.id.service_value);
                servicePrice.setVisibility(View.GONE);

                iconImage.setImageResource(R.drawable.icons_03);


                serviceName.setTypeface(custom_font2);
                serviceName.setText((address!=null && address.length() != 0) ? address : "Lat "+selectMotelLatLng.latitude +" Lon "+selectMotelLatLng.longitude);
                cardViewLocation.addView(viewPrices);

            }
        }

    }

    protected void showInputDialogAndAddElement(final Intent data) {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final TextView editText = (TextView) promptView.findViewById(R.id.editTextDescription);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //resultText.setText("Hello, " + editText.getText());

                        try {
                            String urlImage = getPath(context,data.getData());
                            View viewImages = inflaterItem.inflate(R.layout.item_layout_edit_images, null, true);
                            ImageView imageItem = (ImageView) viewImages.findViewById(R.id.imageItem);
                            TextView txtValue = (TextView) viewImages.findViewById(R.id.service_value);
                            final TextView txtDescription = (TextView) viewImages.findViewById(R.id.imageDescription);
                            txtDescription.setText(editText.getText().toString());
                            txtValue.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    RelativeLayout r = (RelativeLayout)v.getParent();
                                    int index  = cardViewImages.indexOfChild(r);
                                    cardViewImages.removeViewAt(index);
                                    imagesList.remove(index);
                                }
                            });


                            cardViewImages.addView(viewImages);

                            InputStream stream = getContentResolver().openInputStream(
                                    data.getData());
                            final Bitmap bitmap = BitmapFactory.decodeStream(stream);
                            stream.close();
                            //imageItem.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                            imageItem.setImageBitmap(bitmap);
                            Image imageObj = new Image();
                            imageObj.setUrl(urlImage);
                            imageObj.setDescription(editText.getText().toString());
                            imagesList.add(imageObj);
                        } catch (FileNotFoundException e) {
                            Log.i("photo", e.toString());
                        } catch (IOException e) {
                            Log.i("photo", e.toString());
                        } catch (Exception e) {
                            Log.i("photo", e.toString());
                        }

                    }
                })/*
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})*/;

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

}
