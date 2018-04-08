//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.IOException;

public class AnalysisReducer extends Reducer<Text, Text, Text, IntWritable> {

    private MultipleOutputs mos;

    @Override
    protected void setup(Context context){
        mos = new MultipleOutputs(context);
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        int sum = 0;
        String[] partition;
        String[] keyType = key.toString().split(":");

        //Q1 and Q2 Mapping results.
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

            int avgDelay = sum/count;
            mos.write("q1a2", key, new IntWritable(avgDelay));
        }
        //Filter Q3 mapping results.
        else if(keyType[0].equals("y") || keyType[0].equals("overall")){
            // calculate the total count
            for(Text val : values){
                count+= Integer.parseInt(val.toString());
            }

            mos.write("q3", key, new IntWritable(count));
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

            mos.write("q4", key, new Text(sum + ":" + count + ":avg " + sum/count));
        }
        //Filter Q5 mapping results.
        else if(keyType[0].equals("n")){
            int[] yearCount = new int[44];
            //Accumulate delay and flights
            for(Text val: values){
                try{
                    String withoutBrackets = val.toString().substring(1, val.toString().length()-1);
                    partition = withoutBrackets.split(",");

                    for(int i = 0 ; i < partition.length; i++){
                        try{
                            yearCount[i] += Integer.parseInt(partition[i]);
                        }
                        catch(Exception e){
                            yearCount[i] += Integer.parseInt(partition[i].substring(1));
                        }
                    }
                }
                catch(Exception e){
                    //Came across an "NA". That's okay, we can just skip it.
                }
            }
            //output, (tailnum, year), (sum, count)
            mos.write("q5", key, new Text(Arrays.toString(yearCount)));
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException{
        mos.close();
    }

}