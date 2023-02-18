package com.example.book_venue

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_venue.*
import kotlin.random.Random

class AddVenueActivity : AppCompatActivity() {

    private lateinit var summaryResult: HashMap<String,Any>
    private lateinit var venue_types:ArrayList<String>
    private lateinit var imgUris:ArrayList<Uri>
    private lateinit var docId:String
    private var bundle: Bundle? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore:FirebaseFirestore

    private lateinit var mStorageRef: StorageReference
    private lateinit var mStorage: FirebaseStorage
    private lateinit var documentRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_venue)
        supportActionBar?.hide()

        auth=FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()
        mStorage=FirebaseStorage.getInstance()
        mStorageRef=mStorage.reference

        user= auth.currentUser!!

        summaryResult= HashMap()
        venue_types= ArrayList()
        imgUris= ArrayList()
        documentRef=firestore.collection("venue").document()

        bundle = intent?.extras!!
        if(bundle!=null){

            add_update_venue_btn.text = "Update Venue"
            venueTitle.setText(bundle!!.getString("name"))
            venueDescription.setText(bundle!!.getString("description"))
            venueLandmark.setText(bundle!!.getString("landmark"))
            dealerPhNo.setText(bundle!!.getString("dealerPh"))
            rentPrice.setText(bundle!!.getString("rentHour"))
            venueCapacity.setText(bundle!!.getString("capacity"))
            restRooms.setText(bundle!!.getString("restRooms"))

            docId= bundle!!.getString("docId").toString()

            if(bundle!!.getString("parking") != "Yes")
                parkingToggle.isChecked=false

            if(bundle!!.getString("available") != "Yes")
                dayTimeAvailability.isChecked=false

            if(bundle!!.getString("types")!!.contains("Convention Hall"))
                convHall.isChecked=true
            if(bundle!!.getString("types")!!.contains("Sports"))
                sports.isChecked=true
            if(bundle!!.getString("types")!!.contains("Exhibition"))
                exhibition.isChecked=true
            if(bundle!!.getString("types")!!.contains("Wedding"))
                wedding.isChecked=true
            if(bundle!!.getString("types")!!.contains("Festivity"))
                festivity.isChecked=true
            if(bundle!!.getString("types")!!.contains("Party"))
                party.isChecked=true

            chooseImage.visibility=View.INVISIBLE
            clearImage.visibility=View.INVISIBLE
            selected_images_Rview.visibility=View.INVISIBLE
        }

        add_update_venue_btn.setOnClickListener {
            if(validateAndBind()) {
                if(isOnline()) {
                    if(bundle!=null) {

                        if(convHall.isChecked) venue_types.add(convHall.text.toString())
                        if(wedding.isChecked) venue_types.add(wedding.text.toString())
                        if(festivity.isChecked) venue_types.add(festivity.text.toString())
                        if(party.isChecked) venue_types.add(party.text.toString())
                        if(exhibition.isChecked) venue_types.add(exhibition.text.toString())
                        if(sports.isChecked) venue_types.add(sports.text.toString())

                        summaryResult.clear()
                        summaryResult.apply {
                            put("Name",venueTitle.text)
                            put("Description",venueDescription.text.toString())
                            put("Landmark",venueLandmark.text.toString())
                            put("City",spinnerCity.selectedItem.toString())
                            put("State",spinnerState.selectedItem.toString())
                            put("VenueCapacity",venueCapacity.text.toString())
                            put("DealerContact",dealerPhNo.text.toString())
                            put("Types",venue_types.toSet().toString())
                            put("RentPerHour",rentPrice.text.toString())
                            put("RestRooms",restRooms.text.toString())
                            put("Parking",if(parkingToggle.isChecked) "Yes" else "No")
                            put("Availability",if(dayTimeAvailability.isChecked) "Yes" else "No")
                        }
                        updateToFireStore(summaryResult)
                        finish()
                    } else {
                        documentRef.set(summaryResult)
                            .addOnSuccessListener { Toast.makeText(applicationContext,"Venue is Added :)",Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { Toast.makeText(applicationContext,it.message,Toast.LENGTH_SHORT).show() }
                        finish()
                    }
                } else {
                    try {
                        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
                        alertDialog.apply{
                            setTitle("Info")
                            setMessage("Internet not available, Cross check your internet connectivity and try again")
                            setIcon(android.R.drawable.ic_dialog_alert)
                            setButton("OK") { dialog, which -> /*finish()*/ }
                            show()
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }
            } else {
                Toast.makeText(this,"Empty fields are not allowed :(",Toast.LENGTH_SHORT).show()
            }
        }

        val adapter=ImageAdapter(ArrayList())
        selected_images_Rview.adapter=adapter
        selected_images_Rview.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        chooseImage.setOnClickListener { checkPermissionAndGo() }

        clearImage.setOnClickListener {
            imgUris.clear()
            selected_images_Rview?.adapter?.notifyDataSetChanged()
        }

        val spinStates=resources.getStringArray(R.array.states)
        val adapter2= ArrayAdapter(applicationContext,R.layout.dropdown_item,spinStates)
        spinnerState.adapter = adapter2

        spinnerState.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(adapter: AdapterView<*>?) {}

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {

                var adapter1 = ArrayAdapter(applicationContext,R.layout.dropdown_item,resources.getStringArray(R.array.gj_cities).sorted())
                if(position==0){
                    spinnerCity.adapter = adapter1
                }
                if(position==1){
                    adapter1 = ArrayAdapter(applicationContext,R.layout.dropdown_item,resources.getStringArray(R.array.mh_cities).sorted())
                    spinnerCity.adapter = adapter1
                }
                if(position==2){
                    adapter1 = ArrayAdapter(applicationContext,R.layout.dropdown_item,resources.getStringArray(R.array.rj_cities).sorted())
                    spinnerCity.adapter = adapter1
                }
                adapter1.notifyDataSetChanged()
            }
        }

    }

    private fun checkPermissionAndGo() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),2)
        }
        openFileChooser()
    }

    private fun updateToFireStore(updateList:HashMap<String,Any>) {
        if(validateAndBind()){
            val query=firestore.collection("venue").whereEqualTo("docId",docId)
                query.get().addOnSuccessListener { querySnapshot ->
                    val batch=firestore.batch()
                    for(doc in querySnapshot) {
                        val docRef=firestore.collection("venue").document(doc.id)
                        batch.update(docRef,updateList)
                    }
                    batch.commit()
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Data Updated!!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this,
                            "Error : " + task.exception?.message,
                            Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openFileChooser() {
        val intent = Intent().apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Images"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK ) {
            if(data?.clipData!=null) {
                // Get the URIs of the selected images
                imgUris=ArrayList()
                for(i in 0 until data.clipData!!.itemCount) {
                    val item=data.clipData!!.getItemAt(i)
                    imgUris.add(item.uri)
                }

                val adapter=ImageAdapter(imgUris)
                selected_images_Rview.adapter=adapter

            for(i in 0 until imgUris.size) {
                val imageRef=mStorageRef.child("images/venue/${System.currentTimeMillis()}_${Random(10000)}_$i.jpg")
                val uploadTask=imageRef.putFile(imgUris[i])
                    .addOnSuccessListener {
                        // Get the URL of the uploaded image
                        imageRef.downloadUrl.addOnSuccessListener { url ->
                            val imgdata= hashMapOf("url$i" to url.toString())
                            summaryResult.putAll(imgdata)
                        }
                    }
                }
            }
        }
    }

    private fun validateAndBind() : Boolean {

        val name = venueTitle.text.toString()
        val description = venueDescription.text.toString()
        val landmark = venueLandmark.text.toString()
        val city = spinnerCity.selectedItem.toString()
        val state = spinnerState.selectedItem.toString()
        val capacity = venueCapacity.text.toString()
        val dealerContact = dealerPhNo.text.toString()
        val availability = if(dayTimeAvailability.isChecked) "Yes" else "No"
        val parkingAvailability = if(parkingToggle.isChecked) "Yes" else "No"
        val rentPerHour = rentPrice.text.toString()
        val restRooms = restRooms.text.toString()

        if(convHall.isChecked) venue_types.add(convHall.text.toString())
        if(wedding.isChecked) venue_types.add(wedding.text.toString())
        if(festivity.isChecked) venue_types.add(festivity.text.toString())
        if(party.isChecked) venue_types.add(party.text.toString())
        if(exhibition.isChecked) venue_types.add(exhibition.text.toString())
        if(sports.isChecked) venue_types.add(sports.text.toString())

        if((name.isNotEmpty() &&
                    description.isNotEmpty() &&
                    landmark.isNotEmpty() &&
                    city.isNotEmpty() &&
                    state.isNotEmpty() &&
                    capacity.isNotEmpty() &&
                    dealerContact.isNotEmpty() &&
                    rentPerHour.isNotEmpty() &&
                    restRooms.isNotEmpty()
                    //imgUris.isNotEmpty()
                    )
            && (
                    wedding.isChecked ||
                    festivity.isChecked ||
                    convHall.isChecked ||
                    party.isChecked ||
                    exhibition.isChecked ||
                    sports.isChecked
                    )
        ){
            summaryResult.apply {
                put("Name",name)
                put("Description",description)
                put("Landmark",landmark)
                put("City",city)
                put("State",state)
                put("VenueCapacity",capacity)
                put("DealerContact",dealerContact)
                put("Types",venue_types.toSet().toString())
                put("RentPerHour",rentPerHour)
                put("RestRooms",restRooms)
                put("Parking",parkingAvailability)
                put("Availability",availability)
                put("userId",user.uid)
                put("docId",documentRef.id)
            }

            return true
        }
        return false
    }

    private fun clearFields(){

        venueTitle.text.clear()
        venueDescription.text.clear()
        venueLandmark.text.clear()
        dealerPhNo.text.clear()
        venueCapacity.text.clear()
        spinnerCity.adapter=null // reset
        spinnerState.adapter=null
        rentPrice.text.clear()
        restRooms.text.clear()

        convHall.isChecked=false
        sports.isChecked=false
        exhibition.isChecked=false
        wedding.isChecked=false
        festivity.isChecked=false
        party.isChecked=false
        dayTimeAvailability.isChecked=true
        parkingToggle.isChecked=true

        summaryResult.clear()
        venue_types.clear()
        imgUris.clear()
        selected_images_Rview?.adapter?.notifyDataSetChanged()
    }

    private fun isOnline(): Boolean {
        val connManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connManager.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            return false
        }
        return true
    }
}
