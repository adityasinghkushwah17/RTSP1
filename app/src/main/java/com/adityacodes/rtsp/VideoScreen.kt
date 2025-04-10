package com.adityacodes.rtsp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import org.videolan.libvlc.util.VLCVideoLayout

@Composable
fun VideoScreen(
    viewModel: VideoViewModel = viewModel(),
    enterPipMode: () -> Unit,
    toggleRecording: () -> Unit
) {
    val context = LocalContext.current
    val rtspUrl by viewModel.rtspUrl.collectAsState()
    val vlcVideoLayout = remember { VLCVideoLayout(context) }
    var isPlayerReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.player.attachView(vlcVideoLayout)
        isPlayerReady = true
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.release()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF9FAFB), // light background
        contentWindowInsets = WindowInsets.systemBars // 💡 Makes sure system bars are visible
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "RTSP Video Stream",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF2E7D32), // green
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = rtspUrl,
                        onValueChange = { viewModel.updateUrl(it) },
                        label = { Text("RTSP URL") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { if (isPlayerReady) viewModel.play() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A))
                        ) { Text("Play") }

                        Button(
                            onClick = { if (isPlayerReady) toggleRecording() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A))
                        ) { Text("Record") }

                        OutlinedButton(onClick = enterPipMode) {
                            Text("Pop Out")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Live View",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                AndroidView(factory = { vlcVideoLayout })
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun VideoScreenPreview() {
    VideoScreen(enterPipMode = {}, toggleRecording = {})
}

