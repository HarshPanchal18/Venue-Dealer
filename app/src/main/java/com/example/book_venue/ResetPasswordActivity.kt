package com.example.book_venue

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.book_venue.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityResetPasswordBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth=FirebaseAuth.getInstance()

        val mail=intent.getStringExtra("Mail")
        binding.etMail.setText(mail)

        val emailStream= RxTextView.textChanges(binding.etMail)
            .skipInitialValue()
            .map { mail -> !Patterns.EMAIL_ADDRESS.matcher(mail).matches() }
        emailStream.subscribe{ showEmailValidAlert(it) }

        binding.resetPwBtn.setOnClickListener {
            val mail=binding.etMail.text.toString().trim()
            if(isOnline()){
                auth.sendPasswordResetEmail(mail)
                    .addOnCompleteListener(this){ reset ->
                        if(reset.isSuccessful){
                            Intent(this,LoginActivity::class.java).also {
                                it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(it)
                                Toast.makeText(this,"Check email for password reset", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this,reset.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                try {
                    val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    val view: View = LayoutInflater.from(this)
                        .inflate(R.layout.error_dialog, findViewById<ConstraintLayout>(R.id.layoutDialogContainer))
                    builder.setView(view)
                    (view.findViewById<View>(R.id.textTitle) as TextView).text = resources.getString(R.string.network_error_title)
                    (view.findViewById<View>(R.id.textMessage) as TextView).text = resources.getString(R.string.network_error_text)
                    (view.findViewById<View>(R.id.buttonAction) as Button).text = resources.getString(R.string.okay)
                    (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.error)
                    val alertDialog = builder.create()
                    view.findViewById<View>(R.id.buttonAction).setOnClickListener {
                        alertDialog.dismiss()
                        //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                    if (alertDialog.window != null) {
                        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                    }
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                } catch (e: Exception) { e.printStackTrace() }
            }
        }

        binding.backLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    private fun showEmailValidAlert(isNotValid: Boolean) {
        if(isNotValid){
            binding.etMail.error="Email is not valid"
            binding.resetPwBtn.isEnabled=false
            binding.resetPwBtn.backgroundTintList= ContextCompat.getColorStateList(this,android.R.color.darker_gray)
        } else {
            binding.etMail.error=null
            binding.resetPwBtn.isEnabled=true
            binding.resetPwBtn.backgroundTintList=ContextCompat.getColorStateList(this,R.color.primary_color)
        }
    }

    private fun isOnline(): Boolean {
        val conMgr =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            //Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
