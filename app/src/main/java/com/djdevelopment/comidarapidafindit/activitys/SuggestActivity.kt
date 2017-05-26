package com.djdevelopment.comidarapidafindit.activitys

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.ToggleButton

import com.djdevelopment.comidarapidafindit.R
import com.djdevelopment.comidarapidafindit.data.Image
import com.djdevelopment.comidarapidafindit.data.MenuService
import com.djdevelopment.comidarapidafindit.data.Restaurants
import com.djdevelopment.comidarapidafindit.tools.UtilUI
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList

import butterknife.BindView

class SuggestActivity : AppCompatActivity() {

    internal val context: Context = this
    private var menuServices: ArrayList<MenuService> = ArrayList()
    internal var inflaterItem: LayoutInflater? = null
    private val custom_font2: Typeface? = null
    internal var cardViewServices: LinearLayout? = null
    private var telephones: ArrayList<String>? = null
    private var txtName: EditText? = null
    private var creditCards: ArrayList<String>? = null
    private var imagesList: ArrayList<Image>? = null
    private var selectMotelLatLng: LatLng? = null
    internal var cardViewImages: LinearLayout? = null
    internal var cardViewServicesTelephons: LinearLayout? = null
    internal var cardViewCreditCards: LinearLayout? = null
    internal var cardViewLocation: LinearLayout? = null
    internal var cardViewSchedule: LinearLayout? = null
    internal var toogleDelivery: ToggleButton? = null
    internal var key: String = ""
    internal var urlImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        telephones = ArrayList<String>()
        creditCards = ArrayList<String>()
        imagesList = ArrayList<Image>()

        cardViewServices = findViewById(R.id.cardViewServicesPrices) as LinearLayout
        cardViewServicesTelephons = findViewById(R.id.cardViewServicesTelephons) as LinearLayout
        cardViewImages = findViewById(R.id.cardViewImages) as LinearLayout
        cardViewCreditCards = findViewById(R.id.cardViewCreditCards) as LinearLayout
        cardViewLocation = findViewById(R.id.cardViewLocation) as LinearLayout
        cardViewSchedule = findViewById(R.id.cardViewSchedule) as LinearLayout
        toogleDelivery = findViewById(R.id.toogleDelivery) as ToggleButton

        menuServices = ArrayList<MenuService>()

        inflaterItem = this@SuggestActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        cardViewServices = findViewById(R.id.cardViewServicesPrices) as LinearLayout
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        txtName = findViewById(R.id.txtName) as EditText
        val btnSend = findViewById(R.id.btnSend) as Button

        initComponents()

