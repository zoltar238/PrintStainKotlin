package org.example.project.model.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Image: RealmObject {

    @PrimaryKey
    var imageId: Long? = null
    var base64ImageUrl: String? = null
}