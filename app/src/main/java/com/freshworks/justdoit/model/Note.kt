package com.freshworks.justdoit.model

class Note(){
    var title : String? = null
    var description : String? = null
    var created_at : Long? = null
    var id : Int? = null

    constructor(_title : String?,_description : String? , _created_at: Long?, _id : Int?  ) : this() {

        this.title = _title
        this.description = _description
        this.created_at = _created_at
        this.id = _id


    }
}