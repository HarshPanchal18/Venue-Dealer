package com.example.book_venue

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_venue.*

class AddVenueActivity : AppCompatActivity() {

    private lateinit var summaryResult: HashMap<String,Any>
    private lateinit var venue_types:ArrayList<String>

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore:FirebaseFirestore

    private lateinit var chooseImgList:ArrayList<Uri>
    private lateinit var urlList:ArrayList<String>

    private lateinit var mStorageRef: StorageReference
    private lateinit var mStorage: FirebaseStorage

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
        urlList=ArrayList()
        chooseImgList=ArrayList()

        addVenuebtn.setOnClickListener {
            if(constructAndValidate()){
                //uploadImages()
                //uploadImagesToFirebase(chooseImgList)

                val documentRef = firestore.collection("venue").document()
                documentRef.set(summaryResult).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Venue is Added",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(applicationContext,it.message,Toast.LENGTH_SHORT).show()
                }
                clearFields()
            } else {
                Toast.makeText(this,"Oops! you haven't entered proper data",Toast.LENGTH_SHORT).show()
            }
        }

        chooseImage.setOnClickListener { CheckPermissionAndGo() }

        clearImage.setOnClickListener { selectedImg.setImageDrawable(null) }

    }

    private fun CheckPermissionAndGo() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),2)
        }
        openFileChooser()
    }

    private fun uploadImages() {
        for(i in 0..chooseImgList.size){
            val individualUri = chooseImgList[i]
            if(individualUri!=null){
                val imageFolder=FirebaseStorage.getInstance().reference.child("ItemImages")
                val imageName=imageFolder.child("Image $i:")
                imageName.putFile(individualUri).addOnSuccessListener {
                    imageName.downloadUrl.addOnSuccessListener {
                        urlList.add(it.toString())
                        if(urlList.size==chooseImgList.size) {
                            storeLinks(urlList)
                        }
                    }
                }
            } else {
                Toast.makeText(this,"Unable to process :(",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun storeLinks(urlList: ArrayList<String>) {
        val model=ItemModel("",urlList)
        firestore.collection("venue").add(model)
            .addOnSuccessListener {
                model.id=it.id
                firestore.collection("venue").document(model.id)
                    .set(model, SetOptions.merge()).addOnSuccessListener {
                        // If image uploaded successfully
                        Toast.makeText(this,"Images Uploaded",Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Images"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK ) {
            val selectedUri=data?.data
            selectedImg.setImageURI(selectedUri)

            if(selectedUri!=null){
                val imageRef=mStorageRef.child("images/venue/${System.currentTimeMillis()}.jpg")
                val uploadTask=imageRef.putFile(selectedUri)
                    .addOnSuccessListener{
                        // Get the URL of the uploaded image
                        imageRef.downloadUrl.addOnSuccessListener { url ->
                            val imgdata= hashMapOf("url" to url.toString())
                            summaryResult.putAll(imgdata)
                        }
                    }
            }
        }
    }

    private fun constructAndValidate() : Boolean {

        val name=venueTitle.text.toString()
        val description=venueDescription.text.toString()
        val location=venueLocation.text.toString()
        val city=autocompleteCity.text.toString()
        val state=autocompleteState.text.toString()
        val capacity=venueCapacity.text.toString()
        val dealerContact=dealerPhNo.text.toString()
        val availability= dayTimeAvailability.isChecked.toString()
        val rentPerHour=rentPrice.text.toString()

        if(convHall.isChecked) venue_types.add(convHall.text.toString()+"\n")
        if(wedding.isChecked) venue_types.add(wedding.text.toString()+"\n")
        if(festivity.isChecked) venue_types.add(festivity.text.toString()+"\n")
        if(party.isChecked) venue_types.add(party.text.toString()+"\n")
        if(exhibition.isChecked) venue_types.add(exhibition.text.toString()+"\n")
        if(sports.isChecked) venue_types.add(sports.text.toString()+"\n")

        if((name.isNotEmpty() &&
                    description.isNotEmpty() &&
                    location.isNotEmpty() &&
                    city.isNotEmpty() &&
                    state.isNotEmpty() &&
                    capacity.isNotEmpty() &&
                    dealerContact.isNotEmpty() &&
                    rentPerHour.isNotEmpty())
            && (
                    wedding.isChecked ||
                    festivity.isChecked ||
                    convHall.isChecked ||
                    party.isChecked ||
                    exhibition.isChecked ||
                    sports.isChecked
                    )
            //&& imgUri?.path!=null
        ){
            summaryResult["Name"]=name
            summaryResult["Description"]=description
            summaryResult["Location"]=location
            summaryResult["City"]=city
            summaryResult["State"]=state
            summaryResult["Dealer_Ph"]=dealerContact
            summaryResult["Types"]=venue_types.toSet().toString()
            summaryResult["Capacity"]=capacity
            summaryResult["RentPerHour"]=rentPerHour
            summaryResult["Availability"]=availability
            summaryResult["userId"]=user.uid

            Toast.makeText(applicationContext,summaryResult.toString(),Toast.LENGTH_LONG).show()
            return true
        } else {
            return false
        }
    }

    private fun clearFields(){

        venueTitle.text.clear()
        venueDescription.text.clear()
        venueLocation.text.clear()
        dealerPhNo.text.clear()
        venueCapacity.text.clear()
        autocompleteCity.text.clear()
        autocompleteState.text.clear()
        rentPrice.text.clear()

        convHall.isChecked=false
        sports.isChecked=false
        exhibition.isChecked=false
        wedding.isChecked=false
        festivity.isChecked=false
        party.isChecked=false

        summaryResult.clear()
        venue_types.clear()
        finish()
    }

    override fun onResume() {
        super.onResume()
        var cities: Array<String> =resources.getStringArray(R.array.states)
        val states=resources.getStringArray(R.array.states)

        when(autocompleteState.text.toString()){
            "Gujarat" -> {
                cities=resources.getStringArray(R.array.gj_cities)
                val adapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,cities)
                autocompleteCity.setAdapter(adapter)
            }
            "Maharashtra" -> {
                cities=resources.getStringArray(R.array.mh_cities)
                val adapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,cities)
                autocompleteCity.setAdapter(adapter)
            }
            "Rajasthan" -> {
                cities=resources.getStringArray(R.array.rj_cities)
                val adapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,cities)
                autocompleteCity.setAdapter(adapter)
            }
        }

        var adapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,cities)
        autocompleteCity.setAdapter(adapter)

        adapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,states)
        autocompleteState.setAdapter(adapter)

    }
}
