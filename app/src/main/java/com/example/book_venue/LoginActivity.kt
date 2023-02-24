package com.example.book_venue

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.book_venue.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding : ActivityLoginBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Authentication
        auth= FirebaseAuth.getInstance()

        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)

        val userNameStream = RxTextView.textChanges(binding.etMail)
            .skipInitialValue()
            .map { username -> username.isEmpty() }
        userNameStream.subscribe { showTextMinimalAlert(it,"Email/Username") }

        val passwordStream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map { password -> password.isEmpty() }
        passwordStream.subscribe { showTextMinimalAlert(it,"Password") }


        // Button Enable/Disable
        val invalidFieldsStream : Observable<Boolean> = Observable.combineLatest(userNameStream,passwordStream) {
                usernameInvalid: Boolean, passwordInvalid: Boolean -> !usernameInvalid && !passwordInvalid
        }

        invalidFieldsStream.subscribe { isValid:Boolean ->
            if(isValid) {
                binding.loginbtn.isEnabled=true
                binding.loginbtn.backgroundTintList= ContextCompat.getColorStateList(this,R.color.primary_color)
            } else {
                binding.loginbtn.isEnabled=false
                binding.loginbtn.backgroundTintList= ContextCompat.getColorStateList(this,android.R.color.darker_gray)
            }
        }

        binding.loginbtn.setOnClickListener {
            val mail=binding.etMail.text.toString().trim()
            val pass=binding.etPassword.text.toString().trim()
            if(isOnline()){
                loginUser(mail,pass)
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

        binding.googlesignbtn.setOnClickListener {
            if(isOnline()){
                signInGoogle()
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

        binding.tvHaventAccount.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent=Intent(this,ResetPasswordActivity::class.java)
            intent.putExtra("Mail",binding.etMail.text.toString().trim())
            startActivity(intent)
        }
    }

    private fun signInGoogle() {
        googleSignInClient.signOut()
        val signInIntent=googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode== Activity.RESULT_OK){
            val task= GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount?=task.result
            if(account!=null){
                updateUI(account)
            }
        }
        else{
            Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                startActivity(Intent(this,HomeActivity::class.java))
            } else{
                Toast.makeText(this,it.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(mail: String, pass: String) {
        auth.signInWithEmailAndPassword(mail,pass)
            .addOnCompleteListener(this){ login ->
                if(login.isSuccessful){
                    checkMailVerification()
                } else {
                    Toast.makeText(this,login.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkMailVerification(){
        val user=auth.currentUser
        if(user?.isEmailVerified==true){
            finish()
            Intent(this,HomeActivity::class.java).also {
                it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this,"Login Successfully", Toast.LENGTH_SHORT).show()
            }
        } else {
            val snackbar= Snackbar.make(binding.loginScreen,"Verify your mail first",Snackbar.LENGTH_SHORT)
            val snackBarView: View =snackbar.view
            val params=snackBarView.layoutParams as FrameLayout.LayoutParams
            params.gravity= Gravity.TOP
            snackBarView.setBackgroundColor(Color.BLACK)
            snackbar.show()

            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE))

            auth.signOut()
        }
    }

    private fun showTextMinimalAlert(isNotValid: Boolean,text:String) {
        if(text == "Email/Username")
            binding.etMail.error=if(isNotValid) "$text cannot be empty!" else null
        else if(text=="Password")
            binding.etPassword.error=if(isNotValid) "$text cannot be empty!" else null
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
