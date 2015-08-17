**Simple POJO Generator**
=====================

You can generate the POJOs from the Excel ".xlsx" File and from database. The title of the first line is considered as the class attribute and the type of the attribute is determined by the value under of each column
 
Merged the commits from [chanlen's fork](https://github.com/chanllen/Simple-POJO-Generator)

**Usage**
-------

## Generate from Excel ".xlsx" File
-gen xlsx -i <em>the input ".xlsx" file</em> [-o <em>output directory</em>]

e.g. java -jar Simple-POJO-Generator.jar -gen xlsx -i /home/chanllen/sample.xlsx -o /home/chanllen/tmp


## Generate from Database
-gen db -host <em>database_url(JDBC Conn String)</em> [-u <em>user</em>] [-p
	  <em>passwd</em>] [-drv <em>jdbc.driver</em>][-tables
	  <em>table1,table2,...</em>]<br/>
	  You must specify the host, it must be a valid [JDBC Connection string](http://www.java2s.com/Tutorial/Java/0340__Database/AListofJDBCDriversconnectionstringdrivername.htm).<br/>
	  User defaults to "root", password defaults to an empty string.<br/>
	  This is designed to work with MySQL, but you can use it for other database systems, in order to do it you must specify the full Class name of the Driver (and it must be loaded  in the classpath).<br/>
	  If you don't want to generate POJOs for all the tables of the Schema specified you can 
	  provide a comma-separated list of table names(no spaces).
