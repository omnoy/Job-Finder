import 'package:flutter/material.dart';
import 'package:flutter_html/flutter_html.dart';


class HtmlViewer extends StatelessWidget {
  final String htmlContent;

  const HtmlViewer({required this.htmlContent});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Html(data: htmlContent),
    );
  }
}
