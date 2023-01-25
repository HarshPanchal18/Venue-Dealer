package com.example.book_venue

class Upload {
    private lateinit var mName:String
    private lateinit var mImgUrl:String

    constructor() {}

    constructor(name:String,imgUrl:String) {
        var _name=name
        if(name.trim()=="")
            _name = "No name"

        mName=_name
        mImgUrl=imgUrl
    }

    fun getName():String = mName
    fun setName(name:String) { mName= name }

    fun getImgUrl():String =mImgUrl
    fun setImgUrl(url:String){ mImgUrl=url }
}
