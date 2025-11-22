package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

data class FollowerUi(
    val id: String,
    val name: String,
    val avatarRes: Int,
    val isFollowed: Boolean = false
)

data class PostUi(
    val id: Long,
    val author: String,
    val text: String,
    val time: String,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val comments: List<String> = emptyList()
)

class ProfileViewModel : ViewModel() {
    var name by mutableStateOf("Daulet T")
    var bio by mutableStateOf("Android learner · Compose enjoyer")
    fun followingCount(): Int = 0
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

private object Routes {
    const val FEED = "feed"
    const val PROFILE = "profile"
    const val EDIT = "edit"
    const val FOLLOWERS = "followers"
}

data class BottomItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun App() {
    val navController = rememberNavController()
    val profileVm: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val userVm: UserViewModel = hiltViewModel()

    val bottomItems = listOf(
        BottomItem(Routes.FEED, "Feed", Icons.Filled.Home),
        BottomItem(Routes.PROFILE, "Profile", Icons.Filled.Person)
    )

    MaterialTheme {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    items = bottomItems
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.FEED,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Routes.FEED) {
                    FeedScreen()
                }
                composable(Routes.PROFILE) {
                    ProfileScaffold(
                        vm = profileVm,
                        userVM = userVm,
                        onFollowersClick = {
                            navController.navigate(Routes.FOLLOWERS)
                        },
                        onEditClick = {
                            navController.navigate(Routes.EDIT)
                        }
                    )
                }
                composable(Routes.EDIT) {
                    EditProfileScreen(
                        vm = profileVm,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Routes.FOLLOWERS) {
                    FollowersScreen(
                        userVM = userVm,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<BottomItem>
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val selected = when (item.route) {
                Routes.FEED -> currentRoute == Routes.FEED
                Routes.PROFILE -> currentRoute == Routes.PROFILE ||
                        currentRoute == Routes.EDIT ||
                        currentRoute == Routes.FOLLOWERS
                else -> false
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen() {
    val posts = remember {
        mutableStateListOf(
            PostUi(
                id = 1,
                author = "Aruzhan",
                text = "Сегодня разобрала Hilt и подключила DI к профилю 🚀",
                time = "2h ago",
                likes = 8,
                isLiked = false,
                comments = listOf("Это было не просто)", "Теперь всё понятно!")
            ),
            PostUi(
                id = 2,
                author = "Timur",
                text = "Room + Retrofit + Hilt — мощная связка для pet-проектов.",
                time = "5h ago",
                likes = 15,
                isLiked = false,
                comments = listOf("Согласен!", "Ждём туториал)")
            ),
            PostUi(
                id = 3,
                author = "You",
                text = "Мой первый профиль на Compose. Как вам дизайн?",
                time = "1d ago",
                likes = 25,
                isLiked = true,
                comments = listOf("Очень стильный!", "Тени 🔥", "Цвета топ")
            )
        )
    }

    fun toggleLike(id: Long) {
        val index = posts.indexOfFirst { it.id == id }
        if (index != -1) {
            val p = posts[index]
            val newLiked = !p.isLiked
            val newLikes = if (newLiked) p.likes + 1 else (p.likes - 1).coerceAtLeast(0)
            posts[index] = p.copy(isLiked = newLiked, likes = newLikes)
        }
    }

    fun addComment(id: Long, text: String) {
        val index = posts.indexOfFirst { it.id == id }
        if (index != -1) {
            val p = posts[index]
            posts[index] = p.copy(comments = p.comments + text)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Feed") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(posts, key = { _, post -> post.id }) { index, post ->
                PostItem(
                    post = post,
                    index = index,
                    onToggleLike = { toggleLike(post.id) },
                    onAddComment = { comment -> addComment(post.id, comment) }
                )
            }
        }
    }
}

@Composable
fun PostItem(
    post: PostUi,
    index: Int,
    onToggleLike: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var newComment by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 80L)
        visible = true
    }

    val likeColor by animateColorAsState(
        targetValue = if (post.isLiked) Color(0xFFFF5252) else Color(0xFFA6ABCF),
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "likeColorPost"
    )

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(450)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF15193B)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2244)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.author.firstOrNull()?.uppercase() ?: "",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            post.author,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            post.time,
                            color = Color(0xFFA6ABCF),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(post.text, color = Color.White, fontSize = 14.sp)

