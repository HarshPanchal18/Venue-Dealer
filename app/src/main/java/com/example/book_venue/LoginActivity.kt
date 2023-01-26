package com.example.book_venue

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        // Authentication
        auth= FirebaseAuth.getInstance()

        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)

        val userNameStream = RxTextView.textChanges(et_mail)
            .skipInitialValue()
            .map { username -> username.isEmpty() }
        userNameStream.subscribe { showTextMinimalAlert(it,"Email/Username") }

        val passwordStream = RxTextView.textChanges(et_password)
            .skipInitialValue()
            .map { password -> password.isEmpty() }
        passwordStream.subscribe { showTextMinimalAlert(it,"Password") }


        // Button Enable/Disable
        val invalidFieldsStream : Observable<Boolean> = Observable.combineLatest(userNameStream,passwordStream) {
                usernameInvalid: Boolean, passwordInvalid: Boolean -> !usernameInvalid && !passwordInvalid
        }

        invalidFieldsStream.subscribe { isValid:Boolean ->
            if(isValid) {
                loginbtn.isEnabled=true
                loginbtn.backgroundTintList= ContextCompat.getColorStateList(this,R.color.primary_color)
            } else {
                loginbtn.isEnabled=false
                loginbtn.backgroundTintList= ContextCompat.getColorStateList(this,android.R.color.darker_gray)
            }
        }

        loginbtn.setOnClickListener {
            val mail=et_mail.text.toString().trim()
            val pass=et_password.text.toString().trim()
            loginUser(mail,pass)
        }

        googlesignbtn.setOnClickListener { signInGoogle() }

        tv_havent_account.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        tv_forgot_password.setOnClickListener {
            startActivity(Intent(this,ResetPasswordActivity::class.java))
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
                val intent=Intent(this,HomeActivity::class.java)
                intent.putExtra("email",account.email)
                intent.putExtra("name",account.displayName)
                startActivity(intent)
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
            val snackbar= Snackbar.make(loginScreen,"Verify your mail first",Snackbar.LENGTH_SHORT)
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
            et_mail.error=if(isNotValid) "$text cannot be empty!" else null
        else if(text=="Password")
            et_password.error=if(isNotValid) "$text cannot be empty!" else null
    }
}
