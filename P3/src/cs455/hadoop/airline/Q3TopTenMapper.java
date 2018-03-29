//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class Q3TopTenMapper extends Mapper<LongWritable, Text, IntWritable, Text> {

        //Grabs the output of the previous job, uses the reducer to find the top ten.
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //Split the line by separating with each comma.
            String[] values = value.toString().split("\t");
            IntWritable traffic = new IntWritable();

            try{
                traffic.set(Integer.parseInt(values[1]));
            }
            catch(Exception e){
                System.out.println("Formatting error.======================= " + values[1]); 
            }

            //Write the key: Traffic, airport name.
            context.write(traffic, new Text(values[0]));
        }

}