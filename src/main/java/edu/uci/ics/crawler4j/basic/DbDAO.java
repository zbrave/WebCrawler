package edu.uci.ics.crawler4j.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class DbDAO {
	
	public void addDB(Product p) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
    	Connection conn = null;
    	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler","root", "password");
    	// create a Statement from the connection
    	Statement statement = conn.createStatement();

    	// insert the data
    	PreparedStatement Statement2 = conn.prepareStatement("Select * from datas where link='"+p.getLink()+"'");
    	ResultSet result = Statement2.executeQuery();
    	if (!result.next()) {
    		statement.executeUpdate("INSERT INTO datas " + "VALUES (null,'"+p.getLink()+"','"+p.getName()+"','"+p.getImgUrl()+"',"+p.getPrice()+",'"+p.getResource()+"','"+p.getBrand()+"',null,null,null)");
    	}
    	else {
    		statement.executeUpdate("UPDATE datas SET name='"+p.getName()+"',image='"+p.getImgUrl()+"',price="+p.getPrice()+",resource='"+p.getResource()+"',brand='"+p.getBrand()+"' where link='"+p.getLink()+"'");
    	}
    	System.out.println("DB ye eklendi"+p.getBrand());
    	conn.close();
	}
	
	public void addDBout(String s1, String s2) throws ClassNotFoundException, SQLException{
		if (!s2.contains(".html")) {
			System.out.println("Not product page exiting..");
		}
		else {
			Class.forName("com.mysql.jdbc.Driver");
	    	Connection conn = null;
	    	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler","root", "password");
	    	// create a Statement from the connection
	    	Statement statement = conn.createStatement();
	
	    	// insert the data
	    	PreparedStatement Statement2 = conn.prepareStatement("Select * from outs where link='"+s1+"' AND outlink='"+s2+"'");
	    	ResultSet result = Statement2.executeQuery();
	    	if (!result.next()) {
	    		statement.executeUpdate("INSERT INTO outs " + "VALUES (null,'"+s1+"','"+s2+"')");
	    	}
	    	else {
	    		System.out.println("Already added");
	    	}
	    	conn.close();
		}
	}
	
	public int getSize() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
    	Connection conn = null;
    	int size = 0;
    	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler","root", "password");
    	// create a Statement from the connection

    	// insert the data
    	PreparedStatement Statement2 = conn.prepareStatement("Select COUNT(*) size from datas ");
    	ResultSet result = Statement2.executeQuery();
    	while(result.next()){
    		size = result.getInt("size");
    	}
    	conn.close();
    	return size;
	}
	
//	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		DbDAO d = new DbDAO();
//		Product p = new Product();
//		p.setImgUrl("qwe");
//		p.setName("mer2t");
//		p.setLink("sa2");
//		p.setPrice((float) 12.2);
//		p.setResource("VATAN");
//		d.addDB(p);
//		boolean a = d.check("http://www.vatanbilgisayar.com/samsung-a720-galaxy-a7-akilli-telefon-siyah.html", "http://www.vatanbilgisayar.com/samsung-a720-galaxy-a7-akilli-telefon-siyah.html");
//		System.out.println("check:"+a);
//		d.hits();
//	}
	
	public void hits() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
    	Connection conn = null;
    	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler","root", "password");
    	// create a Statement from the connection
    	Statement statement = conn.createStatement();
    	int size = getSize();
    	List<String> links = new ArrayList<String>();
    	// insert the data
    	PreparedStatement Statement2 = conn.prepareStatement("Select * from datas");
    	ResultSet res = Statement2.executeQuery();
    	while (res.next()){
			links.add(res.getString("link"));
		}
    	double[][] array = new double[size][size];
    	double[] u = new double[size];
    	double[] v = new double[size];
    	double[] authority = new double[size];
    	double[] hubs = new double[size];
    	double[] oldAuthority = new double[size];
    	double[] oldHubs = new double[size];
    	for (int i=0; i<size; i++){
    		for (int j=0; j<size; j++) {
    			String row = links.get(i);
    			String col = links.get(j);
    			if (check(row, col)) {
    				array[i][j] = 1;
    			}
    			else {
    				array[i][j] = 0;
    			}
    		}
    	}
    	for (int i=0; i<size; i++){
    		u[i]=1;
    	}
    	array = transposeMatrix(array);//transpose
    	v = mul(array, u);//aut
    	array = transposeMatrix(array);//original
    	u = mul(array, v);//hubs
    	for (int i=0; i<size; i++){
    		for (int j=0; j<size; j++) {
    			System.out.print(array[i][j]+" ");
    		}
    		System.out.println("");
    	}
    	System.out.println("--------");
    	System.out.println("\nU");
    	for (int i=0; i<size; i++){
    		System.out.print(u[i]+" ");
    	}
    	System.out.println("\nV");
    	for (int i=0; i<size; i++){
    		System.out.print(v[i]+" ");
    	}
    	for (int i=0; i<size; i++){
			hubs[i] = u[i];
			authority[i] = v[i];
    	}
    	double div1=0,div2=0;
    	for (int i=0; i<size; i++){
    		div1 += v[i]*v[i];
    		div2 += u[i]*u[i];
    	}
    	for (int i=0; i<size; i++){
    		v[i] = v[i]/Math.sqrt(div1);
    		u[i] = u[i]/Math.sqrt(div2);
    	}
    	System.out.println("\nU");
    	for (int i=0; i<size; i++){
    		System.out.print(u[i]+" ");
    	}
    	System.out.println("\nV");
    	for (int i=0; i<size; i++){
    		System.out.print(v[i]+" ");
    	}
    	for (int i=0; i<size; i++){
    		for (int j=0; j<size; j++) {
    			System.out.print(array[i][j]+" ");
    		}
    		System.out.println("");
    	}
    	System.out.println("--------");
    	for (int i=1; i<=size; i++){
    		double s = u[i-1]+v[i-1]; 
    		String s1 = "UPDATE datas SET auth="+u[i-1]+", hubs="+v[i-1]+", score="+s+" Where id="+i;
    		System.out.println(s1);
    		statement.executeUpdate(s1);
    	}
    	conn.close();
	}
	
	public static double[][] transposeMatrix(double [][] m){
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }
	
	public static double[] mul(double [][] m, double [] n){
        double[] temp = new double[n.length];
        for (int i = 0; i < n.length; i++) {
        	double total = 0;
            for (int j = 0; j < m[0].length; j++) {
                total += m[i][j]*n[j];
            }
            temp[i] = total;
        }
        return temp;
    }
	
	public boolean check(String s1, String s2) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
    	Connection conn = null;
    	boolean res;
    	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler","root", "password");
    	// create a Statement from the connection
    	Statement statement = conn.createStatement();

    	// insert the data
    	PreparedStatement Statement2 = conn.prepareStatement("Select * from outs where link='"+s1+"' AND outlink='"+s2+"'");
    	ResultSet result = Statement2.executeQuery();
    	if (!result.next()) {
    		res = false;
    	}
    	else {
    		res = true;
    	}
    	conn.close();
    	
    	return res;
	}
	
}
