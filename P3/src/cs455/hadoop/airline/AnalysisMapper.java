//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.Arrays;

public class AnalysisMapper extends Mapper<LongWritable, Text, Text, Text> {

    //Line by line parses the file and grabs the appropriate values. Outputs (departure time, delay).
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Split the line by separating with each comma.
        String[] values = value.toString().split(",");

        if(values[0].equals("Year")){
            return; //Skip this line, it is the first line of the file.
        }

        try{
            //Hour, day, month for Q1 and Q2. Grab delay for these dates.
            Text departHour = new Text("h:" + (Integer.parseInt(values[5])/100)%24);
            Text departDay = new Text("d:" + values[3]);
            Text departMonth = new Text("m:" + values[1]);
            Text delay = new Text(values[14] + ":1");

            //Q3
            Text yearOriginAirportCode = new Text("y:" + values[0] + ":" + values[16]);
            Text yearDestinationAirportCode = new Text("y:" + values[0] + ":" + values[17]);
            Text overallOriginAirportCode = new Text("overall:" + values[16]);
            Text overallDestinationAirportCode = new Text("overall:" + values[17]);

            //Q4
            Text carrierCode = new Text("c:" + values[8]);
            Text carrierDelayMinNum = new Text();

                //Only accounting for positive delays.
            if(Integer.parseInt(values[24]) >= 0){
                carrierDelayMinNum.set(values[24] + ":1");
            }
            else{
                carrierDelayMinNum.set("0:0");
            }

            //Q5 : need Year, tail num, delay
                //Key is the (tail num), value is (delay, num flights, year).
            Text tailNum = new Text("n:" + values[10]);
            int[] yearCount = new int[44];
            int year = Integer.parseInt(values[0]);
            int index = year - 1987;
            try{
                yearCount[index] = Integer.parseInt(values[14]);
                yearCount[index+22] = 1;

                //Write output for Q5.
                context.write(tailNum, new Text(Arrays.toString(yearCount)));
            }
            catch(Exception e){
                //Found an NA, skip it.
            }


            //Q1&2: Write the key: departure, and the value: delay.
            context.write(departHour, delay);
            context.write(departDay, delay);
            context.write(departMonth, delay);

            //Write the output for Q3.
            context.write(yearOriginAirportCode, new Text("1"));
            context.write(yearDestinationAirportCode, new Text("1"));
            context.write(overallOriginAirportCode, new Text("1"));
            context.write(overallDestinationAirportCode, new Text("1"));

            //Write output for Q4.
            context.write(carrierCode, carrierDelayMinNum);

        }
        catch(Exception e){
            //Incorrectly formatted incoming data, pass it so it is not included.
            return;
        }
    }
}