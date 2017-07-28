package com.inksmallfrog.frogjbf.config;

public class DataSourceConfig {
	private String name;
	private String driver;
	private String url;
	private String host;
	private String port;
	private String user;
	private String password;
	private int maxConnetionCount;
	public DataSourceConfig(String name, String driver, String url,
			String host, String port, String user, String password,
			int maxConnetionCount) {
		super();
		this.name = name;
		this.driver = driver;
		this.url = url.replace("${host}", host).replace("${port}", port);
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.maxConnetionCount = maxConnetionCount;
	}
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
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
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
	
}
