//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class AnalysisMapper extends Mapper<LongWritable, Text, Text, Text> {

    //Line by line parses the file and grabs the appropriate values. Outputs (departure time, delay).
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Split the line by separating with each comma.
        String[] values = value.toString().split(",");


        //If the input is of the correct format.
        //Year:0, month:1, day of week:2, hour:3
        //tail num:4, arrival delay:5, origin:6,
        //dest:7, weather delay:8, carrierName:9.


        try{
            //Hour, day, month for Q1 and Q2. Grab delay for these dates.
            Text departHour = new Text("h:" + (Integer.parseInt(values[3])/100)%24);
            Text departDay = new Text("d:" + values[2]);
            Text departMonth = new Text("m:" + values[1]);
            Text delay = new Text(values[5] + ":1");

            Text yearOriginAirportCode = new Text("y:" + values[0] + ":" + values[6]);
            Text yearDestinationAirportCode = new Text("y:" + values[0] + ":" + values[7]);
            Text overallOriginAirportCode = new Text("overall:" + values[6]);
            Text overallDestinationAirportCode = new Text("overall:" + values[7]);

            //Write the key: departure, and the value: delay.
            context.write(departHour, delay);
            context.write(departDay, delay);
            context.write(departMonth, delay);

            //Write the output for Q3. 
            context.write(yearOriginAirportCode, new Text("1"));
            context.write(yearDestinationAirportCode, new Text("1"));
            context.write(overallOriginAirportCode, new Text("1"));
            context.write(overallDestinationAirportCode, new Text("1"));
        }
        catch(Exception e){
            //Incorrectly formatted incoming data, pass it so it is not included.
            return;
        }
    }
}