package org.jirant.tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tools.ant.BuildException;

import net.rcarz.jiraclient.Field;
import net.rcarz.jiraclient.Issue;
import net.sf.json.JSONObject;


public abstract class JIRAAbstractIssueIterator extends JIRAAbstractJQLTask {
	
	
	private String format;
	
	private String fields;

	private String[] arFields;

	private String[] arFormats;
	
	public String getFormat() {
    	return format;
    }

	public void setFormat(String format) {
		if (format==null) {
			this.format = null;
			this.arFormats = null;
		} else {
	    	this.format = format.toLowerCase();
			arFormats = this.format==null ? null : this.format.split(",");
		}
    }
	
	public void setFields(String fields) {
    	this.fields = fields;
		arFields = this.fields==null ? null : this.fields.split(",");
    }

	protected void setFields(Issue i, String to) {
        if (arFields==null) {
        	getProject().setProperty(to, i.getKey());
        } else if (arFields.length==1) {
        	getProject().setProperty(to, getFieldValue(i,arFields[0],0));
        } else {
        	for (int j = 0; j < arFields.length; j++) {
        		String name = arFields[j];
            	getProject().setProperty(to+"."+name, getFieldValue(i,name,j));
            }
        }
    }

	protected String getFieldValue(Issue i, String name, int index) {
		Object value = getFieldByPath(i, name);
		if (value==null) {
			return "NULL";
		}
		if (Field.WORKLOG.equals(name)) {
			//through sub-iterator
			return name;
		}
		if (arFormats == null) {
			return value.toString();
		} else if (arFormats.length<=index){
			throw new IllegalArgumentException("'format' does not match 'fields'");
		} 
		
		String f = arFormats[index];
		return formatValue(value,f);
    }


	
	
}
