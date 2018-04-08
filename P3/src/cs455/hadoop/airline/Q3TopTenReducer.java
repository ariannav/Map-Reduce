//Author: Arianna Vacca

package cs455.hadoop.airline;

import java.lang.Integer;
import java.lang.Double;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class Q3TopTenReducer extends Reducer<Text, Text, Text, Text> {

    private int[] topTen = new int[23];
    private long oldCount = 0;
    private long oldSum = 0;
    private long newCount = 0;
    private long newSum = 0;
    private MultipleOutputs mos;

    @Override
    protected void setup(Context context){
        //Set up multiple outputs.
        mos = new MultipleOutputs(context);
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        //Split keys. Should have first position be "t" for Q3, and "c" for Q4.
        String[] keys = key.toString().split(":");

        //Q1 and Q2
        if(keys[0].equals("h") || keys[0].equals("m") || keys[0].equals("d")){
            int min = Integer.MAX_VALUE;
            Text minValue = new Text();
            int max = Integer.MIN_VALUE;
            Text maxValue = new Text();

            //Grab the top one from each key.
            for(Text val: values){
                String[] delayDate = val.toString().split(":");
                int delay = Integer.parseInt(delayDate[0]);
                if(delay < min){
                    min = delay;
                    minValue.set("Best " + key + ":" + delayDate[1]);
                }
                if(delay > max){
                    max = delay;
                    maxValue.set("Worst " + key + ":" + delayDate[1]);
                }
            }
            mos.write("q1a2", minValue, new Text(Integer.toString(min)));
            mos.write("q1a2", maxValue, new Text(Integer.toString(max)));
        }
        //Q3
        else if(keys[0].equals("t")){
            String[] info;

            // Grab the top ten, write them, increment the count associated with that year.
            for(Text val : values){
                //Each value should have year:airport code.
                info = val.toString().split(":");
                try{
                    //Grab year, find index in array, increment and print if under 10.
                    int year = Integer.parseInt(info[1]);
                    int index = year - 1987;
                    if(topTen[index] < 10){
                        if(year < 2000){
                            mos.write("q3before2000", val, key);
                        }
                        else{
                            mos.write("q3before2009", val, key);
                        }
                        topTen[index]++;
                    }
                }
                catch(Exception e){
                    //Overall, position 22 saved for overall values.
                    //Catches exception when year Int Parsing fails.
                    if(topTen[22] < 10){
                        mos.write("q3overall", val, key);
                        topTen[22]++;
                    }
                }
            }
        }
        //Q4
        else if(keys[0].equals("c")){
            //Should be two values: one with the carrier value, one with the airline data value.
            Text carrier = new Text();
            Text temp = new Text();

            for(Text val: values){
                //Value should contain: (Airline name) for carrier line, and (sum:count:average) for data.
                String[] valueSplit = val.toString().split(":");
                if(valueSplit.length == 1){ //Carrier name.
                    carrier.set(val);
                }
                else if(valueSplit.length > 1){ //Sum, count, average.
                    temp.set(val);
                }
            }

            //Do not print if there are no airline statistics about this carrier.
            if(temp.toString().length() == 0){
                return;
            }

            //Both should be set now, can write it to Q4. Don't need the key anymore.
            mos.write("q4", new Text(temp + "\t"), carrier);
        }
        //Q5
        else if(keys[0].equals("n")){
            int[] yearCount = new int[44];
            int manYear = 0;
            //Accumulate delay and flights
            for(Text val: values){
                String withoutBrackets = val.toString().substring(1, val.toString().length()-1);
                String[] partition = withoutBrackets.split(",");

                if(partition.length == 1){
                    try{
                        //From plane-data.csv
                        manYear = Integer.parseInt(val.toString());
                    }
                    catch(Exception e){
                        //Bad year, skip these values.
                        return;
                    }
                }

                for(int i = 0 ; i < partition.length; i++){
                    try{
                        yearCount[i] += Integer.parseInt(partition[i]);
                    }
                    catch(Exception e){
                        yearCount[i] += Integer.parseInt(partition[i].substring(1));
                    }
                }
            }

            for(int i = 0; i < 22; i++){
                if(((i + 1987) - manYear) > 20){
                    oldSum += yearCount[i];
                    oldCount += yearCount[i + 22];
                }
                else{
                    newSum += yearCount[i];
                    newCount += yearCount[i + 22];
                }
            }
            //Output at end.
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException{
        //output, (tailnum, year), (sum, count)
        double oldAvg = oldSum/oldCount;
        double newAvg = newSum/newCount;
        mos.write("q5", new Text("Old plane delay average:"), new Text(Double.toString(oldAvg)));
        mos.write("q5", new Text("New plane delay average:"), new Text(Double.toString(newAvg)));
        mos.close();
    }
}