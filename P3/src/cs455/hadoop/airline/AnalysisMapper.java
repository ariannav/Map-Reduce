//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class AnalysisMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

    //Line by line parses the file and grabs the appropriate values. Outputs (departure time, delay).
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Split the line by separating with each comma.
        String[] values = value.toString().split(",");

        IntWritable depart = new IntWritable();
        IntWritable delay = new IntWritable();

        //If the input is of the correct format.
        try{
            if(values.length == 29){
                //Set the departure and the delay.
                depart.set((Integer.parseInt(values[5])/100)%24);
                delay.set(Integer.parseInt(values[14]));
            }
        }
        catch(Exception e){
            //This was the first line of the file. Pass it.
        }
        //Write the key: departure, and the value: delay.
        context.write(depart, delay);
    }
}