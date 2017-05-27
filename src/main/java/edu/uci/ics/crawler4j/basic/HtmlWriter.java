package edu.uci.ics.crawler4j.basic;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class HtmlWriter {
	
	FileWriter fWriter = null;
    BufferedWriter writer = null;
    
	public void open() {
	    try {
	        fWriter = new FileWriter("output.html");
	        writer = new BufferedWriter(fWriter);
	        writer.write("<!DOCTYPE html><html><head><title>Output</head><body>");
	        writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
//	        writer.close(); //make sure you close the writer object 
	    } catch (Exception e) {
	      //catch any exceptions here
	    }
	}
	public void add(String text) {
	    try {
	        fWriter = new FileWriter("output.html");
	        writer = new BufferedWriter(fWriter);
	        writer.write(text);
	        writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
//	        writer.close(); //make sure you close the writer object 
	    } catch (Exception e) {
	      //catch any exceptions here
	    }
	}
	public void close() {
	    try {
	        fWriter = new FileWriter("output.html");
	        writer = new BufferedWriter(fWriter);
//	        writer.write("<!DOCTYPE html><html><head><title>Output</head><body>");
//	        writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
	        writer.close(); //make sure you close the writer object 
	    } catch (Exception e) {
	      //catch any exceptions here
	    }
	}
}
