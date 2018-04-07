//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class Q3TopTenReducer extends Reducer<Text, Text, Text, Text> {

    private int[] topTen = new int[23];
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

        if(keys[0].equals("t")){
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
    }
}