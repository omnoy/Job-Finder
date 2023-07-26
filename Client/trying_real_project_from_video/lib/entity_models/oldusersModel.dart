import 'dart:convert';

class User {
  final String name;
  final String password;
  final String email;
  final String location;
  final String education;
  final String phoneNum;
  final List<String>? skills;
  final String? myIcon;

  User(
      {required this.name,
      required this.password,
      required this.email,
      required this.location,
      required this.education,
      required this.phoneNum,
      this.skills,
      this.myIcon});

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      name: json['name'],
      password: json['password'],
      email: json['email'],
      location: json['location'],
      education: json['education'],
      phoneNum: json['phoneNum'],
      skills: json['skills'],
      myIcon: json['myIcon'],
    );
  }
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['name'] = name;
    data['password'] = password;
    data['email'] = email;
    data['location'] = location;
    data['education'] = education;
    data['phoneNum'] = phoneNum;
    data['skills'] = skills;
    data['myIcon'] = myIcon;
    return data;
  }

  static User getUserFromJsonString(String jsonString) {
    final Map<String, dynamic> json = jsonDecode(jsonString);
    return User.fromJson(json);
  }
}
