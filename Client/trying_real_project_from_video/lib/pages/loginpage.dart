import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:trying_real_project_from_video/globals/global_variabels.dart';
import '../entity_models/userModel.dart';
import '../forgot_pass_word_pages/forgot_password_page.dart';
import '../globals/global_fetches.dart';
import '../miniapp2/ido_noy.dart';
import '../miniapp2/tab_selector_app.dart';
import 'mainScreenForUser.dart';
import '../entity_models/oldusersModel.dart';
import '../globals/global_methods.dart';
import 'forget_password_screen.dart';
import 'signup_screen.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;

class LoginNew extends StatefulWidget {
  const LoginNew({Key? key}) : super(key: key);
  @override
  State<LoginNew> createState() => _LoginNewState();
}

class _LoginNewState extends State<LoginNew> with TickerProviderStateMixin {
  late Animation<double> _animation;
  late AnimationController _animationController;
  final _loginformkey = GlobalKey<FormState>();
  final DataFetcher dataFetcher = DataFetcher();

  ///its an object that can be used by the scafold widget
  ///to obtain the keyboard focus and simply handle keyboard events.
  final FocusNode _passFocusNode = FocusNode();

  final TextEditingController _emailTextcontroller =
      TextEditingController(text: '');
  final TextEditingController _passwordTextcontroller =
      TextEditingController(text: '');
  bool _obscureText = false;
  bool _isLoading = false;
  @override
  void dispose() {
    _animationController.dispose();
    _emailTextcontroller.dispose();
    _passwordTextcontroller.dispose();
    _passFocusNode.dispose();
    super.dispose();
  }

  @override
  void initState() {
    _animationController =
        AnimationController(vsync: this, duration: const Duration(seconds: 20));
    _animation =
        CurvedAnimation(parent: _animationController, curve: Curves.linear)
          ..addListener(() {
            setState(() {});
          })
          ..addStatusListener((animationStatus) {
            if (animationStatus == AnimationStatus.completed) {
              _animationController.reset();
              _animationController.forward();
            }
          });
    _animationController.forward();
    super.initState();
  }

