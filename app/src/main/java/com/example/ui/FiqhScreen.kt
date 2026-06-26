package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiqhScreen(viewModel: AppViewModel) {
    val bookmarks by viewModel.fiqhBookmarks.collectAsStateWithLifecycle()
    var activeTab by remember { mutableStateOf("المراجع") } // "المراجع", "الامتحان", "المحفوظات"
    var searchQuery by remember { mutableStateOf("") }
    
    var selectedBook by remember { mutableStateOf<FiqhBook?>(null) }
    var selectedChapter by remember { mutableStateOf<FiqhChapter?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab header
        TabRow(
            selectedTabIndex = when (activeTab) {
                "المراجع" -> 0
                "المحفوظات" -> 1
                else -> 2
            }
        ) {
            Tab(selected = activeTab == "المراجع", onClick = { activeTab = "المراجع" }) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("المتون والكتب", fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = activeTab == "المحفوظات", onClick = { activeTab = "المحفوظات" }) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("إشارات مرجعية", fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = activeTab == "الامتحان", onClick = { activeTab = "الامتحان" }) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("مسابقة الفقه", fontWeight = FontWeight.Bold)
                }
            }
        }

        when (activeTab) {
            "المراجع" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ابحث عن مسألة (وضوء، صلاة، سجود، غسل...)", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "تفريغ")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Hero Text
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F5132)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                            }
                            Column {
                                Text(
                                    "المرجعية الدينية الجزائرية المعتمدة",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F5132)
                                )
                                Text(
                                    "ندرس ونعلم الفقه المالكي المعتمد في مساجد قرية القدادرة والجمهورية الجزائرية.",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    // Books list
                    val filteredBooks = MalikiFiqhData.books.map { book ->
                        val matchingChapters = book.chapters.filter { ch ->
                            ch.title.contains(searchQuery, ignoreCase = true) ||
                                    ch.content.contains(searchQuery, ignoreCase = true) ||
                                    ch.explanation.contains(searchQuery, ignoreCase = true)
                        }
                        book.copy(chapters = matchingChapters)
                    }.filter { it.chapters.isNotEmpty() || searchQuery.isEmpty() }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredBooks) { book ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedBook = book },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = book.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F5132)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(book.era, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(
                                        text = "المؤلف: ${book.author}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = book.description,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "فهرس الأبواب المتوفرة (${book.chapters.size}):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    // Preview of chapters
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        book.chapters.forEach { ch ->
                                            val isBookmarked = bookmarks.any { b -> b.bookTitle == book.title && b.chapterTitle == ch.title }
                                            FilterChip(
                                                selected = false,
                                                onClick = {
                                                    selectedBook = book
                                                    selectedChapter = ch
                                                },
                                                label = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Text(ch.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        if (isBookmarked) {
                                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "المحفوظات" -> {
                if (bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Text(
                                "لا توجد إشارات مرجعية محفوظة حالياً.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                "تصفح الأبواب الفقهية واضغط على علامة النجمة الذهبية لحفظ المسألة لتظهر لك هنا للوصول السريع.",
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bookmarks) { b ->
                            // Retrieve the matching chapter details
                            val matchedBook = MalikiFiqhData.books.find { it.title == b.bookTitle }
                            val matchedChapter = matchedBook?.chapters?.find { it.title == b.chapterTitle }
                            
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(b.chapterTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F5132))
                                            Text(b.bookTitle, fontSize = 11.sp, color = Color.Gray)
                                        }
                                        IconButton(onClick = { viewModel.toggleFiqhBookmark(b.bookTitle, b.chapterTitle) }) {
                                            Icon(Icons.Default.Star, contentDescription = "حذف الإشارة", tint = Color(0xFFFFB300))
                                        }
                                    }
                                    
                                    if (matchedChapter != null) {
                                        Text(
                                            text = matchedChapter.content,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Right,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                .padding(10.dp)
                                        )
                                        Text(
                                            text = "الشرح المالكي: ${matchedChapter.explanation}",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "الامتحان" -> {
                FiqhQuizLayout(viewModel)
            }
        }
    }

    // Book Detail Dialog
    if (selectedBook != null && selectedChapter == null) {
        AlertDialog(
            onDismissRequest = { selectedBook = null },
            title = { Text(selectedBook!!.title, fontWeight = FontWeight.Bold, color = Color(0xFF0F5132)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("المؤلف: ${selectedBook!!.author} (${selectedBook!!.era})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(selectedBook!!.description, fontSize = 13.sp, lineHeight = 20.sp)
                    Divider()
                    Text("الأبواب والمسائل المتوفرة للقراءة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    selectedBook!!.chapters.forEach { ch ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedChapter = ch },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ch.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedBook = null }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Chapter Detail Dialog (Glorious read view with Tashkeel and Explanation!)
    if (selectedBook != null && selectedChapter != null) {
        val book = selectedBook!!
        val chapter = selectedChapter!!
        val isBookmarked = bookmarks.any { b -> b.bookTitle == book.title && b.chapterTitle == chapter.title }

        AlertDialog(
            onDismissRequest = { selectedChapter = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(chapter.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F5132))
                        Text(book.title, fontSize = 11.sp, color = Color.Gray)
                    }
                    IconButton(onClick = { viewModel.toggleFiqhBookmark(book.title, chapter.title) }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.Star,
                            tint = if (isBookmarked) Color(0xFFFFB300) else Color.LightGray,
                            contentDescription = "تفضيل"
                        )
                    }
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = BorderStroke(1.5.dp, Color(0xFF0F5132).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = chapter.content,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "الشرح والتعليق المالكي الميسر:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F5132)
                        )
                        Text(
                            text = chapter.explanation,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { selectedChapter = null }) {
                        Text("السابق")
                    }
                    Button(onClick = { selectedChapter = null }) {
                        Text("تمت القراءة")
                    }
                }
            }
        )
    }
}

@Composable
fun FiqhQuizLayout(viewModel: AppViewModel) {
    var questionIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var showResults by remember { mutableStateOf(false) }

    val questions = MalikiFiqhData.quizQuestions

    if (showResults) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            border = BorderStroke(1.dp, Color(0xFF0F5132).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            if (score >= 3) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (score >= 3) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (score >= 3) Color(0xFF2E7D32) else Color(0xFFC62828),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Text(
                    text = "نتيجة اختبار الفقه المالكي",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F5132)
                )

                Text(
                    text = "حصلت على: $score من أصل ${questions.size} إجابات صحيحة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                val reviewText = when (score) {
                    4 -> "ما شاء الله! درجة كاملة. أنت تفقه مذهب إمام دار الهجرة مالك بن أنس تفوقاً ممتازاً!"
                    3 -> "أحسنت! إلمام طيب بمسائل العبادات والوضوء والصلاة في الفقه المالكي المعتمد."
                    else -> "ننصحك بمراجعة متون ابن عاشر والرسالة المتوفرة في مكتبة التطبيق لزيادة التفقه والمدارسة."
                }

                Text(
                    text = reviewText,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        questionIndex = 0
                        selectedOption = null
                        isSubmitted = false
                        score = 0
                        showResults = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إعادة إجراء الاختبار الفقهي")
                }
            }
        }
    } else {
        val currentQuestion = questions[questionIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "السؤال ${questionIndex + 1} من ${questions.size}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F5132)
                )
                Text(
                    "النقاط الحالية: $score",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = (questionIndex + 1).toFloat() / questions.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF0F5132)
            )

            // Question Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                border = BorderStroke(1.dp, Color(0xFF0F5132).copy(alpha = 0.3f))
            ) {
                Text(
                    text = currentQuestion.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 24.sp
                )
            }

            // Options
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currentQuestion.options.forEachIndexed { idx, opt ->
                    val isSelected = selectedOption == idx
                    val isCorrect = idx == currentQuestion.correctAnswerIndex
                    val optionColor = if (isSubmitted) {
                        if (isCorrect) Color(0xFF2E7D32) else if (isSelected) Color(0xFFC62828) else Color.DarkGray
                    } else {
                        if (isSelected) Color(0xFF0F5132) else Color.DarkGray
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isSubmitted) { selectedOption = idx },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                if (isSubmitted) {
                                    if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                } else MaterialTheme.colorScheme.primaryContainer
                            } else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = optionColor.copy(alpha = 0.7f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(optionColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (idx) {
                                        0 -> "أ"
                                        1 -> "ب"
                                        2 -> "ج"
                                        else -> "د"
                                    },
                                    color = optionColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = opt,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Explanation Card
            if (isSubmitted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "التعليق الفقهي والتوضيح:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFF57F17)
                        )
                        Text(
                            currentQuestion.explanation,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Bottom Buttons
            if (!isSubmitted) {
                Button(
                    onClick = {
                        isSubmitted = true
                        if (selectedOption == currentQuestion.correctAnswerIndex) {
                            score++
                        }
                    },
                    enabled = selectedOption != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F5132))
                ) {
                    Text("تأكيد الإجابة والتحقق")
                }
            } else {
                Button(
                    onClick = {
                        if (questionIndex + 1 < questions.size) {
                            questionIndex++
                            selectedOption = null
                            isSubmitted = false
                        } else {
                            showResults = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F5132))
                ) {
                    Text(
                        if (questionIndex + 1 < questions.size) "السؤال التالي" else "عرض نتيجة الامتحان"
                    )
                }
            }
        }
    }
}
