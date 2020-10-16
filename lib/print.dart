import 'dart:async';
import 'dart:io';

import 'package:brotherlabelprintdart/printerModel.dart';
import 'package:brotherlabelprintdart/templateLabel.dart';
import 'package:flutter/services.dart';

class Brotherlabelprintdart {
  static const MethodChannel _channel =
      const MethodChannel('brotherlabelprintdart');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> printLabelFromTemplate(
      String ip, PrinterModel model, List<TemplateLabel> labels) async {
    List<String> data = List<String>();

    for (TemplateLabel label in labels) {
      data += label.toNative();
    }

    try {
      return await _channel.invokeMethod('printLabelFromTemplate',
          {"ip": ip, "model": model.index, "data": data});
    } catch (e) {
      throw 'Template print failed : $e';
    }
  }

  static Future<String> printLabelFromImage(
      String ip, PrinterModel model, File image, int width, int height) async {
    try {
      return await _channel.invokeMethod("printLabelFromImage", {
        "ip": ip,
        "model": model.index,
        "data": image.readAsBytesSync(),
        "width": width,
        "height": height
      });
    } catch (e) {
      throw 'Image print failed : $e';
    }
  }

  static Future<String> printLabelFromPdf(
      String ip, PrinterModel model, File pdf, int numberOfPages) async {
    try {
      return await _channel.invokeMethod("printLabelFromPdf", {
        "ip": ip,
        "model": model.index,
        "data": pdf.readAsBytesSync(),
        "numberOfPages": numberOfPages
      });
    } catch (e) {
      throw 'Pdf print failed : $e';
    }
  }
}
