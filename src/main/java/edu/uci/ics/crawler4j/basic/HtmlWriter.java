package edu.uci.ics.crawler4j.basic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.Locale;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 
import org.json.simple.parser.ParseException;

public class HtmlWriter {
	
	FileWriter fWriter = null;
    BufferedWriter writer = null;
    
	public void open() {
	    try {
	        fWriter = new FileWriter("output.html");
	        writer = new BufferedWriter(fWriter);
	        writer.write("<!DOCTYPE html><html><head><title>Output</title><link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" rel=\"stylesheet\"><script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script></head><body><div class=\"container\"><div class=\"row\"><div class=\"col-md-12\">");
//	        writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
	        writer.close(); //make sure you close the writer object 
	    } catch (Exception e) {
	      //catch any exceptions here
	    }
	}
	public void add(String text) {
	    try {
	        fWriter = new FileWriter("output.html", true);
	        writer = new BufferedWriter(fWriter);
	        writer.write(text);
	        writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
	        writer.close(); //make sure you close the writer object 
	    } catch (Exception e) {
	      //catch any exceptions here
	    }
	}
	public void addForVatan(String text, String link) throws ParseException {
	    try {
	        fWriter = new FileWriter("output.html", true);
	        writer = new BufferedWriter(fWriter);
//	        String jsonString = "[{\"name\":\"Swati\",\"rollNo\":" + "\"MCA/07/01\",\"id\":10},{\"name\":\"Prabhjot\",\"" + "rollNo\":\"MCA/07/39\",\"id\":50}]";   
	        String text2 = "["+text+"]";
//	        System.out.println(jsonString);
//	        System.out.println("["+text+"]");
	        // Create JSON parser object. 
	        JSONParser parser = new JSONParser(); 
	        try { 
	        	// Parse JSON string using JSON parser. 
	        	Object object = parser.parse(text2); 
	        	JSONArray array = (JSONArray) object; 
	        	JSONObject jsonObject = (JSONObject) array.get(0); 
	        	Product p = new Product();
	        	p.setName((String) jsonObject.get("name"));
	        	System.out.println("Name: "+(String) jsonObject.get("name"));
	        	p.setImgUrl((String) jsonObject.get("image"));
	        	System.out.println("URL: "+(String) jsonObject.get("image"));
	        	JSONObject childObj = (JSONObject) jsonObject.get("offers");
//	        	System.out.println("Price1: "+	Integer.parseInt((String) childObj.get("price")));
//	        	System.out.println("Price2: "+ NumberFormat.getNumberInstance(Locale.FRANCE).parse((String) childObj.get("price")));
	        	NumberFormat format = NumberFormat.getNumberInstance(Locale.FRANCE);
	        	Number number = 0;
	        	number = format.parse((String) childObj.get("price"));
	        	Float f = number.floatValue();
	        	p.setPrice(f); 
	        	System.out.println(p.getName()+p.getImgUrl()+p.getPrice());
	        	writer.write("<div class=\"col-sm-6 col-md-4\"><div class=\"thumbnail\" ><h4 class=\"text-center\"><span class=\"label label-info\">");
	        	writer.write((String) jsonObject.get("brand"));
	        	writer.write("</span></h4><img src=\""+p.getImgUrl()+"\" class=\"img-responsive\"><div class=\"caption\"><div class=\"row\"><div class=\"col-md-6 col-xs-6\"><h3>");
	        	writer.write(p.getName());
	        	writer.write("</h3></div><div class=\"col-md-6 col-xs-6 price\"><h3><label>");
	        	writer.write(p.getPrice().toString()+" "+(String) childObj.get("priceCurrency"));
	        	writer.write("</label></h3></div></div><p></p><div class=\"row\"><div class=\"col-md-6\"><a href=\""+link+"\" class=\"btn btn-success btn-product\"><span class=\"glyphicon glyphicon-shopping-cart\"></span> Satın al</a></div></div><p> </p></div></div></div>");
	        	p.setResource("Vatan Bilgisayar");
	        	p.setLink(link);
	        	p.setBrand((String) jsonObject.get("brand"));
	        	DbDAO d = new DbDAO();
	        	d.addDB(p);
	        } 
	        catch (ParseException e) { 
	        	e.printStackTrace(); 
	        } 
//	        writer.write(text);
//	        writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
	        writer.close(); //make sure you close the writer object 
	    } catch (Exception e) {
	      //catch any exceptions here
	    }
	}
	
