## Resume Finder

* [Implementation Overview](#implementation-overview)
* [Parsing Engine](#parsing-engine)
  + [Text Extraction](#text-extraction)
  + [Reconstruct Resume Sections](#reconstruct-resume-sections)
  + [Extract Skill Information](#extract-skill-information)
* [Scoring or Indexing Engine](#scoring-or-indexing-engine)
* [Search Engine](#search-engine)
* [Search UI](#search-ui)
  + [Overview of the code](#overview-of-the-code)
  + [Software Implementation](#software-implementation)
  + [Important Notes for Usage](#important-notes-for-usage)

### Implementation Overview

The application has the following modules:

1. Parsing Engine
2. Scoring Engine
3. Search Engine
4. Search UI

### Parsing Engine
The Parsing Engine is responsible for parsing Resumes in doc/docx and pdf format. 

#### Text Extraction
The first task is to extract text from the resume document that could be in doc/docx or pdf format. The following steps help in this task:

* The doc/docx files are parsed using the [`Apache POI Utils`](https://poi.apache.org/). The doc/docx file is parsed to a [`XWPFDocument`](https://poi.apache.org/apidocs/4.1/org/apache/poi/xwpf/usermodel/XWPFDocument.html), the paragraphs are extracted as [`XWPFParagraph`](https://poi.apache.org/apidocs/4.1/org/apache/poi/xwpf/usermodel/XWPFParagraph.html) objects. This object provides APIs to get the text in the paragraphs.

* The PDF files are parsed using the [`Apache PDFBox v2.0`](https://pdfbox.apache.org/2.0/getting-started.html) library. The pdf file is parsed to a [`PDDocument`](https://pdfbox.apache.org/docs/2.0.13/javadocs/org/apache/pdfbox/pdmodel/PDDocument.html). This document object is passed to a [`PDFTextStripper`](https://pdfbox.apache.org/docs/2.0.13/javadocs/org/apache/pdfbox/text/PDFTextStripper.html) to extract the raw text in the PDF file.

#### Reconstruct Resume Sections

The paragraphs are run through regular expressions to reconstruct the sections in the resume.

#### Extract Skill Information

The [`Stanford CoreNLP`](https://stanfordnlp.github.io/CoreNLP/#coredocument) module is used to annotate, tokenize each section in the resume, to extract skills and the duration these skills have been used. 

The duration of using a skill is aggregated across multiple (Skill, Duration) records obtained from different sections in the resume.

E.g. If the candidate worked at two companies, used Java at both the places for 2years and 1year respectively, the output of this step is a single record (Java, 3years).

This information is written to a JSON file that is used by Scoring Engine. The format of JSON file is as follows:

```JSON
{
  "location" : "<Location of the resume on the disk>",
  "skills" : [ {
    "skill" : "<skill1>",
    "duration" : <duration in months>
  }, {
    "skill" : "<skill2>",
    "duration" : <duration in months>
  } ]
}
```

A JSON file is created for each resume processed by the parsing engine. All the JSON files are written to output location specified while running the parsing engine.

The Scoring engine takes over from here.

### Scoring or Indexing Engine

Apache Lucene library has been used to do the indexing and searching the resume according to the provided user query. All the json files which got generated from parsing engine will get indexed. Document indexing consists of first constructing a document that contains the fields to be indexed or stored, then adding that document to the index.

Document will be created with below fields:



**Field**				**Value**

**Path** : Path of the json file.

**Filename** :	Json filename.

**Location** :	 		Location field from json file.

**skillname+_FIELD** : 	Field name will get generated dynamically with skill and duration as value.

**allSkills**	: 		String contains all the skills.

Once the document get created,this will be added to index.

### Search Engine

The search function takes a search query and returns a set of documents ranked by relevancy with documents most similar to the query having the highest score.

User can provide multiple skill, minimum no of experience and boolean flag for mandatory skill in the form of list to search function.

Search function will build the query using BooleanQuery. BoostQuery has been used for mandatory skills so that more weightage should be given to the resume which has mandatory skills. RangeQuery has been used so that resume which qualify for minimum experience will be considered for search. This query will run against the index document and it will return the location of resume in descending order of their score.

### Search UI

#### Overview of the code


The GUI for the Resume Finder makes it easy to provide input for the needed skills and view output for the results (in the form of a list containing names of the Resume files containing the specified skills).

The GUI contains the following components/controls:

1. Index folder (Text Field): This is used to input the folder name where the search index files are present. It gives flexibility to run the same UI for multiple searches by looking only in the specified folder.
2. Skill name (Text Fields): These are used to input the skill names. Please note that the search is case insensitive. If the skill name is omitted, the other fields for minimum experience and Mandatory checkbox are not considered. An input containing only of blank spaces is consdiered equivalent to leaving the skill field blank.
3. Min Experience (Spinner): These fields are used to input the minimum experience needed in years. The min experience is a year and you can increment or decrement the experience in single year increments. Please note that this field is only considered when skill field is filled in.
4. Mandatory (Checkbox): These fields are used to input if the skills is mandatory - thus affecting the search. Please note that this field is only considered when skill field is filled in.


#### Software Implementation

The GUI is built using Javafx and the architecture is based on the following code components:

1. The java code is under the package: edu.illinois.phantom.ui and the FXML file is under the resources folder edu.illinois.phantom.ui.
2. Resume_Finder.fxml file: This contains the visual definition of the various GUI controls, as well as their IDs which can then be referrred in the controller code. The primary advantage of the fxml file is that it makes the visual components declarative rather than creating them in the code. The format is a XML file enforcing some of the structure using parent/child tags. The UI consists of a simple decorated window (with minimize/maximize/close buttons) with no Menu bar.
3. Main.java: Utility class for recalling ResumeFinderApplication - this is needed to make the build jar working due to some JavaFX related nuances.
4. ResumeFinderApplication.java: This contains the starting code for the UI app. It reads from the FXML file and initiates the UI.
5. ResumeFinderController.java: This contains the primary logic for the UI interaction. The code:
	a) Correlates the UI control java objects with their FXML ids (using @FXML tags)
	b) Sets up the SpinnerValuefactory so that the spinner controls have only integer values and a min and max value.
	c) Associates an event handler with the Button to find resumes matching the skills
	
6. The logic in the event handler consists of:
	a) Getting the various values from the controls (skill name, experience etc)
	b) Getting the index folder name from the textfield
	c) Converting the experience spinner values from years to months (multiplied by 12)
	d) Checking if the skill name is populated for each skill textfield and only then considering the remaining values from the controls
	e) Creating a list of UserQuery objects - each containing the skill details
	f) Performing a search by creating a edu.illinois.phantom.searchengine.SearchEngine object and passing on the list in the above step
	g) Getting the results (locations of matching resumes) and using substring to discard the folder path and only taking the Resume file name
	h) Clearing the Listbox for the results in the UI and adding the results (Resume names) to the Listbox


#### Important Notes for Usage

Due to the nature of the UI libraries across multiple OS, we have created two OS specific builds - one for Mac OS, and one for Windows OS. Please use the platform specific jar to run the application. Alternatively, you can build the specific build/jar file for your OS using "gradle build".
