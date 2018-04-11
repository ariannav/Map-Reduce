//Author: Arianna Vacca

package cs455.hadoop.airline;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class AirportMapper extends Mapper<LongWritable, Text, Text, Text> {

    //Parses the airports.csv file, as well as the Q6 output. Takes place in both jobs.
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] values = value.toString().split(",");

        if(values.length == 1){
            //From Q6 output. Comes in as "city\tcount". Split by tab, and output count, city.
            values = value.toString().split("\t");
            context.write(new Text("w:" + values[1]), new Text(values[0]));
        }
        else if(values[0].equals("iata")){
            //First line of airports.csv, skip it.
            return;
        }
        else{
            //First job, mapping output goes to Q6 and Q7.
            //Outputs: airport abbreviation code, city.
            String city = "";

            try{
                city = values[2];
            }
            catch(Exception e){
                //Improperly formatted line, skip it.
                return;
            }

            //Q6
            context.write(new Text("w:" + values[0].substring(1, values[0].length()-1)), new Text(city));
            //Q7
            context.write(new Text("s:" + values[0].substring(1, values[0].length()-1)), new Text(city));
        }
    }
}