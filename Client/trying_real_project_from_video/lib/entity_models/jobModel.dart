import 'package:flutter/material.dart';

class detailsOfjobs {
  List<Job>? jobs;

  detailsOfjobs({this.jobs});

  detailsOfjobs.fromJson(Map<String, dynamic> json) {
    if (json['jobs'] != null) {
      jobs = <Job>[];
      json['jobs'].forEach((v) {
        jobs!.add(Job.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    if (this.jobs != null) {
      data['jobs'] = this.jobs!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Job {
  final String title;
  final String address;
  final String timeAgo;
  final String companyLogo;
  final String type;
  final String experienceLevel;
  final String experienceLevelColor;
  bool isMyFav;

  Job(
    this.title,
    this.address,
    this.timeAgo,
    this.companyLogo,
    this.type,
    this.experienceLevel,
    this.experienceLevelColor,
    this.isMyFav,
  );

  factory Job.fromJson(Map<String, dynamic> json) {
    return Job(
      json['title'],
      json['address'],
      json['timeAgo'],
      json['companyLogo'],
      json['type'],
      json['experienceLevel'],
      json['experienceLevelColor'],
      json['isMyFav'],
    );
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['title'] = title;
    data['address'] = address;
    data['timeAgo'] = timeAgo;
    data['companyLogo'] = companyLogo;
    data['type'] = type;
    data['experienceLevel'] = experienceLevel;
    data['experienceLevelColor'] = experienceLevelColor;
    data['isMyFav'] = isMyFav;
    return data;
  }
}
