package com.example.myapplication2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication2.ui.theme.MyApplicationTheme
import androidx.compose.material3.ExperimentalMaterial3Api

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var hasOverlayPermission by remember { mutableStateOf(checkOverlayPermission(context)) }
    var isAttackRunning by remember { mutableStateOf(false) }

    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        hasOverlayPermission = checkOverlayPermission(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("maxim") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Attack Service Status: ${if (isAttackRunning) "Running" else "Stopped"}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = {
                    if (!hasOverlayPermission) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        overlayPermissionLauncher.launch(intent)
                    }
                },
                enabled = !hasOverlayPermission
            ) {
                Text("1. Grant Overlay Permission")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    startAttackService(context)
                    isAttackRunning = true
                },
                enabled = hasOverlayPermission && !isAttackRunning
            ) {
                Text("2. Start Background Attack")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    stopAttackService(context)
                    isAttackRunning = false
                },
                enabled = isAttackRunning
            ) {
                Text("3. Stop Background Attack")
            }
        }
    }
}

private fun checkOverlayPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(context)
    } else {
        true
    }
}

private fun startAttackService(context: Context) {
    val intent = Intent(context, AttackService::class.java).apply {
        action = "START"
    }
    context.startService(intent)
}

private fun stopAttackService(context: Context) {
    val intent = Intent(context, AttackService::class.java).apply {
        action = "STOP"
    }
    context.startService(intent)
}