                if (post.comments.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Column(
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            4.dp
                        )
                    ) {
                        post.comments.take(3).forEach { comment ->
                            Text(
                                text = "• $comment",
                                color = Color(0xFFA6ABCF),
                                fontSize = 12.sp
                            )
                        }
                        if (post.comments.size > 3) {
                            Text(
                                text = "+${post.comments.size - 3} more comments",
                                color = Color(0xFF6C63FF),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        16.dp
                    )
                ) {
                    IconButton(onClick = onToggleLike) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Like",
                            tint = likeColor
                        )
                    }
                    Text("${post.likes}", color = Color.White, fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.ChatBubble,
                        contentDescription = "Comments",
                        tint = Color(0xFFA6ABCF)
                    )
                    Text("${post.comments.size}", color = Color.White, fontSize = 13.sp)
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("Add a comment...", fontSize = 12.sp) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newComment.isNotBlank()) {
                                onAddComment(newComment.trim())
                                newComment = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                    ) {
                        Text("Send", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScaffold(
    vm: ProfileViewModel,
    userVM: UserViewModel,
    onFollowersClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val showSnackbar: (String, String?, (() -> Unit)?) -> Unit = remember {
        { message, action, onAction ->
            scope.launch {
                val res = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                if (res == SnackbarResult.ActionPerformed) onAction?.invoke()
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profile",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, null) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null) }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        ProfileScreen(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            vm = vm,
            userVM = userVM,
            onShowSnackbar = showSnackbar,
            onFollowersClick = onFollowersClick
        )
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    vm: ProfileViewModel,
    userVM: UserViewModel,
    onShowSnackbar: (String, String?, (() -> Unit)?) -> Unit,
    onFollowersClick: () -> Unit
) {
    val users by userVM.users.collectAsState()
    val followersCount = users.size
    var isFollowing by rememberSaveable { mutableStateOf(false) }
    var showUnfollowDialog by rememberSaveable { mutableStateOf(false) }

    val followColor by animateColorAsState(
        targetValue = if (isFollowing) Color(0xFF1B5E20) else Color(0xFF6C63FF),
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "followColor"
    )

    val followScale = remember { Animatable(1f) }

    LaunchedEffect(isFollowing) {
        followScale.animateTo(
            1.12f,
            animationSpec = spring(
                dampingRatio = 0.3f,
                stiffness = Spring.StiffnessMedium
            )
        )
        followScale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = 0.7f,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    var statsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(800)
        statsVisible = true
    }
    val statsAlpha by animateFloatAsState(
        targetValue = if (statsVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "statsAlpha"
    )

    var isSyncing by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val avatarScale = remember { Animatable(1f) }

    LaunchedEffect(isSyncing) {
        if (isSyncing) {
            while (isActive) {
                avatarScale.animateTo(
                    1.08f,
                    animationSpec = spring(
                        dampingRatio = 0.4f,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                avatarScale.animateTo(
                    0.96f,
                    animationSpec = spring(
                        dampingRatio = 0.6f,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        } else {
            avatarScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    val cardShape = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 18.dp,
        bottomEnd = 18.dp
    )

    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(Color(0xFF0F1020), Color(0xFF171A33))
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF6C63FF), Color(0xFF00BFA6))
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(120.dp))
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                        cardShape
                    ),
                shape = cardShape,
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF0F1226)),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 56.dp,
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 20.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            vm.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF00E5A8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        vm.bio,
                        fontSize = 14.sp,
                        color = Color(0xFFA6ABCF),
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .alpha(statsAlpha),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                    ) {
                        Box(Modifier.clickable { onFollowersClick() }) {
                            StatChip("Followers", followersCount)
                        }
                        StatChip("Following", vm.followingCount())
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            12.dp
                        )
                    ) {
                        Button(
                            onClick = {
                                if (!isFollowing) {
                                    isFollowing = true
                                    onShowSnackbar("Followed", "UNDO") {
                                        isFollowing = false
                                    }
                                } else showUnfollowDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer(
                                    scaleX = followScale.value,
                                    scaleY = followScale.value
                                ),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = followColor),
                            contentPadding = PaddingValues(
                                vertical = 12.dp
                            )
                        ) {
                            AnimatedContent(
                                targetState = isFollowing,
                                label = "followText"
                            ) { followed ->
                                Text(
                                    if (followed) "Following" else "Follow",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.width(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            StoriesRow()
            Spacer(Modifier.height(24.dp))
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = avatarScale.value,
                        scaleY = avatarScale.value
                    )
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F1226))
                    .padding(3.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.photo_profile),
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            OnlineStatusDot()

            IconButton(
                onClick = {
                    if (!isSyncing) {
                        scope.launch {
                            isSyncing = true
                            try {
                                userVM.refresh()
                                delay(1000)
                            } finally {
                                isSyncing = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 0.dp, y = 4.dp)
                    .size(28.dp)
                    .background(
                        Color(0xFF0F1226).copy(alpha = 0.9f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sync",
                    tint = if (isSyncing) Color(0xFF00E5A8) else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        if (showUnfollowDialog) {
            AlertDialog(
                onDismissRequest = { showUnfollowDialog = false },
                title = { Text("Unfollow?") },
                text = { Text("Are you sure you want to unfollow this user?") },
                confirmButton = {
                    TextButton(onClick = {
                        isFollowing = false
                        showUnfollowDialog = false
                        onShowSnackbar("Unfollowed", "UNDO") { isFollowing = true }
                    }) { Text("Unfollow", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showUnfollowDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun BoxScope.OnlineStatusDot() {
    val infinite = rememberInfiniteTransition(label = "onlineDot")

    val scale by infinite.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )

    val alpha by infinite.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = (-6).dp, y = 6.dp)
            .size(16.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            )
            .background(Color(0xFF00E676), CircleShape)
            .border(2.dp, Color(0xFF0F1226), CircleShape)
    )
}

@Composable
fun StoriesRow() {
    val stories = listOf("You", "Aruzhan", "Timur", "Dana", "Maks", "Kamila")
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Stories",
            color = Color(0xFFA6ABCF),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
            items(stories) { name ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color(0xFF6C63FF),
                                        Color(0xFF00BFA6)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .padding(3.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.photo_profile),
                            contentDescription = name,
                            modifier = Modifier
                                .clip(CircleShape)
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(name, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersScreen(userVM: UserViewModel, onBack: () -> Unit) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val users by userVM.users.collectAsState()
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Followers") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                loading = true
                                try {
                                    userVM.refresh()
                                    delay(1000)
                                } finally {
                                    loading = false
                                }
                            }
                        }
                    ) { Icon(Icons.Default.Refresh, contentDescription = "Refresh") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                ShimmerFollowersPlaceholder()
            } else {
                if (users.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Пока пусто. Нажми Refresh.", color = Color(0xFFA6ABCF))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            10.dp
                        )
                    ) {
                        itemsIndexed(users, key = { _, u -> u.id }) { index, u ->
                            FollowerItem(
                                follower = FollowerUi(
                                    id = u.id.toString(),
                                    name = u.name,
                                    avatarRes = R.drawable.photo_profile
                                ),
                                index = index,
                                onToggleFollow = {},
                                onRemove = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FollowerItem(
    follower: FollowerUi,
    index: Int,
    onToggleFollow: () -> Unit,
    onRemove: () -> Unit
) {
    val animatedColor by animateColorAsState(
        if (follower.isFollowed) Color(0xFF00E5A8) else Color(0xFF6C63FF),
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "btn"
    )

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 70L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF15193B)),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.elevatedCardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(follower.avatarRes),
                    contentDescription = follower.name,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                ) {
                    Text(
                        follower.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "follows you",
                        color = Color(0xFFA6ABCF),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                TextButton(onClick = onRemove) {
                    Text("Remove", color = Color(0xFFFF5252), maxLines = 1)
                }
                Spacer(Modifier.width(6.dp))
                Button(
                    onClick = onToggleFollow,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = animatedColor),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    modifier = Modifier
                        .widthIn(min = 96.dp)
                        .heightIn(min = 36.dp)
                ) {
                    AnimatedContent(targetState = follower.isFollowed, label = "txt") {
                        Text(if (it) "Following" else "Follow", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerFollowersPlaceholder(count: Int = 6) {
    val shimmerColors = listOf(
        Color(0xFF202449),
        Color(0xFF3A3F6F),
        Color(0xFF202449)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val xOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(xOffset - 1000f, 0f),
        end = Offset(xOffset, 0f)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
    ) {
        repeat(count) {
            ShimmerFollowerRow(brush = brush)
        }
    }
}

@Composable
fun ShimmerFollowerRow(brush: Brush) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF15193B)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(brush)
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }

            Box(
                modifier = Modifier
                    .height(32.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(vm: ProfileViewModel, onBack: () -> Unit) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            var name by remember { mutableStateOf(vm.name) }
            var bio by remember { mutableStateOf(vm.bio) }
            TextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text("Name") }
            )
            TextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") }
            )
            Button(
                onClick = {
                    vm.name = name
                    vm.bio = bio
                    scope.launch {
                        snackbar.showSnackbar(
                            "Profile updated",
                            withDismissAction = true
                        )
                        onBack()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}

@Composable
private fun StatChip(title: String, value: Int) {
    Surface(
        shape = RoundedCornerShape(CornerSize(12.dp)),
        color = Color(0xFF15193B),
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 90.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(title, color = Color(0xFFA6ABCF), fontSize = 12.sp)
        }
    }
}
