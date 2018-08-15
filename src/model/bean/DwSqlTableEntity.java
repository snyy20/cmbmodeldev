package model.bean;

/*
 * Welcome to use the TableGo Tools.
 * 
 * http://vipbooks.iteye.com
 * http://blog.csdn.net/vipbooks
 * http://www.cnblogs.com/vipbooks
 * 
 * Author:bianj
 * Email:edinsker@163.com
 * Version:4.1.2
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


public class DwSqlTableEntity implements java.io.Serializable {
    
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 56878543224L;

	/**  
     * SQL表名 
     */   
    private String table_name;  
    
    /**  
     * SQL行数
     */   
    private int load_num;  
    
    /**  
     * 该行SQL内容
     */   
    private String filed_value;  
    
   
    /**  
     * 加载日期
     */   
    private Date load_date;  
    
    /**  
     * 备注1
     */   
    private String comment1;  
    
    /**  
     * 备注2
     */   
    private String comment2;  
    
    /**  
     * 备注3
     */   
    private String comment3;  
	
  
	
	 public Date getLoad_date() {
		return load_date;
	}
	public void setLoad_date(Date load_date) {
		this.load_date = load_date;
	}
	public String getComment1() {
		return comment1;
	}
	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}
	public String getComment2() {
		return comment2;
	}
	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}
	public String getComment3() {
		return comment3;
	}
	public void setComment3(String comment3) {
		this.comment3 = comment3;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public int getLoad_num() {
		return load_num;
	}
	public void setLoad_num(int load_num) {
		this.load_num = load_num;
	}
	public String getFiled_value() {
		return filed_value;
	}
	public void setFiled_value(String filed_value) {
		this.filed_value = filed_value;
	}
	
	
	@Override   
	    public String toString() {   
	        return "Column{" +   
	        		"table_name='" + table_name + '\'' +
	        		",load_num='" + load_num + '\'' +
	        		",filed_value='" + filed_value + '\'' +
	        		",load_date='" + load_date + '\'' +
	        		",comment1='" + comment1 + '\'' +
	        		",comment2='" + comment2 + '\'' +
	                ", comment3=" + comment3 +   
	                '}';   
	    }   
	    public String toString_exp() {   
	       
	    	return table_name + '|' + load_num + '|' + filed_value + '|' + load_date
	    			+ '|' + comment1+ '|' + comment2+ '|' + comment3+'\n';
	        
	    }
}