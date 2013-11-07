package com.projectman;

public class ProjectDataType {
	String name;
	String id;
	
	public ProjectDataType(String nm,String id){
		name = nm;
		this.id = id;
	}
	
	public String getProjectName(){
		return this.name;
	}
	
	public String getProjectId(){
		return this.id;
	}
	
	public void setProjectId(String id){
		this.id = id;
	}
	
	public void setProjectName(String name){
		this.name =  name;
	}

}
