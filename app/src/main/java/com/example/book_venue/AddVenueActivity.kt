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

        addVenuebtn.setOnClickListener {
            if(validateAndBind()){
                if(isOnline()){
                    //documentRef = firestore.collection("venue").document()
                    documentRef.set(summaryResult)
                        .addOnSuccessListener {
                        Toast.makeText(applicationContext,"Venue is Added :)",Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener {
                        Toast.makeText(applicationContext,it.message,Toast.LENGTH_SHORT).show()
                    }
                    clearFields()
                    finish()
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

        autocompleteState.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(adapter: AdapterView<*>?) {}

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val cities: Array<String> =resources.getStringArray(R.array.gj_cities)+
                        resources.getStringArray(R.array.mh_cities)+
                        resources.getStringArray(R.array.rj_cities)

                val states=resources.getStringArray(R.array.states)

                val adapter1= ArrayAdapter(applicationContext,R.layout.dropdown_item,cities.sorted())
                autocompleteCity.setAdapter(adapter1)

                val adapter2= ArrayAdapter(applicationContext,R.layout.dropdown_item,states)
                autocompleteState.setAdapter(adapter2)

                if(position==0){
                    adapter1.clear()
                    adapter1.add(resources.getStringArray(R.array.gj_cities).toString())
                    autocompleteCity.setAdapter(adapter1)
                }
                if(position==1){
                    adapter1.clear()
                    adapter1.add(resources.getStringArray(R.array.mh_cities).toString())
                    autocompleteCity.setAdapter(adapter1)
                }
                if(position==2){
                    adapter1.clear()
                    adapter1.add(resources.getStringArray(R.array.rj_cities).toString())
                    autocompleteCity.setAdapter(adapter1)
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
        val city = autocompleteCity.text.toString()
        val state = autocompleteState.text.toString()
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
                    restRooms.isNotEmpty() &&
                    imgUris.isNotEmpty())
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
        autocompleteCity.text.clear()
        autocompleteState.text.clear()
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

    override fun onResume() {
        super.onResume()

        val cities: Array<String> =(resources.getStringArray(R.array.gj_cities)+
                                   resources.getStringArray(R.array.mh_cities)+
                                   resources.getStringArray(R.array.rj_cities)).sortedArray()

        val states=resources.getStringArray(R.array.states).sortedArray()

        var adapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,cities)
        autocompleteCity.setAdapter(adapter)

        adapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,states)
        autocompleteState.setAdapter(adapter)

    }

    private fun isOnline(): Boolean {
        val connManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connManager.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            //Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
