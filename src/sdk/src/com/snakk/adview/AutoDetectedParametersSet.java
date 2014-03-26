package com.snakk.adview;

public class AutoDetectedParametersSet {
	private static AutoDetectedParametersSet instance;
	private String latitude;
	private String longitude;
	private String ua;
	private Integer connectionSpeed;

	private AutoDetectedParametersSet() {
	}

	public static synchronized AutoDetectedParametersSet getInstance() {
		if (instance == null) {
			instance = new AutoDetectedParametersSet();
		}
		return instance;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

	public Integer getConnectionSpeed() {
		return connectionSpeed;
	}

	public void setConnectionSpeed(Integer connectionSpeed) {
		this.connectionSpeed = connectionSpeed;
	}
}
