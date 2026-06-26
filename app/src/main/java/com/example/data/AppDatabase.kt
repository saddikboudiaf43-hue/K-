package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        ReportEntity::class,
        ConsultationEntity::class,
        VolunteerActivityEntity::class,
        ImamQuestionEntity::class,
        SchoolLessonEntity::class,
        MosqueBookingEntity::class,
        DhikrProgressEntity::class,
        FiqhBookmarkEntity::class,
        QuranBookmarkEntity::class,
        QuranRatingEntity::class,
        DhikrReminderEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "k_mosque_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.appDao())
                }
            }
        }

        suspend fun populateDatabase(dao: AppDao) {
            // Pre-populate Super Admin
            dao.insertUser(
                UserEntity(
                    username = "saddik",
                    fullName = "Saddik Boudiaf",
                    email = "saddikboudiaf43@gmail.com",
                    phone = "0555 12 34 56",
                    role = "SUPER_ADMIN",
                    badges = "المطور,المشرف العام,أدمن التطبيق",
                    moderatorRole = "عام"
                )
            )

            // Pre-populate President of the Association
            dao.insertUser(
                UserEntity(
                    username = "president",
                    fullName = "الحاج بلقاسم قدور",
                    email = "president@association.dz",
                    phone = "0550 99 88 77",
                    role = "PRESIDENT",
                    badges = "رئيس الجمعية,عضو مؤسس",
                    moderatorRole = "عام"
                )
            )

            // Pre-populate Moderator (Imam)
            dao.insertUser(
                UserEntity(
                    username = "mohamed",
                    fullName = "الشيخ محمد بن علي",
                    email = "mohamed@mosque.dz",
                    phone = "0661 22 33 44",
                    role = "MODERATOR",
                    badges = "إمام المسجد,مشرف عام",
                    moderatorRole = "عام"
                )
            )

            // Pre-populate normal user
            dao.insertUser(
                UserEntity(
                    username = "ahmed",
                    fullName = "أحمد قدور",
                    email = "ahmed@example.com",
                    phone = "0770 99 88 77",
                    role = "VILLAGER",
                    badges = "متطوع نشط",
                    volunteerHours = 12
                )
            )

            // Pre-populate Posts (Village Wall)
            dao.insertPost(
                PostEntity(
                    id = 1,
                    authorUsername = "saddik",
                    authorFullName = "Saddik Boudiaf",
                    authorRole = "SUPER_ADMIN",
                    content = "السلام عليكم ورحمة الله وبركاته.\nمرحباً بكم في تطبيق K™ الخاص بمسجد عثمان بن عفان بقرية القدادرة. هذا التطبيق وقفي مجاني بالكامل في سبيل الله لخدمة أهالي القرية وتوطيد أواصر التعاون والمحبة بيننا. نسأل الله التوفيق والقبول.",
                    likesCount = 15,
                    prayersCount = 28,
                    supportsCount = 10,
                    blessingsCount = 8,
                    commentsCount = 2,
                    category = "عام"
                )
            )
            dao.insertPost(
                PostEntity(
                    id = 2,
                    authorUsername = "mohamed",
                    authorFullName = "الشيخ محمد بن علي",
                    authorRole = "MODERATOR",
                    content = "يسر جمعية المسجد الإعلان عن انطلاق مسابقة حفظ القرآن الكريم الأسبوعية للأطفال والشباب بمقر المسجد. التسجيل مفتوح عبر التطبيق أو مباشرة لدى إمام المسجد. نسأل الله أن يجعل أولادنا من حفظة كتابه الكريم.",
                    likesCount = 8,
                    prayersCount = 20,
                    supportsCount = 5,
                    blessingsCount = 12,
                    commentsCount = 1,
                    category = "مناسبة"
                )
            )
            dao.insertPost(
                PostEntity(
                    id = 3,
                    authorUsername = "president",
                    authorFullName = "الحاج بلقاسم قدور",
                    authorRole = "PRESIDENT",
                    content = "السلام عليكم ورحمة الله وبركاته يا أهل قرية القدادرة الأفاضل. بصفتي رئيساً للجمعية الدينية لمسجد عثمان بن عفان، أرحب بكم جميعاً في هذا التطبيق المبارك الذي يجمع شملنا ويسهل تواصلنا وخدمة بيوت الله.",
                    likesCount = 12,
                    prayersCount = 18,
                    supportsCount = 8,
                    blessingsCount = 6,
                    commentsCount = 0,
                    category = "إعلان"
                )
            )

            // Comments
            dao.insertComment(
                CommentEntity(
                    postId = 1,
                    authorUsername = "ahmed",
                    authorFullName = "أحمد قدور",
                    content = "بارك الله فيكم وجزاكم الله كل خير على هذا العمل الطيب والوقف المبارك لخدمة القدادرة."
                )
            )
            dao.insertComment(
                CommentEntity(
                    postId = 1,
                    authorUsername = "mohamed",
                    authorFullName = "الشيخ محمد بن علي",
                    content = "ما شاء الله تبارك الرحمن، تطبيق مبارك وخطوة متميزة لجمع شمل أهالي القرية."
                )
            )
            dao.insertComment(
                CommentEntity(
                    postId = 2,
                    authorUsername = "ahmed",
                    authorFullName = "أحمد قدور",
                    content = "تم تسجيل ابني عبد الرحمن والحمد لله، جزاكم الله خيراً."
                )
            )

            // Volunteer Activities
            dao.insertVolunteerActivity(
                VolunteerActivityEntity(
                    id = 1,
                    title = "حملة تنظيف مقبرة القرية",
                    description = "تنظم جمعية مسجد عثمان بن عفان حملة تطوعية لتنظيف وإعادة تهيئة مقبرة قرية القدادرة ونزع الأعشاب الضارة وإصلاح الممرات. يرجى إحضار أدوات العمل المتوفرة.",
                    date = "الجمعة المقبلة بعد صلاة الصبح",
                    points = 20,
                    requiredVolunteers = 15,
                    joinedCount = 3,
                    joinedUsernames = "saddik,ahmed,mohamed"
                )
            )
            dao.insertVolunteerActivity(
                VolunteerActivityEntity(
                    id = 2,
                    title = "حملة تشجير محيط المسجد والشارع الرئيسي",
                    description = "مبادرة لغرس 100 شجرة زيتون وظل في محيط المسجد وشارع القرية الرئيسي لإعطاء مظهر جمالي وتوفير الظل للجيران والمارة.",
                    date = "السبت صباحاً",
                    points = 15,
                    requiredVolunteers = 10,
                    joinedCount = 1,
                    joinedUsernames = "saddik"
                )
            )

            // School Lessons (Algerian Syllabus)
            dao.insertSchoolLesson(
                SchoolLessonEntity(
                    id = 1,
                    grade = "ابتدائي",
                    subject = "التربية الإسلامية",
                    title = "من صفات المؤمن (الصدق والأمانة)",
                    summary = "شرح مبسط لقيم الصدق والأمانة في حياة التلميذ المسلم مستشهداً بآيات من القرآن الكريم وأحاديث نبوية شريفة لتعليم تلاميذ السنة الخامسة ابتدائي.",
                    volunteerTeacherName = "الأستاذ مراد بلقاسم"
                )
            )
            dao.insertSchoolLesson(
                SchoolLessonEntity(
                    id = 2,
                    grade = "متوسط",
                    subject = "الرياضيات",
                    title = "شرح مفصل لنظرية فيثاغورس وتطبيقاتها",
                    summary = "ملخص رائع مع تمارين محلولة حول كيفية حساب أطوال أضلاع المثلث القائم باستعمال نظرية فيثاغورس لتلاميذ السنة الثالثة متوسط.",
                    volunteerTeacherName = "الأستاذة حنان حداد"
                )
            )
            dao.insertSchoolLesson(
                SchoolLessonEntity(
                    id = 3,
                    grade = "ثانوي",
                    subject = "اللغة العربية",
                    title = "النزعة العقلية في العصر العباسي الثاني",
                    summary = "تحليل أدبي شامل للنزعة العقلية وأثرها في الشعر والأدب خلال العصر العباسي، مع نموذج امتحان مقترح وتصحيحه النموذجي لشعبة آداب وفلسفة.",
                    volunteerTeacherName = "الأستاذ عبد الكريم قدادري"
                )
            )

            // Imam Questions
            dao.insertImamQuestion(
                ImamQuestionEntity(
                    id = 1,
                    askerUsername = "ahmed",
                    question = "ما حكم من دخل المسجد والإمام يخطب الجمعة، هل يصلي تحية المسجد أم يجلس مباشرة؟",
                    answer = "الحمد لله والصلاة والسلام على رسول الله. يشرع لداخل المسجد يوم الجمعة أثناء الخطبة أن يصلي ركعتين خفيفتين (تحية المسجد) قبل الجلوس، لقول النبي صلى الله عليه وسلم: 'إذا جاء أحدكم يوم الجمعة، والإمام يخطب، فليركع ركعتين، وليتجوز فيهما'. والله أعلم.",
                    isPublic = true
                )
            )
        }
    }
}
