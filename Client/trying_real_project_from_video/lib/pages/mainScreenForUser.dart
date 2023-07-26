import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:trying_real_project_from_video/entity_models/oldusersModel.dart';
import 'package:trying_real_project_from_video/globals/global_fetches.dart';
import '../entity_models/userModel.dart';
import '../globals/global_variabels.dart';
import 'hero_profile_page.dart';
import '../entity_models/jobModel.dart';
import 'jobdetails.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AfPage extends StatefulWidget {
  const AfPage({required this.user, Key? key}) : super(key: key);
  final checkUser user;

  @override
  _AfPageState createState() => _AfPageState();
}

class _AfPageState extends State<AfPage> {
  DataFetcher data = DataFetcher();
  List<String> selectedCategories = [];
  late Future<dynamic> FjobList;
  List<dynamic> jobList = [];
  List<String> categories = [
    'Favorite',
    'java',
    'python',
    'c++',
    'c',
    'dart',
    'full stack',
    'frontend',
    'backend',
    'machine learning',
    'deep learning'
  ];
/*
      jobList = await data.fetchStringListFromServer('/jobs');
      if (jobList.isEmpty) {
 */
  Future<void> readJsonJobs() async {
    final String response = await rootBundle.loadString('assets/jobsn.json');
    setState(
      () {
        final data2 = json.decode(response);
        jobList = data2['jobs'].map((job) => Job.fromJson(job)).toList();
      },
    );
    //});
  }
/*

  Future<void> readJsonCategories() async {
    setState(() async {
      categories = await data.fetchStringListFromServer('/categories');
      if (categories.isEmpty) {
        categories = [
          'Favorite',
          'java',
          'python',
          'c++',
          'c',
          'dart',
          'full stack',
          'frontend',
          'backend',
          'machine learning',
          'deep learning'
        ];
      }
    });
  }
 */

  @override
  void initState() {
    super.initState();
    readJsonJobs();
    //FjobList = sendCommand(widget.user.email);
    //jobList = FjobList.map((job) => Job.fromJson(job)).toList();
    //readJsonCategories();
    // selectedCategories = widget.tags;
  }

  Future<dynamic> sendCommand(String email) async {
    var _headers = {
      "Accept": "application/json",
      "content-type": "application/json"
    };
    var body = {
      "command": "getUserRecord",
      "targetObject": {
        "objectId": {
          "superapp": "2023b.ido.ronen",
          "internalObjectId": "70932770-ae3f-4123-87e8-e7e753bd9443"
        }
      },
      "invokedBy": {
        "userId": {"superapp": "2023b.ido.ronen", "email": email}
      }
    };
    final commandurl = Uri.parse(API.urlgetUserRecordCommand);
    var responsenew =
        await http.post(commandurl, headers: _headers, body: body);
    if (responsenew.statusCode == 200) {
      var data = jsonDecode(responsenew.body);
      print("////////create Object Finished///////");
      print(responsenew.statusCode.toString());
      return data;
    } else {
      print('Failed to create object');
      return null;
    }
  }