        btnSend.setOnClickListener(View.OnClickListener {
            //Nombre de restaurante
            val restName: String = txtName!!.text.toString()
            val creditCars: String
            val telephonesArray: String

            val database = FirebaseDatabase.getInstance()
            val myRef = database.reference

            //Localizacion de restaurante
            var LatLngCoord = ""
            if (selectMotelLatLng != null) {
                LatLngCoord = selectMotelLatLng!!.toString()
            }
            if (LatLngCoord.isEmpty()) {
                UtilUI.showAlertDialog(this@SuggestActivity, getString(R.string.location_info), getString(R.string.select_location_info), R.string.iGotIt, null)
                this@SuggestActivity.findViewById(R.id.lblLocation).requestFocus()
                val sv = this@SuggestActivity.findViewById(R.id.ScrollViewSujectMotel) as ScrollView
                sv.scrollTo(0, sv.top)
                return@OnClickListener
            }
            //Menu de restaurante
            if (UtilUI.validateInternetConnetion(this@SuggestActivity)) {

                val resMenuArray = menuServices!!.mapTo(ArrayList<String>()) { "{\"name\": \"" + it.menuName + "\", \"price\": \"" + it.price + "\", \"currency\": \"" + it.currencyType + "\"}" }


                creditCars = TextUtils.join(", ", creditCards!!.toTypedArray())
                telephonesArray = TextUtils.join(", ", telephones!!.toTypedArray())

                val restaurants = Restaurants(restName, resMenuArray, LatLngCoord, creditCars, telephonesArray, false, null, null, (toogleDelivery as ToggleButton).isChecked)

                key = myRef.push().key
                myRef.child("restaurants-suggest").child(key).setValue(restaurants)

                for (images in imagesList!!) {
                    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://comidarapida-cae88.appspot.com/")

                    val file = Uri.fromFile(File(images.url!!))

                    val spaceRef = storageRef.child("images/" + key + "/" + file.lastPathSegment)
                    val uploadTask = spaceRef.putFile(file)

                    uploadTask.addOnSuccessListener { taskSnapshot -> myRef.child("restaurants-suggest").child(key).child("urlImage").push().setValue("{ \"url\": \"" + taskSnapshot.downloadUrl!!.toString() + "\"}") }.addOnFailureListener { e -> e.printStackTrace() }.addOnProgressListener { }
                }

                UtilUI.showAlertDialog(this@SuggestActivity, this@SuggestActivity.getString(R.string.thanksContibution), this@SuggestActivity.getString(R.string.thanksForSubmitInfo), R.string.iGotIt, Runnable { this@SuggestActivity.finish() })


            }
        })
    }

    fun initComponents() {
        addNewFastFood()
        addNewServiceModel()
        suggestCreditCards()
        suggestTelephones()
        suggetImage()
        suggestSchedule()
    }

    private fun addNewFastFood() {

        findViewById(R.id.btnAddNewFastFood).setOnClickListener {
            val intent = Intent(this@SuggestActivity, LocationPickerActivity::class.java)
            startActivityForResult(intent, 11)
        }
    }

    private fun addNewServiceModel() {
        findViewById(R.id.btnAddNewServiceModel).setOnClickListener {
            UtilUI.getMenuServiceDialog(this@SuggestActivity, Runnable {
                val menuService = UtilUI.menuService
                menuServices.add(menuService)


                val viewPrices = inflaterItem!!.inflate(R.layout.item_layout_edit, null, true)
                val serviceName = viewPrices.findViewById(R.id.lblTimeOftheLastVote) as TextView
                val servicePrice = viewPrices.findViewById(R.id.service_value) as TextView
                var currency = ""
                try {
                    serviceName.text = menuService!!.menuName
                    currency = menuService.currencyType.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (currency == "0") {
                    currency = "RD"
                }

                servicePrice.text = String.format("$currency$%,3.2f", menuService!!.price)
                serviceName.typeface = custom_font2
                servicePrice.typeface = custom_font2

                cardViewServices!!.addView(viewPrices)


                servicePrice.setOnClickListener { v ->
                    val r = v.parent as RelativeLayout
                    val index = cardViewServices!!.indexOfChild(r)
                    cardViewServices!!.removeViewAt(index)
                    menuServices!!.removeAt(index)
                }
                UtilUI.menuService
                UtilUI.hideSoftKeyBoard(this@SuggestActivity)
            })

            UtilUI.hideSoftKeyBoard(this@SuggestActivity)
        }

    }

    private fun suggestCreditCards() {

        findViewById(R.id.btnSuggestCreditCards).setOnClickListener {
            val creditCard = ArrayList<String>()
            UtilUI.getCreditCardsDialog(this@SuggestActivity, creditCard, Runnable {
                for (creditCardValue in creditCard) {
                    if (creditCards!!.indexOf(creditCardValue) == -1) {
                        creditCards!!.add(creditCardValue)
                        val viewCreditCards = inflaterItem!!.inflate(R.layout.item_layout_edit, null, true)
                        val txtCreditCard = viewCreditCards.findViewById(R.id.lblTimeOftheLastVote) as TextView
                        val txtValue = viewCreditCards.findViewById(R.id.service_value) as TextView

                        txtCreditCard.text = creditCardValue
                        txtCreditCard.typeface = custom_font2

                        txtValue.setOnClickListener { v ->
                            val r = v.parent as RelativeLayout
                            val index = cardViewCreditCards!!.indexOfChild(r)
                            cardViewCreditCards!!.removeViewAt(index)
                            creditCards!!.removeAt(index)
                        }


                        cardViewCreditCards!!.addView(viewCreditCards)
                    }

                }
            })
        }
    }

    private fun suggestTelephones() {
        findViewById(R.id.btnSuggestedTelephons).setOnClickListener {
            UtilUI.getTelephoneDialog(this@SuggestActivity, Runnable {
                val hasFocus = cardViewServicesTelephons!!.findFocus()
                UtilUI.hideSoftKeyBoard(this@SuggestActivity)
                val phone = UtilUI.telephone
                telephones!!.add(phone)
                val viewPhones = inflaterItem!!.inflate(R.layout.item_layout_edit, null, true)
                val txtPhone = viewPhones.findViewById(R.id.lblTimeOftheLastVote) as TextView
                val txtValue = viewPhones.findViewById(R.id.service_value) as TextView

                txtPhone.text = phone

                txtValue.setOnClickListener { v ->
                    val r = v.parent as RelativeLayout
                    val index = cardViewServicesTelephons!!.indexOfChild(r)
                    cardViewServicesTelephons!!.removeViewAt(index)
                    telephones!!.removeAt(index)
                }


                cardViewServicesTelephons!!.addView(viewPhones)
            })
        }
    }

    private fun suggestSchedule() {
        findViewById(R.id.btnAddSchedule).setOnClickListener {
            UtilUI.getScheduleDialog(this@SuggestActivity, Runnable {
                UtilUI.hideSoftKeyBoard(this@SuggestActivity)

                val viewPhones = inflaterItem!!.inflate(R.layout.item_layout_edit, null, true)

                val scheduleDays = UtilUI.scheduleDays.toString()
                val txtSchedule = viewPhones.findViewById(R.id.lblTimeOftheLastVote) as TextView

                txtSchedule.text = scheduleDays

                txtSchedule.setOnClickListener { v ->
                    val r = v.parent as RelativeLayout
                    val index = cardViewSchedule!!.indexOfChild(r)
                    cardViewSchedule!!.removeViewAt(index)
                }
                cardViewSchedule!!.addView(viewPhones)
            })
        }
    }

    private fun suggetImage() {
        findViewById(R.id.btnAddImage).setOnClickListener {
            val intent: Intent

            if (Build.VERSION.SDK_INT < 19) {
                intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
            } else {
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
            }


            intent.type = "image/*"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            } else {
                intent.putExtra("android.intent.extra.LOCAL_ONLY", true)
            }

            startActivityForResult(intent, 10)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {


            if (data != null && data.data != null) {

                showInputDialogAndAddElement(data)


            }
        }

        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {

            if (data != null && data.extras.containsKey("userPosition")) {

                cardViewLocation!!.removeAllViews()
                selectMotelLatLng = null

                val selectMotelPosition = data.extras.get("userPosition") as LatLng
                selectMotelLatLng = selectMotelPosition

                val viewPrices = inflaterItem!!.inflate(R.layout.item_layout_edit, null, true)
                val serviceName = viewPrices.findViewById(R.id.lblTimeOftheLastVote) as TextView
                val iconImage = viewPrices.findViewById(R.id.iconService) as ImageView
                val address = data.extras.get("address") as String
                val servicePrice = viewPrices.findViewById(R.id.service_value) as TextView
                servicePrice.visibility = View.GONE

                iconImage.setImageResource(R.drawable.icons_03)

                serviceName.typeface = custom_font2
                serviceName.text = if (address != null && address.length != 0) address else "Lat " + selectMotelLatLng!!.latitude + " Lon " + selectMotelLatLng!!.longitude
                cardViewLocation!!.addView(viewPrices)

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // app icon in action bar clicked; go home
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun showInputDialogAndAddElement(data: Intent) {

        // get prompts.xml view
        val layoutInflater = LayoutInflater.from(context)
        val promptView = layoutInflater.inflate(R.layout.input_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(promptView)

        val editText = promptView.findViewById(R.id.editTextDescription) as TextView
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK") { dialog, id ->
                    //resultText.setText("Hello, " + editText.getText());

                    try {

                        urlImage = getPath(context, data.data)
                        val viewImages = inflaterItem!!.inflate(R.layout.item_layout_edit_images, null, true)
                        val imageItem = viewImages.findViewById(R.id.imageItem) as ImageView
                        val txtValue = viewImages.findViewById(R.id.service_value) as TextView
                        val txtDescription = viewImages.findViewById(R.id.imageDescription) as TextView
                        txtDescription.text = editText.text.toString()
                        txtValue.setOnClickListener { v ->
                            val r = v.parent as RelativeLayout
                            val index = cardViewImages!!.indexOfChild(r)
                            cardViewImages!!.removeViewAt(index)
                            imagesList!!.removeAt(index)
                        }


                        cardViewImages!!.addView(viewImages)

                        val stream = contentResolver.openInputStream(
                                data.data)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        stream!!.close()
                        //imageItem.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        imageItem.setImageBitmap(bitmap)
                        val imageObj = Image()
                        imageObj.url = urlImage
                        imageObj.description = editText.text.toString()
                        imagesList!!.add(imageObj)
                    } catch (e: FileNotFoundException) {
                        Log.i("photo", e.toString())
                    } catch (e: IOException) {
                        Log.i("photo", e.toString())
                    } catch (e: Exception) {
                        Log.i("photo", e.toString())
                    }
                }/*
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})*/

        // create an alert dialog
        val alert = alertDialogBuilder.create()
        alert.show()
    }

    companion object {

        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }

        fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } finally {
                if (cursor != null)
                    cursor.close()
            }
            return ""
        }

        @SuppressLint("NewApi")
        fun getPath(context: Context, uri: Uri): String {

            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                } else if (isDownloadsDocument(uri)) {

                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri = Uri.EMPTY
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }// MediaProvider
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.lastPathSegment

                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }// File
            // MediaStore (and general)

            return ""
        }
    }

}
