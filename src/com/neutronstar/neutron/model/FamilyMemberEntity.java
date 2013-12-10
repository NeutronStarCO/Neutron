package com.neutronstar.neutron.model;

import java.util.Date;

import android.graphics.Bitmap;

public class FamilyMemberEntity {
	private static final String TAG = FamilyMemberEntity.class.getSimpleName();
	private int id;
	private String name;
	private Date birthday;
	private String gender;
	private int relation;
	private Bitmap avatar;
	private int type;
	
	public FamilyMemberEntity() {}

	public FamilyMemberEntity(int id, String name, Date birthday, String gender, int relation, Bitmap avatar, int type) 
	{
		super();
		this.id = id;
		this.name = name;
		this.birthday = birthday;
		this.gender = gender;
		this.relation = relation;
		this.avatar = avatar;
		this.type = type;
	}
	
	public int getId() { return this.id; }
	public void setId(int id) { this.id = id; }
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }
	public Date getBirthday() { return this.birthday; }
	public void setBirthday(Date birthday) { this.birthday = birthday; }
	public String getGender() { return this.gender; }
	public void setGender(String gender) {this.gender = gender; }
	public int getRelation() { return this.relation; }
	public void setRelation(int relation) { this.relation = relation; }
	public Bitmap getAvatar() { return avatar; }
	public void setAvatar(Bitmap avatar) { this.avatar = avatar; }
	public int getType() { return this.type; }
	public void setType(int type) { this.type = type; }

}
