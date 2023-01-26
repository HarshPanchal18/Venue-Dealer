package com.example.book_venue

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


/*
* This code defines a class called ImageTextUploader that has a function uploadImages that takes in a list of image URIs and a list of texts as parameters.
* The function iterates through the list of images and texts, and calls the uploadImage and uploadText functions for each image and text,
  passing the unique file name as the parameter.
* The uploadImage function takes an image URI and a unique file name as parameters,
  initializes a reference to the Firebase Storage, and starts an upload task using the putFile() method.
* It also attaches a listener to handle the task's success or failure.
* The uploadText function takes a text and a unique file name as parameters,
  and stores the text data in Firebase Database using the setValue() method.
*/


class ImageTextUploader {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val textRef = FirebaseDatabase.getInstance().getReference("text")

    fun uploadImages(images: List<Uri>, texts: List<String>) {
        for (i in images.indices) {
            val image = images[i]
            val text = texts[i]
            val name = "image$i"
            uploadImage(image, name)
            uploadText(text, name)
        }
    }

    private fun uploadImage(file: Uri, name: String) {
        val imageRef = storageRef.child("images/$name")
        val uploadTask = imageRef.putFile(file)

        uploadTask.addOnFailureListener {
            // Handle failure here
        }.addOnSuccessListener {
            // Handle success here
        }
    }

    private fun uploadText(text: String, name: String) {
        val textData = hashMapOf("name" to name, "text" to text)
        textRef.child(name).setValue(textData)
    }
}
