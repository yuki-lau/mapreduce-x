package process;

import java.io.IOException;

import model.HTTPServerLog;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class CalculatePV {

	public static class PVMapper extends Mapper<Object, Text, Text, IntWritable>{

		private static final IntWritable one = new IntWritable(1);
		private Text requestUrl = new Text();
		
		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
			HTTPServerLog log = HTTPServerLog.parse(value.toString());
			if(log.isValid()){
				requestUrl.set(log.getRequestUrl());
				context.write(requestUrl, one);
			}
		}
	}
	
	public static class PVReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		
		private IntWritable result = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			
			int sum = 0;
			for(IntWritable i: values){
				sum += i.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception{
		
		Configuration conf = new Configuration();
		
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if(otherArgs.length != 2){
			System.err.println("Usage: calculatepv <in> <out>");
			System.exit(2);
		}
		
		// set attributes of job
		Job job = new Job(conf, "calculate pv");
		job.setJarByClass(CalculatePV.class);
		job.setMapperClass(PVMapper.class);
		job.setReducerClass(PVReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 :1);
	}
}
