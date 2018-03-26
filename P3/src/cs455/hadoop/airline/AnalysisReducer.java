//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class AnalysisReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    Text minHour = new Text();
    Text minDay = new Text();
    Text minMonth = new Text();
    int minHourDelay = Integer.MAX_VALUE;
    int minDayDelay = Integer.MAX_VALUE;
    int minMonthDelay = Integer.MAX_VALUE;

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

        switch(type.charAt(0)){
            case 'h': //departHour
                if(avgDelay < minHourDelay){
                    minHourDelay = avgDelay;
                    minHour.set(key);
                }
                break;
            case 'd': //departDay
                if(avgDelay < minDayDelay){
                    minDayDelay = avgDelay;
                    minDay.set(key);
                }
                break;
            case 'm': //departMonth
                if(avgDelay < minMonthDelay){
                    minMonthDelay = avgDelay;
                    minMonth.set(key);
                }
                break;
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException{
        context.write(minHour, new IntWritable(minHourDelay));
        context.write(minDay, new IntWritable(minDayDelay));
        context.write(minMonth, new IntWritable(minMonthDelay));
    }
}