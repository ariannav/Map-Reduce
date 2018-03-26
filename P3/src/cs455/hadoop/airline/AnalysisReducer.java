//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class AnalysisReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        int sum = 0;
        // calculate the total count
        for(IntWritable val : values){
            count++;
            sum += val.get();
        }

        int avgDelay = sum/count;
        context.write(key, new IntWritable(avgDelay));
    }
}