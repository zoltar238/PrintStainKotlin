package org.example.project.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.project.logging.AppLogger
import org.example.project.persistence.repository.ItemsRepoHttpImp
import org.example.project.persistence.repository.UserRepoHttpImp

// Function reserved for admin privileges
fun getAllItems() {
    ItemsRepoHttpImp.getAllItems()
}