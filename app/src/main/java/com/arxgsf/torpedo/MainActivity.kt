package com.arxgsf.torpedo

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.CommentsDisabled
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.arxgsf.torpedo.ui.theme.TorpedoTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TorpedoTheme {
                var token by remember { mutableStateOf("") }
                var permissionGranted by remember { mutableStateOf(false) }
                val permission = rememberPermissionState(
                    Manifest.permission.SEND_SMS,
                    onPermissionResult = { }
                )

                LifecycleResumeEffect(key1 = permission) {
                    permissionGranted = permission.status.isGranted
                    onPauseOrDispose {  }
                }

                LaunchedEffect(token) {
                    if(token.isEmpty()) {
                        token = withContext(Dispatchers.IO){
                            FirebaseMessaging.getInstance().token.await()
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(
                        requestPermission = {permission.launchPermissionRequest()},
                        permissionGranted,
                        token,
                        Modifier.padding(innerPadding)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MainView(
    requestPermission: ()->Unit,
    permissionGranted: Boolean,
    token: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Column(
        modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val imgvec = if(permissionGranted)
            Icons.AutoMirrored.Outlined.Message
        else
            Icons.Outlined.CommentsDisabled

        val txt = if(permissionGranted) "SMS Permission Granted" else "SMS Permission Denied"

        ElevatedButton(onClick = requestPermission) {
            Icon(imageVector = imgvec, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(txt)
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically){
            Text(
                "Token: $token",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1F)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val clipData = ClipData.newPlainText("token", token)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(context, "Token copied!", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Outlined.CopyAll, "Copy Token")
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    name = "permission granted"
)
@Composable
fun SMSGrantedPreview() {
    TorpedoTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainView(
                requestPermission = {},
                true,
                "D*PANUHU@92",
                Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    name = "permission denied",
)
@Composable
fun SMSDeniedPreview() {
    TorpedoTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainView(
                requestPermission = {},
                false,
                "D*PANUHU@92",
                Modifier.padding(innerPadding)
            )
        }
    }
}