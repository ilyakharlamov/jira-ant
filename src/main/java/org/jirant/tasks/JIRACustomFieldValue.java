package org.jirant.tasks;

import net.sf.json.JSONObject;

import org.apache.tools.ant.BuildException;

public class JIRACustomFieldValue extends JIRAAbstractTask {
	
	private String field;
	private String to;
	private String format;
	
	@Override
    public final void execute() throws BuildException {
		if (field==null || to==null) {
			throw new BuildException("Attributes 'field' and 'to' must be set");
		}
		JSONObject object = JIRACustomFieldIterator.getCurrentRow();
		if (object==null) {
			throw new BuildException("Cannot get value for '"+field+"' without enclosing Custom Field iterator");
		}
		if (field.length()==0) {
			getProject().setProperty(to, object.keySet().toString());
		} else {
			getProject().setProperty(to, formatValue(getFieldByPath(object, field),format));
		}
	}

	public void setField(String field) {
    	this.field = field;
    }

	public void setTo(String to) {
    	this.to = to;
    }

	public void setFormat(String format) {
    	this.format = format;
    }
	

}
