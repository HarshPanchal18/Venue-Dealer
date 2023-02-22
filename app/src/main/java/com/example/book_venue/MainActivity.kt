package com.example.book_venue

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.book_venue.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth= FirebaseAuth.getInstance()

        val user=auth.currentUser

        if(isOnline()){
            if(user!=null) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }

        binding.loginbtn.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }

        binding.registerbtn.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser!=null) {
            Intent(this,HomeActivity::class.java).also {
                it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
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
