package org.jirant.tasks;

import java.util.ArrayList;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.IssueHistory;
import net.rcarz.jiraclient.IssueHistoryItem;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

public class JIRAChangeLogIterator extends JIRAAbstractTask implements TaskContainer{
	
	private ArrayList<Task> tasks = new ArrayList<Task>();
	
	private String filterByChangedFields;
	
	private String newFlag;
	
	private String author;
	private String date;
	private String changedField;
	private String from;
	private String to;
	private String fromStr;
	private String toStr;
	private String dateTime;
	
	@Override
    public final void execute() throws BuildException {
		Issue issue = JIRAIssuesIteratorTask.getCurrentJiraIssue();
		if (issue==null) {
			throw new BuildException("Cannot iterarate over a Change Log without the current issue");
		}
		JiraClient client = getJira();
		ArrayList<IssueHistory> list;
        try {
	        list = client.getIssueChangeLog(issue);
        } catch (JiraException e) {
        	throw new BuildException(e);
        }
		if (filterByChangedFields != null) {
			list = client.filterChangeLog(list, filterByChangedFields);
		}
		for (IssueHistory issueHistory : list) {
	        if (newFlag != null) {
	            getProject().setProperty(newFlag, "true");
	        }
	        if (author != null) {
	        	getProject().setProperty(author, issueHistory.getUser().getName());
	        }
	        if (date != null) {
	        	getProject().setProperty(date, dateFormat.format(issueHistory.getCreated()));
	        }
	        if (dateTime != null) {
	        	getProject().setProperty(dateTime, dateTimeFormat.format(issueHistory.getCreated()));
	        }
	        for (IssueHistoryItem item : issueHistory.getChanges()) {
		        if (changedField != null) {
		        	getProject().setProperty(changedField, item.getField());
		        }
		        if (from != null) {
		        	if (item.getFrom()==null) {
		        		getProject().setProperty(from, "");
		        	} else {
		        		getProject().setProperty(from, item.getFrom());
		        	}
		        }
		        if (to != null) {
		        	if (item.getTo()==null) {
		        		getProject().setProperty(to, "");
		        	} else {
		        		getProject().setProperty(to, item.getTo());
		        	}
		        }
		        if (fromStr != null) {
		        	if (item.getFromStr()==null) {
		        		getProject().setProperty(fromStr, "");
		        	} else {
		        		getProject().setProperty(fromStr, item.getFromStr());
		        	}
		        }
		        if (toStr != null) {
		        	if (item.getToStr()==null) {
		        		getProject().setProperty(toStr, "");
		        	} else {
		        		getProject().setProperty(toStr, item.getToStr());
		        	}
		        }
            	for (Task task : tasks) {
                    task.perform();
                }
	            getProject().setProperty(newFlag, "false");
            }
        }
	}

	@Override
    public void addTask(Task task) {
		tasks.add(task);
    }

	public void setFilterByChangedFields(String filterByChangedFields) {
    	this.filterByChangedFields = filterByChangedFields;
    }

	public void setNewFlag(String newFlag) {
    	this.newFlag = newFlag;
    }

	public void setAuthor(String author) {
    	this.author = author;
    }

	public void setDate(String date) {
    	this.date = date;
    }

	public void setChangedField(String changedField) {
    	this.changedField = changedField;
    }

	public void setFrom(String from) {
    	this.from = from;
    }

	public void setTo(String to) {
    	this.to = to;
    }

	public void setFromStr(String fromStr) {
    	this.fromStr = fromStr;
    }

	public void setToStr(String toStr) {
    	this.toStr = toStr;
    }

	public void setDateTime(String dateTime) {
    	this.dateTime = dateTime;
    }

}
