package com.neutronstar.neutron;

import android.provider.BaseColumns;

public final class NeutronContract {

	public NeutronContract() {}
	
	 public static abstract class NeutronAcceleration implements BaseColumns 
	 {
		 public static final String TABLE_NAME = "t_acceleration";
		 public static final String COLUMN_NAME_ACCELERATION = "acceleration";
		 public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
	 }
	 
	 public static abstract class NeutronRMRIndex implements BaseColumns 
	 {
		 public static final String TABLE_NAME = "t_rmrindex";
		 public static final String COLUMN_NAME_RMRINDEX = "rmrindex";
		 public static final String COLUMN_NAME_DATESTAMP = "datestamp";
	 }
	 
	 public static abstract class NeutronUser implements BaseColumns 
	 {
		 public static final String TABLE_NAME = "t_user";
		 public static final String COLUMN_NAME_ID = "id";
		 public static final String COLUMN_NAME_NAME = "name";
		 public static final String COLUMN_NAME_BIRTHDAY = "birthday";
		 public static final String COLUMN_NAME_GENDER = "gender";
		 public static final String COLUMN_NAME_RELATION = "relation";
		 public static final String COLUMN_NAME_TYPE = "type";
		 public static final String COLUMN_NAME_AVATAR = "avatar";
		 public static final String COLUMN_NAME_PASSCODE = "passcode";
		 public static final String COLUMN_NAME_TAG = "tag";
	 }
	 
	 public static abstract class NeutronRecord implements BaseColumns 
	 {
		 public static final String TABLE_NAME = "t_record";
		 public static final String COLUMN_NAME_USERID = "userid";
		 public static final String COLUMN_NAME_ITEM = "item";
		 public static final String COLUMN_NAME_VALUE = "value";
		 public static final String COLUMN_NAME_DATETIME = "datetime";
		 public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
		 public static final String COLUMN_NAME_GROUPTESTID = "grouptestid";
		 public static final String COLUMN_NAME_TAG = "tag";
	 }
	 
	 public static abstract class NeutronGroupTesting implements BaseColumns 
	 {
		 public static final String TABLE_NAME = "t_group_testing";
		 public static final String COLUMN_NAME_USERID = "userid";
		 public static final String COLUMN_NAME_GROUP = "groupitem";
		 public static final String COLUMN_NAME_DATETIME = "datetime";
		 public static final String COLUMN_NAME_TESTINGINST = "testinginst";
		 public static final String COLUMN_NAME_TAG = "tag";

	 }
	 
	 
	 public static abstract class ITEM 
	 {
		 public static final int weight = 1;
		 public static final int height = 2;
		 
		 public static final int B_WBC	= 3;		// °×Ï¸°û¼ÆÊý
		 public static final int B_RBC	= 4;		// ºìÏ¸°û¼ÆÊý
		 public static final int B_HGB	= 5;		// Ñªºìµ°°×
		 public static final int B_HCT	= 6;		// ÑªÏ¸°ûÑ¹»ý
		 public static final int B_PLT	= 7;		// ÑªÐ¡°å
		 public static final int B_PCT	= 8;		// ÑªÐ¡°åÑ¹»ý
		 
		 public static final int U_SG	= 15;		// Äò±ÈÖØ
		 public static final int U_PH	= 16;		// ÄòËá¼î¶È
		 
		 public static final int BP_X	= 17;
		 public static final int BP_Y	= 18;
		 public static final int heart_rate	= 19;
		 
		 public static final int g_bloodTest = 1001;
		 public static final int g_urineTest = 1002;
		 public static final int g_bloodPressure = 1003;
	 }
	 
	 public static abstract class USER 
	 {
		 public static final int me = 1;
		 public static final int mother = 2;
		 public static final int father = 3;
		 public static final int wife = 4;
		 public static final int husband = 5;
		 public static final int daughter = 6;
		 public static final int son = 7;
		 public static final int grandma = 8;
		 public static final int grandpa = 9;
		 public static final int sister = 10;
		 public static final int brother = 11;
		 public static final int friends = 12;	
		 
		 public static final int registered = 1;
		 public static final int subregister = 2;
	 }
	 
	 public static abstract class CONSTANT 
	 {
		 public static final double TypicalWeight = 60;
		 public static final double TypicalHeight = 170;
		 public static final int TypicalAge = 25;
	 }
	 
	 public static abstract class TAG
	 {
		 public static final int normal = 0;
		 public static final int delete = 1;
	 }
	 
	 public static abstract class GENDER
	 {
		 public static final int female = 0;
		 public static final int male = 1; 
	 }
 
	 public static abstract class SYNC
	 {
		 public static final int synced = 0;
		 public static final int unsync = 1;
	 }
	 
	 public static abstract class SERVER
	 {
		 public static final String Address = "http://www.rrd2mysql.com:12581/NeutronServer";
		 public static final String DomainAddress = "http://www.rrd2mysql.com:12581/NeutronServer";
		 public static final String PublicAddress = "http://219.141.181.131:12581/NeutronServer";
		 public static final String InnerAddress = "http://172.20.8.183:12581/NeutronServer";
		 
	 }
	 

}
