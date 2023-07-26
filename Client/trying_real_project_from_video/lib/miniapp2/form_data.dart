import 'package:flutter/material.dart';

class FormData {
  final TextEditingController fullName;
  final TextEditingController phoneNumber;
  final TextEditingController emailAddress;
  final TextEditingController location;
  final TextEditingController objective;
  final TextEditingController summary;
  final List<Map<String, String>>? educations;
  final List<Map<String, String>>? workExperiences;
  final List<Map<String, String>>? skills;
  final List<Map<String, String>>? projects;
  final List<Map<String, String>>? certifications;
  final List<Map<String, String>>? awards;
  final List<Map<String, String>>? publications;
  final List<Map<String, String>>? references;

  FormData({
    required this.fullName,
    required this.phoneNumber,
    required this.emailAddress,
    required this.location,
    required this.objective,
    required this.summary,
    this.educations,
    this.workExperiences,
    this.skills,
    this.projects,
    this.certifications,
    this.awards,
    this.publications,
    this.references,
  });
  Map<String, dynamic> toJson() {
    return {
      "fullName": fullName.text,
      "phoneNumber": phoneNumber.text,
      "emailAddress": emailAddress.text,
      "location": location.text,
      "objective": objective.text,
      "summary": summary.text,
      "educationList": educations,
      "workExperienceList": workExperiences,
      "skillsList": skills,
      "projectsList": projects,
      "certificationsList": certifications,
      "awardsList": awards,
      "publicationsList": publications,
      "referencesList": references,
    };
  }

  @override
  String toString() {
    return 'FormData{fullName: $fullName, phoneNumber: $phoneNumber, emailAddress: $emailAddress, location: $location, objective: $objective, summary: $summary, educations: $educations, workExperiences: $workExperiences, skills: $skills, projects: $projects, certifications: $certifications, awards: $awards, publications: $publications, references: $references}';
  }
}
