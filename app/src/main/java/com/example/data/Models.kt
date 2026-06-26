package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String, // Real real name or unique username is required
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String, // "SUPER_ADMIN", "PRESIDENT", "MODERATOR", "VILLAGER"
    val isBlocked: Boolean = false,
    val blockReason: String = "",
    val volunteerHours: Int = 0,
    val groupPresence: Int = 0,
    val fastingDays: Int = 0,
    val badges: String = "", // Comma-separated like "متطوع,حافظ,عضو جمعية"
    val moderatorRole: String = "" // "عام", "حائط", "دعوي", "إعلانات", "قانوني"
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorUsername: String,
    val authorFullName: String,
    val authorRole: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val prayersCount: Int = 0,
    val supportsCount: Int = 0,
    val blessingsCount: Int = 0,
    val commentsCount: Int = 0,
    val visibility: String = "عام", // "عام", "أصدقاء", "خاص"
    val category: String = "عام", // "عام", "تطوع", "مناسبة", "مستعجل"
    val mediaType: String = "none", // "none", "image", "video"
    val mediaPlaceholder: String = "" // Keyword for drawable/colored icon
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorUsername: String,
    val authorFullName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val postContent: String,
    val postAuthor: String,
    val reporterUsername: String,
    val reason: String, // "تحريض", "إباحية", "خطاب كراهية", "احتيال"
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)

@Entity(tableName = "consultations")
data class ConsultationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val question: String,
    val answer: String = "",
    val isAnonymous: Boolean = true,
    val category: String = "عائلي", // "عائلي", "اجتماعي", "تربية"
    val status: String = "معلق", // "معلق", "تم الرد"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "volunteer_activities")
data class VolunteerActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val location: String = "مسجد عثمان بن عفان",
    val date: String,
    val points: Int = 10,
    val requiredVolunteers: Int = 5,
    val joinedCount: Int = 0,
    val joinedUsernames: String = "" // Comma-separated usernames
)

@Entity(tableName = "imam_questions")
data class ImamQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val askerUsername: String,
    val question: String,
    val answer: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isPublic: Boolean = false
)

@Entity(tableName = "school_lessons")
data class SchoolLessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val grade: String, // "ابتدائي", "متوسط", "ثانوي"
    val subject: String, // "اللغة العربية", "الرياضيات", "العلوم", "التربية الإسلامية"
    val title: String,
    val summary: String,
    val contentPdfUrl: String = "",
    val testQuestions: String = "", // JSON format of Q&A
    val volunteerTeacherName: String = ""
)

@Entity(tableName = "bookings")
data class MosqueBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, // "عقيقة", "وليمة", "مأتم"
    val applicantName: String,
    val date: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "dhikr_progress")
data class DhikrProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val dhikrType: String,
    val count: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "fiqh_bookmarks")
data class FiqhBookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val bookTitle: String,
    val chapterTitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_bookmarks")
data class QuranBookmarkEntity(
    @PrimaryKey val surahId: Int,
    val verseNumber: Int,
    val surahName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_ratings")
data class QuranRatingEntity(
    @PrimaryKey val verseKey: String, // format: "surahId_verseNum"
    val rating: Int, // 1 to 5 stars
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "dhikr_reminders")
data class DhikrReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val dhikrType: String, // "التسبيح", "الحوقلة", "الصلاة على النبي ﷺ", "الاستغفار"
    val intervalSeconds: Int,
    val alertType: String, // "صوت هادئ", "نطق صوتي", "تنبيه اهتزازي", "تنبيه مرئي ومسموع"
    val isEnabled: Boolean = true,
    val lastTriggered: Long = 0L,
    val customNote: String = ""
)


