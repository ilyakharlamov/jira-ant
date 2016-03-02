package org.jirant.tasks;

import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

import org.apache.tools.ant.BuildException;

public abstract class JIRAAbstractJQLTask extends JIRAAbstractTask{

	private String jql;
	
	@Override
    public final void execute() throws BuildException {
		try {
			if (jql==null) {
				throw new IllegalArgumentException("Attribute 'jql' must be set");
			}
			validateParams();
	        process(getJira());
        } catch (JiraException e) {
        	log(jql);
        	e.printStackTrace();
        	log(e.getMessage(),e,0);
        } catch (Exception e) {
        	log(jql);
        	e.printStackTrace();
        	log(e.getMessage(),e,0);
        }
    }
	
	protected abstract void process(JiraClient jiraClient) throws JiraException;

	protected void validateParams() {
    }

	protected String getJql() {
    	return jql;
    }

	public void setJql(String jql) {
    	this.jql = jql;
    }


}
