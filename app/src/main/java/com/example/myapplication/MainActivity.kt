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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy()) {
        ProfileScaffold()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScaffold() {
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
                title = { Text("Profile", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
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
            onShowSnackbar = showSnackbar
        )
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: (String, String?, (() -> Unit)?) -> Unit
) {
    var isFollowing by rememberSaveable { mutableStateOf(false) }
    var followers by rememberSaveable { mutableStateOf(128) }
    var following by rememberSaveable { mutableStateOf(87) }
    var showUnfollowDialog by rememberSaveable { mutableStateOf(false) }
    val followColor by animateColorAsState(
        targetValue = if (isFollowing) Color(0xFF1B5E20) else Color(0xFF6C63FF),
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "followColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFollowing) Color(0xFF1B5E20).copy(alpha = .35f) else Color(0xFF6C63FF).copy(alpha = .35f),
        animationSpec = tween(320),
        label = "borderColor"
    )
    val cardShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
    Box(
        modifier = modifier
            .background(Brush.verticalGradient(listOf(Color(0xFF0F1020), Color(0xFF171A33))))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFF00BFA6))))
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
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)), cardShape),
                shape = cardShape,
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF0F1226)),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 56.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Daulet T",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Verified",
                            tint = Color(0xFF00E5A8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Android learner · Compose enjoyer",
                        fontSize = 14.sp,
                        color = Color(0xFFA6ABCF),
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatChip("Followers", followers)
                        StatChip("Following", following)
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!isFollowing) {
                                    followers++
                                    isFollowing = true
                                    onShowSnackbar("Followed", "UNDO") {
                                        if (isFollowing && followers > 0) followers--
                                        isFollowing = false
                                    }
                                } else {
                                    showUnfollowDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = followColor),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            AnimatedContent(targetState = isFollowing, label = "followText") { followed ->
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
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                    }
                    TextButton(
                        onClick = { },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Message", color = Color(0xFFA6ABCF))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
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
        if (showUnfollowDialog) {
            AlertDialog(
                onDismissRequest = { showUnfollowDialog = false },
                title = { Text("Unfollow?") },
                text = { Text("Are you sure you want to unfollow this user?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (isFollowing && followers > 0) followers--
                            isFollowing = false
                            showUnfollowDialog = false
                            onShowSnackbar("Unfollowed", "UNDO") {
                                isFollowing = true
                                followers++
                            }
                        }
                    ) { Text("Unfollow", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showUnfollowDialog = false }) { Text("Cancel") }
                }
            )
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
            Text(value.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(title, color = Color(0xFFA6ABCF), fontSize = 12.sp)
        }
    }
}
