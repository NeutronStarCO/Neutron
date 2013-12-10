package com.neutronstar.neutron.model;

public class GroupTestingEntity {
	
	private static final String TAG = GroupTestingEntity.class.getSimpleName();
	private String name;
    private double value;
    private String dimension;
    private boolean hasValue = false;

	public GroupTestingEntity() {}
	
	public GroupTestingEntity(String name, double value, String dimension) {
        super();
        this.name = name;
        this.value = value;
        this.dimension = dimension;
        this.hasValue = true;
    }	

    public String getName() { return name;}
    
    public void setName(String name) { this.name = name; }

    public double getValue() { return value; }

    public void setValue(double value) { this.value = value; hasValue = true;}

    public String getDimension() { return dimension; }

    public void setDimension(String dimension) { this.dimension = dimension; }
    
    public boolean hasValue() { return hasValue;}
    
    public void setHasValue(boolean hasValue) { this.hasValue = hasValue;}

}
