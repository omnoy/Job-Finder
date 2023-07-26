import 'package:flutter/material.dart';

class checkUser {
  /*
  var userDetails = {

    "_id": {
      "superapp": '2023b.ido.ronen',
      "internalObjectId": '6bde1043-9b97-41f9-9fce-f3b4077b6f04'
    },
    "type": 'userRecord',
    "alias": 'mrminiUserRecord',
    "active": true,
    "creationTimeStamp": "2023-06-06T13:44:01.907Z",
    "location": [0, 0],
    "createdBy": {
      "email": 'superuser@jobfinder.org',
      "superapp": '2023b.ido.ronen'
    },
    "objectDetails": {
      "owner": {
        "userId": {"superapp": '2023b.ido.ronen', "email": 'mini@gmail.com'}
      },
      "favoriteJobInternalIds": ['3cf1925c-c5d2-439d-ac05-60eac55338c3'],
      "selectedJobTags": ['python']
    },
    "children": [],
    "parents": [],
    "_class": 'superapp.data.SuperAppObjectEntity'
  };
   */
  final String username;
  final String email;
  final String avatar;
  final String role;
  checkUser(
      {required this.username,
      required this.email,
      required this.avatar,
      required this.role});

  factory checkUser.fromJson(Map<String, dynamic> json) {
    return checkUser(
      username: json['username'],
      email: json['_id']['email'],
      role: json['role'],
      avatar: json['avatar'],
    );
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{
      'username': username,
      '_id': {"email": email},
      'role': role,
      'avatar': avatar,
    };
    return data;
  }
}
