import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_html/flutter_html.dart';
import 'package:http/http.dart' as http;
import 'package:trying_real_project_from_video/globals/global_fetches.dart';

import '../entity_models/userModel.dart';
import '../globals/global_methods.dart';
import 'form_data.dart';

class CvMaker extends StatefulWidget {
  const CvMaker({super.key, required this.user});
  final checkUser user;
  @override
  CvMakerState createState() => CvMakerState();
}

class CvMakerState extends State<CvMaker> {
  final _formKey = GlobalKey<FormState>();
  final _fullNameController = TextEditingController();
  final _phoneNumberController = TextEditingController();
  final _emailAddressController = TextEditingController();
  final _locationController = TextEditingController();
  final _objectiveController = TextEditingController();
  final _summaryController = TextEditingController();
  final List<Map<String, String>> _educationList = [];
  final List<Map<String, String>> _workExperienceList = [];
  final List<Map<String, String>> _skillsList = [];
  final List<Map<String, String>> _projectsList = [];
  final List<Map<String, String>> _certificationsList = [];
  final List<Map<String, String>> _awardsList = [];
  final List<Map<String, String>> _publicationsList = [];
  final List<Map<String, String>> _referencesList = [];
  bool _isLoading = false;
  bool _isShow = false;
  //final bool _obscureText = true;
  final DataFetcher fetcherForm = DataFetcher();
  late String htmlStr;

  @override
  void dispose() {
    _fullNameController.dispose();
    _phoneNumberController.dispose();
    _emailAddressController.dispose();
    _locationController.dispose();
    _objectiveController.dispose();
    _summaryController.dispose();
    super.dispose();
  }

  void _addEducation() {
    setState(() {
      _educationList.add({
        'degree': '',
        'institution': '',
        'year': '',
      });
    });
  }

  void _removeEducation(int index) {
    setState(() {
      _educationList.removeAt(index);
    });
  }

  void _addWorkExperience() {
    setState(() {
      _workExperienceList.add({
        'companyName': '',
        'role': '',
        'startDate': '',
        'endDate': '',
      });
    });
  }

  void _removeWorkExperience(int index) {
    setState(() {
      _workExperienceList.removeAt(index);
    });
  }

  void _addSkill() {
    setState(() {
      _skillsList.add({
        'name': '',
        'proficiency': '',
      });
    });
  }

  void _removeSkill(int index) {
    setState(() {
      _skillsList.removeAt(index);
    });
  }

  void _addProject() {
    setState(() {
      _projectsList.add({
        'name': '',
        'description': '',
        'url': '',
      });
    });
  }

  void _removeProject(int index) {
    setState(() {
      _projectsList.removeAt(index);
    });
  }

  void _addCertification() {
    setState(() {
      _certificationsList.add({
        'name': '',
        'organization': '',
        'year': '',
      });
    });
  }

  void _removeCertification(int index) {
    setState(() {
      _certificationsList.removeAt(index);
    });
  }

  void _addAward() {
    setState(() {
      _awardsList.add({
        'name': '',
        'issuer': '',
        'year': '',
      });
    });
  }

  void _removeAward(int index) {
    setState(() {
      _awardsList.removeAt(index);
    });
  }

  void _addPublication() {
    setState(() {
      _publicationsList.add({
        'title': '',
        'authors': '',
        'year': '',
      });
    });
  }

  void _removePublication(int index) {
    setState(() {
      _publicationsList.removeAt(index);
    });
  }

  void _addReference() {
    setState(() {
      _referencesList.add({
        'name': '',
        'designation': '',
        'company': '',
        'phoneNumber': '',
        'emailAddress': '',
      });
    });
  }

  void _removeReference(int index) {
    setState(() {
      _referencesList.removeAt(index);
    });
  }

