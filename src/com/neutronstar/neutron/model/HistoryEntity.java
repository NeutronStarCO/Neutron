package com.neutronstar.neutron.model;

public class HistoryEntity {

	private static final String TAG = HistoryEntity.class.getSimpleName();
	private int rowid;
    private String testingInstitution;
    private String date;
    
    public HistoryEntity() {}

    public HistoryEntity(int rowid,String testingInstitution, String date) {
        super();
        this.rowid = rowid;
        this.testingInstitution = testingInstitution;
        this.date = date;
    }

    public int getRowid() {return rowid;}
    
    public void setRowid(int r) { this.rowid = r;}

    public String getDate() { return date;}

    public void setDate(String date) { this.date = date; }

    public String getTestingInstitution() { return testingInstitution;    }

    public void setTestingInstitution(String testingInstitution) {
        this.testingInstitution = testingInstitution;
    }
    
}
