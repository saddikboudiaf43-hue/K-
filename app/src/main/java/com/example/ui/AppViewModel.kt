package com.example.ui

import android.app.Application
import android.media.MediaPlayer
import android.media.AudioManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.ui.theme.AppThemeStyle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class Reciter(
    val id: String,
    val name: String,
    val baseUrl: String
)

data class Muezzin(
    val id: String,
    val name: String,
    val description: String,
    val voiceType: String = "حجازي عذب", // "حجازي عذب", "جزائري أصيل", "مدني خاشع"
    val isCustomVoice: Boolean = false,
    val audioPath: String = ""
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = AppRepository(database.appDao())

    // --- Active User & Role ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Theme State ---
    private val _currentTheme = MutableStateFlow(AppThemeStyle.GREEN)
    val currentTheme: StateFlow<AppThemeStyle> = _currentTheme.asStateFlow()

    // --- Install Banner State (Persistent) ---
    private val sharedPrefs = application.getSharedPreferences("village_app_prefs", android.content.Context.MODE_PRIVATE)
    private val _showInstallBanner = MutableStateFlow(sharedPrefs.getBoolean("show_install_banner", true))
    val showInstallBanner: StateFlow<Boolean> = _showInstallBanner.asStateFlow()

    // --- Font Scale State (Persistent) ---
    private val _fontScale = MutableStateFlow(sharedPrefs.getFloat("font_scale", 1.0f))
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    fun setFontScale(scale: Float) {
        _fontScale.value = scale
        sharedPrefs.edit().putFloat("font_scale", scale).apply()
    }

    // --- Vibration feedback state (Persistent) ---
    private val _vibrationEnabled = MutableStateFlow(sharedPrefs.getBoolean("vibration_enabled", true))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    fun setVibrationEnabled(enabled: Boolean) {
        _vibrationEnabled.value = enabled
        sharedPrefs.edit().putBoolean("vibration_enabled", enabled).apply()
    }

    // --- Speech alert states (Persistent) ---
    private val _speechAlertsEnabled = MutableStateFlow(sharedPrefs.getBoolean("speech_alerts_enabled", true))
    val speechAlertsEnabled: StateFlow<Boolean> = _speechAlertsEnabled.asStateFlow()

    fun setSpeechAlertsEnabled(enabled: Boolean) {
        _speechAlertsEnabled.value = enabled
        sharedPrefs.edit().putBoolean("speech_alerts_enabled", enabled).apply()
    }

    // --- High contrast visual state (Persistent) ---
    private val _highContrastEnabled = MutableStateFlow(sharedPrefs.getBoolean("high_contrast_enabled", false))
    val highContrastEnabled: StateFlow<Boolean> = _highContrastEnabled.asStateFlow()

    fun setHighContrastEnabled(enabled: Boolean) {
        _highContrastEnabled.value = enabled
        sharedPrefs.edit().putBoolean("high_contrast_enabled", enabled).apply()
    }

    fun dismissInstallBanner() {
        _showInstallBanner.value = false
        sharedPrefs.edit().putBoolean("show_install_banner", false).apply()
    }

    fun resetInstallBanner() {
        _showInstallBanner.value = true
        sharedPrefs.edit().putBoolean("show_install_banner", true).apply()
    }

    // ==================== MUEZZIN & ADHAN AUTOMATION STATE & LOGIC ====================
    private fun saveMuezzinsToPrefs(list: List<Muezzin>) {
        val serialized = list.joinToString("\n") { "${it.id}##${it.name}##${it.description}##${it.voiceType}##${it.isCustomVoice}##${it.audioPath}" }
        sharedPrefs.edit().putString("muezzins_list_custom", serialized).apply()
    }

    private fun loadMuezzinsFromPrefs(): List<Muezzin> {
        val defaultList = listOf(
            Muezzin("muezzin_1", "الشيخ عبد المجيد", "الأذان المدني الجهوري الشجي", "حجازي عذب", false),
            Muezzin("muezzin_2", "المؤذن الشاب سفيان", "أداء خاشع بأسلوب جزائري شجي أصيل", "جزائري أصيل", false),
            Muezzin("muezzin_3", "العم الحاج بلقاسم", "أذان المسجد العتيق بالقرية بصوت دافئ ومهيب", "مدني خاشع", false)
        )
        val serialized = sharedPrefs.getString("muezzins_list_custom", null) ?: return defaultList
        if (serialized.isBlank()) return defaultList
        return try {
            serialized.split("\n").map { line ->
                val parts = line.split("##")
                Muezzin(
                    id = parts[0],
                    name = parts[1],
                    description = parts[2],
                    voiceType = parts[3],
                    isCustomVoice = parts[4].toBoolean(),
                    audioPath = if (parts.size > 5) parts[5] else ""
                )
            }
        } catch (e: Exception) {
            defaultList
        }
    }

    private fun savePrayerMuezzinsToPrefs(map: Map<String, String>) {
        val serialized = map.entries.joinToString(",") { "${it.key}:${it.value}" }
        sharedPrefs.edit().putString("prayer_muezzins_map", serialized).apply()
    }

    private fun loadPrayerMuezzinsFromPrefs(): Map<String, String> {
        val defaultMap = mapOf(
            "الصبح" to "muezzin_1",
            "الشروق" to "muezzin_1",
            "الظهر" to "muezzin_2",
            "العصر" to "muezzin_2",
            "المغرب" to "muezzin_3",
            "العشاء" to "muezzin_3"
        )
        val serialized = sharedPrefs.getString("prayer_muezzins_map", null) ?: return defaultMap
        if (serialized.isBlank()) return defaultMap
        return try {
            serialized.split(",").associate {
                val parts = it.split(":")
                parts[0] to parts[1]
            }
        } catch (e: Exception) {
            defaultMap
        }
    }

    private val _muezzins = MutableStateFlow<List<Muezzin>>(loadMuezzinsFromPrefs())
    val muezzins: StateFlow<List<Muezzin>> = _muezzins.asStateFlow()

    private val _prayerMuezzins = MutableStateFlow<Map<String, String>>(loadPrayerMuezzinsFromPrefs())
    val prayerMuezzins: StateFlow<Map<String, String>> = _prayerMuezzins.asStateFlow()

    private val _autoAdhanEnabled = MutableStateFlow(sharedPrefs.getBoolean("auto_adhan_enabled", true))
    val autoAdhanEnabled: StateFlow<Boolean> = _autoAdhanEnabled.asStateFlow()

    fun setAutoAdhanEnabled(enabled: Boolean) {
        _autoAdhanEnabled.value = enabled
        sharedPrefs.edit().putBoolean("auto_adhan_enabled", enabled).apply()
    }

    // Recording simulation states
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingTimeRemaining = MutableStateFlow(0)
    val recordingTimeRemaining: StateFlow<Int> = _recordingTimeRemaining.asStateFlow()

    // Adhan Player emulation / real player
    private val _isAdhanPlaying = MutableStateFlow(false)
    val isAdhanPlaying: StateFlow<Boolean> = _isAdhanPlaying.asStateFlow()

    private val _activeAdhanPrayer = MutableStateFlow("")
    val activeAdhanPrayer: StateFlow<String> = _activeAdhanPrayer.asStateFlow()

    private val _activeAdhanMuezzin = MutableStateFlow<Muezzin?>(null)
    val activeAdhanMuezzin: StateFlow<Muezzin?> = _activeAdhanMuezzin.asStateFlow()

    private val _adhanProgress = MutableStateFlow(0.0f)
    val adhanProgress: StateFlow<Float> = _adhanProgress.asStateFlow()

    private var adhanPlayer: MediaPlayer? = null
    private var adhanProgressJob: kotlinx.coroutines.Job? = null

    fun addMuezzin(name: String, description: String, voiceType: String) {
        val id = "muezzin_${System.currentTimeMillis()}"
        val newMuezzin = Muezzin(id, name, description, voiceType, false)
        val updatedList = _muezzins.value.toMutableList().apply { add(newMuezzin) }
        _muezzins.value = updatedList
        saveMuezzinsToPrefs(updatedList)
    }

    fun deleteMuezzin(id: String) {
        val updatedList = _muezzins.value.filter { it.id != id }
        _muezzins.value = updatedList
        saveMuezzinsToPrefs(updatedList)

        // Reset assignments pointing to deleted muezzin
        val updatedMap = _prayerMuezzins.value.toMutableMap()
        updatedMap.forEach { (prayer, assignedId) ->
            if (assignedId == id) {
                updatedMap[prayer] = "muezzin_1" // Reset to first default as fallback
            }
        }
        _prayerMuezzins.value = updatedMap
        savePrayerMuezzinsToPrefs(updatedMap)
    }

    fun assignMuezzinToPrayer(prayerName: String, muezzinId: String) {
        val updatedMap = _prayerMuezzins.value.toMutableMap().apply { put(prayerName, muezzinId) }
        _prayerMuezzins.value = updatedMap
        savePrayerMuezzinsToPrefs(updatedMap)
    }

    fun startSimulatedMuezzinRecording(muezzinName: String, description: String, voiceType: String, onFinished: (Muezzin) -> Unit) {
        if (_isRecording.value) return
        _isRecording.value = true
        _recordingTimeRemaining.value = 5 // 5 seconds simulation of voice processing

        viewModelScope.launch {
            while (_recordingTimeRemaining.value > 0) {
                delay(1000)
                _recordingTimeRemaining.value -= 1
            }
            _isRecording.value = false

            val id = "muezzin_custom_${System.currentTimeMillis()}"
            val newMuezzin = Muezzin(
                id = id,
                name = muezzinName,
                description = description,
                voiceType = voiceType,
                isCustomVoice = true,
                audioPath = "مسجل ومحفوظ تلقائياً بنظام القرية 🎙️"
            )

            val updatedList = _muezzins.value.toMutableList().apply { add(newMuezzin) }
            _muezzins.value = updatedList
            saveMuezzinsToPrefs(updatedList)
            onFinished(newMuezzin)
        }
    }

    fun playMuezzinAdhan(prayerName: String, muezzin: Muezzin) {
        stopMuezzinAdhan()

        _isAdhanPlaying.value = true
        _activeAdhanPrayer.value = prayerName
        _activeAdhanMuezzin.value = muezzin
        _adhanProgress.value = 0.0f

        val audioUrl = when (muezzin.voiceType) {
            "حجازي عذب" -> "https://www.islamcan.com/audio/adhan/azan1.mp3"
            "جزائري أصيل" -> "https://www.islamcan.com/audio/adhan/azan15.mp3"
            "مدني خاشع" -> "https://www.islamcan.com/audio/adhan/azan2.mp3"
            else -> "https://www.islamcan.com/audio/adhan/azan1.mp3"
        }

        viewModelScope.launch {
            try {
                adhanPlayer = MediaPlayer().apply {
                    setDataSource(audioUrl)
                    setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
                    setOnPreparedListener { mp ->
                        mp.start()
                        adhanProgressJob = viewModelScope.launch {
                            while (mp.isPlaying) {
                                val duration = mp.duration.toFloat()
                                if (duration > 0f) {
                                    _adhanProgress.value = mp.currentPosition.toFloat() / duration
                                }
                                delay(500)
                            }
                            stopMuezzinAdhan()
                        }
                    }
                    setOnCompletionListener {
                        stopMuezzinAdhan()
                    }
                    setOnErrorListener { _, _, _ ->
                        try {
                            adhanPlayer?.release()
                        } catch (ex: Exception) {}
                        adhanPlayer = null
                        runSimulatedAdhan(prayerName, muezzin)
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runSimulatedAdhan(prayerName, muezzin)
            }
        }
    }

    private fun runSimulatedAdhan(prayerName: String, muezzin: Muezzin) {
        adhanProgressJob?.cancel()
        _isAdhanPlaying.value = true
        _activeAdhanPrayer.value = prayerName
        _activeAdhanMuezzin.value = muezzin
        adhanProgressJob = viewModelScope.launch {
            val durationMs = 20000 // 20 sec fallback simulation
            val stepMs = 500
            var elapsedMs = 0
            while (elapsedMs < durationMs && _isAdhanPlaying.value) {
                delay(stepMs.toLong())
                elapsedMs += stepMs
                _adhanProgress.value = elapsedMs.toFloat() / durationMs.toFloat()
            }
            stopMuezzinAdhan()
        }
    }

    fun stopMuezzinAdhan() {
        _isAdhanPlaying.value = false
        _activeAdhanPrayer.value = ""
        _activeAdhanMuezzin.value = null
        _adhanProgress.value = 0.0f
        adhanProgressJob?.cancel()
        adhanProgressJob = null
        try {
            adhanPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        adhanPlayer = null
    }

    fun updateCurrentUserProfile(fullName: String, phone: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = user.copy(fullName = fullName, phone = phone)
            repository.insertUser(updatedUser)
            _currentUser.value = updatedUser
        }
    }

    // --- Posts (Village Wall) ---
    val allPosts: StateFlow<List<PostEntity>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Comments Map ---
    private val _currentPostComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val currentPostComments: StateFlow<List<CommentEntity>> = _currentPostComments.asStateFlow()

    // --- Reports ---
    val allReports: StateFlow<List<ReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Consultations ---
    val allConsultations: StateFlow<List<ConsultationEntity>> = repository.allConsultations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Volunteer Activities ---
    val allVolunteerActivities: StateFlow<List<VolunteerActivityEntity>> = repository.allVolunteerActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Imam Questions ---
    val allImamQuestions: StateFlow<List<ImamQuestionEntity>> = repository.allImamQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- School Lessons ---
    val allSchoolLessons: StateFlow<List<SchoolLessonEntity>> = repository.allSchoolLessons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Mosque Bookings ---
    val allBookings: StateFlow<List<MosqueBookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Audio Quran Player Emulation State & Real Streaming ---
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _activeSurahId = MutableStateFlow(1)
    val activeSurahId: StateFlow<Int> = _activeSurahId.asStateFlow()

    private val _activeVerseIndex = MutableStateFlow(0)
    val activeVerseIndex: StateFlow<Int> = _activeVerseIndex.asStateFlow()

    private val _repeatCount = MutableStateFlow(1) // 1x, 3x, 5x
    val repeatCount: StateFlow<Int> = _repeatCount.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f) // 1.0x, 1.25x, 1.5x
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _showTafsir = MutableStateFlow(false)
    val showTafsir: StateFlow<Boolean> = _showTafsir.asStateFlow()

    private val _memorizedVerses = MutableStateFlow<Map<String, String>>(emptyMap()) // Key: "surahId_verseNum", Value: "حفظت"/"أراجع"/"صعب"
    val memorizedVerses: StateFlow<Map<String, String>> = _memorizedVerses.asStateFlow()

    // --- Firestore Quran State & Fallback ---
    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    private val _firestoreQuranSurahs = MutableStateFlow<List<QuranSurah>>(emptyList())
    val quranSurahs: StateFlow<List<QuranSurah>> = _firestoreQuranSurahs.asStateFlow()

    // --- Quran Bookmarks & Ratings State ---
    val quranBookmarks: StateFlow<List<QuranBookmarkEntity>> = repository.allQuranBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quranRatings: StateFlow<List<QuranRatingEntity>> = repository.allQuranRatings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Audio Reciter List ---
    val recitersList = listOf(
        Reciter("hussary", "الشيخ محمود خليل الحصري", "https://download.quranicaudio.com/quran/mahmood_khaleel_al-husaree/"),
        Reciter("minshawi", "الشيخ محمد صديق المنشاوي", "https://download.quranicaudio.com/quran/muhammad_siddeeq_al-minshaawee/"),
        Reciter("abdulbasit", "الشيخ عبد الباسط عبد الصمد", "https://download.quranicaudio.com/quran/abdul_baasit_muhammad_abdus-samad/"),
        Reciter("hudhaify", "الشيخ علي الحذيفي", "https://download.quranicaudio.com/quran/ali_alhudaifi/")
    )

    private val _selectedReciter = MutableStateFlow(recitersList[0])
    val selectedReciter: StateFlow<Reciter> = _selectedReciter.asStateFlow()

    // --- MediaPlayer & Progress Tracking ---
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: kotlinx.coroutines.Job? = null

    private val _audioStatus = MutableStateFlow("موقف") // "موقف", "جاري التحميل...", "شغال", "خطأ في الاتصال"
    val audioStatus: StateFlow<String> = _audioStatus.asStateFlow()

    private val _audioDuration = MutableStateFlow(0)
    val audioDuration: StateFlow<Int> = _audioDuration.asStateFlow()

    private val _audioPosition = MutableStateFlow(0)
    val audioPosition: StateFlow<Int> = _audioPosition.asStateFlow()

    // --- Dhikr Reminders States ---
    private val _triggeredReminder = MutableStateFlow<DhikrReminderEntity?>(null)
    val triggeredReminder: StateFlow<DhikrReminderEntity?> = _triggeredReminder.asStateFlow()

    private val _reminderSecondsRemaining = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val reminderSecondsRemaining: StateFlow<Map<Long, Int>> = _reminderSecondsRemaining.asStateFlow()

    private var reminderJob: kotlinx.coroutines.Job? = null
    private var tts: android.speech.tts.TextToSpeech? = null

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val dhikrReminders: StateFlow<List<DhikrReminderEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getDhikrRemindersForUser(user.username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startRemindersTicker() {
        reminderJob?.cancel()
        reminderJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val currentReminders = dhikrReminders.value
                val remainingMap = _reminderSecondsRemaining.value.toMutableMap()

                currentReminders.forEach { reminder ->
                    if (reminder.isEnabled) {
                        val currentVal = remainingMap[reminder.id] ?: reminder.intervalSeconds
                        if (currentVal <= 1) {
                            triggerDhikrReminder(reminder)
                            remainingMap[reminder.id] = reminder.intervalSeconds
                        } else {
                            remainingMap[reminder.id] = currentVal - 1
                        }
                    } else {
                        remainingMap.remove(reminder.id)
                    }
                }
                _reminderSecondsRemaining.value = remainingMap
            }
        }
    }

    private fun triggerDhikrReminder(reminder: DhikrReminderEntity) {
        _triggeredReminder.value = reminder
        incrementDhikrCount(reminder.dhikrType, 1)
        playCustomAlertSound(reminder)
    }

    fun dismissTriggeredReminder() {
        _triggeredReminder.value = null
    }

    fun playCustomAlertSound(reminder: DhikrReminderEntity) {
        viewModelScope.launch {
            try {
                when (reminder.alertType) {
                    "نطق صوتي" -> {
                        if (_speechAlertsEnabled.value) {
                            val phrase = when (reminder.dhikrType) {
                                "التسبيح" -> "سبحان الله وبحمده، سبحان الله العظيم"
                                "الحوقلة" -> "لا حول ولا قوة إلا بالله العلي العظيم"
                                "الصلاة على النبي ﷺ" -> "اللهم صلِّ وسلم وبارك على نبينا محمد"
                                "الاستغفار" -> "أستغفر الله العظيم وأتوب إليه"
                                else -> reminder.dhikrType
                            }
                            tts?.speak(phrase, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }
                    "صوت هادئ" -> {
                        val toneG = android.media.ToneGenerator(android.media.AudioManager.STREAM_ALARM, 80)
                        toneG.startTone(android.media.ToneGenerator.TONE_CDMA_PIP, 250)
                    }
                    "تنبيه اهتزازي" -> {
                        if (_vibrationEnabled.value) {
                            val vibrator = getApplication<Application>().getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                            vibrator?.let {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    it.vibrate(android.os.VibrationEffect.createOneShot(500, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                                } else {
                                    it.vibrate(500)
                                }
                            }
                        }
                    }
                    "تنبيه مرئي ومسموع" -> {
                        val toneG = android.media.ToneGenerator(android.media.AudioManager.STREAM_ALARM, 100)
                        toneG.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 300)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveDhikrReminder(dhikrType: String, intervalSeconds: Int, alertType: String, customNote: String = "") {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val reminder = DhikrReminderEntity(
                username = user.username,
                dhikrType = dhikrType,
                intervalSeconds = intervalSeconds,
                alertType = alertType,
                isEnabled = true,
                customNote = customNote
            )
            repository.insertDhikrReminder(reminder)
        }
    }

    fun toggleDhikrReminder(reminder: DhikrReminderEntity) {
        viewModelScope.launch {
            repository.insertDhikrReminder(reminder.copy(isEnabled = !reminder.isEnabled))
        }
    }

    fun deleteDhikrReminder(id: Long) {
        viewModelScope.launch {
            repository.deleteDhikrReminder(id)
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val dhikrProgress: StateFlow<List<DhikrProgressEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getDhikrProgressForUser(user.username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val fiqhBookmarks: StateFlow<List<FiqhBookmarkEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getFiqhBookmarksForUser(user.username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun incrementDhikrCount(dhikrType: String, increment: Int = 1) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val existing = repository.getDhikrProgressByUserAndType(user.username, dhikrType)
            if (existing != null) {
                repository.insertDhikrProgress(existing.copy(count = existing.count + increment, lastUpdated = System.currentTimeMillis()))
            } else {
                repository.insertDhikrProgress(
                    DhikrProgressEntity(
                        username = user.username,
                        dhikrType = dhikrType,
                        count = increment
                    )
                )
            }
        }
    }

    fun toggleFiqhBookmark(bookTitle: String, chapterTitle: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val bookmarks = fiqhBookmarks.value
            val exists = bookmarks.any { it.bookTitle == bookTitle && it.chapterTitle == chapterTitle }
            if (exists) {
                repository.deleteFiqhBookmark(user.username, bookTitle, chapterTitle)
            } else {
                repository.insertFiqhBookmark(
                    FiqhBookmarkEntity(
                        username = user.username,
                        bookTitle = bookTitle,
                        chapterTitle = chapterTitle
                    )
                )
            }
        }
    }

    // --- Blood Bank Registrants ---
    private val _bloodDonors = MutableStateFlow(
        listOf(
            BloodDonor("أحمد قدور", "A+", "0770 99 88 77", "القدادرة"),
            BloodDonor("سليم بلقاسم", "O-", "0550 11 22 33", "القدادرة"),
            BloodDonor("عبد الرؤوف ديدين", "B+", "0664 55 66 77", "القدادرة")
        )
    )
    val bloodDonors: StateFlow<List<BloodDonor>> = _bloodDonors.asStateFlow()

    // --- Lost & Found State ---
    private val _lostItems = MutableStateFlow(
        listOf(
            LostItem("مفاتيح سيارة رونو", "مفاتيح بها ميدالية فضية عثر عليها أمام المسجد", "0661 22 33 44", System.currentTimeMillis() - 7200000),
            LostItem("محفظة وثائق طبية", "محفظة سوداء مفقودة بين الشارع الرئيسي والمسجد", "0555 12 34 56", System.currentTimeMillis() - 86400000)
        )
    )
    val lostItems: StateFlow<List<LostItem>> = _lostItems.asStateFlow()

    init {
        // Auto-login as default Ahmed on startup, but can change
        viewModelScope.launch {
            val defaultAhmed = repository.getUserByUsername("ahmed")
            _currentUser.value = defaultAhmed
        }
        loadQuranFromFirestore()

        // Setup TextToSpeech for customized Dhikr voice alerts
        try {
            tts = android.speech.tts.TextToSpeech(getApplication()) { status ->
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    tts?.language = java.util.Locale("ar")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Start periodic Dhikr reminders ticker background loop
        startRemindersTicker()
    }

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        reminderJob?.cancel()
    }

    // --- Authentication ---
    fun selectUser(username: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            if (user != null) {
                _currentUser.value = user
            }
        }
    }

    fun loginOrCreateUser(username: String, fullName: String, phone: String, email: String, isRegistering: Boolean) {
        viewModelScope.launch {
            val existing = repository.getUserByUsername(username)
            if (existing != null) {
                if (existing.isBlocked) {
                    // Blocked!
                    _currentUser.value = existing
                } else {
                    _currentUser.value = existing
                }
            } else {
                val newUser = UserEntity(
                    username = username.lowercase().trim(),
                    fullName = fullName,
                    phone = phone,
                    email = email,
                    role = if (username.lowercase() == "saddik") "SUPER_ADMIN" else "VILLAGER"
                )
                repository.insertUser(newUser)
                _currentUser.value = newUser
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    // --- Theme Switch ---
    fun changeTheme(style: AppThemeStyle) {
        _currentTheme.value = style
    }

    // --- Quran Player Actions ---
    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun setRepeatCount(count: Int) {
        _repeatCount.value = count
    }

    fun toggleTafsir() {
        _showTafsir.value = !_showTafsir.value
    }

    fun setActiveSurah(surahId: Int) {
        _activeSurahId.value = surahId
        _activeVerseIndex.value = 0
    }

    fun selectVerse(index: Int) {
        _activeVerseIndex.value = index
    }

    fun markVerseStatus(surahId: Int, verseNum: Int, status: String) {
        val currentMap = _memorizedVerses.value.toMutableMap()
        val key = "${surahId}_${verseNum}"
        currentMap[key] = status
        _memorizedVerses.value = currentMap
    }

    fun loadQuranFromFirestore() {
        val fs = firestore ?: return
        viewModelScope.launch {
            try {
                fs.collection("Content")
                    .document("quran")
                    .collection("Surahs")
                    .get()
                    .addOnSuccessListener { result ->
                        val list = mutableListOf<QuranSurah>()
                        for (doc in result) {
                            try {
                                val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: 1
                                val name = doc.getString("name") ?: "سورة"
                                val type = doc.getString("type") ?: "مكية"
                                val versesCount = doc.getLong("versesCount")?.toInt() ?: 0
                                
                                val rawVerses = doc.get("verses") as? List<Map<String, Any>>
                                val verses = rawVerses?.map { vMap ->
                                    Verse(
                                        number = (vMap["number"] as? Long)?.toInt() ?: 1,
                                        text = vMap["text"] as? String ?: "",
                                        translation = vMap["translation"] as? String ?: "",
                                        tafsir = vMap["tafsir"] as? String ?: ""
                                    )
                                } ?: emptyList()
                                
                                list.add(QuranSurah(id, name, type, versesCount, verses))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (list.isNotEmpty()) {
                            _firestoreQuranSurahs.value = list.sortedBy { it.id }
                        }
                    }
            } catch (e: Exception) {
                // Ignore and fall back to local
            }
        }
    }

    fun selectReciter(reciter: Reciter) {
        _selectedReciter.value = reciter
        if (_isPlaying.value) {
            playActiveSurahAudio()
        }
    }

    fun saveQuranBookmark(surahId: Int, verseNumber: Int, surahName: String) {
        viewModelScope.launch {
            val bookmark = QuranBookmarkEntity(surahId, verseNumber, surahName)
            repository.insertQuranBookmark(bookmark)
            
            // Save to Firestore under Content/quran/Bookmarks/UID_surahId
            val uid = currentUser.value?.username ?: "anonymous"
            val fs = firestore
            if (fs != null) {
                val data = mapOf(
                    "userId" to uid,
                    "surahId" to surahId,
                    "verseNumber" to verseNumber,
                    "surahName" to surahName,
                    "timestamp" to System.currentTimeMillis()
                )
                try {
                    fs.collection("Content")
                        .document("quran")
                        .collection("Bookmarks")
                        .document("${uid}_${surahId}")
                        .set(data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun saveQuranRating(surahId: Int, verseNumber: Int, rating: Int, notes: String = "") {
        viewModelScope.launch {
            val key = "${surahId}_${verseNumber}"
            val qRating = QuranRatingEntity(key, rating, notes)
            repository.insertQuranRating(qRating)
            
            // Save to Firestore under Content/quran/Ratings/UID_key
            val uid = currentUser.value?.username ?: "anonymous"
            val fs = firestore
            if (fs != null) {
                val data = mapOf(
                    "userId" to uid,
                    "surahId" to surahId,
                    "verseNumber" to verseNumber,
                    "rating" to rating,
                    "notes" to notes,
                    "timestamp" to System.currentTimeMillis()
                )
                try {
                    fs.collection("Content")
                        .document("quran")
                        .collection("Ratings")
                        .document("${uid}_${key}")
                        .set(data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun playActiveSurahAudio() {
        val surahId = _activeSurahId.value
        val reciter = _selectedReciter.value
        val surahCode = String.format("%03d", surahId)
        val audioUrl = "${reciter.baseUrl}$surahCode.mp3"

        _audioStatus.value = "جاري التحميل..."
        _isPlaying.value = true

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    mp.start()
                    _audioStatus.value = "شغال"
                    _audioDuration.value = mp.duration
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _audioStatus.value = "مكتمل"
                    stopProgressTracker()
                }
                setOnErrorListener { _, _, _ ->
                    _audioStatus.value = "خطأ في تشغيل الصوت"
                    _isPlaying.value = false
                    stopProgressTracker()
                    true
                }
                prepareAsync()
            } catch (e: Exception) {
                _audioStatus.value = "خطأ في الاتصال"
                _isPlaying.value = false
                stopProgressTracker()
            }
        }
    }

    fun pauseAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _audioStatus.value = "موقف مؤقتاً"
                _isPlaying.value = false
            }
        }
    }

    fun resumeAudio() {
        mediaPlayer?.let {
            it.start()
            _audioStatus.value = "شغال"
            _isPlaying.value = true
        } ?: playActiveSurahAudio()
    }

    fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _audioStatus.value = "موقف"
        stopProgressTracker()
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                mediaPlayer?.let { mp ->
                    try {
                        if (mp.isPlaying) {
                            _audioPosition.value = mp.currentPosition
                            val surah = QuranData.surahs.find { it.id == _activeSurahId.value }
                            if (surah != null && surah.verses.isNotEmpty() && mp.duration > 0) {
                                val percentage = mp.currentPosition.toFloat() / mp.duration.toFloat()
                                val verseIndex = (percentage * surah.verses.size).toInt().coerceIn(0, surah.verses.size - 1)
                                _activeVerseIndex.value = verseIndex
                            }
                        }
                    } catch (e: Exception) {
                        // Media player might have been released
                    }
                }
                delay(1000L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
        _audioPosition.value = 0
    }

    // --- Social Posting (Village Wall) ---
    fun createPost(content: String, visibility: String = "عام", category: String = "عام") {
        val author = _currentUser.value ?: return
        viewModelScope.launch {
            // Apply Smart Bad Word Filter
            val cleanContent = filterSmartBadWords(content)
            val hasBadWords = cleanContent != content

            val postId = repository.insertPost(
                PostEntity(
                    authorUsername = author.username,
                    authorFullName = author.fullName,
                    authorRole = author.role,
                    content = cleanContent,
                    visibility = visibility,
                    category = category
                )
            )

            if (hasBadWords) {
                // Auto-report to moderators/admins
                repository.insertReport(
                    ReportEntity(
                        postId = postId,
                        postContent = content,
                        postAuthor = author.fullName,
                        reporterUsername = "نظام الحماية K™",
                        reason = "خطاب كراهية / محتوى غير لائق",
                        comment = "تم حجب الكلمات المسيئة تلقائياً بواسطة الفلتر الذكي."
                    )
                )
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deletePost(postId)
        }
    }

    fun interactWithPost(postId: Long, type: String) {
        viewModelScope.launch {
            when (type) {
                "like" -> repository.incrementLike(postId)
                "prayer" -> repository.incrementPrayer(postId)
                "support" -> repository.incrementSupport(postId)
                "blessing" -> repository.incrementBlessing(postId)
            }
        }
    }

    // --- Comments ---
    fun loadComments(postId: Long) {
        viewModelScope.launch {
            repository.getCommentsForPost(postId).collect {
                _currentPostComments.value = it
            }
        }
    }

    fun addComment(postId: Long, content: String) {
        val author = _currentUser.value ?: return
        viewModelScope.launch {
            val cleanContent = filterSmartBadWords(content)
            val comment = CommentEntity(
                postId = postId,
                authorUsername = author.username,
                authorFullName = author.fullName,
                content = cleanContent
            )
            repository.insertComment(comment)
            loadComments(postId) // Reload
        }
    }

    // --- Reports & Moderation ---
    fun fileReport(postId: Long, postContent: String, postAuthor: String, reason: String, comment: String) {
        val reporter = _currentUser.value?.username ?: "مجهول"
        viewModelScope.launch {
            repository.insertReport(
                ReportEntity(
                    postId = postId,
                    postContent = postContent,
                    postAuthor = postAuthor,
                    reporterUsername = reporter,
                    reason = reason,
                    comment = comment
                )
            )
        }
    }

    fun resolveReport(reportId: Long) {
        viewModelScope.launch {
            repository.resolveReport(reportId, true)
        }
    }

    fun deleteReport(reportId: Long) {
        viewModelScope.launch {
            repository.deleteReport(reportId)
        }
    }

    fun blockUser(username: String, reason: String) {
        viewModelScope.launch {
            repository.setUserBlockedState(username, true, reason)
        }
    }

    fun unblockUser(username: String) {
        viewModelScope.launch {
            repository.setUserBlockedState(username, false, "")
        }
    }

    fun changeUserRole(username: String, role: String, modRole: String) {
        viewModelScope.launch {
            repository.setUserRole(username, role, modRole)
        }
    }

    // --- Consultations ---
    fun submitConsultation(title: String, question: String, isAnonymous: Boolean, category: String) {
        viewModelScope.launch {
            repository.insertConsultation(
                ConsultationEntity(
                    title = title,
                    question = question,
                    isAnonymous = isAnonymous,
                    category = category,
                    status = "معلق"
                )
            )
        }
    }

    fun answerConsultation(id: Long, answer: String) {
        viewModelScope.launch {
            repository.answerConsultation(id, answer)
        }
    }

    // --- Imam Questions ---
    fun askImam(questionText: String) {
        val author = _currentUser.value?.username ?: "مجهول"
        viewModelScope.launch {
            repository.insertImamQuestion(
                ImamQuestionEntity(
                    askerUsername = author,
                    question = questionText
                )
            )
        }
    }

    fun answerImamQuestion(id: Long, answer: String, isPublic: Boolean) {
        viewModelScope.launch {
            repository.answerImamQuestion(id, answer, isPublic)
        }
    }

    // --- Volunteer Activities ---
    fun publishVolunteerActivity(title: String, description: String, date: String, requiredVolunteers: Int, location: String = "مسجد عثمان بن عفان", points: Int = 10) {
        viewModelScope.launch {
            repository.insertVolunteerActivity(
                VolunteerActivityEntity(
                    title = title,
                    description = description,
                    location = location,
                    date = date,
                    points = points,
                    requiredVolunteers = requiredVolunteers
                )
            )
        }
    }

    fun deleteVolunteerActivity(activityId: Long) {
        viewModelScope.launch {
            repository.deleteVolunteerActivity(activityId)
        }
    }

    fun joinActivity(activityId: Long) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val list = allVolunteerActivities.value
            val activity = list.find { it.id == activityId } ?: return@launch
            val usernames = activity.joinedUsernames.split(",").filter { it.isNotEmpty() }.toMutableList()
            if (!usernames.contains(user.username)) {
                usernames.add(user.username)
                repository.joinVolunteerActivity(activityId, usernames.joinToString(","))
                repository.incrementVolunteerHours(user.username, activity.points)
                // Refresh local user state if needed
                val updatedUser = repository.getUserByUsername(user.username)
                _currentUser.value = updatedUser
            }
        }
    }

    fun leaveActivity(activityId: Long) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val list = allVolunteerActivities.value
            val activity = list.find { it.id == activityId } ?: return@launch
            val usernames = activity.joinedUsernames.split(",").filter { it.isNotEmpty() }.toMutableList()
            if (usernames.contains(user.username)) {
                usernames.remove(user.username)
                repository.leaveVolunteerActivity(activityId, usernames.joinToString(","))
                repository.incrementVolunteerHours(user.username, -activity.points)
                // Refresh local user state if needed
                val updatedUser = repository.getUserByUsername(user.username)
                _currentUser.value = updatedUser
            }
        }
    }

    // --- School Lessons ---
    fun uploadSchoolLesson(grade: String, subject: String, title: String, summary: String) {
        val teacher = _currentUser.value?.fullName ?: "أستاذ متطوع"
        viewModelScope.launch {
            repository.insertSchoolLesson(
                SchoolLessonEntity(
                    grade = grade,
                    subject = subject,
                    title = title,
                    summary = summary,
                    volunteerTeacherName = teacher
                )
            )
        }
    }

    // --- Bookings ---
    fun createBooking(title: String, applicant: String, date: String, notes: String) {
        viewModelScope.launch {
            repository.insertBooking(
                MosqueBookingEntity(
                    title = title,
                    applicantName = applicant,
                    date = date,
                    notes = notes
                )
            )
        }
    }

    fun deleteBooking(id: Long) {
        viewModelScope.launch {
            repository.deleteBooking(id)
        }
    }

    // --- Blood Bank ---
    fun registerBloodDonor(name: String, group: String, phone: String) {
        val list = _bloodDonors.value.toMutableList()
        list.add(BloodDonor(name, group, phone, "القدادرة"))
        _bloodDonors.value = list
    }

    // --- Lost & Found ---
    fun reportLostItem(title: String, description: String, phone: String) {
        val list = _lostItems.value.toMutableList()
        list.add(LostItem(title, description, phone, System.currentTimeMillis()))
        _lostItems.value = list
    }

    // --- Interactive stats update ---
    fun attendGroupPrayer() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.incrementGroupPresence(user.username)
            val updatedUser = repository.getUserByUsername(user.username)
            _currentUser.value = updatedUser
        }
    }

    fun completeFastingDay() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.incrementFastingDays(user.username)
            val updatedUser = repository.getUserByUsername(user.username)
            _currentUser.value = updatedUser
        }
    }

    // --- Helper Filter ---
    private fun filterSmartBadWords(input: String): String {
        val badWords = listOf("كلب", "حمار", "تبا", "كافر", "منافق", "غبي")
        var result = input
        for (word in badWords) {
            if (result.contains(word)) {
                result = result.replace(word, "***")
            }
        }
        return result
    }
}

data class BloodDonor(val name: String, val group: String, val phone: String, val location: String)
data class LostItem(val title: String, val description: String, val phone: String, val timestamp: Long)
