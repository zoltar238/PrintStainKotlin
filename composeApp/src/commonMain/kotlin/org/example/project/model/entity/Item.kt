package org.example.project.model.entity

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Item : RealmObject {

    @PrimaryKey
    var id: Long? = null
    var name: String? = null
    var description: String? = null
    var postDate: RealmInstant? = null
    var startDate: RealmInstant? = null
    var finishDate: RealmInstant? = null
    var shipDate: RealmInstant? = null
    var timesUploaded: Int? = null

    //  One item contains a multiple images
    var images: RealmList<Image>? = realmListOf()

    // Person that posted the item
    var person: Person? = null
}