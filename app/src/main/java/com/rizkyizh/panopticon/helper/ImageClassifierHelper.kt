import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.rizkyizh.panopticon.ml.PanopticNET
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer


class ImageClassifierHelper(
    var threshold: Float = 0.1f,
    var maxResults: Int = 3,
    var numThreads: Int = 4,
    val context: Context,
    val imageClassifierListener: ClassifierListener?
) {
    private var panopticNetModel: PanopticNET? = null

    init {
        setupPanopticNetModel()
    }

    fun setupPanopticNetModel() {
        try {
            panopticNetModel = PanopticNET.newInstance(context)
        } catch (e: Exception) {
            imageClassifierListener?.onError(
                "PanopticNET model failed to initialize. See error logs for details"
            )
            Log.e(
                "ImageClassifierHelper", "PanopticNET failed to load model with error: ${e.message}"
            )
        }
    }

    private var imageProcessor =
        ImageProcessor.Builder().add(ResizeOp(320, 320, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0.0f, 255.0f)).build()

//    private val handler = Handler(Looper.getMainLooper())
//    private val analysisInterval = 1000L // Jarak waktu dalam milidetik (5000ms = 5 detik)

    private var lastAnalysisTime: Long = 0
    private val analysisInterval = 5000L // Jarak waktu dalam milidetik (misalnya, 5000ms = 5 detik)

    @SuppressLint("UnsafeOptInUsageError")
    fun classify(image: ImageProxy) {

        /// ==================
//
//        if (panopticNetModel == null) {
//            setupPanopticNetModel()
//        }
//
//        val bitmap = imageProxyToBitmap(image)
//
//        var tensorImage: TensorImage = TensorImage(DataType.FLOAT32)
//        tensorImage.load(bitmap)
//
//
//        tensorImage = imageProcessor.process(tensorImage)
//
//        var inferenceTime = SystemClock.uptimeMillis()
//        val inputFeature0 =
//            TensorBuffer.createFixedSize(intArrayOf(1, 320, 320, 3), DataType.FLOAT32)
//
//
//        // Print normalized values (just an example)
//        //        for (i in 0 until 3) {
//        //            Log.d("Normalized Value at index $i : ", tensorImage.tensorBuffer.getFloatValue(i).toString())
//        //        }
//
//
//        inputFeature0.loadBuffer(tensorImage.buffer)
//            val outputs = panopticNetModel?.process(inputFeature0)
//            inferenceTime = SystemClock.uptimeMillis() - inferenceTime
//            val outputTensor = outputs?.outputFeature0AsTensorBuffer
//
//
//            if (outputTensor != null) {
//                Log.d("confidence score", outputTensor.floatArray[0].toString())
//            }
//
//        //  imageClassifierListener?.onResults(
//        //  outputTensor!!.floatArray[1],
//        //             inferenceTime
//        //   )
//
//            bitmap.recycle()
//

        ////===============

        if (panopticNetModel == null) {
            setupPanopticNetModel()
        }

        val bitmap = imageProxyToBitmap(image)

        var tensorImage: TensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

        tensorImage = imageProcessor.process(tensorImage)

//        val inferenceTime = SystemClock.uptimeMillis()
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 320, 320, 3), DataType.FLOAT32)

        inputFeature0.loadBuffer(tensorImage.buffer)

        val currentTime = SystemClock.uptimeMillis()

        // Periksa apakah sudah waktunya untuk menjalankan analisis berikutnya
        if (currentTime - lastAnalysisTime >= analysisInterval) {
            val outputs = panopticNetModel?.process(inputFeature0)
//            val inferenceTime = SystemClock.uptimeMillis() - inferenceTime
            val outputTensor = outputs?.outputFeature0AsTensorBuffer

            if (outputTensor != null) {
                Log.d("confidence score", outputTensor.floatArray[0].toString())
            }

            // Tetapkan waktu terakhir analisis menjadi waktu saat ini
            lastAnalysisTime = currentTime

            // Lanjutkan atau lakukan tindakan lain setelah analisis selesai

            // Recycle bitmap
            bitmap.recycle()
        } else {
            // Jika belum waktunya untuk analisis, lakukan sesuatu yang lain atau lewati
            Log.d("Analysis", "Belum waktunya untuk analisis")
        }
    }


    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val image: Image = imageProxy.image ?: throw IllegalStateException("Image is null")

        val width = image.width
        val height = image.height

        // Get the image planes
        val planes = image.planes

        // Get the buffer from the first plane
        val buffer: ByteBuffer = planes[0].buffer

        // Create a new byte array and copy the image data into it
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        // Create a bitmap from the byte array
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bytes))

        // Close the imageProxy to free up resources
        imageProxy.close()

        return bitmap
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: Float, inferenceTime: Long
        )
    }
}
