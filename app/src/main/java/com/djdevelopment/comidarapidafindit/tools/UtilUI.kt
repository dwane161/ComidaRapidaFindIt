package com.djdevelopment.comidarapidafindit.tools

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.djdevelopment.comidarapidafindit.R
import com.djdevelopment.comidarapidafindit.data.MenuService

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

import butterknife.ButterKnife
import butterknife.OnClick

class UtilUI {
    companion object {
        var menuService: MenuService = MenuService()
        var telephone: String = ""
        var scheduleDays = StringBuilder()

        fun getMenuServiceDialog(activity: Activity, callbackPositiveAction: Runnable) {
            val builder = AlertDialog.Builder(activity)
            // Get the layout inflater

            val inflater = activity.layoutInflater
            val viewRoot = inflater.inflate(R.layout.custom_dialog_menu_service, null)
            val custom_title = inflater.inflate(R.layout.custom_title, null)

            (custom_title.findViewById(R.id.txtTitleDialog) as TextView).setText(R.string.str_suggest)

            builder.setCustomTitle(custom_title)

            val motels_services_currency_type = viewRoot.findViewById(R.id.motels_services_currency_type) as Spinner
            val adapter_motels_services_currency_type = ArrayAdapter.createFromResource(activity,
                    R.array.motels_services_currency_type, android.R.layout.simple_spinner_item)

            adapter_motels_services_currency_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            motels_services_currency_type.adapter = adapter_motels_services_currency_type

            builder.setCancelable(false)

            builder.setView(viewRoot).setNegativeButton(R.string.cancel, null)
            builder.setView(viewRoot).setPositiveButton(R.string.lblAdd, null)

            val dialog = builder.create()
            dialog.show()
            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val txtMotelServiceName = viewRoot.findViewById(R.id.txtMotelServiceName) as EditText
                val txtMotelServicePrice = viewRoot.findViewById(R.id.txtMotelServicePrice) as EditText

                if (!UtilUI.validateEmptyFields(activity, txtMotelServiceName, txtMotelServicePrice)) {

                    menuService = MenuService("", 0.0, 0)

                    menuService = MenuService(txtMotelServiceName.text.toString(), java.lang.Double.parseDouble(txtMotelServicePrice.text.toString()),
                            motels_services_currency_type.selectedItemPosition)

                    txtMotelServiceName.setText("")
                    txtMotelServicePrice.setText("")
                    callbackPositiveAction.run()

                }
            }

        }

        fun getCreditCardsDialog(activity: Activity, creditCards: ArrayList<String>, callbackPositiveAction: Runnable) {

            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater

            val credit_cards_type_string = activity.resources.getStringArray(R.array.credit_cards_type)
            val credit_cards_selected = booleanArrayOf(true, true, false, false)
            creditCards.add(credit_cards_type_string[0])
            creditCards.add(credit_cards_type_string[1])
            val custom_title = inflater.inflate(R.layout.custom_title, null)


            (custom_title.findViewById(R.id.txtTitleDialog) as TextView).setText(R.string.CreditCardSuggested)


            builder.setCustomTitle(custom_title)
                    .setMultiChoiceItems(R.array.credit_cards_type, booleanArrayOf(true, true, false, false)) { dialog, which, isChecked ->
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            creditCards.add(credit_cards_type_string[which])
                            credit_cards_selected[which] = true
                        } else if (creditCards.contains(credit_cards_type_string[which])) {
                            // Else, if the item is already in the array, remove it

                            creditCards.removeAt(creditCards.indexOf(credit_cards_type_string[which]))
                            credit_cards_selected[which] = false

                        }
                    }

            builder.setCancelable(false)

