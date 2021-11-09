# Build Experience Profile from Resumes


1.	What are the names and NetIDs of all your team members? Who is the captain? The captain will have more administrative duties than team members.
 * alokk3@illinois.edu
 * dkrovi2@illinois.edu
 * jsaxena3@illinois.edu
 * rathi9@illinois.edu

2. What is your free topic? Please give a detailed description. What is the task? Why is it important or interesting? What is your planned approach? What tools, systems or datasets are involved? What is the expected outcome? How are you going to evaluate your work?

* In this project, we use text extraction and retireval for the following functions:
* Get useful information from a candidate’s (prospective employee) resume 
* Build an experience profile connection candidate’s experience in various tools and technologies, for each of the resumes 
* Rank the available set of resumes based on the skill set, using the keywords provided in the query

    The current keyword based search used by many online websites might not be entirely accurate, as the coorelation between the skills and the experience is often missing. 

    For example, for a skill set of ‘Spark’, instead of just searching for the keyword ‘Spark’ in the resume, we want to know  (for scoring purpose) 
  - if the employee worked in Spark for X number of years, 
  - did he have experience on Spark, in multiple organizations. 

  We then create a score for each profile/resume based on the skill set mentioned in the query and rank them in order of score (highest to lowest). 


3.	Which programming language do you plan to use?

* We will use the standard text retireval tools and programming APIs (MeTA, python, numpy etc) with a customized algorithm to score each resume.

4.	Please justify that the workload of your topic is at least 20*N hours, N being the total number of students in your team. You may list the main tasks to be completed, and the estimated time cost for each task.

The following are the steps and key milestones for this project: 

Task | Time needed | Completion date
---- | ----------- |----------------
Build the resume database with resumes | 4 hours | Nov 8
Progress report | 2 hours | Nov 15
Build the text retrieval algorithm and document indexes | 20 hours | Nov 22
Build scoring engine to pick relevant resumes | 12 hours | Nov 29
Build an experience profile | 8 hours | Dec 5
Software code submission with documentation | 8 hours | Dec 9

# Contributors

 * alokk3@illinois.edu
 * dkrovi2@illinois.edu
 * jsaxena3@illinois.edu
 * rathi9@illinois.edu
