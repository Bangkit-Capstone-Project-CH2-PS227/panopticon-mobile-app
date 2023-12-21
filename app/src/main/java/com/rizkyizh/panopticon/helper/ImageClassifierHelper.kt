import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import androidx.camera.core.ImageProxy
import com.rizkyizh.panopticon.ml.BestFloat32
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import java.nio.ByteBuffer

//class ImageClassifierHelper(
//    var threshold: Float = 0.1f,
//    var maxResults: Int = 3,
//    var numThreads: Int = 4,
//    val modelName: String = "mobilenet_v1_1.0_224_quantized_1_metadata_1.tflite",
//    val context: Context,
//    val imageClassifierListener: ClassifierListener?
//) {
//    private var imageClassifier: ImageClassifier? = null
//
//    init {
//        setupImageClassifier()
//    }
//
//    fun setupImageClassifier() {
//        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
//            .setScoreThreshold(threshold)
//            .setMaxResults(maxResults)
//        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)
//        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())
//        try {
//            imageClassifier =
//                ImageClassifier.createFromFileAndOptions(context, modelName, optionsBuilder.build())
//        } catch (e: IllegalStateException) {
//            imageClassifierListener?.onError(
//                "Image classifier failed to initialize. See error logs for details"
//            )
//            Log.e("ImageClassifierHelper", "TFLite failed to load model with error: " + e.message)
//        }
//    }
//
//    fun classify(image: ImageProxy) {
//        if (imageClassifier == null) {
//            setupImageClassifier()
//        }
//        val bitmapBuffer = Bitmap.createBitmap(
//            image.width,
//            image.height,
//            Bitmap.Config.ARGB_8888
//        )
//        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
//        image.close()
//
//        var inferenceTime = SystemClock.uptimeMillis()
//        val imageProcessor = ImageProcessor.Builder().build()
//        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmapBuffer))
//
//        val imageProcessingOptions = ImageProcessingOptions.builder()
//            .setOrientation(getOrientationFromRotation(image.imageInfo.rotationDegrees))
//            .build()
//
//        val results = imageClassifier?.classify(tensorImage, imageProcessingOptions)
//        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
//        imageClassifierListener?.onResults(
//            results,
//            inferenceTime
//        )
//    }
//    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
//        return when (rotation) {
//            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
//            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
//            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
//            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
//        }
//    }
//    interface ClassifierListener {
//        fun onError(error: String)
//        fun onResults(
//            results: List<Classifications>?,
//            inferenceTime: Long
//        )
//    }
//}


class ImageClassifierHelper(
    var threshold: Float = 0.1f,
    var maxResults: Int = 3,
    var numThreads: Int = 4,
    val context: Context,
    val imageClassifierListener: ClassifierListener?
) {
    private var panopticNetModel: BestFloat32? = null

    init {
        setupPanopticNetModel()
    }

    fun setupPanopticNetModel() {
        try {
            panopticNetModel = BestFloat32.newInstance(context)
        } catch (e: Exception) {
            imageClassifierListener?.onError(
                "PanopticNET model failed to initialize. See error logs for details"
            )
            Log.e(
                "ImageClassifierHelper",
                "PanopticNET failed to load model with error: ${e.message}"
            )
        }
    }

    private var imageProcessor =
        ImageProcessor.Builder().add(ResizeOp(320, 320, ResizeOp.ResizeMethod.BILINEAR)).build()

    @SuppressLint("UnsafeOptInUsageError")
    fun classify(image: ImageProxy) {
        if (panopticNetModel == null) {
            setupPanopticNetModel()
        }

//        val bitmapBuffer = Bitmap.createBitmap(
//            image.width, image.height, Bitmap.Config.ARGB_8888
//        )
//        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
//        image.close()

//        val byteCount = bitmapBuffer.byteCount
//
//        val byteBuffer = ByteBuffer.allocate(byteCount)
//
//        bitmapBuffer.copyPixelsToBuffer(byteBuffer)

        var bitmap = imageProxyToBitmap(image)

        var byteBuffer = TensorImage(DataType.FLOAT32)
        byteBuffer.load(bitmap)
        byteBuffer = imageProcessor.process(byteBuffer)

        var inferenceTime = SystemClock.uptimeMillis()
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 320, 320, 3), DataType.FLOAT32)

        inputFeature0.loadBuffer(byteBuffer.buffer)

        val outputs = panopticNetModel?.process(inputFeature0)


        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        val outputTensor = outputs?.outputAsCategoryList


        // Mengekstrak confidence scores dari tensor
        val confidenceScores = outputTensor?.get(0)

        // Contoh: Jika confidence_scores berada di indeks pertama
//        val confidenceScore = confidenceScores
//
//        if (confidenceScore != null) {
//            println("Confidence Score: $confidenceScore")
//        } else {
//            println("Confidence Score tidak tersedia.")
//        }
//            Log.d("RESULT", outputFeature0?.getFloatValue(0).toString() + "ini apa njer: " + outputFeature0?.getFloatValue(1))


        imageClassifierListener?.onResults(
            confidenceScores,
            inferenceTime
        )
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

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: Category?, inferenceTime: Long
        )
    }
}


// input
//    name: image
//    type: Image<float32>
//    shape: [1, 640,  640, 3]
//    min/max: []/[]

// output
// type: Unknown<float32>
// description: coordinates of detected objects, class labels, and confidence score
// shape: [1, 5, 8400]
// min/max: []/[]