
import 'package:brotherlabelprintdart/pair.dart';

class TemplateLabel {

  int templateKey;
  List<String> texts;

  TemplateLabel(this.templateKey, this.texts);

  List<String> toNative() {
    /* ----------- Did not find a way to pass a Pair to Native method ----------
    List<Pair<String, String>> result = List<Pair<String, String>>();

    result.add(Pair("START", this.templateKey.toString()));

    for(String text in this.texts) {
      result.add(Pair("TEXT", text));
    }

    return result; */

    List<String> result = List<String>();

    result.add("START||$templateKey");

    for(String text in texts) {
      result.add("TEXT||$text");
    }

    result.add("END");

    return result;
  }
}