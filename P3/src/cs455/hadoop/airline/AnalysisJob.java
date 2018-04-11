//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.Tool;
import java.lang.Runtime;

import java.io.IOException;

public class AnalysisJob{
    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration();

            //Set the necessary variables for the job.
            Job job = Job.getInstance(conf, "Initial Flight Analysis");
            job.setJarByClass(AnalysisJob.class);
            //Mapper classes
            job.setMapperClass(AnalysisMapper.class);
            job.setMapperClass(AirportMapper.class);
            job.setCombinerClass(AnalysisCombiner.class);
            job.setReducerClass(AnalysisReducer.class);
            //Map output class types.
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            //Input file paths.
            FileInputFormat.addInputPath(job, new Path(args[0]));
            MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, AnalysisMapper.class);
            MultipleInputs.addInputPath(job, new Path(args[0] + "/../supplementary/airports.csv"), TextInputFormat.class, AirportMapper.class);

            //Output file paths
            FileOutputFormat.setOutputPath(job, new Path(args[1] + "/temp"));
            MultipleOutputs.addNamedOutput(job, "q1a2", TextOutputFormat.class, Text.class, IntWritable.class);
            MultipleOutputs.addNamedOutput(job, "q3", TextOutputFormat.class, Text.class, IntWritable.class);
            MultipleOutputs.addNamedOutput(job, "q4", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job, "q5", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job, "q6", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job, "q7", TextOutputFormat.class, Text.class, Text.class);

            //Wait for job to complete.
            job.waitForCompletion(true);

            Job job2 = Job.getInstance(conf, "Final Flight Analysis");
            job2.setJarByClass(Q3AnalysisJob.class);
            //Mapper classes
            job2.setMapperClass(Q3Q7Mapper.class);
            job2.setMapperClass(CarrierMapper.class);
            job2.setMapperClass(Q1andQ2Mapper.class);
            job2.setMapperClass(PlaneMapper.class);
            job2.setMapperClass(AirportMapper.class);
            job2.setMapperClass(FBICrimeMapper.class);
            //Overwrote sorting class.
            job2.setSortComparatorClass(SortTextComparator.class);  //Homemade class to reverse sort.
            job2.setReducerClass(Job2Reducer.class);
            //Map output class types.
            job2.setMapOutputKeyClass(Text.class);
            job2.setMapOutputValueClass(Text.class);

            //Input & output file path provided in arguments.
            FileInputFormat.addInputPath(job2, new Path(args[1] + "/temp"));
            MultipleInputs.addInputPath(job2, new Path(args[1] + "/temp/q1a2-r-00000"), TextInputFormat.class, Q1andQ2Mapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[1] + "/temp/q3-r-00000"), TextInputFormat.class, Q3Q7Mapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[0] + "/../supplementary/carriers.csv"), TextInputFormat.class, CarrierMapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[1] + "/temp/q4-r-00000"), TextInputFormat.class, CarrierMapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[1] + "/temp/q5-r-00000"), TextInputFormat.class, PlaneMapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[0] + "/../supplementary/plane-data.csv"), TextInputFormat.class, PlaneMapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[1] + "/temp/q6-r-00000"), TextInputFormat.class, AirportMapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[1] + "/temp/q7-r-00000"), TextInputFormat.class, Q3Q7Mapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[1] + "/../q7CrimeData.csv"), TextInputFormat.class, FBICrimeMapper.class);

            //Output paths.
            FileOutputFormat.setOutputPath(job2, new Path(args[1] + "/final"));
            MultipleOutputs.addNamedOutput(job2, "q1a2", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q3before2000", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q3before2009", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q3overall", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q4", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q5", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q6", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q7", TextOutputFormat.class, Text.class, Text.class);

            // Wait for job to complete.
            System.exit(job2.waitForCompletion(true) ? 0 : 1);

        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
        catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}