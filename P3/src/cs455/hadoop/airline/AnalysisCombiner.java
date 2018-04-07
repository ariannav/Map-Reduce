//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class AnalysisCombiner extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        int sum = 0;
        String[] keyType = key.toString().split(":");
        String[] partition;

        if(keyType[0].equals("h") || keyType[0].equals("d") || keyType[0].equals("m")){
            for(Text val : values){
                try{
                    partition = val.toString().split(":");
                    sum += Integer.parseInt(partition[0]);
                    count += Integer.parseInt(partition[1]);
                }
                catch(Exception e){
                    //Came across an "NA". That's okay, we can just skip it.
                }
            }

            String sumCount = new String(sum + ":" + count);
            context.write(key, new Text(sumCount));
        }
        //Filter Q3 mapping results.
        else if(keyType[0].equals("y") || keyType[0].equals("overall")){
            // calculate the total count
            for(Text val : values){
                count+= Integer.parseInt(val.toString());
            }

            String countValue = Integer.toString(count);
            context.write(key, new Text(countValue));
        }
        //Filter Q4 mapping results.
        else if(keyType[0].equals("c")){
            String[] valSec;

            //Calculate the total sum of delay and flights
            for(Text val : values){
                valSec = val.toString().split(":");

                //Delay sum, flight count
                sum += Integer.parseInt(valSec[0]);
                count+= Integer.parseInt(valSec[1]);
            }
            context.write(key, new Text(sum + ":" + count));
        }
    }
}