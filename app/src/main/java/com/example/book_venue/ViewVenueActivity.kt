package com.example.book_venue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_view_venue.*

class ViewVenueActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    lateinit var db:FirebaseFirestore
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_venue)

        supportActionBar?.hide()
        auth= FirebaseAuth.getInstance()
        user= auth.currentUser!!
        firestore=FirebaseFirestore.getInstance()
        db = FirebaseFirestore.getInstance()

        loadVenuesFromDb(user.uid)
    }

    private fun loadVenuesFromDb(user: String) {
        val venueList=ArrayList<Venue>()

        val ref=db.collection("venue")
        ref.whereEqualTo("userId",user)
            .get()
            .addOnSuccessListener {
            if(it.isEmpty){
                Toast.makeText(this@ViewVenueActivity,"No venue found",Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            for(doc in it){
                val venueModel=doc.toObject(Venue::class.java)
                venueList.add(venueModel)
            }
            venueRecycler.apply {
                layoutManager=LinearLayoutManager(this@ViewVenueActivity)
                adapter=VenueAdapter(venueList,this@ViewVenueActivity)
            }
        }
    }

    class VenueViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val venueTitle: TextView
        val venueDesc: TextView
        val availability: TextView
        val nameDealer: TextView
        val contactDealer: TextView
        val loc: TextView
        val city: TextView
        val state: TextView
        val types: TextView
        var mVenue: LinearLayout

        init {
            venueTitle = itemView.findViewById(R.id.venueTitle)
            venueDesc = itemView.findViewById(R.id.venueDescription)
            availability=itemView.findViewById(R.id.availableTime)
            nameDealer=itemView.findViewById(R.id.nameDealer)
            contactDealer=itemView.findViewById(R.id.contactDealer)
            loc=itemView.findViewById(R.id.Location)
            city=itemView.findViewById(R.id.City)
            state=itemView.findViewById(R.id.State)
            types=itemView.findViewById(R.id.types)
            mVenue = itemView.findViewById(R.id.card)

            //Animate Recyclerview
            /*val translateAnim: Animation =
                AnimationUtils.loadAnimation(itemView.context, R.anim.translate_anim)
            mnote.animation = translateAnim*/
        }
    }

}
