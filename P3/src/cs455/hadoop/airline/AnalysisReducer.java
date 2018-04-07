//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import java.util.ArrayList;

import java.io.IOException;

public class AnalysisReducer extends Reducer<Text, Text, Text, IntWritable> {

    private MultipleOutputs mos;
    private Text minHour = new Text();
    private Text minDay = new Text();
    private Text minMonth = new Text();
    private int minHourDelay = Integer.MAX_VALUE;
    private int minDayDelay = Integer.MAX_VALUE;
    private int minMonthDelay = Integer.MAX_VALUE;
    private Text maxHour = new Text();
    private Text maxDay = new Text();
    private Text maxMonth = new Text();
    private int maxHourDelay = Integer.MIN_VALUE;
    private int maxDayDelay = Integer.MIN_VALUE;
    private int maxMonthDelay = Integer.MIN_VALUE;
    private ArrayList<String> carriers = new ArrayList<>(30);

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
            String type = (key.toString().split(":"))[0];

            checkMin(type.charAt(0), avgDelay, key);
            checkMax(type.charAt(0), avgDelay, key);
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
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException{
        //Writing Min Values
        mos.write("q1a2", minHour, new IntWritable(minHourDelay));
        mos.write("q1a2", minDay, new IntWritable(minDayDelay));
        mos.write("q1a2", minMonth, new IntWritable(minMonthDelay));

        //Writing Max
        mos.write("q1a2", maxHour, new IntWritable(maxHourDelay));
        mos.write("q1a2", maxDay, new IntWritable(maxDayDelay));
        mos.write("q1a2", maxMonth, new IntWritable(maxMonthDelay));

        mos.close();
    }

    //Determines if the current average is less than the minimum.
    private void checkMin(char type, int avgDelay, Text key){
        switch(type){
            case 'h': //departHour
                if(avgDelay < minHourDelay){
                    minHourDelay = avgDelay;
                    minHour.set("Best " + key);
                }
                break;
            case 'd': //departDay
                if(avgDelay < minDayDelay){
                    minDayDelay = avgDelay;
                    minDay.set("Best " + key);
                }
                break;
            case 'm': //departMonth
                if(avgDelay < minMonthDelay){
                    minMonthDelay = avgDelay;
                    minMonth.set("Best " + key);
                }
                break;
        }
    }

    private void checkMax(char type, int avgDelay, Text key){
        switch(type){
            case 'h': //departHour
                if(avgDelay > maxHourDelay){
                    maxHourDelay = avgDelay;
                    maxHour.set("Worst " + key);
                }
                break;
            case 'd': //departDay
                if(avgDelay > maxDayDelay){
                    maxDayDelay = avgDelay;
                    maxDay.set("Worst " + key);
                }
                break;
            case 'm': //departMonth
                if(avgDelay > maxMonthDelay){
                    maxMonthDelay = avgDelay;
                    maxMonth.set("Worst " + key);
                }
                break;
        }
    }
}