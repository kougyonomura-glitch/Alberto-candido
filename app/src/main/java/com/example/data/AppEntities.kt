package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inquiries")
data class Inquiry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val contactPerson: String,
    val email: String,
    val phone: String,
    val category: String, // "Recruitment", "Translation", "Training", "Quick Solutions"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "drafted_jobs")
data class DraftedJob(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val description: String, // The generated Japanese text
    val actionPlan: String,  // The generated solution action plan
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
