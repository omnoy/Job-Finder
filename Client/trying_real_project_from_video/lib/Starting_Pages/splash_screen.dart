import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:phlox_animations/phlox_animations.dart';
import '../globals/text_string.dart';

class SplashScreenOne extends StatefulWidget {
  const SplashScreenOne({Key? key}) : super(key: key);

  @override
  State<SplashScreenOne> createState() => _SplashScreenOneState();
}

class _SplashScreenOneState extends State<SplashScreenOne> {
  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
          backgroundColor: Colors.white,
          body: SingleChildScrollView(
            child: Padding(
              padding: EdgeInsets.only(
                top: 48.h,
              ),
              child: Center(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Text(
                      titleApp,
                      style: TextStyle(
                          color: Colors.black,
                          fontSize: 19.sp,
                          fontWeight: FontWeight.w700),
                    ),
                    SizedBox(height: 45.h),
                    PhloxAnimations(
                      duration: const Duration(seconds: 2),
                      fromDegrees: 40,
                      fromScale: 0,
                      fromOpacity: 0.1,
                      // fromY: -100,
                      fromX: 100,
                      child: Text(
                        easyFindjob,
                        textAlign: TextAlign.center,
                        style: TextStyle(
                            color: Colors.black,
                            fontSize: 30.sp,
                            fontWeight: FontWeight.bold),
                      ),
                    ),
                    SizedBox(height: 25.h),
                    PhloxAnimations(
                      duration: const Duration(seconds: 2),
                      fromDegrees: 40,
                      fromScale: 0,
                      fromOpacity: 0.1,
                      // fromY: -100,
                      fromX: 100,
                      child: Text(
                        subtitleApp,
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          color: Colors.grey.shade700,
                          fontSize: 18.sp,
                        ),
                      ),
                    ),
                    PhloxAnimations(
                      duration: const Duration(seconds: 2),
                      fromY: -100,
                      // fromX: -50,
                      child: Image.asset(
                        "assets/splash_screen_img1.gif",
                        height: 350.h,
                        // width: 300,
                      ),
                    ),
                    SizedBox(height: 30.h),
                    PhloxAnimations(
                      duration: const Duration(seconds: 2),
                      fromY: -100,
                      // fromX: -50,
                      child: SizedBox(
                        height: 48.h,
                        width: 140.w,
                        child: ElevatedButton(
                          onPressed: () {
                            Navigator.of(context).pushNamed('/SwitchPageStart');
                          },
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.black,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(20.r),
                            ),
                          ),
                          child: Text(
                            Get_Started,
                            style:
                                TextStyle(fontSize: 15.sp, color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          )),
    );
  }
}
