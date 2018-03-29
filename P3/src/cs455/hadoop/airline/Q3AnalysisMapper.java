//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class Q3AnalysisMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    //Line by line parses the file and grabs the appropriate values. Outputs (departure time, delay).
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Split the line by separating with each comma.
        String[] values = value.toString().split(",");

        Text yearOriginAirportCode = new Text();
        Text yearDestinationAirportCode = new Text();
        Text overallOriginAirportCode = new Text();
        Text overallDestinationAirportCode = new Text();

        //If the input is of the correct format.
        if(values.length == 29){
            if(values[16].equals("Origin")){
                //First line in a File, do not want to include.
                return;
            }
                yearOriginAirportCode.set(values[0] + ":" + values[16]);
                yearDestinationAirportCode.set(values[0] + ":" + values[17]);
                overallOriginAirportCode.set("overall:" + values[16]);
                overallDestinationAirportCode.set("overall:" + values[17]);
        }

        //Write the key: departure, and the value: delay.
        context.write(yearOriginAirportCode, new IntWritable(1));
        context.write(yearDestinationAirportCode, new IntWritable(1));
        context.write(overallOriginAirportCode, new IntWritable(1));
        context.write(overallDestinationAirportCode, new IntWritable(1));
    }
}