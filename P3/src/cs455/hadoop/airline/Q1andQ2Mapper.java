//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class Q1andQ2Mapper extends Mapper<LongWritable, Text, Text, Text> {

    //Grabs the output of the previous job, uses the reducer to find the top values.
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Split the line by separating with each tab.
        String[] values = value.toString().split("\t");

        //Grab the unit, and the value of that unit.
        String[] date = values[0].split(":");

        //Write the unit, (delay, m/d/h:value).
        context.write(new Text(date[0]), new Text(values[1] + ":" + date[1]));

    }
}