  void _submitForm() async {
    if (_formKey.currentState!.validate()) {
      final formData = FormData(
        fullName: _fullNameController,
        phoneNumber: _phoneNumberController,
        emailAddress: _emailAddressController,
        location: _locationController,
        objective: _objectiveController,
        summary: _summaryController,
        educations: _educationList,
        workExperiences: _workExperienceList,
        skills: _skillsList,
        projects: _projectsList,
        certifications: _certificationsList,
        awards: _awardsList,
        publications: _publicationsList,
        references: _referencesList,
      );
      setState(() {
        _isLoading = true;
        _isShow = false;
      });
      try {
        htmlStr = await fetcherForm.sendFormCV(formData, widget.user);
        setState(() {
          _isShow = true;
          _isLoading = false;
        });
      } catch (error) {
        setState(() {
          _isLoading = false;
          _isShow = false;
        });
        GlobalErrorDialogMethod.showErrorDialog(
            error: error.toString(), ctx: context);
        print('Error occurred: $error');
      }

      // Perform form submission logic here
      // Example: Save the form data to a database or send it to a server
      // After the submission is complete, you can reset the form
      _formKey.currentState!.reset();
      _educationList.clear();
      _workExperienceList.clear();
      _skillsList.clear();
      _projectsList.clear();
      _awardsList.clear();
      _certificationsList.clear();
      _publicationsList.clear();
      _referencesList.clear();
    }
  }

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;

