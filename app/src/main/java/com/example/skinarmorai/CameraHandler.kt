// CameraHandler.kt
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.Base64
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.content.Context

class CameraHandler(private val activity: MainActivity) {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            activity.baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startCamera() {
        // Start camera implementation
    }

    fun takePhoto(callback: (bitmap: Bitmap) -> Unit) {
        // Implement photo capture
    }

    fun convertImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
