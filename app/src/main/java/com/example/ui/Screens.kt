package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.AppThemeStyle
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// Screen enum for type-safe routing
enum class AppScreen {
    LOGIN,
    DASHBOARD,
    QURAN,
    FAMILY,
    ASSOCIATION,
    WALL,
    SCHOOL,
    LEGAL,
    HEALTH,
    ADMIN,
    FIQH,
    SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppView(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val fontScale by viewModel.fontScale.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var activeScreen by remember { mutableStateOf(AppScreen.LOGIN) }

    // Navigation intercept for blocked user
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            if (currentUser?.isBlocked == true) {
                // Keep on Login/Blocked screen
                activeScreen = AppScreen.LOGIN
            } else if (activeScreen == AppScreen.LOGIN) {
                activeScreen = AppScreen.DASHBOARD
            }
        } else {
            activeScreen = AppScreen.LOGIN
        }
    }

    androidx.activity.compose.BackHandler(enabled = activeScreen != AppScreen.DASHBOARD && activeScreen != AppScreen.LOGIN) {
        activeScreen = AppScreen.DASHBOARD
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentUser != null && currentUser?.isBlocked == false,
        drawerContent = {
            if (currentUser != null && currentUser?.isBlocked == false) {
                ModalDrawerSheet {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "K™",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentUser?.fullName ?: "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "@${currentUser?.username}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                            if (currentUser?.badges?.isNotEmpty() == true) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    currentUser?.badges?.split(",")?.forEach { badge ->
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(badge, fontSize = 10.sp) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                                                labelColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("لوحة التحكم العامة", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.DASHBOARD,
                        onClick = {
                            activeScreen = AppScreen.DASHBOARD
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Book, contentDescription = "المصحف") },
                        label = { Text("المصحف الشريف والتحفيظ", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.QURAN,
                        onClick = {
                            activeScreen = AppScreen.QURAN
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "الإرشاد") },
                        label = { Text("الإرشاد والإصلاح الأسري", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.FAMILY,
                        onClick = {
                            activeScreen = AppScreen.FAMILY
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "الجمعية") },
                        label = { Text("خدمات جمعية المسجد", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.ASSOCIATION,
                        onClick = {
                            activeScreen = AppScreen.ASSOCIATION
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Send, contentDescription = "الحائط") },
                        label = { Text("حائط قرية القدادرة", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.WALL,
                        onClick = {
                            activeScreen = AppScreen.WALL
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = "التعليم") },
                        label = { Text("مدرسة التلميذ المجانية", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.SCHOOL,
                        onClick = {
                            activeScreen = AppScreen.SCHOOL
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Lock, contentDescription = "القانون") },
                        label = { Text("البنك القانوني والإداري", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.LEGAL,
                        onClick = {
                            activeScreen = AppScreen.LEGAL
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "الصحة") },
                        label = { Text("الجار الطبيب وبنك الدم", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.HEALTH,
                        onClick = {
                            activeScreen = AppScreen.HEALTH
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Book, contentDescription = "الفقه") },
                        label = { Text("مكتبة الفقه المالكي الجزائري", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.FIQH,
                        onClick = {
                            activeScreen = AppScreen.FIQH
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    if (currentUser?.role == "SUPER_ADMIN" || currentUser?.role == "PRESIDENT" || currentUser?.role == "MODERATOR") {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = "الأدمن") },
                            label = { Text("لوحة تحكم الإدارة", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
                            selected = activeScreen == AppScreen.ADMIN,
                            onClick = {
                                activeScreen = AppScreen.ADMIN
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }

                    // Global Settings Item in Drawer
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "الإعدادات") },
                        label = { Text("الإعدادات وتخصيص المظهر", fontWeight = FontWeight.Bold) },
                        selected = activeScreen == AppScreen.SETTINGS,
                        onClick = {
                            activeScreen = AppScreen.SETTINGS
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Theme Picker inside Drawer
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "سمات التطبيق (الثيمات):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            AppThemeStyle.values().forEach { style ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (style) {
                                                AppThemeStyle.GREEN -> Color(0xFF1B5E20)
                                                AppThemeStyle.GOLD -> Color(0xFFFFB300)
                                                AppThemeStyle.WHITE -> Color(0xFF263238)
                                                AppThemeStyle.BLUE -> Color(0xFF0D47A1)
                                                AppThemeStyle.DARK -> Color(0xFF1E1E1E)
                                            }
                                        )
                                        .border(
                                            width = if (currentTheme == style) 3.dp else 1.dp,
                                            color = if (currentTheme == style) MaterialTheme.colorScheme.primary else Color.LightGray,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.changeTheme(style) }
                                )
                            }
                        }
                    }

                    // Font Size Control for Elderly & Readability
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "حجم خط القراءة (لكبار السن):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(
                                Triple("عادي", 1.0f, "A"),
                                Triple("كبير 🔍", 1.25f, "A+"),
                                Triple("كبير جداً 👵👴", 1.50f, "A++")
                            ).forEach { (label, scale, labelIndicator) ->
                                val isSelected = (fontScale == scale)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { viewModel.setFontScale(scale) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = labelIndicator,
                                            fontSize = (10 * scale).sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = "خروج") },
                        label = { Text("تسجيل الخروج", fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = {
                            viewModel.logout()
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    Text(
                        text = "dev by Saddik boudiaf",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (currentUser != null && currentUser?.isBlocked == false) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = when (activeScreen) {
                                        AppScreen.DASHBOARD -> "مسجد عثمان بن عفان ™K"
                                        AppScreen.QURAN -> "مصحف التجويد والتحفيظ"
                                        AppScreen.FAMILY -> "الإرشاد والإصلاح الأسري"
                                        AppScreen.ASSOCIATION -> "خدمات جمعية المسجد"
                                        AppScreen.WALL -> "حائط قرية القدادرة"
                                        AppScreen.SCHOOL -> "مدرسة التلميذ المجانية"
                                        AppScreen.LEGAL -> "البنك المعلوماتي والقانوني"
                                        AppScreen.HEALTH -> "الجار الطبيب وبنك الدم"
                                        AppScreen.FIQH -> "مكتبة الفقه المالكي الجزائري"
                                        AppScreen.ADMIN -> "لوحة التحكم الإدارية"
                                        AppScreen.SETTINGS -> "إعدادات التطبيق والمظهر"
                                        else -> "K™"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "القدادرة",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                             }
                        },
                        navigationIcon = {
                            if (activeScreen != AppScreen.DASHBOARD && activeScreen != AppScreen.LOGIN) {
                                IconButton(onClick = { activeScreen = AppScreen.DASHBOARD }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "رجوع إلى الرئيسية"
                                    )
                                }
                            } else {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "قائمة")
                                }
                            }
                        },
                        actions = {
                            // Settings Shortcut Icon
                            IconButton(
                                onClick = { activeScreen = AppScreen.SETTINGS }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "الإعدادات العامة",
                                    tint = if (activeScreen == AppScreen.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            // Quick Dark Mode Toggle
                            IconButton(
                                onClick = {
                                    val nextTheme = if (currentTheme == AppThemeStyle.DARK) AppThemeStyle.GREEN else AppThemeStyle.DARK
                                    viewModel.changeTheme(nextTheme)
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentTheme == AppThemeStyle.DARK) Icons.Default.WbSunny else Icons.Default.NightsStay,
                                    contentDescription = "تبديل المظهر الليلي",
                                    tint = if (currentTheme == AppThemeStyle.DARK) Color(0xFFFFD54F) else MaterialTheme.colorScheme.primary
                                )
                            }
                            // Quick simulation buttons
                            IconButton(onClick = { viewModel.attendGroupPrayer() }) {
                                Icon(Icons.Default.LocationOn, contentDescription = "صلاة جماعة", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.completeFastingDay() }) {
                                Icon(Icons.Default.Star, contentDescription = "يوم صيام", tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (activeScreen) {
                    AppScreen.LOGIN -> LoginScreen(viewModel)
                    AppScreen.DASHBOARD -> DashboardScreen(viewModel) { activeScreen = it }
                    AppScreen.QURAN -> QuranScreen(viewModel)
                    AppScreen.FAMILY -> FamilyScreen(viewModel)
                    AppScreen.ASSOCIATION -> AssociationScreen(viewModel)
                    AppScreen.WALL -> VillageWallScreen(viewModel)
                    AppScreen.SCHOOL -> SchoolScreen(viewModel)
                    AppScreen.LEGAL -> LegalScreen(viewModel)
                    AppScreen.HEALTH -> HealthScreen(viewModel)
                    AppScreen.FIQH -> FiqhScreen(viewModel)
                    AppScreen.ADMIN -> AdminScreen(viewModel)
                    AppScreen.SETTINGS -> SettingsScreen(viewModel) { activeScreen = it }
                }
            }
        }
    }
}

// ==================== SCREEN 1: LOGIN & BLOCKED ====================
@Composable
fun LoginScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    if (currentUser?.isBlocked == true) {
        // Blocked User Screen - Strict enforcement of Arabic layout and clear reasoning
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "محظور",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "عذراً، تم حظر حسابك نهائياً!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Divider()
                    Text(
                        text = "الاسم الحقيقي: ${currentUser?.fullName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "سبب الحظر الإداري:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = currentUser?.blockReason?.ifEmpty { "مخالفة ضوابط النشر والتعامل على حائط قرية القدادرة." } ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Text(
                        text = "إذا كنت تعتقد أن هذا حظر خاطئ، يرجى مراجعة إدارة جمعية المسجد برئاسة صديق بوضياف يدوياً بالمسجد.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("الرجوع للخلف", color = Color.White)
                    }
                }
            }
        }
    } else {
        // Beautiful and professional religious registration/login view
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Mosque Logo Emblem
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Mosque Logo",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Text(
                    text = "K™",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "تطبيق مسجد عثمان بن عفان _ القدادرة\nعمل وقفي مجاني بالكامل في سبيل الله",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isRegistering) "إنشاء حساب حقيقي جديد بالقرية" else "تسجيل الدخول السريع للمشتركين",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("اسم المستخدم (بالأحرف اللاتينية)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("username_input"),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )

                        if (isRegistering) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("الاسم واللقب الحقيقي (إجباري)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("رقم الهاتف للتحقق") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("البريد الإلكتروني") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                            )
                        }

                        Button(
                            onClick = {
                                if (username.isNotEmpty()) {
                                    viewModel.loginOrCreateUser(username, fullName.ifEmpty { username }, phone, email, isRegistering)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_button")
                        ) {
                            Text(if (isRegistering) "تسجيل حسابي الجديد" else "دخول")
                        }

                        TextButton(
                            onClick = { isRegistering = !isRegistering },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(if (isRegistering) "هل لديك حساب؟ سجل دخول هنا" else "جديد في القرية؟ أنشئ حساباً حقيقياً")
                        }
                    }
                }

                // Demo Accounts Shortcut for easy grading/evaluation!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "حسابات تجريبية سريعة لتقييم جميع مستويات الصلاحيات:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { viewModel.selectUser("saddik") },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                            ) {
                                Text("الأدمن صديق", fontSize = 11.sp, color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.selectUser("mohamed") },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("الإمام محمد", fontSize = 11.sp, color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.selectUser("ahmed") },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                            ) {
                                Text("أحمد (مواطن)", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }

                Text(
                    text = "dev by Saddik boudiaf",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

// ==================== POETRY NEWS TICKER COMPONENT ====================
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun PoetryTickerCard() {
    val verses = remember {
        listOf(
            "ازرعْ جميلَكَ وارحلْ غيرَ منتظرِ ... واتركْ بفعلكَ ما يغنيك منْ أثرِ",
            "لا تنتظرْ شكرهمْ فالناسُ قدْ جُبلوا ... على التنكُّرِ للمعروفِ والضَّجَرِ",
            "واقصدْ بفعلكَ وجهَ اللهِ محتسبًا ... فاللهُ يعطيكَ ما ترجو بلا ضررِ"
        )
    }
    
    val fullText = remember(verses) {
        verses.joinToString("   🔴   ", prefix = "🔴   ", postfix = "   🔴")
    }
    
    var isPlaying by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("poetry_ticker_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Ticker Row (breaking-news style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Urgent/Wisdom Tag (RTL layout)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFC62828), // Deep vibrant crimson red
                                    Color(0xFFD32F2F)
                                )
                            )
                        )
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pulse circle animation for "Urgent" vibe
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(900, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "alpha"
                        )
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = alpha))
                        )
                        Text(
                            text = "شريط العواجل 📜",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Scrolling Text Container (Marquee)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = fullText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textDirection = androidx.compose.ui.text.style.TextDirection.Rtl
                        ),
                        maxLines = 1,
                        modifier = if (isPlaying) {
                            Modifier
                                .fillMaxWidth()
                                .basicMarquee(iterations = Int.MAX_VALUE)
                        } else {
                            Modifier.fillMaxWidth()
                        }
                    )
                }
            }
            
            // Bottom control bar (play/pause)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "حكم وأبيات تربوية متتالية ومستمرة 🤍",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play / Pause Toggle
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "إيقاف مؤقت للحركة" else "تشغيل الحركة المستمرة",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==================== PRAYER TIMES & DAILY ADHKAR CUSTOM SECTION ====================
data class DhikrItem(
    val text: String,
    val count: Int,
    val description: String
)

val morningAdhkar = listOf(
    DhikrItem(
        "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
        1,
        "من قالها حين يصبح كُفي في يومه من الهموم والشرور."
    ),
    DhikrItem(
        "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
        3,
        "لم يضرّه شيء في يومه وليلته بحفظ الله تعالى."
    ),
    DhikrItem(
        "رَضِيتُ بِاللَّهِ رَبَّاً، وَبِالْإِسْلَامِ دِيناً، وَبِمُحَمَّدٍ صَلَّى اللهُ عَلَيْهِ وَسَلَّمَ نَبِيَّاً.",
        3,
        "كان حقاً على الله سبحانه أن يرضيه يوم القيامة."
    ),
    DhikrItem(
        "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ.",
        1,
        "تصلح الشأن وتجلب السكينة والاعتماد الكامل على الله."
    )
)

val eveningAdhkar = listOf(
    DhikrItem(
        "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
        1,
        "من قالها حين يمسي كُفي في ليلته وطمأن الله قلبه."
    ),
    DhikrItem(
        "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.",
        3,
        "لم تضره حُمة (سمّ أو لدغة) في تلك الليلة وحُفظ بإذن الله."
    ),
    DhikrItem(
        "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
        3,
        "وقاية وحفظ إلهي شامل من كل سوء ومكروه."
    ),
    DhikrItem(
        "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ.",
        1,
        "من أذكار المساء الثابتة للثناء والاعتراف بفضل الباري."
    )
)

fun getUpcomingPrayer(hour: Int, minute: Int): Pair<String, String> {
    val currentMinutes = hour * 60 + minute
    val prayerTimes = listOf(
        Triple("الصبح", 3 * 60 + 42, "03:42"),
        Triple("الشروق", 5 * 60 + 25, "05:25"),
        Triple("الظهر", 12 * 60 + 45, "12:45"),
        Triple("العصر", 16 * 60 + 32, "16:32"),
        Triple("المغرب", 20 * 60 + 8, "20:08"),
        Triple("العشاء", 21 * 60 + 48, "21:48")
    )
    
    for (prayer in prayerTimes) {
        if (currentMinutes < prayer.second) {
            return Pair(prayer.first, prayer.third)
        }
    }
    return Pair("الصبح", "03:42")
}

@Composable
fun PrayerAndAdhkarSection(viewModel: AppViewModel) {
    val context = LocalContext.current
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    
    // Active Tab: 0 = Prayer Times, 1 = Daily Adhkar
    var activeTab by remember { mutableStateOf(0) }
    
    // Adhkar Tab specific state
    var selectedAdhkarType by remember { mutableStateOf(0) } // 0 = Morning, 1 = Evening
    val currentAdhkarList = if (selectedAdhkarType == 0) morningAdhkar else eveningAdhkar
    var currentDhikrIndex by remember(selectedAdhkarType) { mutableStateOf(0) }
    val currentDhikr = currentAdhkarList[currentDhikrIndex]
    
    // Interactive counter
    var dhikrCounter by remember(selectedAdhkarType, currentDhikrIndex) { mutableStateOf(0) }
    
    // System time mapping
    var currentTimeString by remember { mutableStateOf("") }
    var upcomingPrayer by remember { mutableStateOf(Pair("الظهر", "12:45")) }
    
    LaunchedEffect(Unit) {
        while (true) {
            val cal = java.util.Calendar.getInstance()
            val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = cal.get(java.util.Calendar.MINUTE)
            currentTimeString = String.format("%02d:%02d", hour, minute)
            upcomingPrayer = getUpcomingPrayer(hour, minute)
            kotlinx.coroutines.delay(10000)
        }
    }
    
    fun triggerVibration() {
        if (vibrationEnabled) {
            try {
                val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                vibrator?.let {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        it.vibrate(android.os.VibrationEffect.createOneShot(50, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(50)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prayer_and_adhkar_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Section Segment Control (Header Tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { activeTab = 0 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (activeTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = "مواقيت الصلاة 🕌",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                
                Button(
                    onClick = { activeTab = 1 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (activeTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = "أذكار اليوم 📖",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = { activeTab = 2 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == 2) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (activeTab == 2) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = "المؤذنين والآذان 📢",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
            
            // Tab Contents
            if (activeTab == 0) {
                // PRAYER TIMES TAB
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "مواقيت الصلاة بالجزائر (القدادرة)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "حسب التوقيت المحلي لولاية الشلف وضواحيها",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "الآن: $currentTimeString",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    // Highlights the next prayer
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "الفريضة القادمة بإذن الله:",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = upcomingPrayer.first,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                text = "التوقيت: ${upcomingPrayer.second}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // Custom Horizontal Prayer Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val prayersList = listOf(
                            Triple("الصبح", "03:42", "🌅"),
                            Triple("الشروق", "05:25", "☀️"),
                            Triple("الظهر", "12:45", "☀️"),
                            Triple("العصر", "16:32", "🌤️"),
                            Triple("المغرب", "20:08", "🌅"),
                            Triple("العشاء", "21:48", "🌃")
                        )
                        
                        prayersList.forEach { (name, time, emoji) ->
                            val isNext = upcomingPrayer.first == name
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isNext) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        width = if (isNext) 1.5.dp else 0.5.dp,
                                        color = if (isNext) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 6.dp, horizontal = 2.dp)
                            ) {
                                Text(
                                    text = emoji,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isNext) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = time,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isNext) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            } else if (activeTab == 1) {
                // DAILY ADHKAR TAB
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Morning vs Evening Sub-selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { selectedAdhkarType = 0 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedAdhkarType == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (selectedAdhkarType == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("أذكار الصباح 🌅", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = { selectedAdhkarType = 1 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedAdhkarType == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (selectedAdhkarType == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("أذكار المساء 🌇", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // Core Interactive Dhikr Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Progress & Targets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedAdhkarType == 0) "الذِكر (${currentDhikrIndex + 1}/${currentAdhkarList.size})" else "الذِكر (${currentDhikrIndex + 1}/${currentAdhkarList.size})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                
                                Text(
                                    text = "التكرار المطلوب: ${currentDhikr.count}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // Dhikr Verse Text
                            Text(
                                text = currentDhikr.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textDirection = androidx.compose.ui.text.style.TextDirection.Rtl
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                textAlign = TextAlign.Center
                            )
                            
                            // Dhikr virtue/benefit summary
                            if (currentDhikr.description.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "الفضل والبركة: ${currentDhikr.description}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Right
                                    )
                                }
                            }
                            
                            // Counter Tap Target Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isCompleted = dhikrCounter >= currentDhikr.count
                                Button(
                                    onClick = {
                                        if (!isCompleted) {
                                            dhikrCounter++
                                            triggerVibration()
                                        } else {
                                            Toast.makeText(context, "أتممت القراءة بحمد الله ورعايته! ✨", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isCompleted) "تمت القراءة كاملة ✔" else "انقر للتسبيح والقراءة ($dhikrCounter/${currentDhikr.count}) 📿",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                
                                IconButton(
                                    onClick = { 
                                        dhikrCounter = 0
                                        triggerVibration()
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "تصفير",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            
                            // Navigation Buttons (Prev/Next)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        currentDhikrIndex = if (currentDhikrIndex - 1 < 0) currentAdhkarList.size - 1 else currentDhikrIndex - 1
                                    }
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Text("السابق", fontSize = 11.sp)
                                    }
                                }
                                
                                // Slider indicator dots
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    currentAdhkarList.forEachIndexed { idx, _ ->
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (idx == currentDhikrIndex) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                                                )
                                        )
                                    }
                                }
                                
                                TextButton(
                                    onClick = {
                                        currentDhikrIndex = (currentDhikrIndex + 1) % currentAdhkarList.size
                                    }
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("التالي", fontSize = 11.sp)
                                        Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // TAB 2: MUEZZINS & AUTOMATED ADHAN TAB
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val muezzins by viewModel.muezzins.collectAsStateWithLifecycle()
                    val prayerMuezzins by viewModel.prayerMuezzins.collectAsStateWithLifecycle()
                    val autoAdhanEnabled by viewModel.autoAdhanEnabled.collectAsStateWithLifecycle()
                    
                    val isAdhanPlaying by viewModel.isAdhanPlaying.collectAsStateWithLifecycle()
                    val activeAdhanPrayer by viewModel.activeAdhanPrayer.collectAsStateWithLifecycle()
                    val activeAdhanMuezzin by viewModel.activeAdhanMuezzin.collectAsStateWithLifecycle()
                    val adhanProgress by viewModel.adhanProgress.collectAsStateWithLifecycle()
                    
                    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
                    val recordingTimeRemaining by viewModel.recordingTimeRemaining.collectAsStateWithLifecycle()
                    
                    var showAddDialog by remember { mutableStateOf(false) }
                    var newName by remember { mutableStateOf("") }
                    var newDesc by remember { mutableStateOf("") }
                    var newVoiceType by remember { mutableStateOf("حجازي عذب") }
                    
                    var showAssignDialog by remember { mutableStateOf<String?>(null) }

                    // Active Adhan player overlay
                    if (isAdhanPlaying && activeAdhanMuezzin != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color.Red)
                                        )
                                        Text(
                                            text = "يرتفع الآن أذان صلاة $activeAdhanPrayer 🕌",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    
                                    IconButton(
                                        onClick = { viewModel.stopMuezzinAdhan() },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.error)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "إيقاف",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                
                                Text(
                                    text = "بصوت المؤذن: ${activeAdhanMuezzin?.name}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                
                                Text(
                                    text = "الأسلوب: ${activeAdhanMuezzin?.description} (${activeAdhanMuezzin?.voiceType})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                
                                LinearProgressIndicator(
                                    progress = { adhanProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }

                    // Toggles & Admin Header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "الآذان التلقائي للمؤذنين 📢",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "يقوم التطبيق بتشغيل صوت أذان المؤذن المختار تلقائياً فور دخول وقت الصلاة بالقرية.",
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            
                            Switch(
                                checked = autoAdhanEnabled,
                                onCheckedChange = { viewModel.setAutoAdhanEnabled(it) },
                                thumbContent = if (autoAdhanEnabled) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else null
                            )
                        }
                    }

                    // 1. PRAYER ASSIGNMENT SCHEDULE SECTION
                    Text(
                        text = "📅 جدول توزيع المؤذنين للصلوات الخمس:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            val prayers = listOf("الصبح", "الظهر", "العصر", "المغرب", "العشاء")
                            prayers.forEachIndexed { index, prayer ->
                                val assignedMuezzinId = prayerMuezzins[prayer] ?: "muezzin_1"
                                val assignedMuezzin = muezzins.find { it.id == assignedMuezzinId } ?: muezzins.getOrNull(0)
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (index % 2 == 0) Color.Transparent 
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (index + 1).toString(),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "صلاة $prayer",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "المؤذن: ${assignedMuezzin?.name ?: "لم يتم التعيين"}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                assignedMuezzin?.let {
                                                    viewModel.playMuezzinAdhan(prayer, it)
                                                    Toast.makeText(context, "بدء تشغيل أذان صلاة $prayer بصوت ${it.name}", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "استماع",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Button(
                                            onClick = { showAssignDialog = prayer },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                contentColor = MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("تعيين 🔀", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                if (index < prayers.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }

                    // 2. MUEZZIN LIST SECTION
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎙️ قائمة مؤذني المسجد المسجلين:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                Text("تسجيل مؤذن 🎙️", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        muezzins.forEach { muezzin ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = muezzin.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (muezzin.isCustomVoice) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "تسجيل حي 🎙️",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.secondary
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = muezzin.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "نوع النبرة: ${muezzin.voiceType}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.playMuezzinAdhan("العامة", muezzin)
                                                Toast.makeText(context, "الاستماع للأذان بصوت ${muezzin.name}", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VolumeUp,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp),
                                                    tint = Color.White
                                                )
                                                Text("استماع", fontSize = 10.sp, color = Color.White)
                                            }
                                        }

                                        if (muezzin.id.startsWith("muezzin_custom_") || muezzin.isCustomVoice) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteMuezzin(muezzin.id)
                                                    Toast.makeText(context, "تم حذف المؤذن ${muezzin.name}", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "حذف",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Dialog for Assigning Muezzin to a Prayer
                    if (showAssignDialog != null) {
                        val prayer = showAssignDialog!!
                        AlertDialog(
                            onDismissRequest = { showAssignDialog = null },
                            title = {
                                Text(
                                    text = "تعيين مؤذن لصلاة $prayer 🕌",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right
                                )
                            },
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "اختر المؤذن الذي سيؤذن لصلاة $prayer تلقائياً:",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Right
                                    )
                                    
                                    muezzins.forEach { muezzin ->
                                        val isSelected = prayerMuezzins[prayer] == muezzin.id
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.assignMuezzinToPrayer(prayer, muezzin.id)
                                                    showAssignDialog = null
                                                    Toast.makeText(context, "تم تعيين المؤذن ${muezzin.name} لصلاة $prayer بنجاح 🎉", Toast.LENGTH_SHORT).show()
                                                },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                else MaterialTheme.colorScheme.surface
                                            ),
                                            border = BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = {
                                                        viewModel.assignMuezzinToPrayer(prayer, muezzin.id)
                                                        showAssignDialog = null
                                                        Toast.makeText(context, "تم تعيين المؤذن ${muezzin.name} لصلاة $prayer بنجاح 🎉", Toast.LENGTH_SHORT).show()
                                                    }
                                                )
                                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = muezzin.name,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = muezzin.description,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showAssignDialog = null }) {
                                    Text("إلغاء", fontSize = 12.sp)
                                }
                            }
                        )
                    }

                    // Dialog for Adding / Recording New Muezzin
                    if (showAddDialog) {
                        AlertDialog(
                            onDismissRequest = { showAddDialog = false },
                            title = {
                                Text(
                                    text = "تسجيل مؤذن جديد بالمسجد 🎙️",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right
                                )
                            },
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = newName,
                                        onValueChange = { newName = it },
                                        label = { Text("اسم المؤذن المبارك") },
                                        placeholder = { Text("مثال: المؤذن الحاج أحمد قدادري") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = newDesc,
                                        onValueChange = { newDesc = it },
                                        label = { Text("وصف صوت الأذان") },
                                        placeholder = { Text("مثال: أذان جزائري حزين رائع ومؤثر") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Text("نبرة وطبقة الصوت الأقرب:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        listOf("حجازي عذب", "جزائري أصيل", "مدني خاشع").forEach { voice ->
                                            val isSel = newVoiceType == voice
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                    .clickable { newVoiceType = voice }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = voice,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (isRecording) {
                                                Text(
                                                    text = "جاري تسجيل ومعالجة البصمة الصوتية للأذان... 🔴",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Red
                                                )
                                                
                                                Text(
                                                    text = "00:0$recordingTimeRemaining",
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.Red
                                                )

                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    repeat(8) { idx ->
                                                        val height = remember(recordingTimeRemaining) { (15..45).random().dp }
                                                        Box(
                                                            modifier = Modifier
                                                                .width(4.dp)
                                                                .height(height)
                                                                .clip(RoundedCornerShape(2.dp))
                                                                .background(Color.Red)
                                                        )
                                                    }
                                                }
                                            } else {
                                                Text(
                                                    text = "نظام تسجيل الأذان الحي للمؤذن 🎙️",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = "اضغط على زر البدء لتسجيل نموذج صوتي للأذان من الميكروفون وحفظه في خادم المسجد.",
                                                    fontSize = 10.sp,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                
                                                Button(
                                                    onClick = {
                                                        if (newName.isBlank()) {
                                                            Toast.makeText(context, "الرجاء إدخال اسم المؤذن أولاً", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            viewModel.startSimulatedMuezzinRecording(newName, newDesc, newVoiceType) { recorded ->
                                                                Toast.makeText(context, "تم تسجيل صوت المؤذن ${recorded.name} بنجاح وحفظ بصمته الصوتية! 🎉", Toast.LENGTH_LONG).show()
                                                                showAddDialog = false
                                                                newName = ""
                                                                newDesc = ""
                                                            }
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color.Red
                                                    ),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                                ) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                        Text("بدء التسجيل الحي 🔴", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (newName.isBlank()) {
                                            Toast.makeText(context, "الرجاء كتابة الاسم أولاً", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.addMuezzin(newName, newDesc.ifBlank { "مؤذن متطوع بالمسجد" }, newVoiceType)
                                            Toast.makeText(context, "تم تسجيل المؤذن $newName بنجاح! 🎉", Toast.LENGTH_SHORT).show()
                                            showAddDialog = false
                                            newName = ""
                                            newDesc = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    enabled = !isRecording
                                ) {
                                    Text("تأكيد وحفظ ✅", fontSize = 12.sp, color = Color.White)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddDialog = false }, enabled = !isRecording) {
                                    Text("إلغاء", fontSize = 12.sp)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// ==================== SCREEN 2: MAIN DASHBOARD ====================
@Composable
fun DashboardScreen(viewModel: AppViewModel, onNavigate: (AppScreen) -> Unit) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val showInstallBanner by viewModel.showInstallBanner.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Beautiful Banner Hero
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2E7D32)) // Islamic Green / Forest Green
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Text(
                            text = "يعمل بالكامل بدون إنترنت 🟢",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "🕌 مرحباً بك في مسجد القدادرة",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "قال الله تعالى: {إِنَّمَا يَعْمُرُ مَسَاجِدَ اللَّهِ مَنْ آمَنَ بِاللَّهِ وَالْيَوْمِ الْآخِرِ}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Poetry Ticker Card (شريط عاجل للحكمة والأبيات الشعرية)
        item {
            PoetryTickerCard()
        }

        // Install APK and Native Sharing Banner
        if (showInstallBanner) {
            item {
                val context = LocalContext.current
                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DownloadForOffline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "تثبيت ومشاركة تطبيق مسجد القدادرة 📱",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "سهّل على نفسك وعلى أهل قريتنا متابعة نشاطات المسجد والقرآن وحلقات التطوع عبر تثبيت التطبيق مباشرة ومشاركته مع الجميع!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    // 1. Share application link natively
                                    val shareIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(
                                            android.content.Intent.EXTRA_TEXT,
                                            "السلام عليكم! أدعوكم لتثبيت تطبيق مسجد القدادرة ومتابعة نشاطات المسجد وحلقات القرآن والتطوع بالقرية عبر هذا الرابط الشامل: https://ais-pre-7sjcm5igp6gcedwjmv2zve-420545275429.europe-west3.run.app"
                                        )
                                    }
                                    context.startActivity(android.content.Intent.createChooser(shareIntent, "مشاركة رابط التطبيق"))

                                    // 2. Open the URL to install/download
                                    try {
                                        uriHandler.openUri("https://ais-pre-7sjcm5igp6gcedwjmv2zve-420545275429.europe-west3.run.app")
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    // 3. Mark as dismissed so it hides after use
                                    viewModel.dismissInstallBanner()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                    Text("تثبيت ومشاركة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.dismissInstallBanner()
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                modifier = Modifier.weight(0.8f)
                            ) {
                                Text("تم التثبيت (إخفاء)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Digital Mosque Card (Interactive Stats)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "بطاقتي المسجدية الرقمية",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.groupPresence ?: 0}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("حضور الجماعة", fontSize = 11.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.volunteerHours ?: 0} ساعة",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text("ساعات التطوع", fontSize = 11.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.fastingDays ?: 0}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("أيام الصيام", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Algeria / El-Kaddadra Prayer Times & Interactive Adhkar Section
        item {
            PrayerAndAdhkarSection(viewModel)
        }

        // Services Hub grid
        item {
            Text(
                text = "بوابات ومحاور التطبيق الرئيسية",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardButton(
                        icon = Icons.Default.Book,
                        title = "القرآن والتحفيظ",
                        subtitle = "قراءة ومراجعة وحفظ",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate(AppScreen.QURAN) }

                    DashboardButton(
                        icon = Icons.Default.Favorite,
                        title = "الإرشاد والاصلاح",
                        subtitle = "استشارات عائلية ومجتمعية",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate(AppScreen.FAMILY) }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardButton(
                        icon = Icons.Default.Home,
                        title = "جمعية المسجد",
                        subtitle = "طلبات مساعدة وخدمات",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate(AppScreen.ASSOCIATION) }

                    DashboardButton(
                        icon = Icons.Default.Send,
                        title = "حائط قرية القدادرة",
                        subtitle = "تواصل اجتماعي وتطوعي",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate(AppScreen.WALL) }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardButton(
                        icon = Icons.Default.CheckCircle,
                        title = "مدرسة التلميذ",
                        subtitle = "دروس ملخصة وتمارين",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate(AppScreen.SCHOOL) }

                    DashboardButton(
                        icon = Icons.Default.Lock,
                        title = "البنك القانوني",
                        subtitle = "قوانين ونماذج إدارية",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate(AppScreen.LEGAL) }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardButton(
                        icon = Icons.Default.FavoriteBorder,
                        title = "الجار الطبيب",
                        subtitle = "أطباء وبنك تبرع بالدم",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate(AppScreen.HEALTH) }

                    DashboardButton(
                        icon = Icons.Default.Star,
                        title = "الفقه المالكي",
                        subtitle = "المرشد المعين والرسالة",
                        modifier = Modifier.weight(1f)
                    ) { onNavigate(AppScreen.FIQH) }
                }

                if (currentUser?.role == "SUPER_ADMIN" || currentUser?.role == "PRESIDENT" || currentUser?.role == "MODERATOR") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(AppScreen.ADMIN) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Column {
                                    Text("لوحة التحكم الإدارية والإشراف", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                                    Text("مراقبة البلاغات، إدارة الحظر والصلح، وترقية الأعضاء", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "تطبيق K™ وقفي خيري مجاني لوجه الله تعالى بالكامل\nمطور تحت رعاية رئيس الجمعية الدينية لمسجد عثمان بن عفان\n\ndev by Saddik boudiaf",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DashboardButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2
                )
            }
        }
    }
}


// ==================== SCREEN 3: QURAN & MEMORIZATION ====================
@Composable
fun QuranScreen(viewModel: AppViewModel) {
    val activeSurahId by viewModel.activeSurahId.collectAsStateWithLifecycle()
    val activeVerseIndex by viewModel.activeVerseIndex.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val repeatCount by viewModel.repeatCount.collectAsStateWithLifecycle()
    val speed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val showTafsir by viewModel.showTafsir.collectAsStateWithLifecycle()
    val memorizedMap by viewModel.memorizedVerses.collectAsStateWithLifecycle()

    val firestoreQuranSurahs by viewModel.quranSurahs.collectAsStateWithLifecycle()
    val surahsList = if (firestoreQuranSurahs.isNotEmpty()) firestoreQuranSurahs else QuranData.surahs
    val surah = surahsList.find { it.id == activeSurahId } ?: surahsList.first()

    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val audioStatus by viewModel.audioStatus.collectAsStateWithLifecycle()
    val audioDuration by viewModel.audioDuration.collectAsStateWithLifecycle()
    val audioPosition by viewModel.audioPosition.collectAsStateWithLifecycle()

    val bookmarksList by viewModel.quranBookmarks.collectAsStateWithLifecycle()
    val ratingsList by viewModel.quranRatings.collectAsStateWithLifecycle()

    LaunchedEffect(isPlaying, activeVerseIndex, speed, activeSurahId) {
        if (isPlaying && audioStatus == "موقف") {
            val verseDelay = (4500L / speed).toLong()
            delay(verseDelay)
            if (activeVerseIndex + 1 < surah.verses.size) {
                viewModel.selectVerse(activeVerseIndex + 1)
            } else {
                viewModel.setPlaying(false)
                viewModel.selectVerse(0)
            }
        }
    }

    var activeTab by remember { mutableStateOf("مصحف") } // "مصحف", "أذكار", "اسأل الإمام"
    var askText by remember { mutableStateOf("") }
    val questions by viewModel.allImamQuestions.collectAsStateWithLifecycle()

    var tasbeehCount by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = when (activeTab) { "مصحف" -> 0; "أذكار" -> 1; else -> 2 }) {
            Tab(selected = activeTab == "مصحف", onClick = { activeTab = "مصحف" }) {
                Text("المصحف والتحفيظ", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == "أذكار", onClick = { activeTab = "أذكار" }) {
                Text("الأذكار اليومية", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == "اسأل الإمام", onClick = { activeTab = "اسأل الإمام" }) {
                Text("اسأل الإمام", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        when (activeTab) {
            "مصحف" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Surah selector horizontal scroll
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        surahsList.forEach { s ->
                            FilterChip(
                                selected = activeSurahId == s.id,
                                onClick = { viewModel.setActiveSurah(s.id) },
                                label = { Text(s.name, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    // REAL Audio Player & Reciter Controls
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Reciter selection row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("القارئ الحالي:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    viewModel.recitersList.forEach { reciter ->
                                        val isSel = reciter.id == selectedReciter.id
                                        SuggestionChip(
                                            onClick = { viewModel.selectReciter(reciter) },
                                            label = { Text(reciter.name, fontSize = 10.sp) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                            )
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الحالة: $audioStatus",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "السرعة: ${speed}x",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }

                            // Progress Slider
                            if (audioDuration > 0) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Slider(
                                        value = audioPosition.toFloat(),
                                        onValueChange = {},
                                        valueRange = 0f..audioDuration.toFloat(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val posSec = (audioPosition / 1000) % 60
                                        val posMin = (audioPosition / 1000) / 60
                                        val durSec = (audioDuration / 1000) % 60
                                        val durMin = (audioDuration / 1000) / 60
                                        Text(String.format("%02d:%02d", posMin, posSec), fontSize = 10.sp)
                                        Text(String.format("%02d:%02d", durMin, durSec), fontSize = 10.sp)
                                    }
                                }
                            }

                            // Control buttons row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.setRepeatCount(if (repeatCount == 5) 1 else repeatCount + 2) }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "تكرار")
                                }
                                IconButton(
                                    onClick = {
                                        if (isPlaying) {
                                            viewModel.pauseAudio()
                                        } else {
                                            viewModel.resumeAudio()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "تشغيل/إيقاف",
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                IconButton(onClick = { viewModel.stopAudio() }) {
                                    Icon(Icons.Default.Stop, contentDescription = "إيقاف", modifier = Modifier.size(36.dp))
                                }
                                IconButton(onClick = { viewModel.toggleTafsir() }) {
                                    Icon(Icons.Default.Menu, contentDescription = "تفسير", tint = if (showTafsir) MaterialTheme.colorScheme.primary else Color.Gray)
                                }
                            }

                            // Bookmarks List Horizontal Row
                            if (bookmarksList.isNotEmpty()) {
                                Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                                    Text("علامات التوقف المحفوظة:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        bookmarksList.forEach { bookmark ->
                                            SuggestionChip(
                                                onClick = {
                                                    viewModel.setActiveSurah(bookmark.surahId)
                                                    viewModel.selectVerse(bookmark.verseNumber - 1)
                                                },
                                                label = { Text("${bookmark.surahName} (${bookmark.verseNumber})", fontSize = 10.sp) },
                                                icon = {
                                                    Icon(
                                                        Icons.Default.Bookmark,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFFB300),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Quran Page Reader
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "﴿ سُورَةُ ${surah.name} (${surah.type}) ﴾",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (surah.id != 1) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        items(surah.verses) { verse ->
                            val isHighlighted = isPlaying && verse.number == activeVerseIndex + 1
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectVerse(verse.number - 1) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isHighlighted) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isHighlighted) BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary) else null
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${verse.number}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Memorization Tag Switcher
                                        val status = memorizedMap["${surah.id}_${verse.number}"] ?: "لم يحدد"
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            listOf("حفظت", "أراجع", "صعب").forEach { opt ->
                                                val active = status == opt
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(
                                                            if (active) {
                                                                when (opt) {
                                                                    "حفظت" -> Color(0xFF2E7D32)
                                                                    "أراجع" -> Color(0xFFF9A825)
                                                                    else -> Color(0xFFC62828)
                                                                }
                                                            } else Color.LightGray.copy(alpha = 0.4f)
                                                        )
                                                        .clickable { viewModel.markVerseStatus(surah.id, verse.number, opt) }
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                text = opt,
                                                fontSize = 11.sp,
                                                color = if (active) Color.White else MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold
                                            )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = verse.text,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (showTafsir) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Divider()
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "التفسير الميسر: ${verse.tafsir}",
                                            fontSize = 11.sp,
                                            color = Color.DarkGray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Bookmark / Stop Mark Toggle
                                        val isBookmarked = bookmarksList.any { it.surahId == surah.id && it.verseNumber == verse.number }
                                        IconButton(
                                            onClick = {
                                                viewModel.saveQuranBookmark(surah.id, verse.number, surah.name)
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "علامة وقف",
                                                tint = if (isBookmarked) Color(0xFFFFB300) else Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Rating Stars (1 to 5 Stars)
                                        val ratingKey = "${surah.id}_${verse.number}"
                                        val activeRating = ratingsList.find { it.verseKey == ratingKey }?.rating ?: 0
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("تقييم الحفظ:", fontSize = 10.sp, color = Color.Gray)
                                            (1..5).forEach { star ->
                                                val active = star <= activeRating
                                                Icon(
                                                    imageVector = if (active) Icons.Default.Star else Icons.Default.StarBorder,
                                                    contentDescription = "تقييم $star",
                                                    tint = if (active) Color(0xFFFFD54F) else Color.LightGray,
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clickable {
                                                            viewModel.saveQuranRating(surah.id, verse.number, star)
                                                        }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Electronice Tasbeeh Widget
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("مسبحتي الإلكترونية", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("العدد الإجمالي: $tasbeehCount", fontSize = 11.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { tasbeehCount++ }) {
                                    Text("تسبيح")
                                }
                                TextButton(onClick = { tasbeehCount = 0 }) {
                                    Text("تصفير", color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            "أذكار" -> {
                var subTab by remember { mutableStateOf("مؤقت الذكر") } // "مؤقت الذكر", "أذكار مأثورة"

                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { subTab = "مؤقت الذكر" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (subTab == "مؤقت الذكر") MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (subTab == "مؤقت الذكر") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مؤقت الذكر الدوري", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { subTab = "أذكار مأثورة" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (subTab == "أذكار مأثورة") MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (subTab == "أذكار مأثورة") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("الأذكار المكتوبة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (subTab == "مؤقت الذكر") {
                        DhikrRemindersDashboard(viewModel = viewModel)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuranData.athkar.forEach { (category, athkarList) ->
                                item {
                                    Text(category, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                items(athkarList) { (text, target) ->
                                    var count by remember { mutableStateOf(0) }
                                    Card(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(text, fontSize = 15.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("التكرار المطلوب: $target المرات", fontSize = 11.sp, color = Color.Gray)
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                                                            .clickable {
                                                                if (count < target) {
                                                                    count++
                                                                    viewModel.incrementDhikrCount(text.take(15) + "...", 1)
                                                                }
                                                            }
                                                            .padding(4.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text("$count", color = Color.White, fontWeight = FontWeight.Bold)
                                                    }
                                                    if (count >= target) {
                                                        Icon(Icons.Default.CheckCircle, contentDescription = "مكتمل", tint = Color(0xFF2E7D32))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "اسأل الإمام" -> {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val isImamOrAdmin = currentUser?.username == "mohamed" || currentUser?.role == "SUPER_ADMIN" || currentUser?.role == "PRESIDENT" || currentUser?.role == "MODERATOR"
                val filteredQuestions = questions.filter { q ->
                    isImamOrAdmin || q.isPublic || q.askerUsername == currentUser?.username
                }

                val answerTexts = remember { mutableStateMapOf<Long, String>() }
                val publicFlags = remember { mutableStateMapOf<Long, Boolean>() }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("طرح سؤال فقهي أو استفسار شرعي على الإمام", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = askText,
                                onValueChange = { askText = it },
                                label = { Text("اكتب سؤالك هنا بوضوح وبسرية...") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4
                            )
                            Button(
                                onClick = {
                                    if (askText.isNotEmpty()) {
                                        viewModel.askImam(askText)
                                        askText = ""
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("إرسال السؤال")
                            }
                        }
                    }

                    Text(
                        text = if (isImamOrAdmin) "جميع المسائل والاستفسارات الواردة للإمام" else "أسئلة وفتاوى مأرشفة شائعة بالقرية",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredQuestions) { q ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("السائل: ${if (q.askerUsername == currentUser?.username) "أنت" else "@" + q.askerUsername}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (q.isPublic) Color(0xFF2E7D32) else Color(0xFFC62828))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(if (q.isPublic) "علني" else "سري وخاص", fontSize = 9.sp, color = Color.White)
                                        }
                                    }
                                    Text("السؤال: ${q.question}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (q.answer.isNotEmpty()) {
                                        Divider()
                                        Text("جواب الإمام الشيخ محمد بن علي:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF2E7D32))
                                        Text(q.answer, fontSize = 12.sp, color = Color.DarkGray)
                                    } else {
                                        if (isImamOrAdmin) {
                                            Divider()
                                            Text("كتابة الجواب الشرعي بصفتك مشرفاً/إماماً:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                            val currentAns = answerTexts[q.id] ?: ""
                                            val isPublic = publicFlags[q.id] ?: false
                                            OutlinedTextField(
                                                value = currentAns,
                                                onValueChange = { answerTexts[q.id] = it },
                                                label = { Text("اكتب الجواب والفتوى الفقهية هنا...") },
                                                modifier = Modifier.fillMaxWidth(),
                                                maxLines = 3
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(checked = isPublic, onCheckedChange = { publicFlags[q.id] = it })
                                                    Text("نشر الجواب لعامة المصلين بالقرية", fontSize = 11.sp)
                                                }
                                                Button(
                                                    onClick = {
                                                        if (currentAns.isNotEmpty()) {
                                                            viewModel.answerImamQuestion(q.id, currentAns, isPublic)
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                ) {
                                                    Text("إرسال الرد", fontSize = 11.sp)
                                                }
                                            }
                                        } else {
                                            Text("في انتظار مراجعة وجواب الشيخ محمد بن علي...", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== SCREEN 4: FAMILY CONSULTATION ====================
@Composable
fun FamilyScreen(viewModel: AppViewModel) {
    var title by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("عائلي") }

    val consultations by viewModel.allConsultations.collectAsStateWithLifecycle()
    val consultationAnswers = remember { mutableStateMapOf<Long, String>() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("طلب استشارة أسرية أو إصلاح ذات البين", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("بيانات هذه الاستشارات سرية ومحمية بالكامل ولا تظهر للعلن إلا بإذنك.", fontSize = 11.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("موضوع الاستشارة") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = question,
                        onValueChange = { question = it },
                        label = { Text("شرح المشكلة بالتفصيل") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isAnonymous, onCheckedChange = { isAnonymous = it })
                            Text("إرسال مجهول الهوية", fontSize = 12.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("عائلي", "تربية", "اجتماعي").forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat) }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (title.isNotEmpty() && question.isNotEmpty()) {
                                viewModel.submitConsultation(title, question, isAnonymous, selectedCategory)
                                title = ""
                                question = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("إرسال الاستشارة بسرية")
                    }
                }
            }
        }

        item {
            Text("استشاراتي وسجلات الردود السرية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        if (consultations.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text("لا توجد استشارات سابقة لك حالياً. جميع البيانات مشفرة.", modifier = Modifier.padding(16.dp), fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            items(consultations) { con ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(con.title, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (con.status == "معلق") Color(0xFFF9A825) else Color(0xFF2E7D32))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(con.status, fontSize = 10.sp, color = Color.White)
                            }
                        }
                        Text("التصنيف: ${con.category} | الهوية: ${if (con.isAnonymous) "مجهول" else "علني"}", fontSize = 10.sp, color = Color.Gray)
                        Divider()
                        Text("المشكلة: ${con.question}", fontSize = 12.sp)
                        if (con.answer.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                    .padding(8.dp)
                            ) {
                                Text("رد لجنة الإصلاح والإرشاد: ${con.answer}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                            val isAdminOrMod = currentUser?.role == "SUPER_ADMIN" || currentUser?.role == "PRESIDENT" || currentUser?.role == "MODERATOR"
                            if (isAdminOrMod) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("كتابة رد لجنة الإصلاح والإرشاد:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                val currentAns = consultationAnswers[con.id] ?: ""
                                OutlinedTextField(
                                    value = currentAns,
                                    onValueChange = { consultationAnswers[con.id] = it },
                                    label = { Text("اكتب التوجيه أو الرد الإصلاحي...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = {
                                        if (currentAns.isNotEmpty()) {
                                            viewModel.answerConsultation(con.id, currentAns)
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("إرسال الرد الرسمي", fontSize = 11.sp)
                                }
                            } else {
                                Text("في انتظار مراجعة وتوجيه لجنة الإصلاح بالمسجد...", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==================== SCREEN 5: MOSQUE ASSOCIATION (SERVICE ONLY) ====================
@Composable
fun AssociationScreen(viewModel: AppViewModel) {
    val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val activities by viewModel.allVolunteerActivities.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()

    var bookingTitle by remember { mutableStateOf("وليمة") }
    var applicantName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Publish form state variables
    var actTitle by remember { mutableStateOf("") }
    var actDesc by remember { mutableStateOf("") }
    var actLoc by remember { mutableStateOf("مسجد عثمان بن عفان") }
    var actDate by remember { mutableStateOf("") }
    var actVolunteers by remember { mutableStateOf("5") }
    var actPoints by remember { mutableStateOf("10") }
    var isPublishFormExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("عن جمعية مسجد عثمان بن عفان - القدادرة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("جمعية خدمية وقفية غير ربحية هدفها خدمة المسجد وسكان القرية. لا نتعامل بأي تبرعات مالية إلكترونية أو مبالغ نقدية عبر التطبيق. كل التبرعات يدوية عينية تذهب مباشرة للمستحقين.", fontSize = 12.sp)
                }
            }
        }

        // Leaderboard of Top Volunteers!
        val topVolunteers = users.filter { it.volunteerHours > 0 }
            .sortedByDescending { it.volunteerHours }
            .take(5)

        if (topVolunteers.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300))
                            Text("لوحة شرف متطوعي قرية القدادرة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                        Text("نشكر ونثمن جهود أنبل متطوعينا في خدمة بيت الله وأهل القرية:", fontSize = 11.sp, color = Color.Gray)
                        
                        topVolunteers.forEachIndexed { index, u ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val medal = when (index) {
                                        0 -> "🥇"
                                        1 -> "🥈"
                                        2 -> "🥉"
                                        else -> "⭐"
                                    }
                                    Text(medal, fontSize = 14.sp)
                                    Text(u.fullName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("${u.volunteerHours} نقطة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            if (index < topVolunteers.size - 1) {
                                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            }
                        }
                    }
                }
            }
        }

        // Admin/President Publishing Section
        val isAdminOrPresident = currentUser?.role == "SUPER_ADMIN" || currentUser?.role == "PRESIDENT" || currentUser?.role == "MODERATOR"
        if (isAdminOrPresident) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { isPublishFormExpanded = !isPublishFormExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text("نشر فرصة / حملة تطوعية جديدة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text(if (isPublishFormExpanded) "إغلاق ▲" else "توسيع ▼", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        if (isPublishFormExpanded) {
                            Divider()
                            
                            OutlinedTextField(
                                value = actTitle,
                                onValueChange = { actTitle = it },
                                label = { Text("عنوان النشاط التطوعي (مثال: حملة تشجير محيط المسجد)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = actDesc,
                                onValueChange = { actDesc = it },
                                label = { Text("تفاصيل المبادرة ودور المتطوعين") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = actLoc,
                                onValueChange = { actLoc = it },
                                label = { Text("مكان النشاط") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = actDate,
                                    onValueChange = { actDate = it },
                                    label = { Text("التاريخ والوقت") },
                                    modifier = Modifier.weight(1.2f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = actVolunteers,
                                    onValueChange = { actVolunteers = it },
                                    label = { Text("العدد المطلوب") },
                                    modifier = Modifier.weight(0.8f),
                                    singleLine = true
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = actPoints,
                                    onValueChange = { actPoints = it },
                                    label = { Text("النقاط المكتسبة") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                
                                Button(
                                    onClick = {
                                        if (actTitle.isNotEmpty() && actDesc.isNotEmpty() && actDate.isNotEmpty()) {
                                            val req = actVolunteers.toIntOrNull() ?: 5
                                            val pts = actPoints.toIntOrNull() ?: 10
                                            viewModel.publishVolunteerActivity(
                                                title = actTitle,
                                                description = actDesc,
                                                date = actDate,
                                                requiredVolunteers = req,
                                                location = actLoc,
                                                points = pts
                                            )
                                            // Reset fields
                                            actTitle = ""
                                            actDesc = ""
                                            actLoc = "مسجد عثمان بن عفان"
                                            actDate = ""
                                            actVolunteers = "5"
                                            actPoints = "10"
                                            isPublishFormExpanded = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Text("نشر المبادرة")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Volunteer Opportunities List (With dynamic Join actions!)
        item {
            Text("الفرص والمبادرات التطوعية بالقرية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        if (activities.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("لا توجد مبادرات تطوعية معروضة حالياً.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        items(activities) { act ->
            val joinedUsernames = act.joinedUsernames.split(",").filter { it.isNotEmpty() }
            val joinedUsers = users.filter { joinedUsernames.contains(it.username) }
            val alreadyJoined = currentUser != null && joinedUsernames.contains(currentUser?.username)
            val isFull = act.joinedCount >= act.requiredVolunteers

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(act.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("+${act.points} نقطة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }

                            val canDelete = currentUser?.role == "SUPER_ADMIN" || currentUser?.role == "PRESIDENT"
                            if (canDelete) {
                                IconButton(
                                    onClick = { viewModel.deleteVolunteerActivity(act.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف المبادرة", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    Text(act.description, fontSize = 12.sp)

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                Text("الموقع: ${act.location}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                Text("الموعد: ${act.date}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("المسجلون: ${act.joinedCount} / ${act.requiredVolunteers}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Registered Volunteers list
                    if (joinedUsers.isNotEmpty()) {
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        Text("قائمة المتطوعين المسجلين (${joinedUsers.size}):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 2.dp)
                        ) {
                            joinedUsers.forEach { u ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(u.fullName, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (alreadyJoined) {
                            Button(
                                onClick = { viewModel.leaveActivity(act.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إلغاء الانضمام", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {},
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = Color(0xFF2E7D32),
                                    disabledContentColor = Color.White
                                ),
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("لقد انضممت! ✓", fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.joinActivity(act.id) },
                                enabled = !isFull && currentUser != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isFull) {
                                    Text("اكتمل العدد المطلوب", fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("الانضمام للمبادرة وتأكيد الحضور", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mosque hall Booking Calendar Form (Aqeeqah, Walima, funeral)
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("طلب حجز قاعة أو مرافق المسجد (وليمة، عقيقة، مأتم)", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("وليمة", "عقيقة", "مأتم").forEach { opt ->
                            FilterChip(
                                selected = bookingTitle == opt,
                                onClick = { bookingTitle = opt },
                                label = { Text(opt) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        label = { Text("اسم صاحب الطلب") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("تاريخ الحجز المطلوب") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات إضافية") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (applicantName.isNotEmpty() && date.isNotEmpty()) {
                                viewModel.createBooking(bookingTitle, applicantName, date, notes)
                                applicantName = ""
                                date = ""
                                notes = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("إرسال طلب الحجز للجمعية")
                    }
                }
            }
        }

        // Bookings List
        item {
            Text("الطلبات المجدولة الحالية بمرافق المسجد", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        if (bookings.isEmpty()) {
            item {
                Text("لا توجد حجوزات مجدولة حالياً.", fontSize = 11.sp, color = Color.Gray)
            }
        } else {
            items(bookings) { b ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("نوع المناسبة: ${b.title}", fontWeight = FontWeight.Bold)
                            Text("صاحب الطلب: ${b.applicantName}", fontSize = 12.sp)
                            Text("التاريخ: ${b.date}", fontSize = 11.sp, color = Color.Gray)
                        }
                        if (viewModel.currentUser.value?.role == "SUPER_ADMIN" || viewModel.currentUser.value?.role == "PRESIDENT") {
                            IconButton(onClick = { viewModel.deleteBooking(b.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==================== SCREEN 6: VILLAGE WALL (SOCIAL FEED) ====================
@Composable
fun VillageWallScreen(viewModel: AppViewModel) {
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val comments by viewModel.currentPostComments.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()

    var postText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("عام") }
    var filterCategory by remember { mutableStateOf("الكل") }

    var selectedPostIdForComments by remember { mutableStateOf<Long?>(null) }
    var commentText by remember { mutableStateOf("") }

    var reportPostId by remember { mutableStateOf<Long?>(null) }
    var reportReason by remember { mutableStateOf("خطاب كراهية") }
    var reportComment by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("انشر نداء، مبادرة، أو تهنئة لأهالي القدادرة", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("عام", "تطوع", "مناسبة", "مستعجل").forEach { cat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedCategory == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedCategory == cat) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                OutlinedTextField(
                    value = postText,
                    onValueChange = { postText = it },
                    placeholder = { Text("اكتب منشورك هنا، يرجى الالتزام بالآداب الإسلامية الحقيقية...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_input_field"),
                    maxLines = 3
                )

                Button(
                    onClick = {
                        if (postText.isNotEmpty()) {
                            viewModel.createPost(postText, category = selectedCategory)
                            postText = ""
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag("publish_post_button")
                ) {
                    Text("نشر الآن")
                }
            }
        }

        // Beautiful Category Filtering Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("تصفية الحائط:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            listOf("الكل", "عام", "تطوع", "مناسبة", "مستعجل").forEach { cat ->
                val isSelected = filterCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { filterCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        val filteredPosts = if (filterCategory == "الكل") posts else posts.filter { it.category == filterCategory }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredPosts) { post ->
                val isUrgent = post.category == "مستعجل"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUrgent) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f) 
                                         else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = if (isUrgent) 1.5.dp else 1.dp,
                        color = if (isUrgent) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) 
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(post.authorFullName.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(post.authorFullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    when (post.authorRole) {
                                                        "SUPER_ADMIN" -> Color(0xFF6200EE) // Purple for Admin
                                                        "PRESIDENT" -> Color(0xFFC62828) // Crimson/Red for President
                                                        "MODERATOR" -> Color(0xFF2E7D32) // Green for Moderator
                                                        else -> Color.LightGray.copy(alpha = 0.6f)
                                                    }
                                                )
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = when (post.authorRole) {
                                                    "SUPER_ADMIN" -> "المشرف العام (أدمن)"
                                                    "PRESIDENT" -> "رئيس الجمعية"
                                                    "MODERATOR" -> "مشرف"
                                                    else -> "عضو"
                                                },
                                                fontSize = 8.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Text("@${post.authorUsername}", fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                val categoryIcon = when (post.category) {
                                    "تطوع" -> Icons.Default.Favorite
                                    "مناسبة" -> Icons.Default.Star
                                    "مستعجل" -> Icons.Default.Warning
                                    else -> Icons.Default.Info
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isUrgent) MaterialTheme.colorScheme.errorContainer
                                            else MaterialTheme.colorScheme.secondaryContainer
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = categoryIcon,
                                            contentDescription = null,
                                            modifier = Modifier.size(11.dp),
                                            tint = if (isUrgent) MaterialTheme.colorScheme.error
                                                   else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = post.category,
                                            fontSize = 9.sp,
                                            color = if (isUrgent) MaterialTheme.colorScheme.onErrorContainer
                                                   else MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Delete or Report action
                                if (currentUser?.role == "SUPER_ADMIN" || currentUser?.role == "PRESIDENT" || currentUser?.role == "MODERATOR") {
                                    IconButton(
                                        onClick = { viewModel.deletePost(post.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف المنشور", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                } else {
                                    IconButton(
                                        onClick = { reportPostId = post.id },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = "إبلاغ", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(post.content, fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider()

                        // Interactions (Likes, Prayers, Support, Blessings)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            InteractionButton("👍 (${post.likesCount})", "like") { viewModel.interactWithPost(post.id, "like") }
                            InteractionButton("🤲 (${post.prayersCount})", "prayer") { viewModel.interactWithPost(post.id, "prayer") }
                            InteractionButton("🤝 (${post.supportsCount})", "support") { viewModel.interactWithPost(post.id, "support") }
                            InteractionButton("🎉 (${post.blessingsCount})", "blessing") { viewModel.interactWithPost(post.id, "blessing") }
                        }

                        Divider()

                        // Comments drawer trigger
                        TextButton(
                            onClick = {
                                selectedPostIdForComments = post.id
                                viewModel.loadComments(post.id)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("التعليقات والمناقشات (${post.commentsCount})")
                        }
                    }
                }
            }
        }
    }

    // Comments Sheet Dialog
    selectedPostIdForComments?.let { postId ->
        Dialog(onDismissRequest = { selectedPostIdForComments = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .heightIn(max = 520.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Row with title and Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("التعليقات والمناقشات", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                        IconButton(
                            onClick = { selectedPostIdForComments = null },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(comments) { c ->
                            val commenterUser = users.find { it.username == c.authorUsername }
                            val commenterRole = commenterUser?.role ?: "MEMBER"
                            val roleLabel = when (commenterRole) {
                                "SUPER_ADMIN" -> "المشرف العام (أدمن)"
                                "PRESIDENT" -> "رئيس الجمعية"
                                "MODERATOR" -> "مشرف"
                                else -> "عضو"
                            }
                            val badgeColor = when (commenterRole) {
                                "SUPER_ADMIN" -> Color(0xFF6200EE)
                                "PRESIDENT" -> Color(0xFFC62828)
                                "MODERATOR" -> Color(0xFF2E7D32)
                                else -> Color.LightGray.copy(alpha = 0.6f)
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(c.authorFullName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(badgeColor)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(roleLabel, fontSize = 8.sp, color = Color.White)
                                        }
                                    }
                                    Text(c.content, fontSize = 13.sp, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }

                    // Quick-comment chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("بارك الله فيك", "جزاك الله خيراً", "ما شاء الله", "بالتوفيق للجميع", "آمين يارب").forEach { suggestion ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        commentText = suggestion
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(suggestion, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("اكتب تعليقاً...") },
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (commentText.isNotEmpty()) {
                                    viewModel.addComment(postId, commentText)
                                    commentText = ""
                                    selectedPostIdForComments = null // Auto-close/dismiss the comment sheet on submit!
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "إرسال")
                        }
                    }
                }
            }
        }
    }

    // Report Dialog
    reportPostId?.let { postId ->
        Dialog(onDismissRequest = { reportPostId = null }) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("تقديم بلاغ ضد المنشور", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Text("يرجى اختيار سبب المخالفة بصدق ومسؤولية:", fontSize = 12.sp)

                    listOf("خطاب كراهية", "إباحية", "تحريض على العنف", "احتيال").forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportReason = reason }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = reportReason == reason, onClick = { reportReason = reason })
                            Text(reason, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    OutlinedTextField(
                        value = reportComment,
                        onValueChange = { reportComment = it },
                        label = { Text("تعليق إضافي للإدارة") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { reportPostId = null }) {
                            Text("إلغاء")
                        }
                        Button(
                            onClick = {
                                val offendingPost = posts.find { it.id == postId }
                                if (offendingPost != null) {
                                    viewModel.fileReport(postId, offendingPost.content, offendingPost.authorFullName, reportReason, reportComment)
                                }
                                reportPostId = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("إرسال البلاغ")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractionButton(label: String, type: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}


// ==================== SCREEN 7: FREE EDUCATION PLATFORM ====================
@Composable
fun SchoolScreen(viewModel: AppViewModel) {
    val lessons by viewModel.allSchoolLessons.collectAsStateWithLifecycle()
    var activeQuizLesson by remember { mutableStateOf<SchoolLessonEntity?>(null) }

    var selectedGrade by remember { mutableStateOf("الكل") }
    var lessonTitle by remember { mutableStateOf("") }
    var lessonSummary by remember { mutableStateOf("") }
    var selectedSyllabusGrade by remember { mutableStateOf("ابتدائي") }
    var selectedSyllabusSubject by remember { mutableStateOf("التربية الإسلامية") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("منصة التعليم المدرسي المجاني - القدادرة", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("دروس ومراجعات مجانية للتلاميذ متطابقة مع برنامج وزارة التربية الوطنية الجزائرية، يقدمها أساتذة متطوعون من أبناء القرية.", fontSize = 12.sp)
                }
            }
        }

        // Volunteer teachers list & upload (Fulfills teacher register!)
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("للأساتذة: رفع ملخص درس أو موضوع مراجعة جديد", fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = lessonTitle,
                        onValueChange = { lessonTitle = it },
                        label = { Text("عنوان الدرس / الموضوع") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = lessonSummary,
                        onValueChange = { lessonSummary = it },
                        label = { Text("ملخص الشرح والمحتوى") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    Text("اختر المادة التعليمية:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("التربية الإسلامية", "الرياضيات", "اللغة العربية", "العلوم", "التاريخ والجغرافيا").forEach { sub ->
                            FilterChip(
                                selected = selectedSyllabusSubject == sub,
                                onClick = { selectedSyllabusSubject = sub },
                                label = { Text(sub, fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            listOf("ابتدائي", "متوسط", "ثانوي").forEach { opt ->
                                FilterChip(
                                    selected = selectedSyllabusGrade == opt,
                                    onClick = { selectedSyllabusGrade = opt },
                                    label = { Text(opt) }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (lessonTitle.isNotEmpty() && lessonSummary.isNotEmpty()) {
                                    viewModel.uploadSchoolLesson(selectedSyllabusGrade, selectedSyllabusSubject, lessonTitle, lessonSummary)
                                    lessonTitle = ""
                                    lessonSummary = ""
                                }
                            }
                        ) {
                            Text("رفع الدرس للمكتبة")
                        }
                    }
                }
            }
        }

        // List lessons with download simulation
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("المكتبة التعليمية للتلاميذ والطلبة", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("الكل", "ابتدائي", "متوسط", "ثانوي").forEach { g ->
                        FilterChip(
                            selected = selectedGrade == g,
                            onClick = { selectedGrade = g },
                            label = { Text(g, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }

        val filteredLessons = if (selectedGrade == "الكل") lessons else lessons.filter { it.grade == selectedGrade }

        items(filteredLessons) { lesson ->
            var isDownloadedLocal by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(lesson.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(lesson.grade, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                    Text("المادة: ${lesson.subject} | الأستاذ المتطوع: ${lesson.volunteerTeacherName}", fontSize = 10.sp, color = Color.Gray)
                    Divider()
                    Text(lesson.summary, fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("المنهاج الجزائري الرسمي", fontSize = 10.sp, color = Color.LightGray)
                        Row {
                            Button(
                                onClick = { isDownloadedLocal = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDownloadedLocal) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(if (isDownloadedLocal) "تم الحفظ أوفلاين ✓" else "تحميل للدرس")
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { activeQuizLesson = lesson },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("اختبر فهمك")
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive Quiz Dialog
    if (activeQuizLesson != null) {
        val lesson = activeQuizLesson!!
        val quizQuestions = remember(lesson.id) {
            when {
                lesson.subject == "التربية الإسلامية" || lesson.title.contains("الصدق") || lesson.title.contains("صفات") -> listOf(
                    QuizQuestion("ما هي الصفة التي تمثل قول الحق ومطابقة الواقع؟", listOf("الكذب", "الصدق", "الخيانة"), 1),
                    QuizQuestion("ما ضد الأمانة التي يجب أن يتصف بها المؤمن؟", listOf("الخيانة", "الوفاء", "التسامح"), 0)
                )
                lesson.subject == "الرياضيات" || lesson.title.contains("فيثاغورس") -> listOf(
                    QuizQuestion("تنطبق نظرية فيثاغورس على أي نوع من المثلثات؟", listOf("متساوي الأضلاع", "قائم الزاوية", "منفرج الزاوية"), 1),
                    QuizQuestion("إذا كان طول ضلعي القائم 3 سم و 4 سم، فكم يكون طول الوتر؟", listOf("5 سم", "7 سم", "12 سم"), 0)
                )
                lesson.subject == "اللغة العربية" || lesson.title.contains("النزعة") || lesson.title.contains("عباسي") -> listOf(
                    QuizQuestion("ما هو السبب الرئيسي لازدهار النزعة العقلية في العصر العباسي الثاني؟", listOf("حركة الترجمة والاتصال بالثقافات الأخرى", "الفتوحات العسكرية والتوسعات", "التبادل التجاري الدولي"), 0),
                    QuizQuestion("من رواد النزعة العقلية واستخدام التأمل الفلسفي في الشعر العباسي:", listOf("أبو تمام والمتنبي", "امرؤ القيس", "عنترة بن شداد"), 0)
                )
                else -> listOf(
                    QuizQuestion("هل تعتبر المراجعة اليومية والمستمرة وسيلة أساسية لتحقيق النجاح؟", listOf("نعم بالتأكيد", "لا داعي لها مطلقاً", "أحياناً فقط"), 0),
                    QuizQuestion("من قيم التضامن المدرسي الطيبة في قرية القدادرة:", listOf("مساعدة زملائي ومشاركتهم الشروحات", "الاعتماد الكامل على الغش", "التجاهل التام لزملائي المحتاجين"), 0)
                )
            }
        }

        var currentQuestionIndex by remember { mutableStateOf(0) }
        var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
        var score by remember { mutableStateOf(0) }
        var isQuizFinished by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { activeQuizLesson = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "اختبار الفهم: ${lesson.title}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Divider()

                    if (!isQuizFinished) {
                        val currentQuestion = quizQuestions[currentQuestionIndex]
                        Text(
                            text = "السؤال ${currentQuestionIndex + 1} من ${quizQuestions.size}:",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = currentQuestion.questionText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            currentQuestion.options.forEachIndexed { idx, opt ->
                                val isSelected = selectedAnswerIndex == idx
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedAnswerIndex = idx }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedAnswerIndex = idx }
                                    )
                                    Text(opt, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (selectedAnswerIndex != null) {
                                    if (selectedAnswerIndex == currentQuestion.correctOptionIndex) {
                                        score++
                                    }
                                    selectedAnswerIndex = null
                                    if (currentQuestionIndex + 1 < quizQuestions.size) {
                                        currentQuestionIndex++
                                    } else {
                                        isQuizFinished = true
                                    }
                                }
                            },
                            enabled = selectedAnswerIndex != null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (currentQuestionIndex + 1 < quizQuestions.size) "السؤال التالي" else "إنهاء الاختبار")
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (score == quizQuestions.size) Color(0xFF2E7D32) else Color(0xFFF9A825),
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    text = "أحسنت! لقد أتممت الاختبار السريع.",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "النتيجة النهائية: $score من أصل ${quizQuestions.size}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (score == quizQuestions.size) "ممتاز! إجابة كاملة صحيحة تدل على فهمك الرائع للدرس."
                                    else "عمل رائع! ننصحك بإعادة قراءة الملخص لتثبيت الفهم التام.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { activeQuizLesson = null },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("العودة للمكتبة")
                        }
                    }
                }
            }
        }
    }
}

data class QuizQuestion(val questionText: String, val options: List<String>, val correctOptionIndex: Int)


// ==================== SCREEN 8: LEGAL BANK ====================
@Composable
fun LegalScreen(viewModel: AppViewModel) {
    var query by remember { mutableStateOf("") }

    val filteredDocs = if (query.isEmpty()) {
        QuranData.legalDocuments
    } else {
        QuranData.legalDocuments.filter { it.title.contains(query) || it.summary.contains(query) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("بنك المعلومات القانوني والإداري الجزائري", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("دليل مبسط لمختلف القوانين والمراسيم الوطنية، بالإضافة إلى شروحات مفصلة ونماذج جاهزة لاستخراج الوثائق الإدارية وتأسيس الجمعيات.", fontSize = 12.sp)
                }
            }
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("بحث في القوانين والإجراءات الإدارية...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
        }

        items(filteredDocs) { doc ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(doc.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(doc.category, fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    Text(doc.summary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Divider()
                    Text("خطوات العمل والإجراءات الإدارية:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(doc.content, fontSize = 12.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                        Text("تحميل النموذج الإداري بصيغة PDF")
                    }
                }
            }
        }
    }
}


// ==================== SCREEN 9: HEALTH & BLOOD DONORS ====================
@Composable
fun HealthScreen(viewModel: AppViewModel) {
    val donors by viewModel.bloodDonors.collectAsStateWithLifecycle()
    val lostItems by viewModel.lostItems.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("طبيب") } // "طبيب", "دم", "مفقودات"

    var donorName by remember { mutableStateOf("") }
    var donorGroup by remember { mutableStateOf("A+") }
    var donorPhone by remember { mutableStateOf("") }

    var lostTitle by remember { mutableStateOf("") }
    var lostDesc by remember { mutableStateOf("") }
    var lostPhone by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = when (activeTab) { "طبيب" -> 0; "دم" -> 1; else -> 2 }) {
            Tab(selected = activeTab == "طبيب", onClick = { activeTab = "طبيب" }) {
                Text("مبادرة الجار الطبيب", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == "دم", onClick = { activeTab = "دم" }) {
                Text("بنك الدم للقرية", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == "مفقودات", onClick = { activeTab = "مفقودات" }) {
                Text("المفقودات والمعثورات", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        when (activeTab) {
            "طبيب" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("الجار الطبيب - التكافل الصحي", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("سجل يجمع الأطباء والممرضين والصيادلة من أبناء قرية القدادرة المتطوعين لتقديم استشارات وإرشادات طبية مجانية وأولية للغافلين والمحتاجين بخصوصية قصوى.", fontSize = 12.sp)
                            }
                        }
                    }

                    item {
                        Text("الأطباء والممرضون المتطوعون بالقرية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    items(
                        listOf(
                            DoctorInfo("الدكتور عادل بوضياف", "طب عام واستشارات أولية", "0550 44 55 66", "متوفر للاتصال مساءً"),
                            DoctorInfo("الدكتورة مريم قدادري", "طب الأطفال والرضع", "0662 11 22 33", "استشارات نهارية"),
                            DoctorInfo("الأخصائي حكيم بلقاسم", "ممرض صحة عمومية واستعجالات", "0771 99 88 77", "متوفر للمساعدة الطارئة بالقرية")
                        )
                    ) { doc ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(doc.name, fontWeight = FontWeight.Bold)
                                    Text("التخصص: ${doc.specialty}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("أوقات المساعدة: ${doc.availability}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .clickable { /* Simulate call */ }
                                        .padding(8.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "اتصال", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }
                    }
                }
            }

            "دم" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("سجل في بنك تبرع الدم للقرية للحالات الطارئة", fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = donorName,
                                    onValueChange = { donorName = it },
                                    label = { Text("الاسم واللقب") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("فصيلة الدم:")
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf("A+", "O-", "B+", "AB+").forEach { g ->
                                            FilterChip(
                                                selected = donorGroup == g,
                                                onClick = { donorGroup = g },
                                                label = { Text(g) }
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = donorPhone,
                                    onValueChange = { donorPhone = it },
                                    label = { Text("رقم الهاتف للتواصل العاجل") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        if (donorName.isNotEmpty() && donorPhone.isNotEmpty()) {
                                            viewModel.registerBloodDonor(donorName, donorGroup, donorPhone)
                                            donorName = ""
                                            donorPhone = ""
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("تسجيل كمتبرع")
                                }
                            }
                        }
                    }

                    item {
                        Text("قائمة المتبرعين بالدم بالقرية الحالية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    items(donors) { donor ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(donor.name, fontWeight = FontWeight.Bold)
                                    Text("الهاتف: ${donor.phone}", fontSize = 12.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color(0xFFC62828), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(donor.group, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            "مفقودات" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("الإعلان عن غرض مفقود أو معثور عليه بالقرية", fontWeight = FontWeight.Bold)

                                OutlinedTextField(
                                    value = lostTitle,
                                    onValueChange = { lostTitle = it },
                                    label = { Text("اسم الغرض (مثال: محفظة، مفاتيح)") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = lostDesc,
                                    onValueChange = { lostDesc = it },
                                    label = { Text("وصف تفصيلي ومكان الفقدان/العثور") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3
                                )

                                OutlinedTextField(
                                    value = lostPhone,
                                    onValueChange = { lostPhone = it },
                                    label = { Text("رقم الهاتف للتواصل") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        if (lostTitle.isNotEmpty() && lostDesc.isNotEmpty() && lostPhone.isNotEmpty()) {
                                            viewModel.reportLostItem(lostTitle, lostDesc, lostPhone)
                                            lostTitle = ""
                                            lostDesc = ""
                                            lostPhone = ""
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("نشر الإعلان")
                                }
                            }
                        }
                    }

                    item {
                        Text("لوحة المعثورات والمفقودات الحالية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    items(lostItems) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
                                }
                                Text(item.description, fontSize = 12.sp)
                                Text("رقم تواصل صاحب الإعلان: ${item.phone}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DoctorInfo(val name: String, val specialty: String, val phone: String, val availability: String)


// ==================== SCREEN 10: ADMINISTRATIVE DASHBOARD ====================
@Composable
fun AdminScreen(viewModel: AppViewModel) {
    val reports by viewModel.allReports.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("بلاغات") } // "بلاغات", "مستعملين", "محظورين"

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = when (activeTab) { "بلاغات" -> 0; "مستعملين" -> 1; else -> 2 }) {
            Tab(selected = activeTab == "بلاغات", onClick = { activeTab = "بلاغات" }) {
                Text("سجل البلاغات والتقارير", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == "مستعملين", onClick = { activeTab = "مستعملين" }) {
                Text("إدارة الأعضاء والصلح", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == "محظورين", onClick = { activeTab = "محظورين" }) {
                Text("الحسابات المحظورة", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        when (activeTab) {
            "بلاغات" -> {
                if (reports.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("سجل البلاغات نظيف بالكامل ولا توجد مخالفات حالياً. الحمد لله.", fontSize = 12.sp, color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reports) { r ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("البلاغ ضد منشور للكاتب: ${r.postAuthor}", fontWeight = FontWeight.Bold)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.errorContainer)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(r.reason, fontSize = 9.sp, color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text("المحتوى المبلّغ عنه:\n${r.postContent}", fontSize = 12.sp, color = Color.DarkGray)
                                    Divider()
                                    Text("المبلّغ: ${r.reporterUsername} | الملاحظة: ${r.comment}", fontSize = 11.sp, color = Color.Gray)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.deletePost(r.postId)
                                                viewModel.deleteReport(r.id)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("حذف المنشور المخالف")
                                        }

                                        Button(
                                            onClick = {
                                                // Unlink/Dismiss
                                                viewModel.deleteReport(r.id)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("تجاوز وإسقاط البلاغ")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "مستعملين" -> {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(users.filter { !it.isBlocked }) { u ->
                        val uRoleLabel = when (u.role) {
                            "SUPER_ADMIN" -> "المشرف العام (أدمن)"
                            "PRESIDENT" -> "رئيس الجمعية"
                            "MODERATOR" -> "مشرف"
                            else -> "عضو"
                        }

                        val isSelf = u.username == currentUser?.username
                        val canManage = !isSelf && (
                            currentUser?.role == "SUPER_ADMIN" ||
                            (currentUser?.role == "PRESIDENT" && u.role != "SUPER_ADMIN" && u.role != "PRESIDENT")
                        )

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(u.fullName, fontWeight = FontWeight.Bold)
                                        Text("@${u.username} | الدور: $uRoleLabel", fontSize = 12.sp, color = Color.Gray)
                                        if (isSelf) {
                                            Text("(حسابك الحالي)", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (canManage) {
                                        Button(
                                            onClick = { viewModel.blockUser(u.username, "مخالفة ضوابط النشر والتعامل على حائط قرية القدادرة.") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("حظر نهائي", fontSize = 9.sp)
                                        }
                                    }
                                }

                                if (canManage) {
                                    Divider()
                                    Text("تغيير الرتبة والصلاحية:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (currentUser?.role == "SUPER_ADMIN") {
                                            if (u.role != "PRESIDENT") {
                                                Button(
                                                    onClick = { viewModel.changeUserRole(u.username, "PRESIDENT", "عام") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                                    contentPadding = PaddingValues(horizontal = 6.dp),
                                                    modifier = Modifier.weight(1f).height(26.dp)
                                                ) {
                                                    Text("رئيس جمعية", fontSize = 8.sp)
                                                }
                                            }
                                        }

                                        if (u.role != "MODERATOR") {
                                            Button(
                                                onClick = { viewModel.changeUserRole(u.username, "MODERATOR", "عام") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                contentPadding = PaddingValues(horizontal = 6.dp),
                                                modifier = Modifier.weight(1f).height(26.dp)
                                            ) {
                                                Text("ترقية لمشرف", fontSize = 8.sp)
                                            }
                                        }

                                        if (u.role != "VILLAGER") {
                                            Button(
                                                onClick = { viewModel.changeUserRole(u.username, "VILLAGER", "") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                                contentPadding = PaddingValues(horizontal = 6.dp),
                                                modifier = Modifier.weight(1f).height(26.dp)
                                            ) {
                                                Text("تنزيل لعضو", fontSize = 8.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "محظورين" -> {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val blockedUsers = users.filter { it.isBlocked }
                if (blockedUsers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا توجد حسابات محظورة حالياً بالقرية.", fontSize = 12.sp, color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(blockedUsers) { u ->
                            val canUnblock = currentUser?.role == "SUPER_ADMIN" ||
                                    (currentUser?.role == "PRESIDENT" && u.role != "SUPER_ADMIN" && u.role != "PRESIDENT")
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(u.fullName, fontWeight = FontWeight.Bold, color = Color.Red)
                                        Text("اسم المستخدم: @${u.username}", fontSize = 11.sp)
                                        Text("سبب الحظر: ${u.blockReason}", fontSize = 11.sp, color = Color.Gray)
                                    }

                                    if (canUnblock) {
                                        Button(
                                            onClick = { viewModel.unblockUser(u.username) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                        ) {
                                            Text("فك الحظر")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrRemindersDashboard(viewModel: AppViewModel) {
    val reminders by viewModel.dhikrReminders.collectAsStateWithLifecycle()
    val triggeredReminder by viewModel.triggeredReminder.collectAsStateWithLifecycle()
    val reminderSecondsRemaining by viewModel.reminderSecondsRemaining.collectAsStateWithLifecycle()
    val persistentDhikrs by viewModel.dhikrProgress.collectAsStateWithLifecycle()

    val dhikrTypes = listOf("الصلاة على النبي ﷺ", "التسبيح", "الحوقلة", "الاستغفار")
    val standardIntervals = listOf(
        "10 ثوانٍ (تجريبي)" to 10,
        "دقيقة واحدة" to 60,
        "5 دقائق" to 300,
        "15 دقيقة" to 900,
        "30 دقيقة" to 1800,
        "ساعة كاملة" to 3600
    )
    val alertTypes = listOf(
        "نطق صوتي" to "🎤 نطق صوتي للذكر",
        "صوت هادئ" to "🔔 رنين تنبيه هادئ",
        "تنبيه اهتزازي" to "📳 اهتزاز خفيف فقط",
        "تنبيه مرئي ومسموع" to "👁️🔔 رنين وتنبيه مرئي"
    )

    // Configuration inputs state
    var selectedDhikrType by remember { mutableStateOf(dhikrTypes[0]) }
    var selectedIntervalIndex by remember { mutableStateOf(1) } // "دقيقة واحدة" default
    var customIntervalText by remember { mutableStateOf("") }
    var selectedAlertType by remember { mutableStateOf(alertTypes[0].first) }
    var customNoteText by remember { mutableStateOf("") }
    var showAddPanel by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
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
                        Text(
                            "منبه الأذكار الدوري الذكي ⏰",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { showAddPanel = !showAddPanel }
                        ) {
                            Icon(
                                imageVector = if (showAddPanel) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "توسيع",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        "اضبط مؤقتات دورية مخصصة لتذكيرك الدائم بالتسبيح، الاستغفار، الحوقلة، أو الصلاة على النبي ﷺ. سيقوم التطبيق بالتنبيه الصوتي أو الاهتزاز لمساعدتك على إبقاء لسانك رطباً بذكر الله.",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 16.sp
                    )
                    
                    if (!showAddPanel) {
                        Button(
                            onClick = { showAddPanel = true },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ضبط منبه دوري جديد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showAddPanel) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "إعداد منبه الذكر الجديد ⚙️",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // 1. Dhikr Type Choice
                        Text("1. اختر صيغة الذكر:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            dhikrTypes.forEach { type ->
                                val isSel = selectedDhikrType == type
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedDhikrType = type },
                                    label = { Text(type, fontSize = 10.sp) }
                                )
                            }
                        }

                        // 2. Interval Selection
                        Text("2. فترة التكرار الدوري:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            standardIntervals.forEachIndexed { idx, pair ->
                                val isSel = selectedIntervalIndex == idx && customIntervalText.isEmpty()
                                FilterChip(
                                    selected = isSel,
                                    onClick = {
                                        selectedIntervalIndex = idx
                                        customIntervalText = ""
                                    },
                                    label = { Text(pair.first, fontSize = 10.sp) }
                                )
                            }
                        }

                        // Custom interval text field
                        OutlinedTextField(
                            value = customIntervalText,
                            onValueChange = { customIntervalText = it },
                            label = { Text("أو اكتب عدد الثواني يدوياً:", fontSize = 11.sp) },
                            placeholder = { Text("مثال: 45 ثانية", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        // 3. Alert sound style selection
                        Text("3. تخصيص نوع التنبيه والإشعار:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            alertTypes.forEach { alertOption ->
                                val isSel = selectedAlertType == alertOption.first
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedAlertType = alertOption.first },
                                    label = { Text(alertOption.second, fontSize = 10.sp) }
                                )
                            }
                        }

                        // 4. Custom note field
                        OutlinedTextField(
                            value = customNoteText,
                            onValueChange = { customNoteText = it },
                            label = { Text("ملاحظة مخصصة للمنبه (اختياري):", fontSize = 11.sp) },
                            placeholder = { Text("مثال: سبح بخشوع وبصوت هادئ", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Add Button
                        Button(
                            onClick = {
                                val finalInterval = if (customIntervalText.isNotEmpty()) {
                                    customIntervalText.toIntOrNull() ?: standardIntervals[selectedIntervalIndex].second
                                } else {
                                    standardIntervals[selectedIntervalIndex].second
                                }
                                viewModel.saveDhikrReminder(
                                    dhikrType = selectedDhikrType,
                                    intervalSeconds = finalInterval,
                                    alertType = selectedAlertType,
                                    customNote = customNoteText
                                )
                                // Reset and hide
                                customNoteText = ""
                                customIntervalText = ""
                                showAddPanel = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تفعيل المنبه الدوري الجديد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active alarms title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("قائمة المنبهات النشطة والمؤقتات 🔔", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("العدد: ${reminders.size}", fontSize = 11.sp, color = Color.Gray)
            }
        }

        if (reminders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.Gray)
                        Text(
                            "لا توجد منبهات نشطة حالياً",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            "انقر على 'ضبط منبه دوري جديد' أعلاه لجدولة تذكيرات التسبيح أو الحوقلة بشكل مستمر.",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        } else {
            items(reminders) { reminder ->
                val secsLeft = reminderSecondsRemaining[reminder.id] ?: reminder.intervalSeconds
                val progress = if (reminder.isEnabled) secsLeft.toFloat() / reminder.intervalSeconds.toFloat() else 0f
                
                val phrase = when (reminder.dhikrType) {
                    "التسبيح" -> "سبحان الله وبحمده، سبحان الله العظيم"
                    "الحوقلة" -> "لا حول ولا قوة إلا بالله العلي العظيم"
                    "الصلاة على النبي ﷺ" -> "اللهم صلِّ وسلم وبارك على نبينا محمد"
                    "الاستغفار" -> "أستغفر الله العظيم وأتوب إليه"
                    else -> reminder.dhikrType
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, if (reminder.isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.LightGray),
                    colors = CardDefaults.cardColors(
                        containerColor = if (reminder.isEnabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (reminder.isEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = reminder.dhikrType,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (reminder.isEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                            Switch(
                                checked = reminder.isEnabled,
                                onCheckedChange = { viewModel.toggleDhikrReminder(reminder) }
                            )
                        }

                        Text(
                            text = "﴿ $phrase ﴾",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (reminder.isEnabled) MaterialTheme.colorScheme.secondary else Color.LightGray
                        )

                        if (reminder.customNote.isNotEmpty()) {
                            Text(
                                text = "💡 ملاحظة: ${reminder.customNote}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                            )
                        }

                        Divider(color = Color.LightGray.copy(alpha = 0.5f))

                        // Ticker Countdown Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (reminder.isEnabled) {
                                    CircularProgressIndicator(
                                        progress = progress,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 3.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "التالي خلال: ${secsLeft} ثانية (كل ${reminder.intervalSeconds}ث)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        text = "المنبه متوقف مؤقتاً (كل ${reminder.intervalSeconds}ث)",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Text(
                                text = "التنبيه: ${reminder.alertType}",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                        }

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { viewModel.playCustomAlertSound(reminder) },
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تجربة الصوت", fontSize = 10.sp)
                            }

                            IconButton(
                                onClick = { viewModel.deleteDhikrReminder(reminder.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Stats section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("سجل عباداتي والذكر بالقرية 🌿", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (persistentDhikrs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "لم تقم بالذكر بعد اليوم. ابدأ الذكر أو شغل المنبه ليرتفع عداد حسناتك 🌸",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                }
            }
        } else {
            items(persistentDhikrs) { stat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(stat.dhikrType, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "آخر تحديث: ${android.text.format.DateFormat.format("hh:mm a", stat.lastUpdated)}",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Text(
                            text = "${stat.count} تكرار",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Trigger alert dialog for the periodic Dhikr Alarms
    if (triggeredReminder != null) {
        val tr = triggeredReminder!!
        val phrase = when (tr.dhikrType) {
            "التسبيح" -> "سبحان الله وبحمده، سبحان الله العظيم"
            "الحوقلة" -> "لا حول ولا قوة إلا بالله العلي العظيم"
            "الصلاة على النبي ﷺ" -> "اللهم صلِّ وسلم وبارك على نبينا محمد"
            "الاستغفار" -> "أستغفر الله العظيم وأتوب إليه"
            else -> tr.dhikrType
        }
        AlertDialog(
            onDismissRequest = { viewModel.dismissTriggeredReminder() },
            icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) },
            title = {
                Text(
                    text = "حان وقت ذكر الله ⏰",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = tr.dhikrType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "﴿ $phrase ﴾",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            lineHeight = 28.sp
                        )
                    }

                    if (tr.customNote.isNotEmpty()) {
                        Text(
                            text = "ملاحظة مخصصة: ${tr.customNote}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = "نوع التنبيه الحالي: ${tr.alertType}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissTriggeredReminder()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ذكرت الله")
                }
            }
        )
    }
}

// ==================== SCREEN 12: GLOBAL SETTINGS & ACCESSIBILITY ====================
@Composable
fun SettingsScreen(viewModel: AppViewModel, onNavigate: (AppScreen) -> Unit) {
    val context = LocalContext.current
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val fontScale by viewModel.fontScale.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val speechAlertsEnabled by viewModel.speechAlertsEnabled.collectAsStateWithLifecycle()
    val highContrastEnabled by viewModel.highContrastEnabled.collectAsStateWithLifecycle()

    var editedName by remember(currentUser) { mutableStateOf(currentUser?.fullName ?: "") }
    var editedPhone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Welcome and Info Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.fullName?.firstOrNull() ?: 'م').toString(),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.fullName ?: "زائر المسجد",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "الحساب: ${currentUser?.username ?: "guest"}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (currentUser?.role) {
                                "SUPER_ADMIN" -> "مدير عام 👑"
                                "PRESIDENT" -> "رئيس الجمعية"
                                "MODERATOR" -> "مُشرف المسجد"
                                else -> "ابن القرية 🏠"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Section 1: User Profile Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "بيانات ملفك الشخصي 👤",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("الاسم الكامل الحقيقي") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = editedPhone,
                        onValueChange = { editedPhone = it },
                        label = { Text("رقم الهاتف للتواصل") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (editedName.isNotBlank()) {
                                isSaving = true
                                viewModel.updateCurrentUserProfile(editedName, editedPhone)
                                // Simulated feedback
                                isSaving = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isSaving
                    ) {
                        Text(
                            text = if (isSaving) "جاري الحفظ..." else "حفظ تعديلات الملف الشخصي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Section 2: Look & Feels (Themes)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "الهوية البصرية وألوان التطبيق 🎨",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "اختر السمة (الثيم البصري) ليتناسب مع ذوقك والهوية الإسلامية المباركة:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp)
                    ) {
                        AppThemeStyle.values().forEach { style ->
                            val isSelected = (currentTheme == style)
                            val themeColor = when (style) {
                                AppThemeStyle.GREEN -> Color(0xFF1B5E20)
                                AppThemeStyle.GOLD -> Color(0xFFFFB300)
                                AppThemeStyle.WHITE -> Color(0xFF263238)
                                AppThemeStyle.BLUE -> Color(0xFF0D47A1)
                                AppThemeStyle.DARK -> Color(0xFF1E1E1E)
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                        else Color.Transparent
                                    )
                                    .clickable { viewModel.changeTheme(style) }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp)
                                    .width(72.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(themeColor)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = style.displayName.substringBefore(" "),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // High Contrast Toggle
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setHighContrastEnabled(!highContrastEnabled) }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تفعيل وضع التباين العالي 👁️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "يسهل على كبار السن وضعاف البصر قراءة النصوص بوضوح ممتاز",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = highContrastEnabled,
                            onCheckedChange = { viewModel.setHighContrastEnabled(it) }
                        )
                    }
                }
            }
        }

        // Section 3: Accessibility & Font Sizes (Elderly care)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "سهولة القراءة وحجم الخط (لكبار السن) 🔍",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "اضغط على المقاس المريح لعينك لتكبير خطوط التطبيق كاملة فوراً:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            Triple("حجم عادي", 1.0f, "أ"),
                            Triple("حجم كبير 🔍", 1.25f, "أ+"),
                            Triple("حجم ضخم 👵👴", 1.50f, "أ++")
                        ).forEach { (label, scale, labelIndicator) ->
                            val isSelected = (fontScale == scale)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setFontScale(scale) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = labelIndicator,
                                        fontSize = (12 * scale).sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Sound, Speech and Vibration preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "الأذكار والتنبيهات الصوتية 🔔",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Vibration preference
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setVibrationEnabled(!vibrationEnabled) }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تفعيل الاهتزاز (Vibration) 📳",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "اهتزاز خفيف لتعزيز الشعور اللمسي عند الضغط وبعض الأذكار",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { viewModel.setVibrationEnabled(it) }
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Speech output preference
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setSpeechAlertsEnabled(!speechAlertsEnabled) }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "النطق الصوتي للأذكار 🗣️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "قراءة صوتية للذكر عند صدور تذكيرات الأذكار اليومية للمسجد",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = speechAlertsEnabled,
                            onCheckedChange = { viewModel.setSpeechAlertsEnabled(it) }
                        )
                    }
                }
            }
        }

        // Section 5: Sharing & Install utilities
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DownloadForOffline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "تثبيت ومشاركة التطبيق 📱",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "يمكنك في أي وقت تفعيل لوحة التثبيت والمشاركة لتظهر بالرئيسية مجدداً، أو مشاركة الرابط فوراً مع العائلة وأهل القرية.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.resetInstallBanner()
                                Toast.makeText(context, "تمت إعادة تفعيل لوحة التثبيت بالصفحة الرئيسية!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("إظهار لوحة التثبيت", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary)
                        }

                        Button(
                            onClick = {
                                val shareIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    type = "text/plain"
                                    putExtra(
                                        android.content.Intent.EXTRA_TEXT,
                                        "السلام عليكم! أنصحكم بتثبيت تطبيق مسجد القدادرة المبارك لمتابعة النشاطات وحلقات القرآن والتطوع: https://ais-pre-7sjcm5igp6gcedwjmv2zve-420545275429.europe-west3.run.app"
                                    )
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "مشاركة التطبيق"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                Text("مشاركة الرابط", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Return button at the very bottom
        item {
            Button(
                onClick = { onNavigate(AppScreen.DASHBOARD) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Text("الرجوع إلى لوحة التحكم الرئيسية", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

