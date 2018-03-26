//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class AnalysisMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    //Line by line parses the file and grabs the appropriate values. Outputs (departure time, delay).
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Split the line by separating with each comma.
        String[] values = value.toString().split(",");

        Text departHour = new Text();
        Text departDay = new Text();
        Text departMonth = new Text();
        IntWritable delay = new IntWritable();

        //If the input is of the correct format.
        try{
            if(values.length == 29){
                //Set the departure and the delay.
                departHour.set("h:" + (Integer.parseInt(values[5])/100)%24);
                departDay.set("d:" + values[3]);
                departMonth.set("m:" + values[1]);
                delay.set(Integer.parseInt(values[14]));
            }
        }
        catch(Exception e){
            //This was the first line of the file. Pass it.
            return; 
        }
        //Write the key: departure, and the value: delay.
        context.write(departHour, delay);
        context.write(departDay, delay);
        context.write(departMonth, delay);
    }
}