Author: Arianna Vacca
Purpose: CS455 PA3
Date: 10 April 2018
********************************************************************************
                                    README
********************************************************************************

COMPILE
===================
    to compile:
        ant

RUN
===================
    Remove Existing HDFS Directory:
        $HADOOP_HOME/bin/hdfs dfs -rm -r /home/census/output/
    Run Hadoop:
        $HADOOP_HOME/bin/hadoop jar dist/airline.jar cs455.hadoop.airline.AnalysisJob /data/main /home/census/output

CLASS DESCRIPTIONS
===================
    - AnalysisJob: Configures the two jobs of the assignment. The first job
        takes two Mapper classes, one parses the main dataset, and the second
        parses the airports.csv file. Each job uses multiple inputs and
        multiple outputs. The second job has 6 mapper classes, one for Q1/Q2,
        one for Q3/Q7, a carriers.csv mapper, a planes.csv mapper, the
        airports.csv mapper, and the FBICrimeMapper, which is used for Q7.
    - AnalysisMapper: Mapper for job 1, parses the main dataset located in
        shared cluster. Writes the appropriate data to output for each question,
        and assigns a character identifier to determine what data belongs to
        which job. All seven questions are acknowledged to in this Mapper class.
    - AnalysisCombiner: Combiner for job 1, separates incoming data by via
        'if' statements depending on the first character of the incoming data.
        Combines data for all seven questions.
    - AnalysisReducer: Reducer for job 1, separates incoming data via 'if'
        statements depending on the first character of the incoming data.
        Uses MultipleOutputs to output data for each question into their
        corresponding files. The reducer joins the data for Q7 based on city.
    - Q1andQ2Mapper: Mapper for job 2. Takes in Q1/Q2 output from job 1 and
        writes the delay, and the corresponding m/d/h. Output is parsed by
        the Job2Reducer.
    - Q3Q7Mapper: Mapper for job 2. Takes in Q3/Q7 output from job 1 and flips
        the key and values in order for them to be sorted by count. Uses the
        SortTextComparator.
    - CarrierMapper: Mapper for job 2. Takes in carriers.csv, writes the carrier
        code and name to output. Also takes in Q4 output from job 1, and writes
        the carrier code and associated values. The carriers.csv and Q4 output
        are joined in the Job2Reducer.
    - PlaneMapper: Parses the carriers file and Q5 output for job 2. Takes the
        carriers file and outputs the tailnum and the manufactured year. Takes
        Q5 output and outputs it in the same format as it is read in.
    - AirportMapper: Parses the airports.csv file, as well as the Q6 output from
        job 1. Used in both job 1 and job 2. Used in job 1 to provide airport
        data to Q6 and Q7, specifically the city. Outputs the airport
        abbreviation code, and the city. In job 2, it takes in the Q6 output
        and writes the count, key as output in order for the counts to be sorted.
    - FBICrimeMapper: Reads in data from the local cluster for Q7. Uses the FBI
        crime data and outputs the data as city, crime rate. Only used in job 2.
    - Job2Reducer: Reduces the output from Q1-Q7 in job 2. Splits based on the
        first character in the key, then sends the data to the appropriate
        method. Each question outputs to a separate file. Takes in data from 6
        mappers, all of which are listed in the AnalysisJob class description.
        Sends output to the 'final' directory.
    - SortTextComparator: A comparator class used for job 2. Depending on the
        first character of the key, the data is either sorted in-order or in
        reverse-order. Output from Q3, Q6, Q7, and FBICrimeMapper is in reverse
        sorted order. The rest has been left unchanged. The output is sent on
        to Job2Reducer.
    - q7visual: A python program used for the visualization of Q7. Uses
        matplotlib to present the data from Q7 in a bar-chart format. 


