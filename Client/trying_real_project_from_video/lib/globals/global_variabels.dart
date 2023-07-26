import 'package:flutter/material.dart';

class API {
  static const String basicUrl =
      'http://localhost:8084/superapp/admin/users?userSuperapp=2023b.ido.ronen&userEmail=admin@jobfinder.org';
  static const String urlPostForm =
      'http://localhost:8084/superapp/miniapp/resumeBuilder?async=true';
  static const String urlPostObject = 'http://localhost:8084/superapp/objects';

  static const String basicurlGetSpecificObject =
      'http://localhost:8084/superapp/objects/2023b.ido.ronen/';

  static const String urlGetSpecificObject =
      '?userSuperapp=2023b.ido.ronen&userEmail=superuser@jobfinder.org';

  ///////////////////////////////////////////
  static const String urlUsers =
      'http://localhost:8084/superapp/users/login/2023b.ido.ronen/';
  static const String urlCreateUser =
      'http://localhost:8084/superapp/admin/users?userSuperapp=2023b.ido.ronen&userEmail=admin@jobfinder.org';
  static const String urlgetUserRecordCommand =
      'http://localhost:8084/superapp/miniapp/jobsGatherer';
  static const String urlDeleteUser =
      'http://localhost:8084/superapp/admin/delete-user';
  static const String urlUpdateUser =
      'http://localhost:8084/superapp/admin/update-user';
  static const String urlPostUser =
      'http://localhost:8084/superapp/admin/update-user';

  /// ////////////// FOR CHACEHD IMAGES/////////////////
  static const String loginUrlImage =
      'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80';
  static const String signUpUrlImage =
      'https://images.pexels.com/photos/443383/pexels-photo-443383.jpeg';
}
