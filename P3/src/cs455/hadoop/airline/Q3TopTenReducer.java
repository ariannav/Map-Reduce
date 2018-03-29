//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class Q3TopTenReducer extends Reducer<IntWritable, Text, Text, IntWritable> {

    private int[] topTen = new int[23];

    @Override
    protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String[] info;

        // Grab the top ten, write them, increment the count associated with that year.
        for(Text val : values){
            info = val.toString().split(":");
            try{
                int year = Integer.parseInt(info[0]);
                int index = year - 1987;
                if(topTen[index] < 10){
                    context.write(val, key);
                    topTen[index]++;
                }
            }
            catch(Exception e){
                //Overall, position 22 saved for overall values.
                if(topTen[22] < 10){
                    context.write(val, key);
                    topTen[22]++;
                }
            }
        }
    }
}