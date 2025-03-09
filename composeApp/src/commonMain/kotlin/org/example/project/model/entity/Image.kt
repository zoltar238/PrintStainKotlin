package org.example.project.model.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Image: RealmObject {

    @PrimaryKey
    var id: Long? = null
    var base64Image: String? = null
}