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

import java.io.IOException;

public class AnalysisJob{
    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration();

            //Set the necessary variables for the job.
            Job job = Job.getInstance(conf, "Flight Analysis Q1, Q2, and Q3");
            job.setJarByClass(AnalysisJob.class);
            job.setMapperClass(AnalysisMapper.class);
            job.setCombinerClass(AnalysisCombiner.class);
            job.setReducerClass(AnalysisReducer.class);
            //Map output class types.
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            //Input file paths.
            FileInputFormat.addInputPath(job, new Path(args[0]));

            //Output file paths
            FileOutputFormat.setOutputPath(job, new Path(args[1] + "/Q1andQ2"));
            MultipleOutputs.addNamedOutput(job, "q1a2", TextOutputFormat.class, Text.class, IntWritable.class);
            MultipleOutputs.addNamedOutput(job, "q3", TextOutputFormat.class, Text.class, IntWritable.class);
            MultipleOutputs.addNamedOutput(job, "q4", TextOutputFormat.class, Text.class, Text.class);

            //Wait for job to complete.
            job.waitForCompletion(true);

            Job job2 = Job.getInstance(conf, "Flight Analysis Q3: Finding Top 10");
            job2.setJarByClass(Q3AnalysisJob.class);
            job2.setMapperClass(Q3TopTenMapper.class);
            job2.setMapperClass(CarrierMapper.class);
            job2.setSortComparatorClass(SortTextComparator.class);  //Homemade class to reverse sort.
            job2.setReducerClass(Q3TopTenReducer.class);
            //Map output class types.
            job2.setMapOutputKeyClass(Text.class);
            job2.setMapOutputValueClass(Text.class);

            //Input & output file path provided in arguments.
            FileInputFormat.addInputPath(job2, new Path(args[1] + "/Q1andQ2"));
            MultipleInputs.addInputPath(job2, new Path(args[1] + "/Q1andQ2/q3-r-00000"), TextInputFormat.class, Q3TopTenMapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[0] + "/../supplementary/carriers.csv"), TextInputFormat.class, CarrierMapper.class);
            MultipleInputs.addInputPath(job2, new Path(args[1] + "/Q1andQ2/q4-r-00000"), TextInputFormat.class, CarrierMapper.class);

            FileOutputFormat.setOutputPath(job2, new Path(args[1] + "/Q3andQ4"));
            MultipleOutputs.addNamedOutput(job2, "q3before2000", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q3before2009", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q3overall", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job2, "q4", TextOutputFormat.class, Text.class, Text.class);

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