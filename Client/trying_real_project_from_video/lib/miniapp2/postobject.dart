import 'package:flutter/material.dart';

class PostObject {
  final String alias;
  final String type;
  final String active;
  final String email;

  PostObject(
      {required this.alias,
      required this.type,
      required this.active,
      required this.email});

  factory PostObject.fromJson(Map<String, dynamic> json) {
    return PostObject(
      type: json['type'],
      alias: json['alias'],
      active: json['active'],
      email: json['email'],
    );
  }
/*

  Map<String, dynamic> toJson() {
    return {
      "alias": alias,
      "type": type,
      "active": active,
      "createdBy": {
        'userId': {'email': email}
      },
    };
  }
 */
}
