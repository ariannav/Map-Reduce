//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class Q3TopTenMapper extends Mapper<LongWritable, Text, Text, Text> {

    //Grabs the output of the previous job, uses the reducer to find the top ten.
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Split the line by separating with each tab.
        String[] values = value.toString().split("\t");

        //Write the key: Traffic (t:number), value: airport name. We want to sort by traffic. 
        context.write(new Text("t:" + values[1]), new Text(values[0]));

    }
}