            builder.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.lblAdd, null)

            val dialog = builder.create()
            dialog.show()
            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (UtilUI.validateMultipleChoiceWithMessage(activity, *credit_cards_selected)) {

                    dialog.dismiss()
                    callbackPositiveAction.run()

                } else {
                    dialog.dismiss()
                }
            }

        }

        fun getTelephoneDialog(activity: Activity, callbackPositiveAction: Runnable) {
            val builder = AlertDialog.Builder(activity)
            // Get the layout inflater
            val inflater = activity.layoutInflater
            val viewRoot = inflater.inflate(R.layout.custom_dialog_telephone, null)
            val custom_title = inflater.inflate(R.layout.custom_title, null)


            (custom_title.findViewById(R.id.txtTitleDialog) as TextView).setText(R.string.TelephoneSuggested)

            builder.setCustomTitle(custom_title).setCancelable(false)
                    .setView(viewRoot)
                    .setNegativeButton(R.string.cancel, null)
                    .setView(viewRoot)
                    .setPositiveButton(R.string.lblAdd, null)

            val dialog = builder.create()
            dialog.show()
            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val txtTelephone = viewRoot.findViewById(R.id.txtTelephone) as EditText

                if (!UtilUI.validateEmptyFields(activity, txtTelephone)) {
                    telephone = txtTelephone.text.toString()
                    dialog.dismiss()
                    callbackPositiveAction.run()
                }
            }

        }

        fun getScheduleDialog(activity: Activity, callbackPositiveAction: Runnable) {

            val builder = AlertDialog.Builder(activity)
            // Get the layout inflater
            val inflater = activity.layoutInflater
            val viewRoot = inflater.inflate(R.layout.custom_dialog_schedule, null)
            val custom_title = inflater.inflate(R.layout.custom_title, null)
            ButterKnife.bind(viewRoot)

            BehaviorOfButtonSchedule(viewRoot)

            builder.setCustomTitle(custom_title).setCancelable(false)
                    .setView(viewRoot)
                    .setNegativeButton(R.string.cancel, null)
                    .setView(viewRoot)
                    .setPositiveButton(R.string.lblAdd, null)

            val dialog = builder.create()
            dialog.show()
            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { callbackPositiveAction.run() }

            scheduleDays.toString()

        }

        fun validateMultipleChoiceWithMessage(activity: Activity, vararg choices: Boolean): Boolean {
            val isValid = validateMultipleChoice(*choices)
            if (!isValid) {
                showToaststMessage(activity.getString(R.string.msErrorMultipleChoiceInvalid), activity)
            }

            return isValid
        }

        fun validateMultipleChoice(vararg choices: Boolean): Boolean {
            var isValid = false
            for (choice in choices) {
                if (choice) {
                    isValid = true
                    break
                }
            }

            return isValid
        }

        fun showToaststMessage(strTexto: String, context: Context) {
            val strNoInternet = strTexto
            Toast.makeText(context, strNoInternet, Toast.LENGTH_LONG).show()

        }

        fun hideSoftKeyBoard(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(activity.window.currentFocus!!.windowToken, 0)
        }

        fun validateEmptyFields(context: Context, vararg txts: EditText): Boolean {
            var isCampoVacio = false
            for (itemTxt in txts) {
                if ("" == itemTxt.text.toString().trim { it <= ' ' }) {
                    val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
                    itemTxt.startAnimation(shake)
                    itemTxt.error = context.getString(R.string.msErrorEmptyField)
                    itemTxt.setText("")
                    isCampoVacio = true
                } else {
                    itemTxt.error = null
                }
            }
            return isCampoVacio

        }

        fun showAlertDialog(context: Context, title: String, message: String, stringButton: Int, callBackaPositiveButton: Runnable?) {
            var alertDialogBuilder: AlertDialog.Builder? = AlertDialog.Builder(
                    context)

            // set title
            alertDialogBuilder!!.setTitle(title)

            // set dialog message
            alertDialogBuilder
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(stringButton) { dialog, id ->
                        // if this button is clicked
                        callBackaPositiveButton?.run()
                    }

            // create alert dialog
            val alertDialog = alertDialogBuilder.create()

            // show it
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
            alertDialogBuilder = null
        }

        fun hasConnection(context: Context): Boolean {
            val cm = context.getSystemService(
                    Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var isConnection = false
            var wifiNetwork: NetworkInfo? = null
            try {
                wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            } catch (e: Exception) {
                println(e)
            }

            if (wifiNetwork != null && wifiNetwork.isConnected) {
                isConnection = true
            }
            var mobileNetwork: NetworkInfo? = null
            try {
                mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            } catch (e: Exception) {
                println(e)
            }

            if (mobileNetwork != null && mobileNetwork.isConnected) {
                isConnection = true
            }
            var activeNetwork: NetworkInfo? = null
            try {
                activeNetwork = cm.activeNetworkInfo
            } catch (e: Exception) {
                println(e)
            }

            if (activeNetwork != null && activeNetwork.isConnected) {
                isConnection = true
            }

            return isConnection
        }

        fun validateInternetConnetion(context: Context): Boolean {
            var connection = false
            if (hasConnection(context)) {
                connection = true
            } else {


                showAlertDialog(context, context.getString(R.string.message), context.getString(R.string.msInternetNoConnection), R.string.ok, null)
            }
            return connection
        }

        fun getBitmapFromURL(src: String?): Bitmap? {
            try {
                Log.e("src", src)
                val url = URL(src)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)
                Log.e("Bitmap", "returned")
                return myBitmap
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("Exception", e.message)
                return null
            }

        }

        fun BehaviorOfButtonSchedule(viewRoot: View) {
            val btnD = viewRoot.findViewById(R.id.btnD) as Button
            val btnL = viewRoot.findViewById(R.id.btnL) as Button
            val btnMa = viewRoot.findViewById(R.id.btnMa) as Button
            val btnMi = viewRoot.findViewById(R.id.btnMi) as Button
            val btnJ = viewRoot.findViewById(R.id.btnJ) as Button
            val btnV = viewRoot.findViewById(R.id.btnV) as Button
            val btnS = viewRoot.findViewById(R.id.btnS) as Button
            btnD.setOnClickListener {
                if (btnD.tag == 1) {
                    btnD.background = viewRoot.resources.getDrawable(R.drawable.button_bg_normal)
                    btnD.tag = 2
                } else {
                    btnD.background = viewRoot.resources.getDrawable(R.drawable.button_bg_selected)
                    scheduleDays.append("D")
                    btnD.tag = 1
                }
            }
            btnL.setOnClickListener {
                if (btnL.tag == 1) {
                    btnL.background = viewRoot.resources.getDrawable(R.drawable.button_bg_normal)
                    scheduleDays.deleteCharAt(scheduleDays.length - 1)
                    btnL.tag = 2
                } else {
                    btnL.background = viewRoot.resources.getDrawable(R.drawable.button_bg_selected)
                    scheduleDays.append("L")
                    btnL.tag = 1
                }
            }
            btnMa.setOnClickListener {
                if (btnMa.tag == 1) {
                    btnMa.background = viewRoot.resources.getDrawable(R.drawable.button_bg_normal)
                    scheduleDays.deleteCharAt(scheduleDays.length - 1)
                    btnMa.tag = 2
                } else {
                    btnMa.background = viewRoot.resources.getDrawable(R.drawable.button_bg_selected)
                    scheduleDays.append("M")
                    btnMa.tag = 1
                }
            }
            btnMi.setOnClickListener {
                if (btnMi.tag == 1) {
                    btnMi.background = viewRoot.resources.getDrawable(R.drawable.button_bg_normal)
                    scheduleDays.deleteCharAt(scheduleDays.length - 1)
                    btnMi.tag = 2
                } else {
                    btnMi.background = viewRoot.resources.getDrawable(R.drawable.button_bg_selected)
                    scheduleDays.append("M")
                    btnMi.tag = 1
                }
            }
            btnJ.setOnClickListener {
                if (btnJ.tag == 1) {
                    btnJ.background = viewRoot.resources.getDrawable(R.drawable.button_bg_normal)
                    scheduleDays.deleteCharAt(scheduleDays.length - 1)
                    btnJ.tag = 2
                } else {
                    btnJ.background = viewRoot.resources.getDrawable(R.drawable.button_bg_selected)
                    scheduleDays.append("J")
                    btnJ.tag = 1
                }
            }
            btnV.setOnClickListener {
                if (btnV.tag == 1) {
                    btnV.background = viewRoot.resources.getDrawable(R.drawable.button_bg_normal)
                    scheduleDays.deleteCharAt(scheduleDays.length - 1)
                    btnV.tag = 2
                } else {
                    btnV.background = viewRoot.resources.getDrawable(R.drawable.button_bg_selected)
                    scheduleDays.append("V")
                    btnV.tag = 1
                }
            }
            btnS.setOnClickListener {
                if (btnS.tag == 1) {
                    btnS.background = viewRoot.resources.getDrawable(R.drawable.button_bg_normal)
                    scheduleDays.deleteCharAt(scheduleDays.length - 1)
                    btnS.tag = 2
                } else {
                    btnS.background = viewRoot.resources.getDrawable(R.drawable.button_bg_selected)
                    scheduleDays.append("S")
                    btnS.tag = 1
                }
            }

        }
    }
}
