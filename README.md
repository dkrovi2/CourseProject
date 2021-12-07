# Build Experience Profile from Resumes


1. **What are the names and NetIDs of all your team members? Who is the captain? The captain will have more administrative duties than team members.**

    * alokk3@illinois.edu
    * dkrovi2@illinois.edu
    * jsaxena3@illinois.edu
    * rathi9@illinois.edu

2. **What is your free topic? Please give a detailed description. What is the task? Why is it important or interesting? What is your planned approach? What tools, systems or datasets are involved? What is the expected outcome? How are you going to evaluate your work?**

  In this project, we use text extraction and retrieval for the following functions:

      * Parse resumes in doc and pdf format
      * Parse job descriptions in doc and pdf format
      * Build an analysis engine to extract experience details of a candidate on various tools and technologies
      * Rank the available set of resumes based on the skill set specified in the job description

  The current keyword based search used by many online websites might not be entirely accurate, as the correlation between the skills and the experience is often missing. 

  For example, for a skill set of ‘Spark’, instead of just searching for the keyword ‘Spark’ in the resume, we want to know  (for scoring purpose) 
      - if the employee worked in Spark for X number of years, 
      - did he have experience on Spark, in multiple organizations. 

  We then create a score for each profile/resume based on the skill set mentioned in the query and rank them in order of score (highest to lowest). 

3. **Which programming language do you plan to use?**

    We will use the standard text retrieval tools and programming APIs (MeTA, python, numpy etc) with a customized algorithm to score each resume.

4. **Please justify that the workload of your topic is at least 20 \* N hours, N being the total number of students in your team. You may list the main tasks to be completed, and the estimated time cost for each task.**

    The following are the steps and key milestones for this project: 

    | Task                                                          |  Time needed |             ETA |
    |:--------------------------------------------------------------|-------------:|----------------:|
    | Gather representative data set for training and evaluation    |      8 hours |          Nov  8 |
    | Parsing engine to parse resumes and job descriptions          |     20 hours |          Nov 15 |
    | Progress report                                               |      2 hours |          Nov 15 |
    | Analysis engine to analyze resumes                            |     30 hours |          Nov 22 |
    | Scoring engine to match resumes to provided job description   |     30 hours |          Nov 29 |
    | Basic UI to search for resumes matching a job description     |     24 hours |          Dec  5 |
    | Software documentation                                        |      8 hours |          Dec  9 |
    | **Total**                                                     |**122 hours** |                 |


# Project Video Demo
https://mediaspace.illinois.edu/media/t/1_tuxjijvb

# Contributors

 * alokk3@illinois.edu
 * dkrovi2@illinois.edu
 * jsaxena3@illinois.edu
 * rathi9@illinois.edu
