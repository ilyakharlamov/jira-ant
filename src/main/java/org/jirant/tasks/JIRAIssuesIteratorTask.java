package org.jirant.tasks;
import java.util.ArrayList;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

/**
 * This custom Ant task 
 * @author Gleb Gadyatskiy
 */
public class JIRAIssuesIteratorTask extends JIRAAbstractIssueIterator implements TaskContainer{
	
	private static final Integer CURSOR = 100;
	
	private static Issue currentJiraIssue;

	private String value;
	
	private String fields;
	
	private String expandFields;
	
	private String maxResults;
	
	private ArrayList<Task> tasks = new ArrayList<Task>();

	protected void validateParams() {
		if (value==null) {
			throw new IllegalArgumentException("Attribute 'value' must be set");
		}
    }

	@Override
    protected void process(JiraClient client) throws JiraException {
		int start=0;
		int cursor = CURSOR;
		int max;
		if (maxResults==null) {
			max = 0;
		} else {
			try {
	            max = Integer.parseInt(maxResults);
            } catch (NumberFormatException e) {
    			throw new IllegalArgumentException("Invalid value in 'maxResults' "+maxResults+" "+e.getMessage());
            }
            if (max<cursor) {
            	cursor = max;
            }
		}
		String[] fields = this.fields==null ? null : this.fields.split(",");
		while (true) {
			String searchFields = this.fields;
			if (searchFields != null && searchFields.indexOf(".")>0) {
				StringBuilder builder = new StringBuilder(searchFields);
				int complexParameterPosition=-1;
				for (int j = 0; j < builder.length(); j++) {
	                switch (builder.charAt(j)) {
                    case '.':
                    	if (complexParameterPosition<0) {
                    		complexParameterPosition = j;
                    	}
	                    break;
                    case ',':
                    	if (complexParameterPosition>=0) {
                    		builder.replace(complexParameterPosition, j, "");
                    		j = complexParameterPosition;
                    		complexParameterPosition = -1;
                    	}
	                    break;
                    }
                }
				if (complexParameterPosition>=0) {
					//remove last .
					builder.setLength(complexParameterPosition);
				}
				searchFields = builder.toString();
			}
            Issue.SearchResult sr = client.searchIssues(getJql(),searchFields,expandFields,cursor,start);
            for (Issue i : sr.issues) {
                if (fields==null) {
                	getProject().setProperty(value, i.getKey());
                } else if (fields.length==1) {
                	getProject().setProperty(value, getFieldValue(i,fields[0],0));
                } else {
                	for (int j = 0; j < fields.length; j++) {
                		String name = fields[j];
                    	getProject().setProperty(value+"."+name, getFieldValue(i,name,j));
                    }
                }
            	currentJiraIssue = i;
    			try {
	                for (Task task : tasks) {
	                    task.perform();
	                }
                } finally {
                    currentJiraIssue = null;
                }
    		}
            start = sr.issues.size()+start;
            if (sr.total<=start) {
            	break;
            }
            if (max>0 && max<=start) {
            	break;
            }
		}
    }

	static Issue getCurrentJiraIssue() {
    	return currentJiraIssue;
    }

	public void setFields(String fields) {
    	this.fields = fields;
    }

	public void setValue(String value) {
    	this.value = value;
    }

	public void setMaxResults(String maxResults) {
    	this.maxResults = maxResults;
    }

	@Override
    public void addTask(Task arg0) {
		tasks.add(arg0);
    }

	public void setExpandFields(String expandFields) {
    	this.expandFields = expandFields;
    }
	
}
