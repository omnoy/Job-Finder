import 'package:flutter/material.dart';

class SelectionTags extends StatefulWidget {
  final List<String> selectedCategories;
  final Function(List<String>) onSelectionChanged;

  SelectionTags({
    required this.selectedCategories,
    required this.onSelectionChanged,
  });

  @override
  _SelectionTagsState createState() => _SelectionTagsState();
}

class _SelectionTagsState extends State<SelectionTags> {
  List<String> titles = [
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
  List<String> places = ['צפון', 'מרכז', 'דרום', 'השרון', 'ירושלים', 'השפלה'];
  List<String> education = [
    'High School',
    'Bachelor\'s Degree',
    'Master\'s Degree',
    'Doctorate Degree',
    'student'
  ];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance?.addPostFrameCallback((_) {
      widget.onSelectionChanged(widget.selectedCategories);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('selection tags'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 8.0),
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Titles :   ',
                  style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Colors.cyan),
                ),
                const SizedBox(width: 8.0),
                Expanded(
                  child: SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Wrap(
                      spacing: 8.0,
                      children: titles.map((title) {
                        return FilterChip(
                          label: Text(
                            title,
                            style: TextStyle(fontSize: 14),
                          ),
                          selected: widget.selectedCategories.contains(title),
                          onSelected: (selected) {
                            setState(() {
                              if (selected) {
                                widget.selectedCategories.add(title);
                              } else {
                                widget.selectedCategories.remove(title);
                              }
                            });
                            WidgetsBinding.instance?.addPostFrameCallback((_) {
                              widget.onSelectionChanged(
                                  widget.selectedCategories);
                            });
                          },
                          selectedColor: Colors.cyan.withOpacity(0.5),
                        );
                      }).toList(),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8.0),
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Places :   ',
                  style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Colors.cyan),
                ),
                const SizedBox(width: 8.0),
                Expanded(
                  child: SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Wrap(
                      spacing: 8.0,
                      children: places.map((place) {
                        return FilterChip(
                          label: Text(
                            place,
                            style: TextStyle(fontSize: 14),
                          ),
                          selected: widget.selectedCategories.contains(place),
                          onSelected: (selected) {
                            setState(() {
                              if (selected) {
                                widget.selectedCategories.add(place);
                              } else {
                                widget.selectedCategories.remove(place);
                              }
                            });
                            WidgetsBinding.instance?.addPostFrameCallback((_) {
                              widget.onSelectionChanged(
                                  widget.selectedCategories);
                            });
                          },
                          selectedColor: Colors.cyan.withOpacity(0.5),
                        );
                      }).toList(),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8.0),
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Education :   ',
                  style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Colors.cyan),
                ),
                const SizedBox(width: 8.0),
                Expanded(
                  child: SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Wrap(
                      spacing: 8.0,
                      children: education.map((edu) {
                        return FilterChip(
                          label: Text(
                            edu,
                            style: TextStyle(fontSize: 14),
                          ),
                          selected: widget.selectedCategories.contains(edu),
                          onSelected: (selected) {
                            setState(() {
                              if (selected) {
                                widget.selectedCategories.add(edu);
                              } else {
                                widget.selectedCategories.remove(edu);
                              }
                            });
                            WidgetsBinding.instance?.addPostFrameCallback((_) {
                              widget.onSelectionChanged(
                                  widget.selectedCategories);
                            });
                          },
                          selectedColor: Colors.cyan.withOpacity(0.5),
                        );
                      }).toList(),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16.0),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context, widget.selectedCategories);
              },
              child: Text('Submit'),
            ),
          ],
        ),
      ),
    );
  }
}
