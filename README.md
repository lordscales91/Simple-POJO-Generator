**Simple POJO Generator**
=====================

This is a simple program that uses JDBC to generate simple POJOs. That means, POJOs using just simple types, dates and timestamps are treated as *Strings*, actually it only generates the private fields with no constructors neither *getters* or *setters*, but if you are using a good IDE like [Eclipse](http://www.eclipse.org/) surely it provides some auto-generate tools for that purposes.

**Note:** You can find the JavaDoc documentation at http://lordscales91.github.io/Simple-POJO-Generator/

**Usage**
-------

-host <em>database_url(JDBC Conn String)</em> [-u <em>user</em>] [-p
	  <em>passwd</em>] [-drv <em>jdbc.driver</em>][-tables
	  <em>table1,table2,...</em>]<br/>
	  You must specify the host, it must be a valid [JDBC Connection string](http://www.java2s.com/Tutorial/Java/0340__Database/AListofJDBCDriversconnectionstringdrivername.htm).<br/>
	  User defaults to "root", password defaults to an empty string.<br/>
	  This is designed to work with MySQL, but you can use it for other database systems, in order to do it you must specify the full Class name of the Driver (and it must be loaded  in the classpath).<br/>
	  If you don't want to generate POJOs for all the tables of the Schema specified you can 
	  provide a comma-separated list of table names(no spaces).
