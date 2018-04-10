//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.Arrays;

public class AnalysisMapper extends Mapper<LongWritable, Text, Text, Text> {

    private String[] values;

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Split the line by separating with each comma.
        values = value.toString().split(",");

        if(values[0].equals("Year")){
            return; //Skip this line, it is the first line of the file.
        }

        //Question by question:
        q1a2(context);
        q3(context);
        q4(context);
        q5(context);
        q6(context);
        q7(context);

    }

    //What is the best time-of-the-day/day-of-week/time-of-year to fly to minimize delays?
    //What is the worst time-of-the-day / day-of-week/time-of-year to fly to minimize delays?
    private void q1a2(Context context){
        try{
            //Hour, day, month for Q1 and Q2. Grab delay for these dates.
            Text departHour = new Text("h:" + (Integer.parseInt(values[5])/100)%24);
            Text departDay = new Text("d:" + values[3]);
            Text departMonth = new Text("m:" + values[1]);
            Text delay = new Text(values[14] + ":1");

            //Q1&2: Write the key: departure date, and the value: delay.
            context.write(departHour, delay);
            context.write(departDay, delay);
            context.write(departMonth, delay);
        }
        catch(Exception e){
            //Improperly formatted line, can be skipped.
        }
    }

    //What are the major hubs (busiest airports) in continental U.S.? Please list the top 10. Has
    //there been a change over the 21-year period covered by this dataset?
    private void q3(Context context){
        try{
            //Q3, write (year, origin/destination), and (overall, origin/destination).
            Text yearOriginAirportCode = new Text("y:" + values[0] + ":" + values[16]);
            Text yearDestinationAirportCode = new Text("y:" + values[0] + ":" + values[17]);
            Text overallOriginAirportCode = new Text("overall:" + values[16]);
            Text overallDestinationAirportCode = new Text("overall:" + values[17]);

            //Write key: year, origin/destination and value: 1.
            context.write(yearOriginAirportCode, new Text("1"));
            context.write(yearDestinationAirportCode, new Text("1"));
            context.write(overallOriginAirportCode, new Text("1"));
            context.write(overallDestinationAirportCode, new Text("1"));
        }
        catch(Exception e){
            //Improperly formatter line, can be skipped.
        }
    }

    //Which carriers have the most delays? You should report on the total number of delayed
    //flights and also the total number of minutes that were lost to delays. Which carrier has the
    //highest average delay?
    private void q4(Context context){
        try{
            //Grab the carrier code.
            Text carrierCode = new Text("c:" + values[8]);
            Text carrierDelayMinNum = new Text();

            //Only account for positive delays. Grab minutes, and count.
            if(Integer.parseInt(values[24]) > 0){
                carrierDelayMinNum.set(values[24] + ":1");

                //Write key: carrier code, and value: delay min, count.
                context.write(carrierCode, carrierDelayMinNum);
            }

        }
        catch(Exception e){
            //Incorrectly formatted line. Skipping it.
            return;
        }
    }

    //Do older planes cause more delays? Contrast their on-time performance with newer planes.
    //Planes that are more than 20 years old can be considered old.
    private void q5(Context context){
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
    }

    //Which cities experience the most weather-related delays? Please list the top 10.
    private void q6(Context context){
        //Q6
        try{
            int weatherDelay = Integer.parseInt(values[25]);
            int deptDelay = Integer.parseInt(values[15]);

            if(deptDelay > 0 && weatherDelay > 0){
                //Write origin airport as key, 1 as value.
                context.write(new Text("w:" + values[16]), new Text("1"));
            }
        }
        catch(Exception e){
            //Found an NA value, we can skip it.
        }
    }

    //Which cities experience the largest number of security related delays and cancellations?
    //How does this relate to current crime rates in these cities?
    private void q7(Context context) throws IOException, InterruptedException{
        char cancellationCode = 'N';
        try{
            cancellationCode = values[22].charAt(0);
            int securityDelay = Integer.parseInt(values[27]);

            if(securityDelay > 0 || cancellationCode == 'D'){
                //Write origin airport as key, 1 as value.
                context.write(new Text("s:" + values[16]), new Text("1"));
            }
        }
        catch(Exception e){
            //Found an NA value, we can check for cancellation code.
            if(cancellationCode == 'D'){
                //Still write it as a '1'.
                context.write(new Text("s:" + values[16]), new Text("1"));
            }
        }
    }
}

