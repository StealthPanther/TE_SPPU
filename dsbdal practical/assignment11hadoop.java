
//assignment 11 hadoop
import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

    public static class M extends Mapper<Object, Text, Text, IntWritable> {
        public void map(Object k, Text v, Context c) throws IOException, InterruptedException {
            StringTokenizer t = new StringTokenizer(v.toString());
            while (t.hasMoreTokens())
                c.write(new Text(t.nextToken()), new IntWritable(1));
        }
    }

    public static class R extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text k, Iterable<IntWritable> v, Context c)
                throws IOException, InterruptedException {
            int s = 0;
            for (IntWritable i : v) s += i.get();
            c.write(k, new IntWritable(s));
        }
    }

    public static void main(String[] a) throws Exception {
        Job j = Job.getInstance(new Configuration(), "wordcount");
        j.setJarByClass(WordCount.class);
        j.setMapperClass(M.class);
        j.setReducerClass(R.class);
        j.setOutputKeyClass(Text.class);
        j.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(j, new Path(a[0]));
        FileOutputFormat.setOutputPath(j, new Path(a[1]));
        System.exit(j.waitForCompletion(true) ? 0 : 1);
    }
}


// javac -classpath `hadoop classpath` WordCount.java
// jar cf wordcount.jar WordCount*.class
// hadoop jar wordcount.jar WordCount input.txt output


// javac -classpath `hadoop classpath` WordCount.java
// jar cf wordcount.jar WordCount*.class
// hdfs dfs -mkdir input
// hdfs dfs -put input.txt input
// hadoop jar wordcount.jar WordCount input output
// hdfs dfs -cat output/part-r-00000