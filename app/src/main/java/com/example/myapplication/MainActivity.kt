package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.UserViewModel
import kotlinx.coroutines.launch

data class FollowerUi(val id: Long, val name: String, val avatarRes: Int, val isFollowed: Boolean = false)

class ProfileViewModel : ViewModel() {
    var name by mutableStateOf("Daulet T")
    var bio by mutableStateOf("Android learner · Compose enjoyer")
    fun followingCount(): Int = 0
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    val nav = rememberNavController()
    val profileVm: ProfileViewModel = viewModel()
    val userVm: UserViewModel = viewModel()
    MaterialTheme {
        NavHost(navController = nav, startDestination = "home") {
            composable("home") { HomeScreen(onGoProfile = { nav.navigate("profile") }) }
            composable("profile") {
                ProfileScaffold(
                    vm = profileVm,
                    userVM = userVm,
                    onFollowersClick = {
                        userVm.refresh()
                        nav.navigate("followers")
                    },
                    onEditClick = { nav.navigate("edit") }
                )
            }
            composable("edit") { EditProfileScreen(vm = profileVm, onBack = { nav.popBackStack() }) }
            composable("followers") { FollowersScreen(userVM = userVm, onBack = { nav.popBackStack() }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onGoProfile: () -> Unit) {
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Home") }) }) { p ->
        Column(
            modifier = Modifier.padding(p).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onGoProfile, shape = RoundedCornerShape(14.dp)) { Text("Open Profile") }
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
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
                if (res == SnackbarResult.ActionPerformed) onAction?.invoke()
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
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
    val cardShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
    Box(modifier = modifier.background(Brush.verticalGradient(listOf(Color(0xFF0F1020), Color(0xFF171A33))))) {
        Box(
            modifier = Modifier.fillMaxWidth().height(220.dp)
                .background(Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFF00BFA6))))
        )
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(120.dp))
            ElevatedCard(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)), cardShape),
                shape = cardShape,
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF0F1226)),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 56.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(vm.name, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E5A8), modifier = Modifier.size(18.dp))
                    }
                    Text(
                        vm.bio,
                        fontSize = 14.sp, color = Color(0xFFA6ABCF),
                        modifier = Modifier.padding(top = 4.dp), maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(Modifier.clickable { onFollowersClick() }) { StatChip("Followers", followersCount) }
                        StatChip("Following", vm.followingCount())
                    }
                    Row(
                        modifier = Modifier.padding(top = 18.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!isFollowing) {
                                    isFollowing = true
                                    onShowSnackbar("Followed", "UNDO") { isFollowing = false }
                                } else showUnfollowDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = followColor),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            AnimatedContent(targetState = isFollowing, label = "followText") { followed ->
                                Text(if (followed) "Following" else "Follow", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.width(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            contentPadding = PaddingValues(0.dp)
                        ) { Icon(Icons.Default.Notifications, contentDescription = "Notifications") }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            StoriesRow()
            Spacer(Modifier.height(24.dp))
        }
        Box(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp).size(100.dp)
                .clip(CircleShape).background(Color(0xFF0F1226)).padding(3.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.photo_profile),
                contentDescription = "Profile photo",
                modifier = Modifier.clip(CircleShape).fillMaxSize(),
                contentScale = ContentScale.Crop
            )
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
                dismissButton = { TextButton(onClick = { showUnfollowDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
fun StoriesRow() {
    val stories = listOf("You", "Aruzhan", "Timur", "Dana", "Maks", "Kamila")
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text("Stories", color = Color(0xFFA6ABCF), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(stories) { name ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(70.dp)
                            .background(brush = Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFF00BFA6))), shape = CircleShape)
                            .padding(3.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.photo_profile),
                            contentDescription = name,
                            modifier = Modifier.clip(CircleShape).fillMaxSize(),
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

    LaunchedEffect(Unit) {
        if (users.isEmpty() && !loading) {
            loading = true
            try { userVM.refresh() } finally { loading = false }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Followers") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    IconButton(
                        onClick = {
                            loading = true
                            scope.launch {
                                try { userVM.refresh() } finally { loading = false }
                            }
                        }
                    ) { Icon(Icons.Default.Refresh, contentDescription = "Refresh") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            if (users.isEmpty() && !loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Пока пусто. Нажми Refresh.", color = Color(0xFFA6ABCF))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(users, key = { it.id }) { u ->
                        FollowerItem(
                            follower = FollowerUi(u.id.toLong(), u.name, R.drawable.photo_profile),
                            onToggleFollow = {},
                            onRemove = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FollowerItem(
    follower: FollowerUi,
    onToggleFollow: () -> Unit,
    onRemove: () -> Unit
) {
    val animatedColor by animateColorAsState(
        if (follower.isFollowed) Color(0xFF00E5A8) else Color(0xFF6C63FF),
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "btn"
    )
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF15193B)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(follower.avatarRes),
                contentDescription = follower.name,
                modifier = Modifier.size(44.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(follower.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("follows you", color = Color(0xFFA6ABCF), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            TextButton(onClick = onRemove) { Text("Remove", color = Color(0xFFFF5252), maxLines = 1) }
            Spacer(Modifier.width(6.dp))
            Button(
                onClick = onToggleFollow,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = animatedColor),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.widthIn(min = 96.dp).heightIn(min = 36.dp)
            ) {
                AnimatedContent(targetState = follower.isFollowed, label = "txt") {
                    Text(if (it) "Following" else "Follow", color = Color.White)
                }
            }
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
            modifier = Modifier.widthIn(min = 90.dp).padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(title, color = Color(0xFFA6ABCF), fontSize = 12.sp)
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
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { p ->
        Column(
            modifier = Modifier.padding(p).fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var name by remember { mutableStateOf(vm.name) }
            var bio by remember { mutableStateOf(vm.bio) }
            TextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text("Name") })
            TextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") })
            Button(
                onClick = {
                    vm.name = name
                    vm.bio = bio
                    scope.launch {
                        snackbar.showSnackbar("Profile updated", withDismissAction = true)
                        onBack()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}
