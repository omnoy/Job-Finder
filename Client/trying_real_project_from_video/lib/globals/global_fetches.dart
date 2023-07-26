import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:trying_real_project_from_video/miniapp2/form_data.dart';
import 'dart:io';
import '../entity_models/userModel.dart';
import '../miniapp2/postCommand.dart';
import 'global_variabels.dart';

class DataFetcher {
  ///  ///////////////// get functions ////////////
  Future<checkUser> fetchUserFromServer(String endpoint) async {
    final response = Uri.parse(API.urlUsers + endpoint);
    var _headers = {
      "Accept": "application/json",
      "content-type": "application/json"
    };
    var getResponse = await http.get(response, headers: _headers);
    print(getResponse);
    print(getResponse.body);
    print(API.urlUsers + endpoint);
    print(getResponse.statusCode);

    if (getResponse.statusCode == 200) {
      if (getResponse.body != null && getResponse.body.isNotEmpty) {
        final te = jsonDecode(getResponse.body);
        final theEmail = te['userId']['email'];
        final name = te['username'];
        final theRole = te['role'];
        final theavatar = te['avatar'];
        //final res = checkUser.fromJson();
        //print(res['userId']);
        final theUser = checkUser(
          username: name,
          role: theRole,
          avatar: theavatar,
          email: theEmail,
        );
        print(theUser);
        return theUser;
      } else {
        throw Exception('Empty response body');
      }
    } else {
      throw Exception('Failed to fetch data from the server');
    }
  }

  Future<List<String>> fetchStringListFromServer(String endpoint) async {
    final response = await http.get(Uri.parse('asdas')); //API.urlUsers));

    if (response.statusCode == 200) {
      final List<dynamic> jsonData = jsonDecode(response.body);
      final List<String> stringList =
          jsonData.map((value) => value.toString()).toList();
      return stringList;
    } else {
      return [];
    }
  }

  /// ////////////////////// post //////////////////
  Future<void> createUser(checkUser user) async {
    final uri = Uri.parse(API.urlPostUser);
    final Map<String, dynamic> data = user.toJson();
    final response = await http.post(uri, body: data);
    if (response.statusCode == 201) {
      // User created successfully
    } else {
      throw Exception('Failed to post data to the server');
    }
  }

  /// //////////////////// delete /////////////
  Future<void> deleteUser(String userId) async {
    final uri = Uri.parse(API.urlDeleteUser + '/$userId');
    final response = await http.delete(uri);
    if (response.statusCode == 200) {
      // User deleted successfully
    } else {
      throw Exception('Failed to delete user from data in the server');
    }
  }

  /// /////////////////   put   ///////////////////
  Future<void> updateUser(checkUser user) async {
    final uri = Uri.parse(API.urlUpdateUser);
    final data = user.toJson();
    final response = await http.put(uri, body: data);
    if (response.statusCode == 200) {
      print("User updated successfully");
    } else {
      throw Exception('Failed to delete user from data in the server');
    }
  }

  /// ////////////////////// mini app 2 /////////////////
  Future<dynamic> sendFormCV(FormData form, checkUser user) async {
    var _headers = {
      "Accept": "application/json",
      "content-type": "application/json"
    };
    final objectUrl = Uri.parse(API.urlPostObject);
    var _objectpayload = jsonEncode({
      "alias": "UserResume",
      "type": "HTML",
      "active": true,
      "createdBy": {
        "userId": {
          "superapp": "2023b.ido.ronen",
          "email": "superuser@jobfinder.org"
        }
      },
      "objectDetails": {"status": "inProgress"}
    });
    var returnedObject =
        await http.post(objectUrl, body: _objectpayload, headers: _headers);
    var objectId =
        jsonDecode(returnedObject.body)['objectId']['internalObjectId'];
    final userEmail = user.email.toString();
    var originalJson = {
      "command": "BUILD_RESUME",
      "targetObject": {
        "objectId": {
          "superapp": "2023b.ido.ronen",
          "internalObjectId": "$objectId"
        }
      },
      "invokedBy": {
        "userId": {"superapp": "2023b.ido.ronen", "email": userEmail}
      },
      "commandAttributes": {}
    };
    originalJson['commandAttributes'] = form; //form.toJson();
    final uri = Uri.parse(API.urlPostForm);
    var _payload = json.encode(originalJson);
    var response = await http.post(uri, body: _payload, headers: _headers);

    final getObjectUri = Uri.parse(
        API.basicurlGetSpecificObject + objectId + API.urlGetSpecificObject);
    var status = "notready";
    late http.Response getResponse;
    do {
      getResponse = await http.get(getObjectUri, headers: _headers);
      print("loading");
      status = jsonDecode(getResponse.body)['objectDetails']['status'];
      await Future.delayed(Duration(seconds: 5));
    } while (status != "ready");
    print("after");
    if (returnedObject.statusCode == 200) {
      print("////////create Object Finished///////");
      print(returnedObject.statusCode.toString());
    } else {
      print('Failed to create object');
    }
    if (response.statusCode == 200) {
      print("////////post command Finished///////");
      print(response.statusCode.toString());
    } else {
      print('Failed post command');
    }
    //print(getResponse.body);
    var html = jsonDecode(getResponse.body)['objectDetails']['resume']['html'];
    print(html.runtimeType);
    return html;
  }

/*

  @override
  Future<List<User>> getUsersList() async {
    List<User> usersList = [];
    var url = Uri.parse('$basicDataUrl/users');
    var response = await http.get(url);
    print('status code : ${response.statusCode}');
    var body = json.decode(response.body); //convert
    //parse
    for (var i = 0; i < body.length; i++) {
      usersList.add(User.fromJson(body[i]));
    }
    return usersList;
  }

  @override
  Future<String> patchCompleted(User user) async {
    var url = Uri.parse('$basicDataUrl/users/${user.name}');
    String resData = '';
    await http.patch(
      url,
      body: {
        'email': (user.email).toString(),
      },
      headers: {'Authorization': 'your token'},
    ).then((response) {
      Map<String, dynamic> result = json.decode(response.body);
      print(result);
      return resData = result['name'];
    });
    return resData;
  }


 */
}

/*
 {
      "command": "BUILD_RESUME",
      "targetObject": {
        "objectId": {
          "superapp": "2023b.ido.ronen",
          "internalObjectId": "eb448fce-410a-4e70-b2c1-393cc2f5a6c1"
        }
      },
      "invokedBy": {
        "userId": {"superapp": "2023b.ido.ronen", "email": "miniapp@demo.org"}
      }
    }

 */

///implementing in app widgets..
/*

final DataFetcher dataFetcher = DataFetcher();

  @override
  void initState() {
    super.initState();
    fetchData();
  }

  Future<void> fetchData() async {
    try {
      final List<MyData> data = await dataFetcher.fetchDataFromServer();
      // Handle the fetched data as needed
 */
