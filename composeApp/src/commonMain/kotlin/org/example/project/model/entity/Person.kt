package org.example.project.model.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Person: RealmObject {

    @PrimaryKey val personId: Long? = null
    val username : String? = null
}