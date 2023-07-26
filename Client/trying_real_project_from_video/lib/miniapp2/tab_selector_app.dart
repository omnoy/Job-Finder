import 'package:flutter/material.dart';
import 'package:flutter/material.dart';
import 'package:trying_real_project_from_video/entity_models/oldusersModel.dart';
import 'package:trying_real_project_from_video/miniapp2/ido_noy.dart';
import 'package:trying_real_project_from_video/pages/mainScreenForUser.dart';

import '../entity_models/userModel.dart';

class TabSelector extends StatefulWidget {
  @override
  _TabSelectorState createState() => _TabSelectorState();
}

class _TabSelectorState extends State<TabSelector> {
  int _currentIndex = 0;

  void _onTabSelected(int index) {
    setState(() {
      _currentIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          children: [
            TabButton(
              text: 'Job finder',
              isSelected: _currentIndex == 0,
              onTabSelected: () => _onTabSelected(0),
            ),
            TabButton(
              text: 'Resume builedr',
              isSelected: _currentIndex == 1,
              onTabSelected: () => _onTabSelected(1),
            ),
          ],
        ),
        Expanded(
          child: IndexedStack(
            index: _currentIndex,
            children: [
              /*
              AfPage(
                  user: User(
                      email: 'asd',
                      education: 'as',
                      location: '',
                      name: 'asd',
                      password: 'adasdasd',
                      phoneNum: '6116116',
                      myIcon: 'icon3.png',
                      skills: ['asdas'])),
               */
              CvMaker(
                  user: checkUser(
                      username: 'username',
                      email: 'email@gmail.com',
                      avatar: 'avatar',
                      role: 'role')),
            ],
          ),
        ),
      ],
    );
  }
}

class TabButton extends StatelessWidget {
  final String text;
  final bool isSelected;
  final VoidCallback onTabSelected;

  const TabButton({
    required this.text,
    required this.isSelected,
    required this.onTabSelected,
  });

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: InkWell(
        onTap: onTabSelected,
        child: Container(
          color: isSelected ? Colors.blue : null,
          padding: const EdgeInsets.all(16.0),
          child: Center(
            child: Text(
              text,
              style: TextStyle(
                color: isSelected ? Colors.white : Colors.black,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
