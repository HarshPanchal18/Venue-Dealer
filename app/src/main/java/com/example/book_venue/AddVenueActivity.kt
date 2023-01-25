package com.example.book_venue

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_venue.*
import kotlinx.android.synthetic.main.activity_add_venue.view.*

class AddVenueActivity : AppCompatActivity() {

    private val PICK_IMG_REQUEST=1
    val summaryResult=StringBuilder()
    val venue_types=StringBuilder()

    private var imgUri:Uri?=null
    private lateinit var mStorageRef: StorageReference
    private lateinit var mDatabaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_venue)
        supportActionBar?.hide()

        mStorageRef=FirebaseStorage.getInstance().getReference("uploads") // save in folder uploads
        mDatabaseRef=FirebaseDatabase.getInstance().getReference("uploads")

        addVenuebtn.setOnClickListener {
            constructAndValidate()
        }

        chooseImage.setOnClickListener {
            openFileChooser()
        }

        clearImage.setOnClickListener {
            imageView.setImageDrawable(null)
        }
    }

    private fun uploadFile() {
        val fileRef = mStorageRef.child(System.currentTimeMillis().toString()
                + "."
                + getFileExtension(imgUri!!))

        fileRef.putFile(imgUri!!)
            .addOnSuccessListener {
                val handler= Handler()
                handler.postDelayed({
                        uploadProgressbar.progress=0
                    },200)

                Toast.makeText(this,"Upload successful",Toast.LENGTH_SHORT).show()
                val upload=Upload(filename.text.toString().trim(),
                    it.metadata?.reference?.downloadUrl.toString())

                val uploadId=mDatabaseRef.push().key
                mDatabaseRef.child(uploadId!!).setValue(upload)
            }

            .addOnFailureListener { Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show() }

            .addOnProgressListener {
                val progress:Double=100.0.times(it.bytesTransferred / it.totalByteCount) // get current progress
                uploadProgressbar.progress=progress.toInt()
            }
    }

    private fun getFileExtension(uri:Uri) :String {
        val cr=contentResolver
        val mime=MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))!!
    }

    private fun openFileChooser() {
        val intent= Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,PICK_IMG_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(
            requestCode==PICK_IMG_REQUEST // check for image request
            && resultCode== RESULT_OK // if user actually picked an image
            && data!=null
            && data.data!=null
        ) {
            imgUri= data.data!!
            imageView.setImageURI(imgUri)
        }
    }

    private fun constructAndValidate() {

        val name=venueTitle.text.toString()
        val location=venueLocation.text.toString()
        val city=autocompleteCity.text.toString()
        val state=autocompleteState.text.toString()
        val capacity=venueCapacity.text.toString()
        val availability= dayTimeAvailability.isChecked.toString()

        if(convHall.isChecked) venue_types.append(convHall.text.toString()+"\n")
        if(wedding.isChecked) venue_types.append(wedding.text.toString()+"\n")
        if(festivity.isChecked) venue_types.append(festivity.text.toString()+"\n")
        if(party.isChecked) venue_types.append(party.text.toString()+"\n")
        if(exhibition.isChecked) venue_types.append(exhibition.text.toString()+"\n")
        if(sports.isChecked) venue_types.append(sports.text.toString()+"\n")

        if((name.isNotEmpty() &&
                location.isNotEmpty() &&
                city.isNotEmpty() &&
                state.isNotEmpty() &&
                capacity.isNotEmpty())
            && (
                    wedding.isChecked ||
                    festivity.isChecked ||
                    convHall.isChecked ||
                    party.isChecked ||
                    exhibition.isChecked ||
                    sports.isChecked)
            && imgUri?.path!=null
        ){
            summaryResult.append(name+"\n",location+"\n",city+"\n",state+"\n",venue_types,capacity+"\n",availability)
            Toast.makeText(this,summaryResult.toString(),Toast.LENGTH_SHORT).show()
            uploadFile()
            clearFields()
        } else {
            Toast.makeText(this,"Oops! you haven't entered proper data",Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields(){

        venueTitle.text.clear()
        venueLocation.text.clear()
        dealerPhNo.text.clear()
        venueCapacity.text.clear()
        autocompleteCity.text.clear()
        autocompleteState.text.clear()
        filename.text.clear()

        convHall.isChecked=false
        sports.isChecked=false
        exhibition.isChecked=false
        wedding.isChecked=false
        festivity.isChecked=false
        party.isChecked=false

        summaryResult.clear()
        venue_types.clear()
        imageView.setImageResource(android.R.color.transparent)
        //imageView.setImageDrawable(null)
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
