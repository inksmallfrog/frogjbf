package com.inksmallfrog.frogjbf.config;

import com.inksmallfrog.frogjbf.exception.UnsupportDataSourceException;

/**
 * This class defined the structure of the data-source config
 *
 * support: ORACLE
 *
 */
public class DataSourceConfig {
	private String name;				//data-source name
	private String db;					//data-source db
	private String dbName;				//data-source dbName
	private String driver;				//data-source driver
	private String url;					//data-source url
	private String host;				//data-source host
	private int port;				//data-source port
	private String user;				//data-source username
	private String password;			//data-source password
	private int maxConnetionCount;		//max connection limited
	private long timeout;				//timeout

	//Constructor
	public DataSourceConfig(String name, String db, String dbName,
			String host, int port, String user, String password,
			int maxConnetionCount, long timeout) throws UnsupportDataSourceException {
		super();
		this.name = name;
		this.db = db.toLowerCase();
		this.host = host;
		this.port = (-1 == port) ? getDefaultPortByDB(this.db) : port;
		this.dbName = dbName;
		this.user = user;
		this.password = password;
		this.maxConnetionCount = maxConnetionCount;
		this.timeout = timeout;
		this.driver = getDriverByDB(this.db);
		this.url = generateUrlByDB(this.db);
	}

	//Setters && Getters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getMaxConnetionCount() {
		return maxConnetionCount;
	}
	public void setMaxConnetionCount(int maxConnetionCount) {
		this.maxConnetionCount = maxConnetionCount;
	}
	public void setTimeout(int timeout){
		this.timeout = timeout;
	}
	public long getTimeout(){
		return timeout;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	private int getDefaultPortByDB(String db) throws UnsupportDataSourceException {
		switch (db) {
			case "oracle":
				return 1521;
			case "mysql":
				return 3306;
			case "db2":
				return 50000;
			case "sybase":
				return 5007;
			case "postgresql":
				return -1;
			case "sql server2000":
				return 1433;
			case "sql server":
				return 1433;
			default:
				throw new UnsupportDataSourceException(db);
		}
	}

	private String getDriverByDB(String db) throws UnsupportDataSourceException {
		switch (db) {
			case "oracle":
				return "oracle.jdbc.driver.OracleDriver";
			case "mysql":
				return "com.mysql.jdbc.Driver";
			case "db2":
				return "com.ibm.db2.jcc.DB2Driver";
			case "sybase":
				return "com.sybase.jdbc.SybDriver";
			case "postgresql":
				return "org.postgresql.Driver";
			case "sql server2000":
				return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
			case "sql server":
				return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			default:
				throw new UnsupportDataSourceException(db);
		}
	}

	private String generateUrlByDB(String db) throws UnsupportDataSourceException {
		switch (db) {
			case "oracle":
				return "jdbc:oracle:thin:@" + this.host + ":" + this.port + ":" + this.dbName;
			case "mysql":
				return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbName;
			case "db2":
				return "jdbc:db2://" + this.host + ":" + this.port + "/" + this.dbName;
			case "sybase":
				return "jdbc:sybase:Tds:" + this.host + ":" + this.port + "/" + this.dbName;
			case "postgresql":
				return "jdbc:postgresql://" + this.host + "/" + this.dbName;
			case "sql server2000":
				return "jdbc:microsoft:sqlserver://" + this.host + ":" + this.port + ";DatabaseName=" + this.dbName;
			case "sql server":
				return "jdbc:sqlserver://" + this.host + ":" + this.port + "; DatabaseName=" + this.dbName;
			default:
				throw new UnsupportDataSourceException(db);
		}
	}
}
