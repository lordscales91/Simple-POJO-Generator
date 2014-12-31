package util.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PojoGenerator {

	/**
	 * usage: -host <em>database_url(JDBC Conn String)</em> -u <em>user</em> [-p
	 * <em>passwd</em>] [-drv <em>jdbc.driver</em>][-tables
	 * <em>table1,table2,...</em>]<br/><br/>
	 * You must specify the host, it must be a valid JDBC Connection string.<br/>
	 * You must also specify the user, password defaults to an empty string.<br/>
	 * This is designed to work with MySQL, but you can use it for other database systems, 
	 * in order to do it you must specify the full Class name of the Driver (and it must be in the 
	 * classpath).<br/>
	 * If you don't want to generate POJOs for all the tables of the Schema specified you can 
	 * provide a comma-separated list of table names.
	 */
	public static void main(String[] args) {
		if (args.length >= 4 && args.length % 2 == 0) {
			String driver = "com.mysql.jdbc.Driver";
			String host = null;
			String user = null;
			String passwd = "";
			String[] tables = null;
			for (int i = 0; i < args.length; i += 2) {
				if (args[i].startsWith("-")) {
					String cmd = args[i];
					String param = args[i + 1];
					if (cmd.equals("-host")) {
						host = param;
					} else if (cmd.equals("-u")) {
						user = param;
					} else if (cmd.equals("-p")) {
						passwd = param;
					} else if (cmd.equals("-drv")) {
						driver = param;
					} else if (cmd.equals("-tables")) {
						tables = (param.contains(",")) ? param.split(",")
								: new String[]{param};
					}
				}
			}
			if (host != null && user != null) {
				try {
					Class.forName(driver);
					Connection con = DriverManager.getConnection(host, user,
							passwd);
					generate(con, tables);
				} catch (ClassNotFoundException e) {
					System.err.println("The Driver " + driver
							+ " is not in the classpath");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				// invalid arguments
			}

		} else {
			// Invalid arguments
		}

	}

	public static void generate(Connection con, String[] tables) {
		List<String> tableList=null;
		if(tables!=null) {
			tableList=Arrays.asList(tables);
		}
		generate(con, tableList);
	}

	public static void generate(Connection con, List<String> tables) {
		try {
			
			if (tables == null) {
				// obtain the tables
				tables=new ArrayList<>();
				DatabaseMetaData dbmeta = con.getMetaData();
				ResultSet rs = dbmeta.getTables(con.getCatalog(), null, "%", null);
				while(rs.next()) {
					if(rs.getString("TABLE_TYPE").equals("TABLE")) {
						//Normally we want to create POJOs only for 
						//Standard Tables (No Views, Temporary Tables...)
						tables.add(rs.getString("TABLE_NAME"));						
					}					
				}
			}
			File pojoDir=new File("pojos");
			if(!pojoDir.exists()) {
				pojoDir.mkdir();
				System.out.println("pojos dir created");
			}
			for (String table : tables) {
				String pojoName = toCamelCase(table);
				File pojo=new File(pojoDir, pojoName+".java");
				FileWriter fw=new FileWriter(pojo);
				BufferedWriter bw=new BufferedWriter(fw);
				System.out.println("Creating POJO for "+table+"...");
				bw.write("package pojos;");
				bw.newLine();
				bw.newLine();
				bw.write("public class "+pojoName+" {");
				bw.newLine();
				bw.newLine();
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("SELECT * FROM "+table);
				ResultSetMetaData rsmeta = rs.getMetaData();
				for(int i=1;i<=rsmeta.getColumnCount();i++) {
					String clsName=rsmeta.getColumnClassName(i);
					String type=clsName.substring(clsName.lastIndexOf(".")+1);
					if(type.contains("Date") || type.contains("Time")) {
						type= "String";
					}else if(type.equalsIgnoreCase("integer")) {
						type="int";
					}  else if (type.equals("BigInteger")) {
						type=clsName;//use full class name since we can't move back and import it
					} else if (!type.equals("String")) {
						type=type.toLowerCase();
					}
					System.out.println("Adding field for "+rsmeta.getColumnLabel(i));
					bw.write("private "+type+" "+rsmeta.getColumnLabel(i)+";");
					bw.newLine();
				}
				bw.newLine();
				bw.write("}");
				bw.close();
				fw.close();	
				System.out.println("************************************");
			}
			System.out.println("FINISHED");
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String toCamelCase(String str) {
		String result=str.substring(0, 1).toUpperCase();
		if(str.contains("_")) {
			String[] aux=str.split("_");
			result+=aux[0].substring(1).toLowerCase();
			for(int i=1;i<aux.length;i++) {
				result+=aux[i].substring(0, 1).toUpperCase();
				result+=aux[i].substring(1).toLowerCase();
			}
		} else {
			result+=str.substring(1);
		}
		return result;
	}

}
