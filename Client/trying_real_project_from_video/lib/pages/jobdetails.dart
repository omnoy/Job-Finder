import 'package:flutter/material.dart';

import '../entity_models/jobModel.dart';

class JobDetailScreen extends StatelessWidget {
  final Job job;

  const JobDetailScreen({Key? key, required this.job}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(job.title ?? 'Job Title'),
      ),
      body: SingleChildScrollView(
        padding: EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            /*

            Text(
              'Job Description',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            SizedBox(height: 16.0),
            Text(
              job.jobDescription ?? 'No job description available.',
              style: TextStyle(
                fontSize: 16,
              ),
            ),
            SizedBox(height: 24.0),
            Text(
              'Requirements',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            SizedBox(height: 16.0),
            Text(
              job.requirements ?? 'No requirements available.',
              style: TextStyle(
                fontSize: 16,
              ),
            ),
            SizedBox(height: 24.0),
             */
            Text(
              'Job Details',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            SizedBox(height: 16.0),
            ListTile(
              leading: CircleAvatar(
                backgroundColor:
                    Colors.transparent, // Set background color to transparent
                backgroundImage:
                    AssetImage(job.companyLogo ?? 'assets/default_logo.png'),
              ),
              title: Text(job.title ?? 'Job Title'),
              subtitle: Text(job.address ?? 'Job Address'),
            ),
            SizedBox(height: 8.0),
            ListTile(
              leading: Icon(Icons.access_time),
              title: Text(job.timeAgo ?? 'Time ago'),
            ),
            SizedBox(height: 8.0),
            ListTile(
              leading: Icon(Icons.business),
              title: Text(job.type ?? 'Job Type'),
            ),
            SizedBox(height: 8.0),
            ListTile(
              leading: Icon(Icons.star),
              title: ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: Container(
                  padding: EdgeInsets.symmetric(vertical: 8, horizontal: 15),
                  decoration: BoxDecoration(
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
              ),
            ),
          ],
        ),
      ),
    );
  }
}
