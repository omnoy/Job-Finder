import 'package:flutter/material.dart';
import 'package:trying_real_project_from_video/entity_models/userModel.dart';

import 'mainScreenForUser.dart';
import '../entity_models/oldusersModel.dart';

class HeroProfilePage extends StatelessWidget {
  final checkUser user;

  const HeroProfilePage({Key? key, required this.user}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leadingWidth: 20,
        leading: IconButton(
          padding: EdgeInsets.only(left: 20),
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => AfPage(user: user),
              ),
            );
          },
          icon: Icon(
            Icons.arrow_back_ios,
            color: Colors.grey.shade600,
          ),
        ),
      ),
      body: Center(
        child: Hero(
          tag: '${user.username}${user.email}',
          child: CircleAvatar(
            radius: 120,
            backgroundColor: Colors.white,
            backgroundImage: AssetImage(user.avatar ?? ''),
          ),
        ),
      ),
    );
  }
}
