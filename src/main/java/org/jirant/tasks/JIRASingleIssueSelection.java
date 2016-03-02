package org.jirant.tasks;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

public class JIRASingleIssueSelection extends JIRAAbstractIssueIterator {

	private String to;
	
	private String found;
	
	protected void validateParams() {
		if (to==null) {
			throw new IllegalArgumentException("Attribute 'to' must be set");
		}
    }

	@Override
    protected void process(JiraClient client) throws JiraException {
        Issue.SearchResult sr = client.searchIssues(getJql(),null,null,1,0);
        if (sr.issues.size()>0) {
            Issue i = sr.issues.get(0);
            setFields(i,to);
            if (found != null) {
            	getProject().setProperty(found,"1");
            }
		} else if (found != null) {
        	getProject().setProperty(found,"0");
		}
    }

	public void setTo(String to) {
    	this.to = to;
    }

	public void setFound(String found) {
    	this.found = found;
    }

}
