//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class AnalysisJob {
    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration();

            //Set the necessary variables for the job.
            Job job = Job.getInstance(conf, "Flight Analysis Q1 and Q2");
            job.setJarByClass(AnalysisJob.class);
            job.setMapperClass(AnalysisMapper.class);
            job.setReducerClass(AnalysisReducer.class);
            //Map output class types.
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(IntWritable.class);

            //Input & output file path provided in arguments.
            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1] + "/Q1andQ2"));

            // Wait for job to complete.
            job.waitForCompletion(true);

            System.out.println("Starting Job 2..."); 

            //Set the necessary variables for the job.
            Job job2 = Job.getInstance(conf, "Flight Analysis Q3");
            job2.setJarByClass(AnalysisJob.class);
            job2.setMapperClass(Q3AnalysisMapper.class);
            //job.setCombinerClass(AnalysisReducer.class);
            job2.setReducerClass(Q3AnalysisReducer.class);
            //Map output class types.
            job2.setMapOutputKeyClass(Text.class);
            job2.setMapOutputValueClass(IntWritable.class);

            //Input & output file path provided in arguments.
            FileInputFormat.addInputPath(job2, new Path(args[0]));
            FileOutputFormat.setOutputPath(job2, new Path(args[1] + "/Q3"));

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