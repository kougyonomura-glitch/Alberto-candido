package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InquiryDao {
    @Query("SELECT * FROM inquiries ORDER BY timestamp DESC")
    fun getAllInquiries(): Flow<List<Inquiry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: Inquiry)

    @Delete
    suspend fun deleteInquiry(inquiry: Inquiry)
}

@Dao
interface DraftedJobDao {
    @Query("SELECT * FROM drafted_jobs ORDER BY timestamp DESC")
    fun getAllDraftedJobs(): Flow<List<DraftedJob>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraftedJob(draftedJob: DraftedJob)

    @Update
    suspend fun updateDraftedJob(draftedJob: DraftedJob)

    @Delete
    suspend fun deleteDraftedJob(draftedJob: DraftedJob)
}
