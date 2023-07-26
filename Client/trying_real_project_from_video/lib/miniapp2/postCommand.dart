import 'package:flutter/material.dart';
import 'package:trying_real_project_from_video/entity_models/userModel.dart';
import 'package:trying_real_project_from_video/miniapp2/form_data.dart';

class AllToghter {
  final PostCommand com;
  final FormData form;

  AllToghter({required this.com, required this.form});

  factory AllToghter.fromJson(Map<String, dynamic> json) {
    return AllToghter(
      com: PostCommand.fromJson(json['com']),
      form: json['form'],
    );
  }
}

class PostCommand {
  String command;
  TargetObject targetObject;
  InvokedBy invokedBy;

  PostCommand({
    required this.command,
    required this.targetObject,
    required this.invokedBy,
  });

  factory PostCommand.fromJson(Map<String, dynamic> json) {
    return PostCommand(
      command: json['command'],
      targetObject: TargetObject.fromJson(json['targetObject']),
      invokedBy: InvokedBy.fromJson(json['invokedBy']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'command': command,
      'targetObject': targetObject.toJson(),
      'invokedBy': invokedBy.toJson(),
    };
  }
}

class TargetObject {
  ObjectId? objectId;

  TargetObject({this.objectId});

  factory TargetObject.fromJson(Map<String, dynamic> json) {
    return TargetObject(
      objectId:
          json['objectId'] != null ? ObjectId.fromJson(json['objectId']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'objectId': objectId?.toJson(),
    };
  }
}

class ObjectId {
  String? superapp;
  String? internalObjectId;

  ObjectId({this.superapp, this.internalObjectId});

  factory ObjectId.fromJson(Map<String, dynamic> json) {
    return ObjectId(
      superapp: json['superapp'],
      internalObjectId: json['internalObjectId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'superapp': superapp,
      'internalObjectId': internalObjectId,
    };
  }
}

class InvokedBy {
  UserId? userId;

  InvokedBy({this.userId});

  factory InvokedBy.fromJson(Map<String, dynamic> json) {
    return InvokedBy(
      userId: json['userId'] != null ? UserId.fromJson(json['userId']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId?.toJson(),
    };
  }
}

class UserId {
  String? superapp;
  String? email;

  UserId({this.superapp, this.email});

  factory UserId.fromJson(Map<String, dynamic> json) {
    return UserId(
      superapp: json['superapp'],
      email: json['email'],
    );
  }

  void setEmail(String newEmail) {
    email = newEmail;
  }

  Map<String, dynamic> toJson() {
    return {
      'superapp': superapp,
      'email': email,
    };
  }
}
