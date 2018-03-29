//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class Q3AnalysisReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;

        // calculate the total count
        for(IntWritable val : values){
            count+= val.get();
        }

        context.write(key, new IntWritable(count));
    }
}