  @override
  Widget build(BuildContext context) {
    final filteredJobs = jobList.where((job) {
      if (selectedCategories.contains('Favorite')) {
        return job.isMyFav;
      } else {
        return selectedCategories.isEmpty ||
            selectedCategories.contains(job.title);
      }
    }).toList();

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        /*
        leadingWidth: 20,
        leading: IconButton(
          padding: EdgeInsets.only(left: 30),
          onPressed: () {},
          icon: Icon(
            Icons.arrow_back_ios,
            color: Colors.grey.shade600,
          ),
        ),
         */
        actions: [
          SafeArea(
            child: Padding(
              padding: EdgeInsets.symmetric(horizontal: 16),
              child: Row(
                children: [
                  GestureDetector(
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) =>
                              HeroProfilePage(user: widget.user),
                        ),
                      );
                    },
                    child: Column(
                      children: [
                        Hero(
                          tag: '${widget.user.username}${widget.user.email}',
                          child: CircleAvatar(
                            backgroundColor: Colors.white,
                            backgroundImage:
                                AssetImage(widget.user.avatar ?? ''),
                          ),
                        ),
                        SizedBox(width: 15),
                        Text(
                          'Profile',
                          style: TextStyle(
                            color: Colors.blue,
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                  ),
                  SizedBox(width: 15)
                ],
              ),
            ),
          ),
        ],
        title: Container(
          height: 65,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text(
                    "Welcome Back ",
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Colors.blue,
                    ),
                  ),
                  Text(
                    widget.user.username,
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Colors.blue,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 10),
              const Text(
                "Let's find your next job",
                style: TextStyle(
                  fontSize: 16,
                  fontStyle: FontStyle.italic,
                  color: Colors.grey,
                ),
              ),
            ],
          ),
        ),
      ),
      body: Column(
        children: [
          Expanded(
            flex: 1,
            child: Container(
              padding: const EdgeInsets.all(0.8),
              margin: const EdgeInsets.all(0.8),
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: categories.map((category) {
                    return FilterChip(
                      selected: selectedCategories.contains(category),
                      label: Text(category),
                      onSelected: (selected) {
                        setState(() {
                          if (category == 'Favorite') {
                            if (selected) {
                              selectedCategories.add(category);
                            } else {
                              selectedCategories.remove(category);
                            }
                          } else {
                            if (selected) {
                              selectedCategories.remove('Favorite');
                              selectedCategories.add(category);
                            } else {
                              selectedCategories.remove(category);
                              if (selectedCategories.isEmpty) {
                                selectedCategories.add('Favorite');
                              }
                            }
                          }
                        });
                      },
                    );
                  }).toList(),
                ),
              ),
            ),
          ),
          Expanded(
            flex: 10,
            child: ListView.builder(
              padding: EdgeInsets.all(10),
              itemCount: filteredJobs.length,
              itemBuilder: (context, index) {
                final job = filteredJobs[index];
                return jobComponent(context: context, job: job, index: index);
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget jobComponent(
      {required BuildContext context, required Job job, required int index}) {
    final tag = '${job.title}_$index';
    return Hero(
      tag: tag,
      child: GestureDetector(
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => JobDetailScreen(job: job),
            ),
          );
        },
        child: Container(
          padding: EdgeInsets.all(10),
          margin: EdgeInsets.only(bottom: 15),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(20),
            color: Colors.white,
            boxShadow: [
              BoxShadow(
                color: Colors.grey.withOpacity(0.2),
                spreadRadius: 0,
                blurRadius: 2,
                offset: Offset(0, 1),
              ),
            ],
          ),
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Row(
                      children: [
                        Container(
                          width: 60,
                          height: 60,
                          child: ClipRRect(
                            borderRadius: BorderRadius.circular(20),
                            child: Image.asset(
                                job.companyLogo ?? 'assets/default_logo.png'),
                          ),
                        ),
                        SizedBox(width: 10),
                        Flexible(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                job.title ?? 'Job Title',
                                style: TextStyle(
                                  color: Colors.black,
                                  fontSize: 15,
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
                              SizedBox(height: 5),
                              Text(
                                job.address ?? 'Job Address',
                                style: TextStyle(color: Colors.grey[500]),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                  GestureDetector(
                    onTap: () {
                      setState(() {
                        job.isMyFav = !job.isMyFav;
                      });
                    },
                    child: AnimatedContainer(
                      height: 35,
                      padding: EdgeInsets.all(5),
                      duration: Duration(milliseconds: 300),
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(
                          color: job.isMyFav
                              ? Colors.red.shade100
                              : Colors.grey.shade300,
                        ),
                      ),
                      child: Center(
                        child: job.isMyFav
                            ? Icon(
                                Icons.favorite,
                                color: Colors.red,
                              )
                            : Icon(
                                Icons.favorite_outline,
                                color: Colors.grey.shade600,
                              ),
                      ),
                    ),
                  ),
                ],
              ),
              SizedBox(height: 20),
              Container(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Container(
                          padding:
                              EdgeInsets.symmetric(vertical: 8, horizontal: 15),
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(12),
                            color: Colors.grey.shade200,
                          ),
                          child: Text(
                            job.type ?? 'Job Type',
                            style: TextStyle(color: Colors.black),
                          ),
                        ),
                        SizedBox(width: 10),
                        Container(
                          padding:
                              EdgeInsets.symmetric(vertical: 8, horizontal: 15),
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(12),
                            color: Color(int.parse(
                                    "0xff${job.experienceLevelColor ?? '000000'}"))
                                .withAlpha(20),
                          ),
                          child: Text(
                            job.experienceLevel ?? 'Experience Level',
                            style: TextStyle(
                              color: Color(int.parse(
                                  "0xff${job.experienceLevelColor ?? '000000'}")),
                            ),
                          ),
                        ),
                      ],
                    ),
                    Text(
                      job.timeAgo ?? 'Time ago',
                      style: TextStyle(
                        color: Colors.grey.shade800,
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
