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
    Change Environment Variable:
        export HADOOP_CONF_DIR=/s/bach/m/under/arivacca/client-config/client-config
    Remove Existing HDFS Directory:
        $HADOOP_HOME/bin/hdfs dfs -rm -r /home/census/output/
    Run Hadoop:
        $HADOOP_HOME/bin/hadoop jar dist/airline.jar cs455.hadoop.airline.AnalysisJob /data/main /home/census/output
    Run Q7:
        $HADOOP_HOME/bin/hdfs dfs -get /home/census/output/final/q7-r-00000 .
        python src/cs455/hadoop/airline/q7visual.py q7-r-00000
        (Make sure you are in the same directory as the P3 src directory.)

VIEW OUTPUT
===================
    View Available Files in HDFS:
        $HADOOP_HOME/bin/hdfs dfs -ls /home/census/output/final
    See contents of file:
        Q1/Q2:  q1a2-r-00000
        Q3:     q3before2000-r-00000
                q3before2009-r-00000
                q3overall-r-00000
        Q4:     q4-r-00000
        Q5:     q5-r-00000
        Q6:     q6-r-00000
        Q7:     q7-r-00000

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


METHODOLOGY
===================
Q1/Q2:      Job 1 takes the month, day, hour, and the corresponding arrival
            delay. Reduces it to a delay for each month, day, and hour. There
            are 12 month delays, 7 day delays, and 24 hour delays.
            Job 2 takes the output from job 1, and finds the max and min of each
            value. The reducer receives 3 keys for Q1: 'm', 'd', and 'h'.
            Finds the min and max for each key.
Q3:         Job 1 takes both the origin and the destination airport. For each
            year, and the overall value, it counts the number of times that
            airport appears. The reducer produces a collected count of
            occurrences for each airport, each year.
            Job 2 takes the output from job 1, and sorts the output for each
            year (and overall values) in descending order. The reducer receives
            the values for each year, and outputs the first ten that appear.
            The output goes to different files depending on the range of years.
Q4:         Job 1 takes the carrier code, carrier delay, and the count of
            delayed flights. It only takes these values if carrier delay > 0.
            Job 1 outputs the carriers and the sum of their delays, and delayed
            flights.
            Job 2 takes the output of job 1 and the carriers.csv data and
            combines them by key in the reducer. They are joined on the carrier
            code. The carrier code is replaced with each Carrier's name, and is
            output with the total number of delayed minutes, the number of
            delayed flights, and the average.
Q5:         Job 1 takes the tail number, arrival delay, and count of flights. It
            outputs these values in an array, where the indices correspond to
            the year of the flight. The combiner and reducer accumulates these
            values for each position in the array. The reducer ends with one
            array for each plane, containing the delayed minutes and flights
            for each year for that plane.
            Job 2 takes the output of job 1, and the data in the planes.csv file
            and joins these values on the tail number. The reducer loops through
            the array values for each tailnum, and looks at the manufactured
            year. If the plane is considered 'old' for that year, the delay sum
            and the count is added to the 'Old sum' and 'Old count'. Otherwise
            the output is added to the 'New sum' and 'New count'. The total avg
            of these two values is output by dividing the sum by the count for
            old and new planes.
Q6:         Job 1 takes the weather delay and the departure delay. If both are
            greater than 0, we can say the weather delay is a result of the
            origin airport. There are no other assumptions made. When departure
            and weather delay > 0, the airport code and a '1' are sent to the
            combiner and reducer. Additionally, job 1 also takes in the
            airports.csv file in another mapper. Each airport is output as the
            airport key, and the city. The main data and the airports.csv data
            are joined in the reducer, which outputs the city and its
            corresponding number of weather delays.
            Job 2 takes in the output from job 1 and sorts the values in
            descending order depending on the number of weather delays. The
            reducer receives these values and loops through the first ten.
            These are the top ten cities with the most weather delay.
Q7:         Job 1 takes the security delay and the security-related
            cancellations and outputs them with the airport code. Like in Q6,
            airports.csv is also parsed in job 1, and is output as airport code,
            city. The main data set and the airports.csv file are joined on the
            airport code, and the security related incidents are joined with the
            city.
            In job 2, values from FBI Crime In the US Data (source below) is
            reduced by city, rate of violent crimes. Job 2 also takes in the Q7
            output from job 1. Both the job 1 output and the FBI crime data are
            sorted in descending order, and the top ten of both datasets are
            found. The top cities from each dataset are output.
            The python file takes the output data, and displays it in a bar
            graph to compare the crime rates for the top ten cities and their
            corresponding airport data. The output figure is stored in the
            current directory under q7fig.png.
            FBI Data Source: https://ucr.fbi.gov/crime-in-the-u.s/

