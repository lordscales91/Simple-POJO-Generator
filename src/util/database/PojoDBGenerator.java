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

import extra.utils.StringUtil;
import model.PojoObject;

public class PojoDBGenerator {

	/**
	 * usage: -host <em>database_url(JDBC Conn String)</em> [-u <em>user</em>] [-p
	 * <em>passwd</em>] [-drv <em>jdbc.driver</em>][-tables
	 * <em>table1,table2,...</em>]<p>
	 * You must specify the host, it must be a valid <a href="http://www.java2s.com/Tutorial/Java/0340__Database/AListofJDBCDriversconnectionstringdrivername.htm">
	 * JDBC Connection string</a>.<p>
	 * User defaults to "root", password defaults to an empty string.
	 * This is designed to work with MySQL, but you can use it for other database systems, 
	 * in order to do it you must specify the full Class name of the Driver (and it must be loaded 
	 * in the classpath).<p>
	 * If you don't want to generate POJOs for all the tables of the Schema specified you can 
	 * provide a comma-separated list of table names(no spaces).
	 */
	public static void main(String[] args) {
		if (args.length >= 2 && args.length % 2 == 0) {
			String driver = "com.mysql.jdbc.Driver";
			String host = null;
			String user = "root";
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
			if (host != null) {
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
	/**
	 * Provided a Connection object and an optional array of tables it will generate
	 * the POJOs for those tables or all the tables in the DB if null
	 * @param con JDBC Connection object
	 * @param tables list of tables, can be null
	 */
	public static void generate(Connection con, String[] tables) {
		List<String> tableList=null;
		if(tables!=null) {
			tableList=Arrays.asList(tables);
		}
		generate(con, tableList);
	}
	/**
	 * Provided a Connection object and an optional List of tables it will generate
	 * the POJOs for those tables or all the tables in the DB if null
	 * @param con JDBC Connection object
	 * @param tables list of tables, can be null
	 */
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
			String packageName="pojos";//This may be introduced as a parameter in the future
			File pojoDir=new File("pojos");	
			List<PojoObject> pojos=new ArrayList<>();
			for (String table : tables) {
				String pojoName = StringUtil.toCamelCase(table);
				PojoObject pojo=new PojoObject(packageName, pojoName);
				System.out.println("Creating POJO for "+table+"...");
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
					} else if (type.contains("[B")) {//Fix conversion of binary types
						type="byte[]";			
					} else if (!clsName.contains("java.lang.")) {//if it's not in the java.lang pkg 
						pojo.addImport(clsName);			//we need to import it
					} else if (!type.equals("String")) {
						type=type.toLowerCase();
					}
					String fieldName = rsmeta.getColumnLabel(i);
					System.out.println("Adding field for "+fieldName);
					pojo.addField(type, fieldName);					
				}
				pojos.add(pojo);
				System.out.println("************************************");
			}
			writePojos(pojoDir, pojos);
			System.out.println("FINISHED");
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Write the Pojo to the file
	 * @param pojoDir the directory to save the files
	 * @param pojos list of PojoObject to be written
	 */
	public static void writePojos(File pojoDir, List<PojoObject> pojos)
			throws IOException {

		if(!pojoDir.exists()) {
			pojoDir.mkdir();
			System.out.println("pojos dir created");
		}
		System.out.println(pojoDir.getAbsolutePath());
		for(PojoObject p:pojos) {
			String pojoName=p.getPojoName();
			File pojo=new File(pojoDir, pojoName+".java");
			FileWriter fw=new FileWriter(pojo);
			System.out.println(fw.toString());
			BufferedWriter bw=new BufferedWriter(fw);		
			System.out.println("Writing POJO "+pojo.getName()+" ...");
			bw.write("package pojos;");
			bw.newLine();
			bw.newLine();
			List<String> imports = p.getImports();
			for (String imp : imports) {
				bw.write("import "+imp+";");
				bw.newLine();
			}
			bw.newLine();
			bw.write("public class "+pojoName+" {");
			bw.newLine();
			bw.newLine();
			
			List<String> fields = p.getFields();				
			for (String field : fields) {
				String type=p.getTypeOfField(field);
				bw.write("	private "+type+" "+field+";");
				bw.newLine();					
			}

			for (String field : fields) {
				String type=p.getTypeOfField(field);
				bw.newLine();
				bw.write("	public "+type+" "+"get" + field.substring(0, 1).toUpperCase() + field.substring(1) +"(){");
				bw.newLine();
				bw.write("		return " + field + ";");
				bw.newLine();
				bw.write("	}");
				bw.newLine();
				bw.newLine();

				bw.write("	public void set" + field.substring(0, 1).toUpperCase() + field.substring(1) +"(" + type + " " + field + "){");
				bw.newLine();
				bw.write("		this." + field + " = " + field + ";");
				bw.newLine();
				bw.write("	}");
			}
			bw.newLine();
			bw.write("}");
			bw.flush();
			bw.close();
			fw.close();

		}
	}

}
