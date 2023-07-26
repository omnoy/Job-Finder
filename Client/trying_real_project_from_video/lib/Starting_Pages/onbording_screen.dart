import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:phlox_animations/phlox_animations.dart';

import 'package:trying_real_project_from_video/pages/loginpage.dart';

import '../globals/text_string.dart';

class SwitchPageStart extends StatefulWidget {
  const SwitchPageStart({super.key});

  @override
  _SwitchPageStartState createState() => _SwitchPageStartState();
}

class _SwitchPageStartState extends State<SwitchPageStart> {
  int currentIndex = 0;
  late PageController _controller;

  @override
  void initState() {
    _controller = PageController(initialPage: 0);
    super.initState();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Column(
        children: [
          Expanded(
            child: PageView.builder(
              controller: _controller,
              itemCount: contents.length,
              onPageChanged: (int index) {
                setState(() {
                  currentIndex = index;
                });
              },
              itemBuilder: (_, i) {
                return Padding(
                  padding: const EdgeInsets.only(top: 50, right: 40, left: 40),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Wrap(
                        children: [
                          PhloxAnimations(
                            duration: const Duration(seconds: 1),
                            fromY: -100,
                            // fromX: -50,
                            child: Image.asset(
                              contents[i].image!,
                              height: 280.h,
                            ),
                          ),
                          SizedBox(
                            height: 40.h,
                          ),
                          Container(
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: List.generate(
                                contents.length,
                                (index) => buildDot(index, context),
                              ),
                            ),
                          ),
                          SizedBox(
                            height: 75.h,
                          ),
                          PhloxAnimations(
                            duration: const Duration(seconds: 1),
                            fromDegrees: 40,
                            fromScale: 0,
                            fromOpacity: 0.1,
                            // fromY: -100,
                            fromX: 100,
                            child: Text(
                              contents[i].title!,
                              style: TextStyle(
                                  fontSize: 35.sp,
                                  fontWeight: FontWeight.w700,
                                  color: Colors.black),
                            ),
                          ),
                          SizedBox(height: 18.h),
                          PhloxAnimations(
                            duration: const Duration(seconds: 1),
                            fromDegrees: 40,
                            fromScale: 0,
                            fromOpacity: 0.1,
                            // fromY: -100,
                            fromX: 100,
                            child: Text(
                              contents[i].discription!,
                              textAlign: TextAlign.start,
                              style: TextStyle(
                                fontSize: 16.sp,
                                fontWeight: FontWeight.w500,
                                color: Colors.grey,
                              ),
                            ),
                          ),
                          SizedBox(height: 10.h),
                        ],
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
          Align(
            alignment: Alignment.topRight,
            widthFactor: 2.0.w,
            child: InkWell(
              onTap: () {
                Navigator.of(context).pushNamed('/LoginPage');
              },
              child: Text(
                Skip,
                style: TextStyle(
                  color: Colors.grey.shade800,
                  fontSize: 20.sp,
                ),
              ),
            ),
          ),
          SizedBox(
            height: 50.h,
          ),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 40.w, vertical: 25.h),
            child: Row(
              children: [
                InkWell(
                  onTap: () {
                    _controller.previousPage(
                      duration: const Duration(milliseconds: 100),
                      curve: Curves.bounceIn,
                    );
                  },
                  child: Container(
                    height: 45.h,
                    width: 60.w,
                    decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(15.r),
                        border: Border.all(color: Colors.black, width: 1.5.w)),
                    child: Icon(Icons.arrow_back, size: 25.sp),
                  ),
                ),
                const Spacer(),
                SizedBox(
                  height: 45.h,
                  width: 130.w,
                  child: ElevatedButton(
                    onPressed: () {
                      if (currentIndex == contents.length - 1) {
                        Navigator.pushReplacement(
                          context,
                          MaterialPageRoute(
                            builder: (_) => LoginNew(),
                          ),
                        );
                      }
                      _controller.nextPage(
                        duration: Duration(milliseconds: 100),
                        curve: Curves.bounceIn,
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      primary: Colors.black,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(20),
                      ),
                    ),
                    child: Text(
                      Next,
                      style: TextStyle(fontSize: 20.sp, color: Colors.white),
                    ),
                  ),
                ),
              ],
            ),
          )
        ],
      ),
    );
  }

  Container buildDot(int index, BuildContext context) {
    return Container(
      height: 5.h,
      width: currentIndex == index ? 40.w : 25.w,
      margin: const EdgeInsets.only(right: 5),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(20.r),
        color: currentIndex == index ? Colors.black : Colors.grey.shade400,
      ),
    );
  }
}

class UnbordingContent {
  String? image;
  String? title;
  String? discription;

  UnbordingContent({this.image, this.title, this.discription});
}

List<UnbordingContent> contents = [
  UnbordingContent(
      title: titlepage1, image: "assets/gif1.gif", discription: subtitlepage1),
  UnbordingContent(
      title: titlepage2,
      image: "assets/send_gif.gif",
      discription: subtitlepage2),
  UnbordingContent(
      title: titlepage3, image: "assets/gif3.gif", discription: subtitlepage3),
];
