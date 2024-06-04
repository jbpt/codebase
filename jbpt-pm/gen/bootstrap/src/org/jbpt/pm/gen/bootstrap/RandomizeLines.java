package org.jbpt.pm.gen.bootstrap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomizeLines {
   public static void main(String[] args) throws Exception {
	   System.out.println("START READING");
      List<String> fileList = new ArrayList<String>();
      BufferedReader reader = new BufferedReader(new FileReader("./inputs_o.csv"));
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
         fileList.add(line);
      }
      reader.close();
      
      System.out.println(fileList.size());
      System.out.println("DONE READING");
      System.out.println("START SHUFFLE");
      
      Collections.shuffle(fileList);
      
      System.out.println(fileList.size());
      
      System.out.println("DONE SHUFFLE");
      System.out.println("START WRITE");
      
      for (String line : fileList) {
    	  appendToFile("./inputs_r.csv", line);
      }
      
      System.out.println("DONE WRITE");
   }
   
   private static void appendToFile(String file, String content) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		out.println(content);
		out.close();
	}
}
