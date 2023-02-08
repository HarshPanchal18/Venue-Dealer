package com.example.book_venue

/*data class Venue(
    var title: String = "",
    var desc: String = "",
    var landmark: String = "",
    var city: String = "",
    var state: String = "",
    var availableTime: Boolean = false,
    var dealerContact: String = "",
    var venueTypes: ArrayList<String> = ArrayList(),
)
*/

class Venue {
    var title: String = ""
    var desc: String = ""
    var landmark: String = ""
    var city: String = ""
    var state: String = ""
    var availableTime: Boolean = false
    var dealerContact: String = ""
    var venueTypes: ArrayList<String> = ArrayList()

    constructor(title: String = "",
                 desc: String = "",
                 landmark: String = "",
                 city: String = "",
                 state: String = "",
                 availableTime: Boolean = false,
                 dealerContact: String = "",
                 venueTypes: ArrayList<String> = ArrayList()){

        this.title=title
        this.desc=desc
        this.landmark=landmark
        this.city=city
        this.state=state
        this.dealerContact=dealerContact
        this.venueTypes=venueTypes
        this.availableTime=availableTime
    }

    constructor(){}
}
