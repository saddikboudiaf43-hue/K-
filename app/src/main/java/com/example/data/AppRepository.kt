package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val allUsers: Flow<List<UserEntity>> = appDao.getAllUsers()
    val allPosts: Flow<List<PostEntity>> = appDao.getAllPosts()
    val allReports: Flow<List<ReportEntity>> = appDao.getAllReports()
    val allConsultations: Flow<List<ConsultationEntity>> = appDao.getAllConsultations()
    val allVolunteerActivities: Flow<List<VolunteerActivityEntity>> = appDao.getAllVolunteerActivities()
    val allImamQuestions: Flow<List<ImamQuestionEntity>> = appDao.getAllImamQuestions()
    val allSchoolLessons: Flow<List<SchoolLessonEntity>> = appDao.getAllSchoolLessons()
    val allBookings: Flow<List<MosqueBookingEntity>> = appDao.getAllBookings()
    val moderators: Flow<List<UserEntity>> = appDao.getModerators()

    suspend fun getUserByUsername(username: String): UserEntity? = appDao.getUserByUsername(username)

    suspend fun insertUser(user: UserEntity) = appDao.insertUser(user)

    suspend fun setUserBlockedState(username: String, blocked: Boolean, reason: String) {
        appDao.setUserBlockedState(username, blocked, reason)
    }

    suspend fun setUserRole(username: String, role: String, modRole: String) {
        appDao.setUserRole(username, role, modRole)
    }

    suspend fun incrementVolunteerHours(username: String, hours: Int) {
        appDao.incrementVolunteerHours(username, hours)
    }

    suspend fun incrementFastingDays(username: String) {
        appDao.incrementFastingDays(username)
    }

    suspend fun incrementGroupPresence(username: String) {
        appDao.incrementGroupPresence(username)
    }

    suspend fun insertPost(post: PostEntity): Long = appDao.insertPost(post)

    suspend fun deletePost(postId: Long) {
        appDao.deletePost(postId)
        appDao.deleteCommentsForPost(postId)
    }

    suspend fun incrementLike(postId: Long) = appDao.incrementLike(postId)
    suspend fun incrementPrayer(postId: Long) = appDao.incrementPrayer(postId)
    suspend fun incrementSupport(postId: Long) = appDao.incrementSupport(postId)
    suspend fun incrementBlessing(postId: Long) = appDao.incrementBlessing(postId)

    suspend fun insertComment(comment: CommentEntity) {
        appDao.insertComment(comment)
        appDao.incrementCommentsCount(comment.postId)
    }

    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>> = appDao.getCommentsForPost(postId)

    suspend fun insertReport(report: ReportEntity) = appDao.insertReport(report)

    suspend fun resolveReport(reportId: Long, resolved: Boolean) = appDao.resolveReport(reportId, resolved)

    suspend fun deleteReport(reportId: Long) = appDao.deleteReport(reportId)

    suspend fun insertConsultation(consultation: ConsultationEntity) = appDao.insertConsultation(consultation)

    suspend fun answerConsultation(id: Long, answer: String) = appDao.answerConsultation(id, answer)

    suspend fun insertVolunteerActivity(activity: VolunteerActivityEntity) = appDao.insertVolunteerActivity(activity)

    suspend fun joinVolunteerActivity(activityId: Long, joinedUsernames: String) = appDao.joinVolunteerActivity(activityId, joinedUsernames)

    suspend fun leaveVolunteerActivity(activityId: Long, joinedUsernames: String) = appDao.leaveVolunteerActivity(activityId, joinedUsernames)

    suspend fun deleteVolunteerActivity(activityId: Long) = appDao.deleteVolunteerActivity(activityId)

    suspend fun insertImamQuestion(question: ImamQuestionEntity) = appDao.insertImamQuestion(question)

    suspend fun answerImamQuestion(id: Long, answer: String, isPublic: Boolean) = appDao.answerImamQuestion(id, answer, isPublic)

    suspend fun insertSchoolLesson(lesson: SchoolLessonEntity) = appDao.insertSchoolLesson(lesson)

    suspend fun insertBooking(booking: MosqueBookingEntity) = appDao.insertBooking(booking)

    suspend fun deleteBooking(bookingId: Long) = appDao.deleteBooking(bookingId)

    // --- Dhikr Progress ---
    fun getDhikrProgressForUser(username: String): Flow<List<DhikrProgressEntity>> = appDao.getDhikrProgressForUser(username)

    suspend fun getDhikrProgressByUserAndType(username: String, dhikrType: String): DhikrProgressEntity? =
        appDao.getDhikrProgressByUserAndType(username, dhikrType)

    suspend fun insertDhikrProgress(progress: DhikrProgressEntity) = appDao.insertDhikrProgress(progress)

    // --- Fiqh Bookmarks ---
    fun getFiqhBookmarksForUser(username: String): Flow<List<FiqhBookmarkEntity>> = appDao.getFiqhBookmarksForUser(username)

    suspend fun insertFiqhBookmark(bookmark: FiqhBookmarkEntity) = appDao.insertFiqhBookmark(bookmark)

    suspend fun deleteFiqhBookmark(username: String, bookTitle: String, chapterTitle: String) =
        appDao.deleteFiqhBookmark(username, bookTitle, chapterTitle)

    // --- Quran Bookmarks & Ratings ---
    val allQuranBookmarks: Flow<List<QuranBookmarkEntity>> = appDao.getAllQuranBookmarks()
    val allQuranRatings: Flow<List<QuranRatingEntity>> = appDao.getAllQuranRatings()

    suspend fun insertQuranBookmark(bookmark: QuranBookmarkEntity) = appDao.insertQuranBookmark(bookmark)
    suspend fun deleteQuranBookmark(surahId: Int) = appDao.deleteQuranBookmark(surahId)
    suspend fun insertQuranRating(rating: QuranRatingEntity) = appDao.insertQuranRating(rating)

    // --- Dhikr Reminders ---
    fun getDhikrRemindersForUser(username: String): Flow<List<DhikrReminderEntity>> = appDao.getDhikrRemindersForUser(username)
    suspend fun insertDhikrReminder(reminder: DhikrReminderEntity) = appDao.insertDhikrReminder(reminder)
    suspend fun deleteDhikrReminder(id: Long) = appDao.deleteDhikrReminder(id)
}
