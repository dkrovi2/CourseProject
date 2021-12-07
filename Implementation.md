## Resume Finder

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

### Scoring Engine



### Search Engine


### Search UI

