package com.example.book_venue

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.book_venue.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth=FirebaseAuth.getInstance()
        user= auth.currentUser!!

        binding.accountName.text="${user.displayName}\n${user.email}"

        binding.logoutbtn.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            val view: View = LayoutInflater.from(this)
                .inflate(R.layout.warning_dialog, findViewById<ConstraintLayout>(R.id.layoutDialogContainer))
            builder.setView(view)
            (view.findViewById<View>(R.id.textTitle) as TextView).text = resources.getString(R.string.warning_title)
            (view.findViewById<View>(R.id.textMessage) as TextView).text = resources.getString(R.string.dummy_text)
            (view.findViewById<View>(R.id.buttonYes) as Button).text = resources.getString(R.string.yes)
            (view.findViewById<View>(R.id.buttonNo) as Button).text = resources.getString(R.string.no)
            (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.warning)

            val alertDialog = builder.create()
            view.findViewById<View>(R.id.buttonYes).setOnClickListener {
                alertDialog.dismiss()
                Toast.makeText(this, "Yes", Toast.LENGTH_SHORT).show()
            }
            view.findViewById<View>(R.id.buttonNo).setOnClickListener {
                alertDialog.dismiss()
                Toast.makeText(this, "No", Toast.LENGTH_SHORT).show()
            }
            if (alertDialog.window != null) {
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            alertDialog.show()

            /*val builder = AlertDialog.Builder(this)//,R.style.AlertDialogTheme)
            /*val view: View = LayoutInflater.from(this).inflate(
                R.layout.warning_dialog,
                findViewById<View>(R.id.layoutDialogContainer) as ConstraintLayout)
            builder.setView(view)*/
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //positive action
            builder.setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                Intent(this,MainActivity::class.java).also {
                    it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }

            //cancel action
            builder.setNegativeButton("No")
            { _, _ -> Toast.makeText(applicationContext, "Welcome Back :)", Toast.LENGTH_SHORT).show() }

            //create the alert dialog
            val alertDialog: AlertDialog = builder.create()

            //other properties
            alertDialog.setCancelable(false)
            alertDialog.show()
*/
        }

        binding.dialog.setOnClickListener {
            showWarningDialog()
        }

        binding.addVenuebtn.setOnClickListener {
            startActivity(Intent(this,AddVenueActivity::class.java))
        }

        binding.viewVenuebtn.setOnClickListener {
            startActivity(Intent(this,ViewVenueActivity::class.java))
        }
    }

    // Ask again for exit
    private var backPressedTime:Long=0
    override fun onBackPressed() {

        if(backPressedTime+2000>System.currentTimeMillis()) {
            super.onBackPressed()
            exitProcess(0)
        }

        Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show()
        backPressedTime= System.currentTimeMillis()
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(this)
            .inflate(R.layout.success_dialog,
                findViewById<ConstraintLayout>(R.id.layoutDialogContainer))
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitle) as TextView).text = resources.getString(R.string.success_title)
        (view.findViewById<View>(R.id.textMessage) as TextView).text = resources.getString(R.string.dummy_text)
        (view.findViewById<View>(R.id.buttonAction) as Button?)!!.text = resources.getString(R.string.okay)
        (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.done)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonAction).setOnClickListener {
            alertDialog.dismiss()
            Toast.makeText(this@HomeActivity, "Success", Toast.LENGTH_SHORT).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()
    }

    private fun showWarningDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(this)
            .inflate(R.layout.warning_dialog, findViewById<ConstraintLayout>(R.id.layoutDialogContainer))
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitle) as TextView).text = resources.getString(R.string.warning_title)
        (view.findViewById<View>(R.id.textMessage) as TextView).text = resources.getString(R.string.dummy_text)
        (view.findViewById<View>(R.id.buttonYes) as Button).text = resources.getString(R.string.yes)
        (view.findViewById<View>(R.id.buttonNo) as Button).text = resources.getString(R.string.no)
        (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.warning)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonYes).setOnClickListener {
            alertDialog.dismiss()
            Toast.makeText(this, "Yes", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.buttonNo).setOnClickListener {
            alertDialog.dismiss()
            Toast.makeText(this, "No", Toast.LENGTH_SHORT).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(this)
            .inflate(R.layout.error_dialog,
                findViewById<ConstraintLayout>(R.id.layoutDialogContainer))
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitle) as TextView).text = resources.getString(R.string.error_title)
        (view.findViewById<View>(R.id.textMessage) as TextView).text = resources.getString(R.string.dummy_text)
        (view.findViewById<View>(R.id.buttonAction) as Button).text = resources.getString(R.string.okay)
        (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.error)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonAction).setOnClickListener {
            alertDialog.dismiss()
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()
    }
}
