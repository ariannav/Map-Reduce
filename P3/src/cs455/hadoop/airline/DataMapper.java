//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.NullWritable;
import java.io.IOException;

public class DataMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

    //Line by line parses the file and grabs the appropriate values. Outputs (departure time, delay).
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Split the line by separating with each comma.
        String[] values = value.toString().split(",");

        NullWritable empty = NullWritable.get(); 
        Text condensedData = new Text();

        //Determining the dataset.
        try{
            //Main dataset.
            if(values.length == 29){
                if(values[16].equals("Origin")){
                    //First line in a File, do not want to include.
                    return;
                }
                //Output only necessary data.
                //Year, month, day of week, hour, tail num, arrival delay, origin, dest, weather delay, carrier code.
                condensedData.set(values[0] + "," + values[1] + "," + values[3] + "," +
                                  values[5] + "," + values[10] + "," + values[14] + "," +
                                  values[16] + "," + values[17] + "," + values[25] + "," +
                                  values[8]);
            }
        }
        catch(Exception e){
            //This was the first line of the file. Pass it.
            return;
        }
        //Write the key: departure, and the value: delay.
        context.write(condensedData, empty);
    }
}