package com.example.book_venue

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.book_venue.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding:ActivityRegisterBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        // Authentication
        auth=FirebaseAuth.getInstance()

        // Validation starts here
        val nameStream= RxTextView.textChanges(binding.etFullname)
            .skipInitialValue()
            .map { name -> name.isEmpty() }
        nameStream.subscribe{ showNameExistAlert(it) }

        val emailStream=RxTextView.textChanges(binding.etMail)
            .skipInitialValue()
            .map { mail -> !Patterns.EMAIL_ADDRESS.matcher(mail).matches() }
        emailStream.subscribe{ showEmailValidAlert(it) }

        /*val userNameStream=RxTextView.textChanges(et_username)
            .skipInitialValue()
            .map { username -> username.length < 6 }
        userNameStream.subscribe { showTextMinimalAlert(it,"Username") }*/

        val passwordStream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map { password -> password.length < 8 }
        passwordStream.subscribe { showTextMinimalAlert(it,"Password") }

        val passwordConfirmStream =
            Observable.merge(
                RxTextView.textChanges(binding.etPassword).skipInitialValue()
                    .map { password -> password.toString() != binding.etConfPassword.text.toString() },

                RxTextView.textChanges(binding.etConfPassword).skipInitialValue()
                    .map { confirmPassword -> confirmPassword.toString() != binding.etPassword.text.toString() })
        passwordConfirmStream.subscribe { showPasswordConfirmAlert(it) }

        // Button Enable/Disable
        val invalidFieldsStream : Observable<Boolean> = Observable.combineLatest(
            nameStream,
            emailStream,
            //userNameStream,
            passwordStream,
            passwordConfirmStream
        ) {
                nameInvalid: Boolean,
                emailInvalid: Boolean,
                //usernameInvalid: Boolean,
                passwordInvalid: Boolean,
                cpasswordInvalid: Boolean
            ->
            !nameInvalid && !emailInvalid && /*!usernameInvalid &&*/ !passwordInvalid && !cpasswordInvalid
        }

        invalidFieldsStream.subscribe { isValid:Boolean ->
            if (isValid) {
                binding.registerbtn.isEnabled=true
                binding.registerbtn.backgroundTintList= ContextCompat.getColorStateList(this,R.color.primary_color)
            } else {
                binding.registerbtn.isEnabled=false
                binding.registerbtn.backgroundTintList=ContextCompat.getColorStateList(this,android.R.color.darker_gray)
            }
        }

        binding.registerbtn.setOnClickListener {
            val mail=binding.etMail.text.toString().trim()
            val pass=binding.etPassword.text.toString().trim()

            if(isOnline()){
                registerUser(mail,pass)
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
        }

        binding.tvHaveAccount.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    private fun registerUser(mail: String, pass: String) {
        auth.createUserWithEmailAndPassword(mail,pass)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    startActivity(Intent(this,LoginActivity::class.java))
                    Toast.makeText(this,"Registered Successfully",Toast.LENGTH_SHORT).show()
                    sendMailVerification()
                } else {
                    Toast.makeText(this,it.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendMailVerification() {
        val user=auth.currentUser
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener {
                Toast.makeText(applicationContext,"Verification mail is sent, verify and login again",Toast.LENGTH_SHORT).show()
                auth.signOut()
                finish()
                startActivity(Intent(this,LoginActivity::class.java))
            }
        } else {
            Toast.makeText(applicationContext,"Failed to sent mail",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNameExistAlert(isNotValid:Boolean) {
        binding.etFullname.error=if(isNotValid) "Name cannot be empty" else null
    }

    private fun showTextMinimalAlert(isNotValid: Boolean,text:String) {
        /*if(text=="Username")
            et_username.error=if(isNotValid) "$text must be more than 6 letters!" else null
        else*/ if(text=="Password")
            binding.etPassword.error=if(isNotValid) "$text must be more than 8 letters!" else null
    }

    private fun showEmailValidAlert(isNotValid: Boolean) {
        binding.etMail.error=if(isNotValid) "Email is not valid!" else null
    }

    private fun showPasswordConfirmAlert(isNotValid: Boolean) {
        binding.etPassword.error=if(isNotValid) "Password are not the same" else null
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
