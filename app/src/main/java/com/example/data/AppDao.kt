package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Users ---
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'MODERATOR'")
    fun getModerators(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET isBlocked = :blocked, blockReason = :reason WHERE username = :username")
    suspend fun setUserBlockedState(username: String, blocked: Boolean, reason: String)

    @Query("UPDATE users SET role = :role, moderatorRole = :modRole WHERE username = :username")
    suspend fun setUserRole(username: String, role: String, modRole: String)

    @Query("UPDATE users SET volunteerHours = volunteerHours + :hours WHERE username = :username")
    suspend fun incrementVolunteerHours(username: String, hours: Int)

    @Query("UPDATE users SET fastingDays = fastingDays + 1 WHERE username = :username")
    suspend fun incrementFastingDays(username: String)

    @Query("UPDATE users SET groupPresence = groupPresence + 1 WHERE username = :username")
    suspend fun incrementGroupPresence(username: String)

    // --- Posts ---
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Long)

    @Query("UPDATE posts SET likesCount = likesCount + 1 WHERE id = :postId")
    suspend fun incrementLike(postId: Long)

    @Query("UPDATE posts SET prayersCount = prayersCount + 1 WHERE id = :postId")
    suspend fun incrementPrayer(postId: Long)

    @Query("UPDATE posts SET supportsCount = supportsCount + 1 WHERE id = :postId")
    suspend fun incrementSupport(postId: Long)

    @Query("UPDATE posts SET blessingsCount = blessingsCount + 1 WHERE id = :postId")
    suspend fun incrementBlessing(postId: Long)

    @Query("UPDATE posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    suspend fun incrementCommentsCount(postId: Long)

    // --- Comments ---
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun deleteCommentsForPost(postId: Long)

    // --- Reports ---
    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("UPDATE reports SET isResolved = :resolved WHERE id = :reportId")
    suspend fun resolveReport(reportId: Long, resolved: Boolean)

    @Query("DELETE FROM reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: Long)

    // --- Consultations ---
    @Query("SELECT * FROM consultations ORDER BY timestamp DESC")
    fun getAllConsultations(): Flow<List<ConsultationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultation(consultation: ConsultationEntity)

    @Query("UPDATE consultations SET answer = :answer, status = 'تم الرد' WHERE id = :id")
    suspend fun answerConsultation(id: Long, answer: String)

    // --- Volunteer Activities ---
    @Query("SELECT * FROM volunteer_activities ORDER BY id DESC")
    fun getAllVolunteerActivities(): Flow<List<VolunteerActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteerActivity(activity: VolunteerActivityEntity)

    @Query("UPDATE volunteer_activities SET joinedCount = joinedCount + 1, joinedUsernames = :joinedUsernames WHERE id = :activityId")
    suspend fun joinVolunteerActivity(activityId: Long, joinedUsernames: String)

    @Query("UPDATE volunteer_activities SET joinedCount = joinedCount - 1, joinedUsernames = :joinedUsernames WHERE id = :activityId")
    suspend fun leaveVolunteerActivity(activityId: Long, joinedUsernames: String)

    @Query("DELETE FROM volunteer_activities WHERE id = :activityId")
    suspend fun deleteVolunteerActivity(activityId: Long)

    // --- Imam Questions ---
    @Query("SELECT * FROM imam_questions ORDER BY timestamp DESC")
    fun getAllImamQuestions(): Flow<List<ImamQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImamQuestion(question: ImamQuestionEntity)

    @Query("UPDATE imam_questions SET answer = :answer, isPublic = :isPublic WHERE id = :id")
    suspend fun answerImamQuestion(id: Long, answer: String, isPublic: Boolean)

    // --- School Lessons ---
    @Query("SELECT * FROM school_lessons ORDER BY id DESC")
    fun getAllSchoolLessons(): Flow<List<SchoolLessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchoolLesson(lesson: SchoolLessonEntity)

    // --- Mosque Bookings ---
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<MosqueBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: MosqueBookingEntity)

    @Query("DELETE FROM bookings WHERE id = :bookingId")
    suspend fun deleteBooking(bookingId: Long)

    // --- Dhikr Progress ---
    @Query("SELECT * FROM dhikr_progress WHERE username = :username")
    fun getDhikrProgressForUser(username: String): Flow<List<DhikrProgressEntity>>

    @Query("SELECT * FROM dhikr_progress WHERE username = :username AND dhikrType = :dhikrType LIMIT 1")
    suspend fun getDhikrProgressByUserAndType(username: String, dhikrType: String): DhikrProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDhikrProgress(progress: DhikrProgressEntity)

    // --- Fiqh Bookmarks ---
    @Query("SELECT * FROM fiqh_bookmarks WHERE username = :username")
    fun getFiqhBookmarksForUser(username: String): Flow<List<FiqhBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiqhBookmark(bookmark: FiqhBookmarkEntity)

    @Query("DELETE FROM fiqh_bookmarks WHERE username = :username AND bookTitle = :bookTitle AND chapterTitle = :chapterTitle")
    suspend fun deleteFiqhBookmark(username: String, bookTitle: String, chapterTitle: String)

    // --- Quran Bookmarks ---
    @Query("SELECT * FROM quran_bookmarks")
    fun getAllQuranBookmarks(): Flow<List<QuranBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranBookmark(bookmark: QuranBookmarkEntity)

    @Query("DELETE FROM quran_bookmarks WHERE surahId = :surahId")
    suspend fun deleteQuranBookmark(surahId: Int)

    // --- Quran Ratings ---
    @Query("SELECT * FROM quran_ratings")
    fun getAllQuranRatings(): Flow<List<QuranRatingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranRating(rating: QuranRatingEntity)

    // --- Dhikr Reminders ---
    @Query("SELECT * FROM dhikr_reminders WHERE username = :username")
    fun getDhikrRemindersForUser(username: String): Flow<List<DhikrReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDhikrReminder(reminder: DhikrReminderEntity)

    @Query("DELETE FROM dhikr_reminders WHERE id = :id")
    suspend fun deleteDhikrReminder(id: Long)
}
