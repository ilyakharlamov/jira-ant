package org.jirant.tasks;

import java.util.ArrayList;

import net.rcarz.jiraclient.Issue;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

public class JIRACustomFieldIterator extends JIRAAbstractTask  implements TaskContainer{
	
	private static JSONObject currentRow;
	
	private ArrayList<Task> tasks = new ArrayList<Task>();
	
	private String field;
	
	@Override
    public final void execute() throws BuildException {
		if (field==null) {
			throw new BuildException("property 'base' must be set");
		}
		Issue issue = JIRAIssuesIteratorTask.getCurrentJiraIssue();
		if (issue==null) {
			throw new BuildException("Cannot iterarate over a Custome field without the current issue");
		}
		Object obj = getFieldByPath(issue,field);
		if (obj instanceof JSONArray) {
			JSONObject oldValue = currentRow;
            JSONArray ar = (JSONArray) obj;
            for (int j = 0; j < ar.size(); j++) {
                Object row = ar.get(j);
                if (row instanceof JSONObject) {
                	currentRow = (JSONObject) row;
                	for (Task task : tasks) {
	                    task.perform();
                    }
                } else if (row==null){
        			throw new BuildException("Row is null");
                } else {
        			throw new BuildException("Unexpected object "+row.getClass().getName());
                }
            }
			currentRow = oldValue;
        } else if (obj instanceof JSONNull) {
        	//do nothing
        } else if (obj==null) {
			throw new BuildException("Unknown field '"+field+"'");
        } else {
			throw new BuildException("Cannot iterate over field '"+field+"', class "+obj.getClass().getName());
        }
	}

	static JSONObject getCurrentRow() {
    	return currentRow;
    }

	@Override
    public void addTask(Task task) {
		tasks.add(task);
    }

	public void setField(String field) {
    	this.field = field;
    }
	

}