	public void addForTeknosa(String text, String link) throws ParseException {
	    try {
	        fWriter = new FileWriter("output.html", true);
	        writer = new BufferedWriter(fWriter);
//	        String jsonString = "[{\"name\":\"Swati\",\"rollNo\":" + "\"MCA/07/01\",\"id\":10},{\"name\":\"Prabhjot\",\"" + "rollNo\":\"MCA/07/39\",\"id\":50}]";   
	        
//	        System.out.println(jsonString);
//	        System.out.println(text);
	        int ind1 = 0,ind2 = 0;
	        for (int i=0; i < text.length(); i++) {
	        	char l = text.charAt(i);
	        	if (l == '{'){
	        		System.out.println("ilk"+i);
	        		ind1=i;
	        		break;
	        	}
	        }
	        for (int i=0; i < text.length(); i++) {
	        	char l = text.charAt(i);
	        	if (l == '!' && ind2 < ind1){
	        		System.out.println("son"+i);
	        		ind2=i-18;
	        	}
	        }
	        System.out.println("bitti");
	        text = text.substring(ind1, ind2);
	        String text2 = "["+text+"]";
	        System.out.println(text2);
	        // Create JSON parser object. 
	        JSONParser parser = new JSONParser(); 
	        try { 
	        	// Parse JSON string using JSON parser. 
	        	Object object = parser.parse(text2); 
	        	JSONArray array = (JSONArray) object; 
	        	JSONObject jsonObject = (JSONObject) array.get(0); 
	        	Product p = new Product();
	        	p.setName((String) jsonObject.get("name"));
	        	System.out.println("Name: "+(String) jsonObject.get("name"));
	        	p.setImgUrl((String) jsonObject.get("image"));
	        	System.out.println("URL: "+(String) jsonObject.get("image"));
	        	JSONObject childObj = (JSONObject) jsonObject.get("offers");
	        	JSONObject childObj2 = (JSONObject) jsonObject.get("brand");
	        	p.setBrand((String) childObj2.get("name"));
//	        	System.out.println("Price1: "+	Integer.parseInt((String) childObj.get("price")));
//	        	System.out.println("Price2: "+ NumberFormat.getNumberInstance(Locale.FRANCE).parse((String) childObj.get("price")));
	        	NumberFormat format = NumberFormat.getNumberInstance(Locale.FRANCE);
	        	Number number = 0;
	        	number = format.parse((String) childObj.get("price"));
	        	Float f = number.floatValue();
	        	p.setPrice(f); 
	        	System.out.println(p.getName()+p.getImgUrl()+p.getPrice());
//	        	writer.write("<div class=\"col-sm-6 col-md-4\"><div class=\"thumbnail\" ><h4 class=\"text-center\"><span class=\"label label-info\">");
//	        	writer.write((String) jsonObject.get("brand"));
//	        	writer.write("</span></h4><img src=\""+p.getImgUrl()+"\" class=\"img-responsive\"><div class=\"caption\"><div class=\"row\"><div class=\"col-md-6 col-xs-6\"><h3>");
//	        	writer.write(p.getName());
//	        	writer.write("</h3></div><div class=\"col-md-6 col-xs-6 price\"><h3><label>");
//	        	writer.write(p.getPrice().toString()+" "+(String) childObj.get("priceCurrency"));
//	        	writer.write("</label></h3></div></div><p></p><div class=\"row\"><div class=\"col-md-6\"><a href=\""+link+"\" class=\"btn btn-success btn-product\"><span class=\"glyphicon glyphicon-shopping-cart\"></span> Satın al</a></div></div><p> </p></div></div></div>");
	        	p.setResource("İstanbul Bilişim");
	        	p.setLink(link);
	        	DbDAO d = new DbDAO();
	        	d.addDB(p);
	        } 
	        catch (ParseException e) { 
	        	e.printStackTrace(); 
	        } 
//	        writer.write(text);
//	        writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
	        writer.close(); //make sure you close the writer object 
	    } catch (Exception e) {
	      //catch any exceptions here
	    }
	}
	
	public void addForMediaMarkt(String text, String link) throws ParseException {
	    
	}
	
	public void close() {
	    try {
	        fWriter = new FileWriter("output.html", true);
	        writer = new BufferedWriter(fWriter);
	        writer.write("</div></div></div></body></html>");
//	        writer.write("<!DOCTYPE html><html><head><title>Output</head><body>");
//	        writer.newLine(); //this is not actually needed for html files - can make your code more readable though 
	        writer.close(); //make sure you close the writer object 
	    } catch (Exception e) {
	      //catch any exceptions here
	    }
	}
}
