package com.example.book_venue

class Venue {
    var title:String?=null
    var desc: String?=null
    var location: String?=null
    var city: String?=null
    var state: String?=null
    var availableTime: Boolean=false
    var dealerContact: String?=null
    lateinit var venueType: ArrayList<String>

    constructor(
                title: String?=null,
                desc: String?=null,
                location: String?=null,
                city: String?=null,
                state: String?=null,
                availableTime: Boolean,
                dealerContact: String?=null,
                venueType: ArrayList<String> ){
        this.title=title
        this.desc=desc
        this.location=location
        this.city=city
        this.state=state
        this.dealerContact=dealerContact
        this.venueType=venueType
    }

    constructor(){}
}
/*
class ChatMessage {
    var messageText: String?=null
    var messageUser: String?=null? = null
    var messageTime: Long = 0

    constructor(messageText: String?=null, messageUser: String?=null?) {
        this.messageText = messageText
        this.messageUser = messageUser
        messageTime = Date().getTime()
    }

    constructor(messageText: String?=null) {
        this.messageText = messageText
    }

    constructor(){}
}
*/
