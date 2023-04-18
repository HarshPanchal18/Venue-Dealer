package com.example.book_venue.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.book_venue.R
import com.example.book_venue.adapters.ImageAdapter
import com.example.book_venue.databinding.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.regex.Pattern
import kotlin.random.Random

class AddVenueActivity : AppCompatActivity() {

    private lateinit var summaryResult: HashMap<String, Any>
    private lateinit var imgdata: HashMap<String, Any>
    private lateinit var venue_types: ArrayList<String>
    private lateinit var imgUris: ArrayList<Uri>
    private lateinit var imgURLs: ArrayList<String>
    private var urls: ArrayList<Map<String, String>>? = null
    private lateinit var docId: String

    private lateinit var binding: ActivityAddVenueBinding
    private var bundle: Bundle? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore

    private lateinit var mStorageRef: StorageReference
    private lateinit var mStorage: FirebaseStorage
    private lateinit var documentRef: DocumentReference

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVenueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)

        initializeObjects()
        verifyInputs()

        bundle = intent.extras

        binding.apply {
            if (bundle != null) {
                docId = bundle!!.getString("docId").toString()

                toolbarText.text = "Update " + bundle!!.getString("name")
                addUpdateVenueBtn.text = "Update Venue"
                venueTitle.setText(bundle!!.getString("name"))
                venueDescription.setText(bundle!!.getString("description"))
                venueLandmark.setText(bundle!!.getString("landmark"))
                dealerPhNo.setText(bundle!!.getString("dealerPh"))
                rentPrice.setText(bundle!!.getString("rentHour"))
                venueCapacity.setText(bundle!!.getString("capacity"))
                restRooms.setText(bundle!!.getString("restRooms"))
                additionalTextbox.setText(bundle!!.getString("notes"))

                if (bundle!!.getString("parking") != "Yes") parkingNo.isChecked = true
                else parkingYes.isChecked = true

                if (bundle!!.getString("available") != "Yes") dayTimeAvailability.isChecked = false

                val spinnerStateValue: Int = when (bundle?.getString("state")) {
                    "Maharashtra" -> 1
                    "Rajasthan" -> 2
                    else -> 0 // Gujarat
                }
                spinnerState.setSelection(spinnerStateValue)

                val bundledCity = bundle!!.getString("city")
                val cityIndex: Int = when (spinnerStateValue) {
                    0 -> resources.getStringArray(R.array.gj_cities).sorted().indexOf(bundledCity)
                    1 -> resources.getStringArray(R.array.mh_cities).sorted().indexOf(bundledCity)
                    2 -> resources.getStringArray(R.array.rj_cities).sorted().indexOf(bundledCity)
                    else -> 0
                }
                spinnerCity.postDelayed({
                    spinnerCity.setSelection(cityIndex)
                }, 500)

                // Following is the Alternative trick of -> if (bundle!!.getString("types")!!.contains("Party")) party.isChecked = true
                // Pair (A,B) - Represents a generic pair of two values.
                // There is no meaning attached to values in this class, it can be used for any purpose.
                val checkBoxLists = arrayOf(
                    Pair(convHall, "Convention Hall"),
                    Pair(sports, "Sports"),
                    Pair(party, "Party"),
                    Pair(wedding, "Wedding"),
                    Pair(exhibition, "Exhibition"),
                    Pair(festivity, "Festivity"))

                val types = bundle!!.getString("types")
                checkBoxLists.forEach { (checkbox, label) ->
                    when {
                        types?.contains(label) == true -> checkbox.isChecked = true
                    }
                    //For each checkbox and label pair, we check if the types string contains the label, and set the checkbox's isChecked property accordingly.
                }
            } // end of if(bundle!=null)

            addUpdateVenueBtn.setOnClickListener {
                if (validateAndBind()) {
                    if (isOnline()) { // checking connectivity
                        if (bundle != null) {
                            setHashMapForUpdate()
                            if (bundle?.getString("url0")
                                    .isNullOrEmpty() && urls?.isNotEmpty() == true
                            )
                                summaryResult["url0"] = urls?.first()?.get("url") ?: ""

                            updateToFireStore(docId, summaryResult)
                        } else {
                            createNewVenue(summaryResult)
                        } // if(bundle!=null)
                    } else {
                        try {
                            showErrorDialog("You\\'re not connected with Internet! Check your connection and retry.")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } // end of isOnline()
                } else {
                    showErrorDialog("Empty fields are not allowed :(")
                } // end of if(validateAndBind())...else
            } // end of Listener

            chooseImageBtn.setOnClickListener { checkPermissionAndGo() }

            clearImageBtn.setOnClickListener {
                imgUris.clear()
                selectedImagesRview.adapter?.notifyDataSetChanged()
                selectedImagesRview.visibility = View.GONE
                clearImageBtn.visibility = View.GONE
            }
        } // end of binding
    } // end of onCreate()

    private fun checkPermissionAndGo() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                2)
        }
        showInfoDialog("Pay attention\nYou can\'t delete the photos once uploaded.")
    }

    private fun createNewVenue(summaryResult: HashMap<String, Any>) {
        if (bundle?.getString("url0").isNullOrEmpty() && urls?.isNotEmpty() == true)
            summaryResult["url0"] = urls?.first()?.get("url") ?: ""

        if (urls?.isNotEmpty() == true)
            summaryResult["images"] = FieldValue.arrayUnion(*(urls!!.toTypedArray()))

        summaryResult["docId"] = documentRef.id
        documentRef.set(summaryResult)
            .addOnSuccessListener { showSuccessDialog("Venue is created") }
            .addOnFailureListener { showErrorDialog(it.message.toString()) }
    }

    private fun updateToFireStore(currentDocId: String, updateList: HashMap<String, Any>) {

        val builder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val wbinding: WarningDialogBinding = WarningDialogBinding.bind(LayoutInflater.from(this)
            .inflate(R.layout.warning_dialog,
                findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

        builder.setView(wbinding.root)
        wbinding.textTitle.text = resources.getString(R.string.warning_title)
        wbinding.textMessage.text = resources.getString(R.string.continue_text)
        wbinding.buttonYes.text = resources.getString(R.string.yes)
        wbinding.buttonNo.text = resources.getString(R.string.no)
        wbinding.imageIcon.setImageResource(R.drawable.warning)

        val alertDialog = builder.create()
        wbinding.buttonYes.setOnClickListener {
            alertDialog.dismiss()
            if (validateAndBind()) {
                if (urls?.isNotEmpty() == true) {
                    firestore.collection("venue")
                        .document(currentDocId)
                        .update("images", FieldValue.arrayUnion(*(urls!!.toTypedArray())))
                }
                firestore.collection("venue")
                    .document(currentDocId).update(updateList)
                    .addOnSuccessListener { showSuccessDialog("Venue updated successfully") }
                    .addOnFailureListener { e -> showErrorDialog(e.message.toString()) }
            }
        }
        wbinding.buttonNo.setOnClickListener {
            alertDialog.dismiss()
        }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun openFileChooser() {
        val intent = Intent().apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Images"), 1)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data?.clipData != null) {
                // Get the URIs of the selected images
                imgUris = ArrayList()
                for (i in 0 until data.clipData!!.itemCount) {
                    val item = data.clipData!!.getItemAt(i)
                    imgUris.add(item.uri)
                }

                binding.selectedImagesRview.visibility = View.VISIBLE
                binding.clearImageBtn.visibility = View.VISIBLE

                val adapter = ImageAdapter(imgUris)
                binding.selectedImagesRview.adapter = adapter

                for (i in 0 until imgUris.size) {
                    val imageRef =
                        mStorageRef.child("images/venue/${System.currentTimeMillis()}_${Random(10000)}_$i.jpg")
                    imageRef.putFile(imgUris[i])
                        .addOnSuccessListener {
                            binding.imageProgress.visibility = View.VISIBLE
                            // Get the URL of the uploaded image
                            imageRef.downloadUrl.addOnSuccessListener { url ->
                                imgURLs.add(url.toString())
                                urls = ArrayList(imgURLs.map { c -> mapOf("url" to c) })
                                binding.imageProgress.visibility = View.INVISIBLE
                            }
                        }
                }
            }
        }
    } // end of onActivityResult()

    private fun validateAndBind(): Boolean {

        binding.apply {

            val name = venueTitle.text.toString()
            val description = venueDescription.text.toString()
            val landmark = venueLandmark.text.toString()
            val city = spinnerCity.selectedItem.toString()
            val state = spinnerState.selectedItem.toString()
            val capacity = venueCapacity.text.toString()
            val dealerContact = dealerPhNo.text.toString()
            val availability = if (dayTimeAvailability.isChecked) "Yes" else "No"
            val parkingAvailability = if (parkingYes.isChecked) "Yes" else "No"
            val rentPerHour = rentPrice.text.toString()
            val restRooms = restRooms.text.toString()

            val checkBoxes = listOf(convHall, wedding, festivity, party, exhibition, sports)

            for (checkBox in checkBoxes) {
                if (checkBox.isChecked)
                    venue_types.add(checkBox.text.toString())
            }

            if (
                (name.isNotEmpty()
                        && description.isNotEmpty()
                        && landmark.isNotEmpty()
                        && city.isNotEmpty()
                        && state.isNotEmpty()
                        && capacity.isNotEmpty()
                        && dealerContact.isNotEmpty()
                        && rentPerHour.isNotEmpty()
                        && restRooms.isNotEmpty()
                        //&& imgUris.isNotEmpty()
                        )
                &&
                (wedding.isChecked
                        || festivity.isChecked
                        || convHall.isChecked
                        || party.isChecked
                        || exhibition.isChecked
                        || sports.isChecked
                        )
            ) {
                summaryResult.apply {
                    put("Name", name)
                    put("Description", description)
                    put("Landmark", landmark)
                    put("City", city)
                    put("State", state)
                    put("VenueCapacity", capacity)
                    put("DealerContact", dealerContact)
                    put("Types", venue_types.toSet().toString())
                    put("RentPerHour", rentPerHour)
                    put("RestRooms", restRooms)
                    put("Parking", parkingAvailability)
                    put("Availability", availability)
                    put("userId", user.uid)

                    if (additionalTextbox.text.toString().isNotEmpty())
                        put("Notes", additionalTextbox.text.toString())
                }
                return true
            }
            return false
        } // end of binding.apply{}
    } // end of validateAndBind()

    private fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network =
            connectivityManager.activeNetwork // returns a 'Network' object representing the currently active network.
        // retrieve the network capabilities using the getNetworkCapabilities() method which returns a NetworkCapabilities object that provides information about the network.
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        // NET_CAPABILITY_INTERNET capability, indicates that the device has an internet connection available or not
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun showSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val sbinding: SuccessDialogBinding = SuccessDialogBinding.bind(LayoutInflater.from(this)
            .inflate(R.layout.success_dialog,
                this.findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

        builder.setView(sbinding.root)
        sbinding.textTitle.text = resources.getString(R.string.success_title)
        sbinding.textMessage.text = message
        sbinding.buttonAction.text = resources.getString(R.string.okay)
        sbinding.imageIcon.setImageResource(R.drawable.done)

        val alertDialog = builder.create()
        sbinding.buttonAction.setOnClickListener {
            alertDialog.dismiss()
            finish()
        }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val ebinding: ErrorDialogBinding = ErrorDialogBinding.bind(LayoutInflater.from(this)
            .inflate(R.layout.error_dialog,
                findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

        builder.setView(ebinding.root)
        ebinding.textTitle.text = resources.getString(R.string.network_error_title)
        ebinding.textMessage.text = message
        ebinding.buttonAction.text = resources.getString(R.string.okay)
        ebinding.imageIcon.setImageResource(R.drawable.error)

        val alertDialog = builder.create()
        ebinding.buttonAction.setOnClickListener { alertDialog.dismiss() }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showInfoDialog(message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val ebinding: InfoDialogBinding = InfoDialogBinding.bind(LayoutInflater.from(this)
            .inflate(R.layout.info_dialog,
                findViewById<ConstraintLayout>(R.id.layoutDialogContainer)))

        builder.setView(ebinding.root)
        ebinding.textTitle.text = resources.getString(R.string.info_title)
        ebinding.textMessage.text = message
        ebinding.buttonAction.text = resources.getString(R.string.i_understand)

        val alertDialog = builder.create()
        ebinding.buttonAction.setOnClickListener {
            alertDialog.dismiss()
            openFileChooser()
        }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun initializeObjects() {

        // firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mStorageRef = mStorage.reference

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore.firestoreSettings = settings

        user = auth.currentUser!!

        summaryResult = HashMap()
        imgdata = HashMap()
        urls = ArrayList()
        venue_types = ArrayList()
        imgUris = ArrayList()
        imgURLs = ArrayList()
        documentRef = firestore.collection("venue").document()

        // adapting spinners
        val spinStates = resources.getStringArray(R.array.states)
        val adapterStates = ArrayAdapter(applicationContext, R.layout.dropdown_item, spinStates)
        binding.spinnerState.adapter = adapterStates

        binding.spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(adapter: AdapterView<*>?) {}

            override fun onItemSelected(
                adapter: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {

                var adapterCity = ArrayAdapter(applicationContext,
                    R.layout.dropdown_item, resources.getStringArray(R.array.gj_cities).sorted())
                when (position) {
                    0 -> {}
                    1 -> {
                        adapterCity = ArrayAdapter(applicationContext,
                            R.layout.dropdown_item,
                            resources.getStringArray(R.array.mh_cities).sorted())
                    }
                    2 -> {
                        adapterCity = ArrayAdapter(applicationContext,
                            R.layout.dropdown_item,
                            resources.getStringArray(R.array.rj_cities).sorted())
                    }
                }
                binding.spinnerCity.adapter = adapterCity
                adapterCity.notifyDataSetChanged()
            }
        }

        // Image adapter for selected images
        val adapter = ImageAdapter(ArrayList())
        binding.selectedImagesRview.adapter = adapter
        binding.selectedImagesRview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.goBack.setOnClickListener { finish() }

        if (user.email.toString() != null)
            binding.dealerPhNo.setText(user.email.toString())
    }

    private fun mailMobileValidate(text: String?): Boolean {

        val pattern =
            Pattern.compile("^(?:(?:\\+|0{0,2})91([\\- ])?|[0]?)?[6789]\\d{9}\$|^[\\w.%+-]+@[A-Za-z0-9]+(?:[.-][A-Za-z0-9]+)*\\.[A-Za-z]{2,}\$")

        /*
        ^ indicates the start of the string
        (?:(?:\+|0{0,2})91([\- ])?|[0]?)? matches the optional country code for India, followed by an optional hyphen or space separator
        [6789]\d{9} matches a 10-digit phone number starting with 6, 7, 8, or 9
        | indicates an OR condition
        ^[\w.%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$ matches an email address with alphanumeric characters and some special characters, followed by an @ symbol, domain name with alphanumeric characters, hyphens, and periods, and a top-level domain name with two or more characters.
        $ indicates the end of the string
        */

        val m = pattern.matcher(text!!)
        return m.matches()
    }

    private fun hasMultipleWords(text: String?): Boolean {
        val pattern = Pattern.compile("^[\\w\\d, ]+\$")
        val m = pattern.matcher(text!!)
        return m.matches()
    }

    private fun verifyInputs() {

        binding.dealerPhNo.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mailMobileValidate(binding.dealerPhNo.text.toString()))
                    binding.addUpdateVenueBtn.isEnabled = true
                else {
                    binding.addUpdateVenueBtn.isEnabled = false
                    binding.dealerPhNo.error = "Invalid input"
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.venueLandmark.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (hasMultipleWords(binding.venueLandmark.text.toString()))
                    binding.addUpdateVenueBtn.isEnabled = true
                else {
                    binding.addUpdateVenueBtn.isEnabled = false
                    binding.venueLandmark.error = "Add detailed address"
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setHashMapForUpdate() {

        binding.apply {

            val checkBoxes = listOf(convHall, wedding, festivity, party, exhibition, sports)

            for (checkBox in checkBoxes) {
                if (checkBox.isChecked)
                    venue_types.add(checkBox.text.toString())
            }

            summaryResult.apply {
                put("Name", venueTitle.text.toString())
                put("Description", venueDescription.text.toString())
                put("Landmark", venueLandmark.text.toString())
                put("City", spinnerCity.selectedItem.toString())
                put("State", spinnerState.selectedItem.toString())
                put("VenueCapacity", venueCapacity.text.toString())
                put("DealerContact", dealerPhNo.text.toString())
                put("Types", venue_types.toSet().toString())
                put("RentPerHour", rentPrice.text.toString())
                put("RestRooms", restRooms.text.toString())
                put("Parking",
                    if (parkingYes.isChecked) "Yes" else "No")
                put("Availability",
                    if (dayTimeAvailability.isChecked) "Yes" else "No")

                if (additionalTextbox.text.toString().isNotEmpty())
                    put("Notes", additionalTextbox.text.toString())
            }
        } // end of binding.apply{}
    } // end of setHashmapForUpdate()

}
