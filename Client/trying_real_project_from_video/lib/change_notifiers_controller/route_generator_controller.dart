import 'package:flutter/material.dart';
import 'package:trying_real_project_from_video/Starting_Pages/splashWelcomePage.dart';
import 'package:trying_real_project_from_video/entity_models/userModel.dart';
import 'package:trying_real_project_from_video/miniapp2/ido_noy.dart';
import '../Starting_Pages/onbording_screen.dart';
import '../Starting_Pages/splash_screen.dart';
import '../pages/loginpage.dart';
import '../pages/mainScreenForUser.dart';

class RouteGenerator {
  static Route<dynamic> generateRoute(RouteSettings settings) {
    // Getting arguments passed in while calling Navigator.pushNamed
    final args = settings.arguments;
    switch (settings.name) {
      case '/':
        return MaterialPageRoute(
            builder: (_) => const Splash()); //SplashScreenOne
      case '/SplashScreenOne':
        return MaterialPageRoute(builder: (_) => SplashScreenOne());
      case '/SwitchPageStart':
        return MaterialPageRoute(builder: (_) => SwitchPageStart());
      case '/LoginPage':
        return MaterialPageRoute(builder: (_) => LoginNew());

      default:
        // If there is no such named route in the switch statement, e.g. /third
        return _errorRoute();
    }
  }

  static Route<dynamic> _errorRoute() {
    return MaterialPageRoute(builder: (_) {
      return Scaffold(
        appBar: AppBar(
          title: const Text('Error'),
        ),
        body: const Center(
          child: Text('ERROR'),
        ),
      );
    });
  }
}
