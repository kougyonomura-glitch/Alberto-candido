package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val inquiryDao: InquiryDao,
    private val draftedJobDao: DraftedJobDao
) {
    val allInquiries: Flow<List<Inquiry>> = inquiryDao.getAllInquiries()
    val allDraftedJobs: Flow<List<DraftedJob>> = draftedJobDao.getAllDraftedJobs()

    suspend fun insertInquiry(inquiry: Inquiry) {
        inquiryDao.insertInquiry(inquiry)
    }

    suspend fun deleteInquiry(inquiry: Inquiry) {
        inquiryDao.deleteInquiry(inquiry)
    }

    suspend fun insertDraftedJob(draftedJob: DraftedJob) {
        draftedJobDao.insertDraftedJob(draftedJob)
    }

    suspend fun updateDraftedJob(draftedJob: DraftedJob) {
        draftedJobDao.updateDraftedJob(draftedJob)
    }

    suspend fun deleteDraftedJob(draftedJob: DraftedJob) {
        draftedJobDao.deleteDraftedJob(draftedJob)
    }
}
