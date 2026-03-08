package com.example.autism

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.os.Environment
import java.io.File

class MainActivity : FlutterActivity() {

    private val METHOD_CHANNEL = "app/landmark_method"
    private lateinit var extractor: VideoExtractor

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        extractor = VideoExtractor(this)

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            METHOD_CHANNEL
        ).setMethodCallHandler { call, result ->

            when (call.method) {
                "extractLandmarksToCsv" -> {
                    val videoPath = call.argument<String>("videoPath")
                    val fps = (call.argument<Double>("fps") ?: 30.0).toFloat()
                    val keepIds =
                        call.argument<List<Int>>("keepIds") ?: emptyList()

                    if (videoPath == null) {
                        result.error("NO_VIDEO", "videoPath is null", null)
                        return@setMethodCallHandler
                    }

                    val outDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    val csvFile = File(
                        outDir,
                        "landmarks_${System.currentTimeMillis()}.csv"
                    )

                    extractor.startExtraction(
                        videoPath = videoPath,
                        fps = fps,
                        keepIds = keepIds,
                        outputCsv = csvFile
                    ){
                        result.success(csvFile.absolutePath)
                    }
                }

                else -> result.notImplemented()
            }
        }
    }
}
