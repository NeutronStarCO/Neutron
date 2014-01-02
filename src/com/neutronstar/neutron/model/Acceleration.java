package com.neutronstar.neutron.model;

import java.io.Serializable;

public class Acceleration implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4572703078330071684L;
	private double acceleration;
	private String timestamp;
	private int uploadtag = 0;
	
	public Acceleration() {}
	
	public Acceleration(double acceleration, String timestamp, int uploadtag)
	{
		super();
		this.acceleration = acceleration;
		this.timestamp = timestamp;
		this.uploadtag = uploadtag;
	}
	
	public int getUploadtag() {return this.uploadtag;}
	public void setUploadtag(int uploadtag) {this.uploadtag = uploadtag;}
	
	public double getAcceleration() {return this.acceleration; }
	public void setAcceleration(double acceleration) { this.acceleration = acceleration; }
	
	public String getTimestamp() {return this.timestamp;}
	public void setTimestamp(String timestamp) {this.timestamp = timestamp;}
	
}
