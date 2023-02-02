package com.example.book_venue

import android.net.Uri
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

fun uploadImagesToFirebase(imageUris: List<Uri>) {
    val storageRef = FirebaseStorage.getInstance().reference
    val databaseRef = FirebaseDatabase.getInstance().reference
    val imageCount = AtomicInteger(0) // to keep track of the number of successful uploads
    val countDownLatch = CountDownLatch(imageUris.size) // to wait for all the uploads to complete, when the latch reaches 0, it means all the uploads have completed
    // and the function can check the value of the AtomicInteger() to determine if all the images have been uploaded successfully.

    imageUris.forEach { imageUri ->
        val imageRef = storageRef.child("images/${imageUri.lastPathSegment}")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            val imagePath = taskSnapshot.storage.path
            databaseRef.child("images").push().setValue(imagePath)
                .addOnSuccessListener {
                    imageCount.incrementAndGet()
                    countDownLatch.countDown()
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseError", "Error uploading image: $exception")
                    countDownLatch.countDown()
                }
        }
            .addOnFailureListener { exception ->
                Log.e("FirebaseError", "Error uploading image: $exception")
                countDownLatch.countDown()
            }
    }

    try {
        countDownLatch.await()
        if (imageCount.get() == imageUris.size) {
            Log.d("Firebase", "All images uploaded successfully")
        } else {
            Log.e("Firebase", "Error uploading images")
        }
    } catch (e: InterruptedException) {
        Log.e("FirebaseError", "Interrupted while uploading images: $e")
    }
}
