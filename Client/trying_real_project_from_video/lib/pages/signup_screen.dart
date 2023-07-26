import 'dart:io';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:trying_real_project_from_video/pages/tags_select_page.dart';

import '../globals/global_methods.dart';
import '../globals/global_variabels.dart';

class SignUp extends StatefulWidget {
  String? selectedAvatar;
  @override
  State<SignUp> createState() => _SignUpState();
}

class _SignUpState extends State<SignUp> with TickerProviderStateMixin {
  late Animation<double> _animation;
  late AnimationController _animationController;
  final _signUpFormkey = GlobalKey<FormState>();
  bool _isLoading = false;

  /// needs to be the avatar selection.
  final TextEditingController _nameController = TextEditingController(text: '');
  final TextEditingController _emailController =
      TextEditingController(text: '');
  final TextEditingController _passwordController =
      TextEditingController(text: '');
  final TextEditingController _phoneNumController =
      TextEditingController(text: '');
  final FocusNode _emailFocusNode = FocusNode();
  final FocusNode _passwordFocusNode = FocusNode();
  final FocusNode _phoneNumFocusNode = FocusNode();
  final avatarList = [for (int i = 1; i <= 10; i++) 'icon$i.png'];

  List<String> titles = [
    'Favorite',
    'java',
    'python',
    'c++',
    'c',
    'dart',
    'full stack',
    'frontend',
    'backend',
    'machine learning',
    'deep learning'
  ];
  List<String> places = ['צפון', 'מרכז', 'דרום', 'השרון', 'ירושלים', 'השפלה'];
  List<String> education = [
    'High School',
    'Bachelor\'s Degree',
    'Master\'s Degree',
    'Doctorate Degree',
    'student'
  ];
  List<String> selectedCategories = [];