  void _submitFormLogin() async {
    final isValid = _loginformkey.currentState!.validate();
    if (isValid) {
      setState(() {
        _isLoading = true;
      });

      try {
        //get 1 user
        final currectUser =
            await dataFetcher.fetchUserFromServer(_emailTextcontroller.text);
        Navigator.pushReplacement(context,
            MaterialPageRoute(builder: (context) => AfPage(user: currectUser)));
        /*
        Navigator.pushReplacement(
            context,
            MaterialPageRoute(
                builder: (context) => CvMaker(user: currectUser)));
         */
      } catch (error) {
        setState(() {
          _isLoading = false;
        });
        GlobalErrorDialogMethod.showErrorDialog(
            error: "there user you entered is not registered", ctx: context);
        print('Error occurred: $error');
      }
    }

    setState(() {
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          CachedNetworkImage(
            imageUrl: API.loginUrlImage,
            placeholder: (context, url) => Image.asset(
              'assets/gif2.gif',

              /// to change !!
              fit: BoxFit.cover,
            ),
            errorWidget: (context, url, error) => const Icon(
              (Icons.error),
            ),
            width: double.infinity,
            height: double.infinity,
            fit: BoxFit.cover,
            alignment: FractionalOffset(_animation.value, 0),
          ),
          Container(
            color: Colors.black54,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 80),
              child: ListView(
                children: [
                  Padding(
                    padding: const EdgeInsets.only(left: 80, right: 80, top: 5),
                    child: SizedBox(
                        height: 160,
                        width: 128,
                        child:
                            Image.asset('assets/Shutterstock_2031933263.png')),
                  ),
                  const SizedBox(
                    height: 15,
                  ),
                  //TabSelector(),
                  Form(
                    key: _loginformkey,
                    child: Column(
                      children: [
                        TextFormField(
                          textInputAction: TextInputAction.next,

                          ///it will describe the part of user interface
                          ///its simply change the focus to that .
                          onEditingComplete: () => FocusScope.of(context)
                              .requestFocus(_passFocusNode),
                          keyboardType: TextInputType.emailAddress,
                          controller: _emailTextcontroller,
                          validator: (val) {
                            if ((val!.isEmpty) ||
                                !RegExp(r"^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,253}"
                                        r"[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,253}[a-zA-Z0-9])?)*$"!)
                                    .hasMatch(val)) {
                              return "Enter a valid E-mail address";
                            }
                            return null;
                          },
                          style: const TextStyle(color: Colors.white),
                          decoration: const InputDecoration(
                            //labelText: 'Email adress',
                            hintText: 'Email',
                            hintStyle: TextStyle(color: Colors.white),
                            enabledBorder: UnderlineInputBorder(
                              borderSide: BorderSide(color: Colors.white),
                            ),
                            focusedBorder: UnderlineInputBorder(
                              borderSide: BorderSide(color: Colors.white),
                            ),
                            errorBorder: UnderlineInputBorder(
                              borderSide: BorderSide(color: Colors.red),
                            ),
                          ),
                        ),
                        const SizedBox(
                          height: 5,
                        ),
                        TextFormField(
                          textInputAction: TextInputAction.next,
                          onEditingComplete: () => FocusScope.of(context)
                              .requestFocus(_passFocusNode),
                          keyboardType: TextInputType.visiblePassword,
                          controller: _passwordTextcontroller,

                          ///will change the password dynamically
                          obscureText: !_obscureText,
                          validator: (val) {
                            if ((val!.isEmpty) || (val.length < 7)) {
                              return "please Enter a valid password";
                            } else {
                              return null;
                            }
                          },
                          style: const TextStyle(color: Colors.white),
                          decoration: InputDecoration(
                            suffixIcon: GestureDetector(
                              onTap: () {
                                setState(() {
                                  _obscureText = !_obscureText;
                                });
                              },
                              child: Icon(
                                _obscureText
                                    ? Icons.visibility
                                    : Icons.visibility_off,
                                color: Colors.white,
                              ),
                            ),
                            //labelText: 'password',
                            hintText: 'password',
                            hintStyle: const TextStyle(color: Colors.white),
                            enabledBorder: const UnderlineInputBorder(
                              borderSide: BorderSide(color: Colors.white),
                            ),
                            focusedBorder: const UnderlineInputBorder(
                              borderSide: BorderSide(color: Colors.white),
                            ),
                            errorBorder: const UnderlineInputBorder(
                              borderSide: BorderSide(color: Colors.red),
                            ),
                            /*
                            focusedErrorBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(100.0),
                                borderSide:
                                    BorderSide(color: Colors.red, width: 2.0)),
                             */
                          ),
                        ),
                        const SizedBox(
                          height: 15,
                        ),
                        /*

                        Align(
                          alignment: Alignment.bottomRight,
                          child: TextButton(
                            onPressed: () {
                              Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                      builder: (context) =>
                                          ForgotPasswordPage()));
                            },
                            child: const Text(
                              'Forget password?',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 17,
                                fontStyle: FontStyle.italic,
                              ),
                            ),
                          ),
                        ), */
                        const SizedBox(
                          height: 10,
                        ),
                        SizedBox(
                          height: 55.0,
                          width: 320.0,
                          child: MaterialButton(
                            onPressed: _submitFormLogin,
                            color: Colors.cyan,
                            elevation: 8,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(13),
                            ),
                            child: Padding(
                              padding: const EdgeInsets.symmetric(vertical: 14),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: const [
                                  Text(
                                    "login",
                                    style: TextStyle(
                                      color: Colors.white,
                                      fontWeight: FontWeight.bold,
                                      fontSize: 20,
                                    ),
                                  )
                                ],
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(
                          height: 40,
                        ),
                        Center(
                          child: RichText(
                            text: TextSpan(children: [
                              const TextSpan(
                                text: 'Do not have an account?',
                                style: TextStyle(
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                  fontSize: 16,
                                ),
                              ),
                              const TextSpan(text: '     '),
                              TextSpan(
                                  recognizer: TapGestureRecognizer()
                                    ..onTap = () => Navigator.push(
                                        context,
                                        MaterialPageRoute(
                                            builder: (context) => SignUp())),
                                  text: 'Signup',
                                  style: const TextStyle(
                                    color: Colors.cyan,
                                    fontWeight: FontWeight.bold,
                                    fontSize: 16,
                                  )),
                            ]),
                          ),
                        )
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
