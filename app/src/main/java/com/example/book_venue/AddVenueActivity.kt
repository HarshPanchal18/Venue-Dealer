package com.example.book_venue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_venue.*

class AddVenueActivity : AppCompatActivity() {

    val summaryResult=StringBuilder()
    val venue_types=StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_venue)

        addbtn.setOnClickListener {
            constructAndValidate()
        }
    }

    private fun constructAndValidate(){

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

        if(name.isNotEmpty() &&
            location.isNotEmpty() &&
            city.isNotEmpty() &&
            state.isNotEmpty() &&
            capacity.isNotEmpty()
        ){
            summaryResult.append(name+"\n",location+"\n",city+"\n",state+"\n",venue_types,capacity+"\n",availability)
            Toast.makeText(this,summaryResult.toString(),Toast.LENGTH_SHORT).show()
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

        convHall.isChecked=false
        sports.isChecked=false
        exhibition.isChecked=false
        wedding.isChecked=false
        festivity.isChecked=false
        party.isChecked=false

        summaryResult.clear()
        venue_types.clear()
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
