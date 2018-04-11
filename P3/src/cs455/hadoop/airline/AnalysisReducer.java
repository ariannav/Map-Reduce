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
    private String[] partition;

    @Override
    protected void setup(Context context){
        mos = new MultipleOutputs(context);
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        char keyType = (char)key.charAt(0);
        //Q1 and Q2 values.
        if(keyType == 'h' || keyType == 'd' || keyType  == 'm'){
            processQ1aQ2(key, values, context);
        }
        //Filter Q3 mapping results.
        else if(keyType  == 'y' || keyType  == 'o'){
            processQ3(key, values, context);
        }
        //Filter Q4 mapping results.
        else if(keyType  == 'c'){
            processQ4(key, values, context);
        }
        //Filter Q5 mapping results.
        else if(keyType  == 'n'){
            processQ5(key, values, context);
        }
        //Filter Q6 results.
        else if(keyType  == 'w'){
            processQ6(key, values, context);
        }
        //Filter Q7 results.
        else if(keyType == 's'){
            processQ7(key, values, context);
        }
    }

    //Q1/2: Comes in with date key, and sum:count value.
    private void processQ1aQ2(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
        int sum = 0;
        int count = 0;

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


    //Q3: Comes in with year:airport key and 1 value.
    private void processQ3(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
        int count = 0;
        // calculate the total count
        for(Text val : values){
            count+= Integer.parseInt(val.toString());
        }

        mos.write("q3", key, new IntWritable(count));
    }

    //Q4: Comes in with carrier code key and sum/count value.
    private void processQ4(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
        String[] valSec;
        int sum = 0;
        int count = 0;

        //Calculate the total sum of delay and flights
        for(Text val : values){
            valSec = val.toString().split(":");
            //Delay sum, flight count
            sum += Integer.parseInt(valSec[0]);
            count+= Integer.parseInt(valSec[1]);
        }

        mos.write("q4", key, new Text(sum + ":" + count + ":avg " + sum/count));
    }

    //Q5: Comes in with tail number key, and array of delays and flight counts value.
    private void processQ5(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
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

    //Q6: Comes in with Airport code key, and count.
    private void processQ6(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
        int count = 0;
        Text city = new Text();
        //Accumulate counts for each airport.
        for(Text val: values){
            try{
                count += Integer.parseInt(val.toString());
            }
            catch(Exception e){
                //From airports file. Save as city.
                city.set(val.toString());
            }
        }

        mos.write("q6", city, new Text(Integer.toString(count)));
    }

    //Q7: Comes in with airport code key, and count.
    private void processQ7(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
        int count = 0;
        Text city = new Text();
        //Accumulate counts
        for(Text val: values){
            try{
                count += Integer.parseInt(val.toString());
            }
            catch(Exception e){
                //From airports file, set the city. 
                city.set(val.toString());
            }
        }
        mos.write("q7", city, new Text(Integer.toString(count)));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException{
        mos.close();
    }

}