    return Scaffold(
      body: Container(
        width: size.width,
        height: size.height,
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 40),
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Colors.blue, Colors.indigo],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
        ),
        child: SingleChildScrollView(
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Lets make your cv below',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 28,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 30),
                TextFormField(
                  keyboardType: TextInputType.text,
                  controller: _fullNameController,
                  validator: (val) {
                    if (val!.isEmpty) {
                      return 'This field is required';
                    }
                    return null;
                  },
                  style: const TextStyle(color: Colors.white),
                  decoration: const InputDecoration(
                    hintText: 'Full Name',
                    hintStyle: TextStyle(color: Colors.white),
                    enabledBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    focusedBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    errorBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.red),
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                TextFormField(
                  keyboardType: TextInputType.number,
                  controller: _phoneNumberController,
                  validator: (val) {
                    if (val!.isEmpty) {
                      return 'This field is required';
                    }
                    return null;
                  },
                  style: const TextStyle(color: Colors.white),
                  decoration: const InputDecoration(
                    hintText: 'Phone Number',
                    hintStyle: TextStyle(color: Colors.white),
                    enabledBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    focusedBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    errorBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.red),
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                TextFormField(
                  keyboardType: TextInputType.emailAddress,
                  controller: _emailAddressController,
                  validator: (val) {
                    if (val!.isEmpty) {
                      return 'This field is required';
                    }
                    return null;
                  },
                  style: const TextStyle(color: Colors.white),
                  decoration: const InputDecoration(
                    hintText: 'Email Address',
                    hintStyle: TextStyle(color: Colors.white),
                    enabledBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    focusedBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    errorBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.red),
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                TextFormField(
                  keyboardType: TextInputType.text,
                  controller: _locationController,
                  validator: (val) {
                    if (val!.isEmpty) {
                      return 'This field is required';
                    }
                    return null;
                  },
                  style: const TextStyle(color: Colors.white),
                  decoration: const InputDecoration(
                    hintText: 'Location',
                    hintStyle: TextStyle(color: Colors.white),
                    enabledBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    focusedBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    errorBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.red),
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                TextFormField(
                  keyboardType: TextInputType.text,
                  controller: _objectiveController,
                  validator: (val) {
                    if (val!.isEmpty) {
                      return 'This field is required';
                    }
                    return null;
                  },
                  style: const TextStyle(color: Colors.white),
                  decoration: const InputDecoration(
                    hintText: 'Objective',
                    hintStyle: TextStyle(color: Colors.white),
                    enabledBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    focusedBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    errorBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.red),
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                TextFormField(
                  keyboardType: TextInputType.text,
                  controller: _summaryController,
                  validator: (val) {
                    if (val!.isEmpty) {
                      return 'This field is required';
                    }
                    return null;
                  },
                  style: const TextStyle(color: Colors.white),
                  decoration: const InputDecoration(
                    hintText: 'Summary',
                    hintStyle: TextStyle(color: Colors.white),
                    enabledBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    focusedBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.white),
                    ),
                    errorBorder: UnderlineInputBorder(
                      borderSide: BorderSide(color: Colors.red),
                    ),
                  ),
                ),
                const SizedBox(height: 30),

                ///Name
                const Text(
                  'Education',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 10),

                /// the options that open to fill
                ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _educationList.length,
                  itemBuilder: (ctx, index) {
                    return EducationForm(
                      education: _educationList[index],
                      onChanged: (newEducation) {
                        setState(() {
                          _educationList[index] = newEducation;
                        });
                      },
                      onRemove: () {
                        _removeEducation(index);
                      },
                    );
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: _addEducation,
                  style:
                      ElevatedButton.styleFrom(backgroundColor: Colors.green),
                  child: const Text(
                    'Add Education',
                  ),
                ),
                const SizedBox(height: 30),

                ///newthing
                ///Name
                const Text(
                  'Work Experience',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 10),

                /// the options that open to fill
                ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _workExperienceList.length,
                  itemBuilder: (ctx, index) {
                    return WorkExperienceForm(
                      workExperience: _workExperienceList[index],
                      onChanged: (newWorkExperience) {
                        setState(() {
                          _workExperienceList[index] = newWorkExperience;
                        });
                      },
                      onRemove: () {
                        //here
                        _removeWorkExperience(index);
                      },
                    );
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  //here
                  onPressed: _addWorkExperience,
                  style:
                      ElevatedButton.styleFrom(backgroundColor: Colors.green),
                  child: const Text('Add Work Experience'),
                ),
                const SizedBox(height: 30),

                ///end new thing
                ///newthing
                ///Name
                const Text(
                  'skills',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),

                /// the options that open to fill
                ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _skillsList.length,
                  itemBuilder: (ctx, index) {
                    return SkillsForm(
                      skills: _skillsList[index],
                      onChanged: (newskills) {
                        setState(() {
                          _skillsList[index] = newskills;
                        });
                      },
                      onRemove: () {
                        //here
                        _removeSkill(index);
                      },
                    );
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  //here
                  onPressed: _addSkill,
                  style:
                      ElevatedButton.styleFrom(backgroundColor: Colors.green),
                  child: const Text('Add skill'),
                ),
                const SizedBox(height: 30),

                ///end new thing

                ///newthing
                ///Name
                const Text(
                  'Projects',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),

                /// the options that open to fill
                ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _projectsList.length,
                  itemBuilder: (ctx, index) {
                    return ProjectForm(
                      project: _projectsList[index],
                      onChanged: (projects) {
                        setState(() {
                          _projectsList[index] = projects;
                        });
                      },
                      onRemove: () {
                        //here
                        _removeProject(index);
                      },
                    );
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  //here
                  onPressed: _addProject,
                  style:
                      ElevatedButton.styleFrom(backgroundColor: Colors.green),
                  child: const Text('Add Project'),
                ),
                const SizedBox(height: 30),

                ///end new thing

                ///newthing
                ///Name
                const Text(
                  'Certification',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),

                /// the options that open to fill
                ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _certificationsList.length,
                  itemBuilder: (ctx, index) {
                    return CertificationsForm(
                      certification: _certificationsList[index],
                      onChanged: (certifications) {
                        setState(() {
                          _certificationsList[index] = certifications;
                        });
                      },
                      onRemove: () {
                        //here
                        _removeCertification(index);
                      },
                    );
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  //here
                  onPressed: _addCertification,
                  style:
                      ElevatedButton.styleFrom(backgroundColor: Colors.green),
                  child: const Text('Add Certifications'),
                ),
                const SizedBox(height: 30),

                ///end new thing

                ///newthing
                ///Name
                const Text(
                  'Award',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),

                /// the options that open to fill
                ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _awardsList.length,
                  itemBuilder: (ctx, index) {
                    return AwardsForm(
                      award: _awardsList[index],
                      onChanged: (award) {
                        setState(() {
                          _awardsList[index] = award;
                        });
                      },
                      onRemove: () {
                        //here
                        _removeAward(index);
                      },
                    );
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  //here
                  onPressed: _addAward,
                  style:
                      ElevatedButton.styleFrom(backgroundColor: Colors.green),
                  child: const Text('Add award'),
                ),
                const SizedBox(height: 30),

                ///end new thing

                ///newthing
                ///Name
                const Text(
                  'publication',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),

                /// the options that open to fill
                ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _publicationsList.length,
                  itemBuilder: (ctx, index) {
                    return PublicationForm(
                      publication: _publicationsList[index],
                      onChanged: (publication) {
                        setState(() {
                          _publicationsList[index] = publication;
                        });
                      },
                      onRemove: () {
                        //here
                        _removePublication(index);
                      },
                    );
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  //here
                  onPressed: _addPublication,
                  style:
                      ElevatedButton.styleFrom(backgroundColor: Colors.green),
                  child: const Text('Add publication'),
                ),
                const SizedBox(height: 30),

                ///end new thing

                ///newthing
                ///Name
                const Text(
                  'reference',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),

                /// the options that open to fill
                ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _referencesList.length,
                  itemBuilder: (ctx, index) {
                    return ReferenceForm(
                      reference: _referencesList[index],
                      onChanged: (reference) {
                        setState(() {
                          _referencesList[index] = reference;
                        });
                      },
                      onRemove: () {
                        //here
                        _removeReference(index);
                      },
                    );
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  //here
                  onPressed: _addReference,
                  style:
                      ElevatedButton.styleFrom(backgroundColor: Colors.green),
                  child: const Text('Add reference'),
                ),
                const SizedBox(height: 30),

                ///end new thing
                _isLoading
                    ? const Center(child: CircularProgressIndicator())
                    : ElevatedButton(
                        onPressed: _submitForm,
                        child: const Text('Submit'),
                      ),
                const SizedBox(
                  height: 20,
                ),
                _isShow
                    ? const Center(
                        child: Text(
                        "enjoy your resume ",
                        style: TextStyle(
                            fontSize: 14, fontWeight: FontWeight.bold),
                      ))
                    : const Text(""),
                _isShow
                    ? Container(
                        height: 800,
                        child: ListView(
                          children: [
                            Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: Html(data: htmlStr),
                            ),
                          ],
                        ),
                      )
                    : Text(''),
                const SizedBox(
                  height: 8,
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class EducationForm extends StatelessWidget {
  final Map<String, String> education;
  final ValueChanged<Map<String, String>> onChanged;
  final VoidCallback onRemove;

  const EducationForm({
    super.key,
    required this.education,
    required this.onChanged,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: education['degree'],
          onChanged: (val) => onChanged({...education, 'degree': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'Degree',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: education['institution'],
          onChanged: (val) => onChanged({...education, 'institution': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'Institution',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.number,
          initialValue: education['year'],
          onChanged: (val) => onChanged({...education, 'year': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'Year',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        ElevatedButton(
          onPressed: onRemove,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          child: const Text('Remove'),
        ),
        const SizedBox(height: 20),
      ],
    );
  }
}

//here
class WorkExperienceForm extends StatelessWidget {
  //here
  final Map<String, String> workExperience;
  final ValueChanged<Map<String, String>> onChanged;
  final VoidCallback onRemove;

  const WorkExperienceForm({
    super.key,
    //here
    required this.workExperience,
    required this.onChanged,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: workExperience['companyName'],
          onChanged: (val) =>
              onChanged({...workExperience, 'companyName': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'companyName',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: workExperience['startDate'],
          onChanged: (val) => onChanged({...workExperience, 'startDate': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'startDate',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.number,
          initialValue: workExperience['endDate'],
          onChanged: (val) => onChanged({...workExperience, 'endDate': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'endDate',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        ElevatedButton(
          onPressed: onRemove,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          child: const Text('Remove'),
        ),
        const SizedBox(height: 20),
      ],
    );
  }
}

//here
class SkillsForm extends StatelessWidget {
  //here
  final Map<String, String> skills;
  final ValueChanged<Map<String, String>> onChanged;
  final VoidCallback onRemove;

  const SkillsForm({
    super.key,
    //here
    required this.skills,
    required this.onChanged,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: skills['name'],
          onChanged: (val) => onChanged({...skills, 'name': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'skill name',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: skills['proficiency'],
          onChanged: (val) => onChanged({...skills, 'proficiency': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'proficiency',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        ElevatedButton(
          onPressed: onRemove,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          child: const Text('Remove'),
        ),
        const SizedBox(height: 20),
      ],
    );
  }
}

//here
class ProjectForm extends StatelessWidget {
  //here
  final Map<String, String> project;
  final ValueChanged<Map<String, String>> onChanged;
  final VoidCallback onRemove;

  const ProjectForm({
    super.key,
    //here
    required this.project,
    required this.onChanged,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: project['name'],
          onChanged: (val) => onChanged({...project, 'name': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'project name',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: project['description'],
          onChanged: (val) => onChanged({...project, 'description': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'description',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: project['url'],
          onChanged: (val) => onChanged({...project, 'url': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'url',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        ElevatedButton(
          onPressed: onRemove,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          child: const Text('Remove'),
        ),
        const SizedBox(height: 20),
      ],
    );
  }
}

//here
class CertificationsForm extends StatelessWidget {
  //here
  final Map<String, String> certification;
  final ValueChanged<Map<String, String>> onChanged;
  final VoidCallback onRemove;

  const CertificationsForm({
    super.key,
    //here
    required this.certification,
    required this.onChanged,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: certification['name'],
          onChanged: (val) => onChanged({...certification, 'name': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'certification name',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: certification['organization'],
          onChanged: (val) =>
              onChanged({...certification, 'organization': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'organization',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: certification['year'],
          onChanged: (val) => onChanged({...certification, 'year': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'year',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        ElevatedButton(
          onPressed: onRemove,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          child: const Text('Remove'),
        ),
        const SizedBox(height: 20),
      ],
    );
  }
}

//here
class AwardsForm extends StatelessWidget {
  //here
  final Map<String, String> award;
  final ValueChanged<Map<String, String>> onChanged;
  final VoidCallback onRemove;

  const AwardsForm({
    super.key,
    //here
    required this.award,
    required this.onChanged,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: award['award name'],
          onChanged: (val) => onChanged({...award, 'award name': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'award name',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: award['issuer'],
          onChanged: (val) => onChanged({...award, 'issuer': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'issuer',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: award['year'],
          onChanged: (val) => onChanged({...award, 'year': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'year',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        ElevatedButton(
          onPressed: onRemove,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          child: const Text('Remove'),
        ),
        const SizedBox(height: 20),
      ],
    );
  }
}

//here
class PublicationForm extends StatelessWidget {
  //here
  final Map<String, String> publication;
  final ValueChanged<Map<String, String>> onChanged;
  final VoidCallback onRemove;

  const PublicationForm({
    super.key,
    //here
    required this.publication,
    required this.onChanged,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: publication['title'],
          onChanged: (val) => onChanged({...publication, 'title': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'title',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: publication['authors'],
          onChanged: (val) => onChanged({...publication, 'authors': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'authors',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: publication['year'],
          onChanged: (val) => onChanged({...publication, 'year': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'year',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        ElevatedButton(
          onPressed: onRemove,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          child: const Text('Remove'),
        ),
        const SizedBox(height: 20),
      ],
    );
  }
}

//here
class ReferenceForm extends StatelessWidget {
  //here
  final Map<String, String> reference;
  final ValueChanged<Map<String, String>> onChanged;
  final VoidCallback onRemove;

  const ReferenceForm({
    super.key,
    //here
    required this.reference,
    required this.onChanged,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: reference['name'],
          onChanged: (val) => onChanged({...reference, 'name': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'name',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: reference['designation'],
          onChanged: (val) => onChanged({...reference, 'designation': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'designation',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: reference['company'],
          onChanged: (val) => onChanged({...reference, 'company': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'company',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: reference['phoneNumber'],
          onChanged: (val) => onChanged({...reference, 'phoneNumber': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'phoneNumber',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        TextFormField(
          keyboardType: TextInputType.text,
          initialValue: reference['email address'],
          onChanged: (val) => onChanged({...reference, 'email address': val}),
          style: const TextStyle(color: Colors.white),
          decoration: const InputDecoration(
            hintText: 'email address',
            hintStyle: TextStyle(color: Colors.white),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            focusedBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.white),
            ),
            errorBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.red),
            ),
          ),
        ),
        const SizedBox(height: 10),
        ElevatedButton(
          onPressed: onRemove,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          child: const Text('Remove'),
        ),
        const SizedBox(height: 20),
      ],
    );
  }
}
