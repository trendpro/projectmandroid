package com.projectman;

public class ProjectDataType {
	private String name;
	private String id;
	private String description;
	
	public ProjectDataType(String nm,String id, String description){
		this.name = nm;
		this.id = id;
		this.description = description;
	}
	
	public String getProjectName(){
		return this.name;
	}
	
	public String getProjectId(){
		return this.id;
	}
	
	public String getProjectDescription(){
		return this.description;
	}
	
	public void setProjectId(String id){
		this.id = id;
	}
	
	public void setProjectName(String name){
		this.name =  name;
	}
	
	public void setProjectDescription(String desc){
		this.description = desc;
	}

}
