//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class AnalysisReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

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

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        int sum = 0;
        // calculate the total count
        for(IntWritable val : values){
            count++;
            sum += val.get();
        }

        int avgDelay = sum/count;
        String type = (key.toString().split(":"))[0];

        checkMin(type.charAt(0), avgDelay, key);
        checkMax(type.charAt(0), avgDelay, key);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException{
        //Writing Min Values
        context.write(minHour, new IntWritable(minHourDelay));
        context.write(minDay, new IntWritable(minDayDelay));
        context.write(minMonth, new IntWritable(minMonthDelay));

        //Writing Max
        context.write(maxHour, new IntWritable(maxHourDelay));
        context.write(maxDay, new IntWritable(maxDayDelay));
        context.write(maxMonth, new IntWritable(maxMonthDelay));
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