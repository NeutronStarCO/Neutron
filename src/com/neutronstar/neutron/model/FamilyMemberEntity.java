package com.neutronstar.neutron.model;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class FamilyMemberEntity implements Serializable{
	private static final long serialVersionUID = 1190790643377053664L;
	private static final String TAG = FamilyMemberEntity.class.getSimpleName();
	private int id;
	private String name;
	private Date birthday;
	private String IDD;
	private String phoneNumber;
	private int gender;
	private int relation;
	private int relationTag;
	private byte[] bitmapBytes;
	private int type;
	
	public FamilyMemberEntity() {}


	public FamilyMemberEntity(int id, String name, Date birthday, String IDD, String phoneNumber, int gender, int relation, int relationTag, Bitmap avatar, int type) 
	{
		super();
		this.id = id;
		this.name = name;
		this.birthday = birthday;
		this.IDD = IDD;
		this.phoneNumber = phoneNumber;
		this.gender = gender;
		this.relation = relation;
		this.relationTag = relationTag;
		ByteArrayOutputStream baops = new ByteArrayOutputStream();
		avatar.compress(CompressFormat.PNG, 0, baops);
        this.bitmapBytes =  baops.toByteArray();
		this.type = type;
	}
	
	public int getId() { return this.id; }
	public void setId(int id) { this.id = id; }
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }
	public Date getBirthday() { return this.birthday; }
	public void setBirthday(Date birthday) { this.birthday = birthday; }
	public String getIDD() { return this.IDD; }
	public void setIDD(String IDD) {this.IDD = IDD; }
	public String getPhoneNumber() { return this.phoneNumber; }
	public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
	public int getGender() { return this.gender; }
	public void setGender(int gender) {this.gender = gender; }
	public int getRelation() { return this.relation; }
	public void setRelation(int relation) { this.relation = relation; }
	public int getRelationTag() { return this.relationTag; }
	public void setRelationTag(int relationTag) { this.relationTag = relationTag; }
	public int getType() { return this.type; }
	public void setType(int type) { this.type = type; }
	
	public Bitmap getAvatar(){ return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length); }
	public void setAvatar(Bitmap avatar) 
	{ 
		ByteArrayOutputStream baops = new ByteArrayOutputStream();
		avatar.compress(CompressFormat.PNG, 0, baops);
        this.bitmapBytes =  baops.toByteArray();
	}

}
