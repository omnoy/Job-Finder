import 'package:flutter/material.dart';

import '../entity_models/oldusersModel.dart';

class checkScreen extends StatefulWidget {
  const checkScreen({Key? key}) : super(key: key);

  @override
  State<checkScreen> createState() => _checkScreenState();
}

class _checkScreenState extends State<checkScreen> {
  final yellow = Color(0xFFFFF59D);
  final black = Colors.black54;

  Offset distance = Offset(6, 6);
  double blur = 20.0;
  @override
  Widget build(BuildContext context) {
    final backgroundColor = const Color(0xFFE7ECEF);

    return Scaffold(
      body: Stack(
        children: [
          Image.asset(
            "backGround.png",
            fit: BoxFit.cover,
            height: double.infinity,
            width: double.infinity,
          ),
          Center(
            child: Container(
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(30.0),
                  color: backgroundColor,
                  boxShadow: [
                    BoxShadow(
                      blurRadius: blur,
                      offset: -distance,
                      color: black,
                    ),
                    BoxShadow(
                      blurRadius: blur,
                      offset: distance,
                      color: yellow,
                    ),
                  ]),
              child: const SizedBox(height: 200, width: 200),
            ),
          ),
        ],
      ),
    );
  }
}
/*

      body: FutureBuilder<List<User>>(
        future: userCon.fetchUsersList(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) {
            return Center(
              child: Text('error'),
            );
          }
          return ListView.separated(
              itemBuilder: (context, index) {
                var user = snapshot.data?[index];
                return Container(
                  height: 100.0,
                  padding: const EdgeInsets.symmetric(horizontal: 16.0),
                  child: Row(
                    children: [
                      Expanded(flex: 1, child: Text('#$user?.name')),
                      Expanded(flex: 3, child: Text('#$user?.email')),
                      Expanded(
                        flex: 3,
                        child: Row(
                          children: [],
                        ),
                      ),
                    ],
                  ),
                );
              },
              separatorBuilder: (context, index) {
                return const Divider(thickness: 0.5, height: 0.5);
              },
              itemCount: snapshot.data?.length ?? 0);
        },
      ),
    );
  }
}
       */
