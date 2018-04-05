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

        //If the input is of the correct format.
        //Year:0, month:1, day of week:2, hour:3
        //tail num:4, arrival delay:5, origin:6,
        //dest:7, weather delay:8, carrierName:9.
        Text yearOriginAirportCode = new Text(values[0] + ":" + values[6]);
        Text yearDestinationAirportCode = new Text(values[0] + ":" + values[7]);
        Text overallOriginAirportCode = new Text("overall:" + values[6]);
        Text overallDestinationAirportCode = new Text("overall:" + values[7]); 

        //Write the key: departure, and the value: delay.
        context.write(yearOriginAirportCode, new IntWritable(1));
        context.write(yearDestinationAirportCode, new IntWritable(1));
        context.write(overallOriginAirportCode, new IntWritable(1));
        context.write(overallDestinationAirportCode, new IntWritable(1));
    }
}