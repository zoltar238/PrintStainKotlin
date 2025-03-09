package org.example.project.persistence.repository

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.example.project.model.entity.Image
import org.example.project.model.entity.Item
import org.example.project.model.entity.Person

fun initRealm(): Realm {
    val schema = setOf(
        Item::class,
        Person::class,
        Image::class
    )

    val config =
        RealmConfiguration.Builder(schema).name("printstainDatabase").initialRealmFile("printstainDatabase").build()

    return Realm.open(config)
}