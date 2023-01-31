package com.example.book_venue

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_venue.*
import kotlinx.android.synthetic.main.activity_add_venue.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddVenueActivity : AppCompatActivity() {

    private val PICK_IMG_REQUEST=101
    private lateinit var summaryResult: HashMap<String,Any>
    private lateinit var venue_types:ArrayList<String>

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore:FirebaseFirestore

    private lateinit var images: List<Uri>
    private lateinit var texts: List<String>
    private lateinit var imageTextUploader: ImageTextUploader

    private var imgUri:Uri?=null
    private lateinit var mStorageRef: StorageReference
    private lateinit var mDatabaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_venue)
        supportActionBar?.hide()

        auth=FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()
        user= auth.currentUser!!

        mDatabaseRef=FirebaseDatabase.getInstance().reference
        summaryResult= HashMap()
        venue_types= ArrayList()

        addVenuebtn.setOnClickListener {
            if(constructAndValidate()){

                val documentRef = firestore.collection("venue")
                    .document()

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

        chooseImage.setOnClickListener {
            openFileChooser()
        }

        clearImage.setOnClickListener {
            imageView.setImageDrawable(null)
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Images"), 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*if(
            requestCode==PICK_IMG_REQUEST // check for image request
            && resultCode== RESULT_OK // if user actually picked an image
            && data!=null
            && data.data!=null
        ) {
            imgUri= data.data
            imageView.setImageURI(imgUri)
        }*/
        if (requestCode == 101 && resultCode == RESULT_OK) {
            images = if (data?.clipData != null) {
                (0 until data.clipData!!.itemCount)
                    .map { data.clipData!!.getItemAt(it).uri }
            } else {
                listOf(data?.data!!)
            }
            texts = List(images.size) { "" }
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

            //Toast.makeText(this,summaryResult.toString(),Toast.LENGTH_SHORT).show()
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
        filename.text.clear()
        rentPrice.text.clear()

        convHall.isChecked=false
        sports.isChecked=false
        exhibition.isChecked=false
        wedding.isChecked=false
        festivity.isChecked=false
        party.isChecked=false

        summaryResult.clear()
        venue_types.clear()
        imageView.setImageResource(android.R.color.transparent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        val cities=resources.getStringArray(R.array.cities)
        val states=resources.getStringArray(R.array.states)

        var adapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,cities)
        autocompleteCity.setAdapter(adapter)

        adapter= ArrayAdapter(applicationContext,R.layout.dropdown_item,states)
        autocompleteState.setAdapter(adapter)

    }
}
