package org.jirant.tasks;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.sf.json.JSONObject;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * This custom Ant task 
 * @author Gleb Gadyatskiy
 */
public abstract class JIRAAbstractTask extends Task {
	
	public static final SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public static final SimpleDateFormat jiraDateTimeParser = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
	
	protected JiraClient getJira() {
		JiraClient client = JIRAConnectorTask.getJiraClient();
		if (client==null) {
			throw new BuildException("JIRA Connector was not initialized");
		}
		return client;
	}

	protected Object getFieldByPath(Issue issue, String field) {
		if ("key".equals(field)) {
			return issue.getKey();
		}
		if ("id".equals(field)) {
			return issue.getId();
		}
		if ("url".equals(field)) {
			return issue.getUrl();
		}
		String[] path = field.split("\\.");
		Object obj;
		if (path.length>1) {
			obj = issue.getField(path[0]);
			for (int j = 1; j < path.length; j++) {
				if (obj instanceof Map) {
					Map object = (Map)obj;
	                obj = object.get(path[j]);
                } else {
                	throw new BuildException("Cannot get '"+path[j]+"' from "+field+", value="+issue.getField(path[0]));
                }
            } 
		} else {
			obj = issue.getField(field);
		}
		return obj;
    }

	protected Object getFieldByPath(JSONObject object, String field) {
		String[] path = field.split("\\.");
		Object value;
		if (path.length>1) {
			Map map = (Map)object;
			value = map.get(path[0]);
			for (int j = 1; j < path.length; j++) {
				if (value instanceof Map) {
					Map obj = (Map)value;
	                value = obj.get(path[j]);
                } else {
                	throw new BuildException("Cannot get '"+path[j]+"' from "+field+", value="+object);
                }
            } 
		} else {
			value = object.get(field);
		}
	    return value;
    }

	protected String formatValue(Object value, String f) {
		if (value==null) {
			return "NULL";
		}
		if (value instanceof String) {
			String sValue = (String)value;
			try {
				//2013-04-19T16:27:01.000-0400
	            if ("d".equals(f) || "date".equals(f)) {
	            	value = sValue.substring(0,10);
	            } else if ("t".equals(f) || "time".equals(f)) {
	            	value = sValue.substring(11,19);
	            } else if ("dt".equals(f) || "datetime".equals(f)) {
	            	value = sValue.substring(0,10)+" "+sValue.substring(11,19);
	            } else if ("d2d".equals(f) || "d2bd".equals(f) ) {
	            	value = jiraDateTimeParser.parse(sValue.substring(0,19));
	            }
            } catch (ParseException e) {
	            e.printStackTrace();
            	log(e.getMessage(),e,0);
            }
        }
		if (value instanceof String) {
			return (String)value;
		}
		if (value instanceof Date) {
			Date date = (Date)value;
			if ("d".equals(f) || "date".equals(f)) {
				return dateFormat.format(date);
			} else if ("t".equals(f) || "time".equals(f)) {
				return timeFormat.format(date);
			} else if ("dt".equals(f) || "datetime".equals(f)) {
				return dateTimeFormat.format(date);
			} else if ("d2d".equals(f) ) {
				return String.valueOf(java.lang.Math.round((System.currentTimeMillis()-date.getTime())/1000/60/60/24));
			} else if ("d2bd".equals(f) ) {
				int days = java.lang.Math.round((System.currentTimeMillis()-date.getTime())/1000/60/60/24/7*5);
				return String.valueOf(days);
			}
		}
		return value.toString();
    }
	
}
