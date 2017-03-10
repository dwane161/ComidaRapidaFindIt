package com.djdevelopment.comidarapidafindit.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.djdevelopment.comidarapidafindit.R;
import com.djdevelopment.comidarapidafindit.data.MenuService;

import java.util.ArrayList;

/**
 * Created by Dwane Jimenez on 2/23/2017.
 */

public class UtilUI {
    public static MenuService menuService = null;
    public  static String telephone = null;

    public  static MenuService getMenuServiceDialog(final Activity activity, final Runnable callbackPositiveAction ){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater

        LayoutInflater inflater = activity.getLayoutInflater();
        final View viewRoot =  inflater.inflate(R.layout.custom_dialog_menu_service, null);
        View custom_title =  inflater.inflate(R.layout.custom_title, null);


        ((TextView)custom_title.findViewById(R.id.txtTitleDialog)).setText("Sugerir");


        builder.setCustomTitle(custom_title);

        final Spinner motels_services_currency_type = (Spinner) viewRoot.findViewById(R.id.motels_services_currency_type);
        ArrayAdapter<CharSequence> adapter_motels_services_currency_type = ArrayAdapter.createFromResource(activity,
                R.array.motels_services_currency_type, android.R.layout.simple_spinner_item);

        adapter_motels_services_currency_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        motels_services_currency_type.setAdapter(adapter_motels_services_currency_type);

        builder.setCancelable(false);

        builder.setView(viewRoot) .setNegativeButton(R.string.cancel, null);
        builder.setView(viewRoot) .setPositiveButton(R.string.lblAdd, null);

        final AlertDialog dialog = 			    builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText txtMotelServiceName  =  (EditText) viewRoot.findViewById(R.id.txtMotelServiceName);
                EditText txtMotelServicePrice  =  (EditText) viewRoot.findViewById(R.id.txtMotelServicePrice);

                if(!UtilUI.validateEmptyFields(activity,txtMotelServiceName,txtMotelServicePrice)){

                    menuService = new MenuService("",0,0);

                    menuService = new MenuService(txtMotelServiceName.getText().toString(),  Double.parseDouble(txtMotelServicePrice.getText().toString()),
                            motels_services_currency_type.getSelectedItemPosition());

                    dialog.dismiss();
                    callbackPositiveAction.run();

                }

            }
        });

        return menuService;

    }

    public  static void  getCreditCardsDialog(final Activity activity, final ArrayList<String> creditCards, final Runnable callbackPositiveAction ){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        final String[] credit_cards_type_string =activity.getResources().getStringArray(R.array.credit_cards_type);
        final boolean []  credit_cards_selected = {true,true,false,false};
        creditCards.add(credit_cards_type_string[0]);
        creditCards.add(credit_cards_type_string[1]);
        View custom_title =  inflater.inflate(R.layout.custom_title, null);


        ((TextView)custom_title.findViewById(R.id.txtTitleDialog)).setText(R.string.CreditCardSuggested);



        builder.setCustomTitle(custom_title)
                .setMultiChoiceItems(R.array.credit_cards_type,new boolean[]{true,true,false,false},new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            creditCards.add(credit_cards_type_string[which]);
                            credit_cards_selected[which] = true;
                        } else if (creditCards.contains(credit_cards_type_string[which])) {
                            // Else, if the item is already in the array, remove it

                            creditCards.remove(creditCards.indexOf(credit_cards_type_string[which]));
                            credit_cards_selected[which] = false;

                        }

                    }
                });

        builder.setCancelable(false);

        builder.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.lblAdd, null);

        final AlertDialog dialog = 			    builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(UtilUI.validateMultipleChoiceWithMessage(activity,credit_cards_selected)){

                    dialog.dismiss();
                    callbackPositiveAction.run();

                }else{
                    dialog.dismiss();
                }
            }
        });

    }

    public  static String  getTelephoneDialog(final Activity activity,final Runnable callbackPositiveAction ){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        final View viewRoot =  inflater.inflate(R.layout.custom_dialog_telephone, null);
        View custom_title =  inflater.inflate(R.layout.custom_title, null);


        ((TextView)custom_title.findViewById(R.id.txtTitleDialog)).setText(R.string.TelephoneSuggested);

        builder.setCustomTitle(custom_title).setCancelable(false)
                .setView(viewRoot)
                .setNegativeButton(R.string.cancel, null)
                .setView(viewRoot)
                .setPositiveButton(R.string.lblAdd, null);

        final AlertDialog dialog = 			    builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText  txtTelephone  =  (EditText) viewRoot.findViewById(R.id.txtTelephone);


                if(!UtilUI.validateEmptyFields(activity,txtTelephone)){


                    telephone = 	   txtTelephone.getText().toString();
                    dialog.dismiss();
                    callbackPositiveAction.run();

                }

            }
        });







        return telephone;

    }

    public static boolean validateMultipleChoiceWithMessage(final Activity activity,boolean ...choices){
        boolean isValid = validateMultipleChoice(choices);
        if( !isValid){
            showToaststMessage(activity.getString(R.string.msErrorMultipleChoiceInvalid), activity);
        }

        return isValid;
    }

    public static boolean validateMultipleChoice(boolean ...choices){
        boolean isValid = false;
        for( boolean  choice :choices){
            if(choice){
                isValid = true;
                break;
            }
        }

        return isValid;
    }

    public static void showToaststMessage(String strTexto,Context context){
        String strNoInternet = strTexto;
        Toast.makeText(context,strNoInternet,Toast.LENGTH_LONG).show();

    }

    public static void hideSoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null )
            imm.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean validateEmptyFields(Context context, EditText... txts) {
        boolean isCampoVacio = false;
        for(EditText itemTxt : txts){
            if("".equals(itemTxt.getText().toString().trim())){
                Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
                itemTxt.startAnimation(shake);
                itemTxt.setError(context.getString(R.string.msErrorEmptyField));
                itemTxt.setText("");
                isCampoVacio = true;
            }else{
                itemTxt.setError(null);
            }
        }
        return isCampoVacio;

    }

    public static void showAlertDialog(Context context, String title, View view, int stringButton, final Runnable callBackaPositiveButton){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(stringButton,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialogBuilder = null;
    }

    public static void showAlertDialog(Context context, String title, String message, int stringButton, final Runnable callBackaPositiveButton){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(stringButton,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked
                        if(callBackaPositiveButton!=null){

                            callBackaPositiveButton.run();
                        }
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialogBuilder = null;
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        boolean isConnection = false;
        NetworkInfo wifiNetwork =null;
        try{
            wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }catch(Exception e){
            System.out.println(e);
        }
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            isConnection = true;
        }
        NetworkInfo mobileNetwork = null;
        try{
            mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        }catch(Exception e){
            System.out.println(e);
        }
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            isConnection = true;
        }
        NetworkInfo activeNetwork = null;
        try{
            activeNetwork = cm.getActiveNetworkInfo();
        }catch(Exception e){
            System.out.println(e);
        }
        if (activeNetwork != null && activeNetwork.isConnected()) {
            isConnection = true;
        }

        return isConnection;
    }

    public static boolean validateInternetConnetion(Context context, Runnable callBackConfirmButton){
        boolean connection = false;
        if( hasConnection(context)){
            connection = true;
        }else{


            showAlertDialog(context, context.getString(R.string.message), context.getString(R.string.msInternetNoConnection), R.string.ok, callBackConfirmButton );
        }
        return connection;
    }


}
