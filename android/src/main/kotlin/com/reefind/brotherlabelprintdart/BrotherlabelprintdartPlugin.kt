package com.reefind.brotherlabelprintdart

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.NonNull
import com.brother.ptouch.sdk.Printer
import com.brother.ptouch.sdk.PrinterInfo
import com.brother.ptouch.sdk.PrinterStatus
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File
import java.nio.IntBuffer

/** BrotherlabelprintdartPlugin */
public class BrotherlabelprintdartPlugin: FlutterPlugin, MethodCallHandler {
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    val channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "brotherlabelprintdart")
    channel.setMethodCallHandler(BrotherlabelprintdartPlugin());
  }

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "brotherlabelprintdart")
      channel.setMethodCallHandler(BrotherlabelprintdartPlugin())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method) {
      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      "printLabelFromTemplate" -> result.success(printLabelFromTemplate(
              call.argument<String>("ip").orEmpty(),
              call.argument<Int>("model")!!,
              call.argument<List<String>>("data").orEmpty()
      ))
      "printLabelFromImage" -> result.success(printLabelFromImage(
              call.argument<String>("ip").orEmpty(),
              call.argument<Int>("model")!!,
              call.argument<ByteArray>("data")!!,
              call.argument<Int>("width")!!,
              call.argument<Int>("height")!!
      ))
      "printLabelFromPdf" -> result.success(printLabelFromPdf(
              call.argument<String>("ip").orEmpty(),
              call.argument<Int>("model")!!,
              call.argument<ByteArray>("data")!!,
              call.argument<Int>("numberOfPages")!!
      ))
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {

  }

  private fun printLabelFromImage(printerIp: String, printerModel: Int, data: ByteArray, width : Int, height : Int) : String{
    val printer = Printer()

    val info = PrinterInfo()

    info.ipAddress = printerIp
    info.printerModel = PrinterInfo.Model.valueFromID(printerModel)
    info.port = PrinterInfo.Port.NET
    printer.printerInfo = info

    val thread = Thread(Runnable {
      try {
        Log.d("BROTHER LABEL PRINT", "Thread started")

        //val result = PrinterStatus()

        val dataInt = IntArray(data.size / 2) {
          (data[it * 2].toUByte().toInt() + (data[(it * 2) + 1].toInt() shl 8))
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(dataInt))

        val result = printer.printImage(bitmap);

        if(result.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
          Log.e("BROTHER PRINTER ERROR", result.errorCode.toString())
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      Log.d("BROTHER LABEL PRINT", "Print finished")
    })

    thread.start()
    Log.d("BROTHER LABEL PRINT", "Printing on : $printerIp")

    return "Finished"
  }

  private fun printLabelFromTemplate(printerIp : String, printerModel : Int, data : List<String>) : String {
    val printer = Printer()

    val info = PrinterInfo()
    info.ipAddress = printerIp
    info.printerModel = PrinterInfo.Model.valueFromID(printerModel)
    info.port = PrinterInfo.Port.NET
    printer.printerInfo = info

    val thread = Thread(Runnable {
      try {
        Log.d("BROTHER LABEL PRINT", "Thread started")

        var result = PrinterStatus()

        for(d in data) {
          val tmp = d.split("||")
          when(tmp[0]) {
            "START" -> printer.startPTTPrint(tmp[1].toInt(), null)
            "TEXT" -> printer.replaceText(tmp[1])
            "END" -> result = printer.flushPTTPrint()
          }
        }

        if(result.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
          Log.e("BROTHER PRINTER ERROR", result.errorCode.toString())
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      Log.d("BROTHER LABEL PRINT", "Print finished")
    })

    thread.start()
    Log.d("BROTHER LABEL PRINT", "Printing on : $printerIp")

    return "Finished"
  }

  private fun printLabelFromPdf(printerIp: String, printerModel: Int, data: ByteArray, numberOfPages : Int) : String{
    val printer = Printer()

    val info = PrinterInfo()

    info.ipAddress = printerIp
    info.printerModel = PrinterInfo.Model.valueFromID(printerModel)
    info.port = PrinterInfo.Port.NET
    printer.printerInfo = info

    val thread = Thread(Runnable {
      try {
        Log.d("BROTHER LABEL PRINT", "Thread started")

        var result = PrinterStatus()

        val pdfFile = File.createTempFile("label", "pdf");
        pdfFile.writeBytes(data)

        for(i in 0..numberOfPages) {
          result = printer.printPDF(pdfFile.path, i);
        }

        if(result.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
          Log.e("BROTHER PRINTER ERROR", result.errorCode.toString())
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      Log.d("BROTHER LABEL PRINT", "Print finished")
    })

    thread.start()
    Log.d("BROTHER LABEL PRINT", "Printing on : $printerIp")

    return "Finished"
  }
}