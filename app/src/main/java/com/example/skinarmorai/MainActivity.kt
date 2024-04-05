// MainActivity.kt
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button // Import from androidx.compose.material3
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text // Import from androidx.compose.material3
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.skinarmorai.NetworkHandlerAPI
import com.example.skinarmorai.ui.theme.SkinArmorAITheme


class MainActivity : ComponentActivity() {
    private lateinit var cameraHandler: CameraHandler
    private lateinit var networkHandler: NetworkHandlerAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkinArmorAITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    cameraHandler = CameraHandler(this)
                    networkHandler = NetworkHandlerAPI()

                    MyApp(cameraHandler, networkHandler)
                }
            }
        }

        if (cameraHandler.allPermissionsGranted()) {
            cameraHandler.startCamera()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(CameraHandler.REQUIRED_PERMISSIONS)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                // All permissions are granted.
                cameraHandler.startCamera()
            } else {
                // If permissions are not granted, inform the user.
                // In a production app, handle this gracefully.
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}

@Composable
fun MyApp(cameraHandler: CameraHandler, networkHandler: NetworkHandlerAPI) {
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var result by remember { mutableStateOf<String?>(null) }
    var showResultScreen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    MaterialTheme {

        if (!showResultScreen) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Camera preview
                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(300.dp)
                            .padding(bottom = 16.dp)
                    )
                }

                // Capture button
                Button(
                    onClick = {
                        cameraHandler.takePhoto { bitmap ->
                            imageBitmap = bitmap
                            val base64Image = cameraHandler.convertImageToBase64(bitmap)
                            result = networkHandler.sendImageToServer(base64Image)
                            showResultScreen = true
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                   /* Text("Capture Image")*/
                }
            }
        } else {
            // Show result screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                /*Text(
                    text = result ?: "No result available",
                    modifier = Modifier.padding(16.dp)
                )*/


                // Button to go back to camera screen
                Button(
                    onClick = { showResultScreen = false },
                    modifier = Modifier.padding(16.dp)
                ) {
                   /* Text("Go back")*/
                }
            }
        }
    }
}