  bool _obscureText = false;
  bool _isloading = false;
  void dispose() {
    _animationController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _phoneNumController.dispose();
    _emailFocusNode.dispose();
    _passwordFocusNode.dispose();
    _phoneNumFocusNode.dispose();
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

  void _showAvatarChooser() {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        return Container(
          padding: EdgeInsets.all(16),
          color: Colors.transparent,
          child: Wrap(
            spacing: 16,
            runSpacing: 16,
            children: avatarList
                .map((avatar) => GestureDetector(
                      onTap: () {
                        setState(() {
                          widget.selectedAvatar = avatar;
                        });
                        Navigator.pop(context);
                      },
                      child: Container(
                        width: 80,
                        height: 80,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          image: DecorationImage(
                            fit: BoxFit.cover,
                            image: AssetImage(avatar),
                          ),
                        ),
                        child: widget.selectedAvatar == avatar
                            ? Icon(Icons.check_circle, color: Colors.white)
                            : null,
                      ),
                    ))
                .toList(),
          ),
        );
      },
    );
  }

  void _handleSelectionChanged(List<String> selected) {
    setState(() {
      selectedCategories = selected;
    });

    ///for debugging
    print('Selected Categories: $selectedCategories');
  }

  Future<void> _navigateToSelectionClass() async {
    final result = await Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => SelectionTags(
          selectedCategories: List.from(selectedCategories),
          onSelectionChanged: _handleSelectionChanged,
        ),
      ),
    );

    // Return the selected values to the calling screen
    if (result != null) {
      _handleSelectionChanged(result);
    }
  }

  void _submitFormSignUp() async {
    final isValid = _signUpFormkey.currentState!.validate();
    if (isValid) {
      setState(() {
        _isLoading = true;
      });
      /*
      try{
        // await create the user !
        await SheetsFlutter.getByEmail(
            email: _emailTextcontroller.text.trim().toLowerCase(),
            password: _passwordTextcontroller.text.trim());
      }
      final User? user =....

      just puting his params inside the data base ! ..
      name: _nameController.text.trim()..
         */
      try {
        Navigator.canPop(context) ? Navigator.pop(context) : null;
      } catch (error) {
        setState(() {
          _isLoading = false;
        });
        GlobalErrorDialogMethod.showErrorDialog(
            error: error.toString(), ctx: context);
      }
    }

    setState(() {
      _isLoading = false;
    });
  }
  /*
  void _submitFormLogin() async {
    final isValid = _loginformkey.currentState!.validate();
    if (isValid) {
      setState(() {
        _isLoading = true;
      });
      try {
        /*
        await SheetsFlutter.getByEmail(
            email: _emailTextcontroller.text.trim().toLowerCase(),
            password: _passwordTextcontroller.text.trim());
         */
        Navigator.canPop(context) ? Navigator.pop(context) : null;
      } catch (error) {
        setState(() {
          _isLoading = false;
        });
        GlobalMethod.showErrorDialog(error: error.toString(), ctx: context);
        print('error occurred $error');
      }
    }
    setState(() {
      _isLoading = false;
    });
  }


   */

  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    return Scaffold(
      body: Stack(
        children: [
          CachedNetworkImage(
            imageUrl: API.signUpUrlImage,
            placeholder: (context, url) => Image.asset(
              'assets/wallpaper.jpg',
              fit: BoxFit.fill,
            ),
            errorWidget: (context, url, error) => const Icon(Icons.error),
            width: double.infinity,
            height: double.infinity,
            fit: BoxFit.cover,
            alignment: FractionalOffset(_animation.value, 0),
          ),
          Container(
            color: Colors.black54,
            child: Padding(
              padding:
                  const EdgeInsets.symmetric(horizontal: 16.0, vertical: 60),
              child: ListView(
                children: [
                  Form(
                    key: _signUpFormkey,
                    child: Column(
                      children: [
                        const SizedBox(height: 16),
                        GestureDetector(
                          onTap: _showAvatarChooser,
                          child: Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: Container(
                              width: size.width * 0.16,
                              height: size.height * 0.16,
                              decoration: BoxDecoration(
                                border: Border.all(
                                  width: 1,
                                  color: Colors.purple,
                                ),
                                borderRadius: BorderRadius.circular(20),
                              ),
                              child: ClipRRect(
                                borderRadius: BorderRadius.circular(16),
                                child: widget.selectedAvatar != null
                                    ? CircleAvatar(
                                        radius: 40,
                                        backgroundImage:
                                            AssetImage(widget.selectedAvatar!),
                                        backgroundColor: Colors.transparent,
                                      )
                                    : const Icon(Icons.camera_alt_outlined),
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(
                          height: 18,
                        ),
                        TextFormField(
                          textInputAction: TextInputAction.next,
                          onEditingComplete: () => FocusScope.of(context)
                              .requestFocus(_emailFocusNode),
                          keyboardType: TextInputType.name,
                          controller: _nameController,
                          validator: (val) {
                            if (val!.isEmpty) {
                              return "This field is missing";
                            }
                            return null;
                          },
                          style: const TextStyle(color: Colors.white),
                          decoration: const InputDecoration(
                            hintText: 'First Name',
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
                          height: 20,
                        ),
                        TextFormField(
                          textInputAction: TextInputAction.next,
                          onEditingComplete: () => FocusScope.of(context)
                              .requestFocus(_passwordFocusNode),
                          keyboardType: TextInputType.emailAddress,
                          controller: _emailController,
                          validator: (val) {
                            if (val!.isEmpty ||
                                !RegExp(r"^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,253}"
                                        r"[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,253}[a-zA-Z0-9])?)*$")
                                    .hasMatch(val)) {
                              return "This field is missing";
                            }
                            return null;
                          },
                          style: const TextStyle(color: Colors.white),
                          decoration: const InputDecoration(
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
                          height: 20,
                        ),
                        TextFormField(
                          textInputAction: TextInputAction.next,
                          onEditingComplete: () => FocusScope.of(context)
                              .requestFocus(_phoneNumFocusNode),
                          keyboardType: TextInputType.visiblePassword,
                          controller: _passwordController,
                          obscureText: !_obscureText,
                          validator: (val) {
                            if (val!.isEmpty || val.length < 7) {
                              return "enter a valid password , min 7 letters.";
                            }
                            return null;
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
                          ),
                        ),
                        const SizedBox(
                          height: 20,
                        ),
                        TextFormField(
                          textInputAction: TextInputAction.next,
                          onEditingComplete: () => FocusScope.of(context)
                              .requestFocus(_passwordFocusNode),
                          keyboardType: TextInputType.phone,
                          controller: _phoneNumController,
                          validator: (val) {
                            if ((val!.isEmpty) ||
                                !RegExp(r"^(\d+)*$").hasMatch(val)) {
                              return "Enter a valid Mobile Number";
                            }
                            return null;
                          },
                          style: const TextStyle(color: Colors.white),
                          decoration: const InputDecoration(
                            hintText: 'phone number',
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
                          height: 25,
                        ),
                        MaterialButton(
                          onPressed: _navigateToSelectionClass,
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
                                  'Select your location ,IQ level and job types tags',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontWeight: FontWeight.bold,
                                    fontSize: 15,
                                  ),
                                )
                              ],
                            ),
                          ),
                        ),
                        SizedBox(
                          height: 20,
                        ),
                        _isloading
                            ? Center(
                                child: Container(
                                  width: 70,
                                  height: 70,
                                  child: const CircularProgressIndicator(),
                                ),
                              )
                            : MaterialButton(
                                onPressed: () {
                                  _submitFormSignUp();
                                },
                                color: Colors.cyan,
                                elevation: 8,
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(13),
                                ),
                                child: Padding(
                                  padding:
                                      const EdgeInsets.symmetric(vertical: 14),
                                  child: Row(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: const [
                                      Text(
                                        'SignUp',
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
                        const SizedBox(
                          height: 40,
                        ),
                        Center(
                          child: RichText(
                            text: TextSpan(children: [
                              const TextSpan(
                                text: 'already have an account?',
                                style: TextStyle(
                                    color: Colors.white,
                                    fontWeight: FontWeight.bold,
                                    fontSize: 16),
                              ),
                              const TextSpan(text: '     '),
                              TextSpan(
                                recognizer: TapGestureRecognizer()
                                  ..onTap = () => Navigator.canPop(context)
                                      ? Navigator.pop(context)
                                      : null,
                                text: 'Login',
                                style: const TextStyle(
                                    color: Colors.cyan,
                                    fontWeight: FontWeight.bold,
                                    fontSize: 16),
                              ),
                            ]),
                          ),
                        )

                        /// places education
                        /*
                        const SizedBox(height: 8.0),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              'Titles :   ',
                              style: TextStyle(
                                  fontSize: 18,
                                  fontWeight: FontWeight.bold,
                                  color: Colors.cyan),
                            ),
                            const SizedBox(height: 8.0),
                            Expanded(
                              child: SingleChildScrollView(
                                scrollDirection: Axis.horizontal,
                                child: Wrap(
                                  spacing: 8.0,
                                  children: titles.map((title) {
                                    return FilterChip(
                                      label: Text(
                                        title,
                                        style: TextStyle(fontSize: 14),
                                      ),
                                      selected:
                                          selectedCategories.contains(title),
                                      onSelected: (selected) {
                                        setState(() {
                                          if (selected) {
                                            selectedCategories.add(title);
                                          } else {
                                            selectedCategories.remove(title);
                                          }
                                        });
                                      },
                                    );
                                  }).toList(),
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 8.0),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              'places :   ',
                              style: TextStyle(
                                  fontSize: 18,
                                  fontWeight: FontWeight.bold,
                                  color: Colors.cyan),
                            ),
                            const SizedBox(height: 8.0),
                            Expanded(
                              child: SingleChildScrollView(
                                scrollDirection: Axis.horizontal,
                                child: Wrap(
                                  spacing: 8.0,
                                  children: places.map((places) {
                                    return FilterChip(
                                      label: Text(
                                        places,
                                        style: TextStyle(fontSize: 14),
                                      ),
                                      selected:
                                          selectedCategories.contains(places),
                                      onSelected: (selected) {
                                        setState(() {
                                          if (selected) {
                                            selectedCategories.add(places);
                                          } else {
                                            selectedCategories.remove(places);
                                          }
                                        });
                                      },
                                    );
                                  }).toList(),
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 8.0),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              'education :   ',
                              style: TextStyle(
                                  fontSize: 18,
                                  fontWeight: FontWeight.bold,
                                  color: Colors.cyan),
                            ),
                            const SizedBox(height: 8.0),
                            Expanded(
                              child: SingleChildScrollView(
                                scrollDirection: Axis.horizontal,
                                child: Wrap(
                                  spacing: 8.0,
                                  children: education.map((education) {
                                    return FilterChip(
                                      label: Text(
                                        education,
                                        style: TextStyle(fontSize: 14),
                                      ),
                                      selected: selectedCategories
                                          .contains(education),
                                      onSelected: (selected) {
                                        setState(() {
                                          if (selected) {
                                            selectedCategories.add(education);
                                          } else {
                                            selectedCategories
                                                .remove(education);
                                          }
                                        });
                                      },
                                    );
                                  }).toList(),
                                ),
                              ),
                            ),
                          ],
                        ),
                        */
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
