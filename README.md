# Build Experience Profile from Resumes

In this project, we use text extraction and retireval for the following functions:

1. Get useful information from a candidate’s (prospective employee) resume
2. Build an experience profile connection candidate’s experience in various tools and technologies, for each of the resumes
3. Rank the available set of resumes based on the skill set, using the keywords provided in the query

The current keyword based search used by many online websites might not be entirely accurate, as the coorelation between the skills and the experience is often missing. 

For example, for a skill set of ‘Spark’, instead of just searching for the keyword ‘Spark’ in the resume, we want to know  (for scoring purpose) 
 - if the employee worked in Spark for X number of years, 
 - did he have experience on Spark, in multiple organizations. 

We then create a score for each profile/resume based on the skill set mentioned in the query and rank them in order of score (highest to lowest). 

We use the standard text retireval tools and programming APIs (MeTA, python, numpy etc) with a customized algorithm to score each resume.


# Contributors

 * alokk3@illinois.edu
 * dkrovi2@illinois.edu
 * jsaxena3@illinois.edu
 * rathi9@illinois.edu
