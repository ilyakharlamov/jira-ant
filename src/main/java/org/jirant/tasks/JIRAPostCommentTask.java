package org.jirant.tasks;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

import org.apache.tools.ant.BuildException;

public class JIRAPostCommentTask extends JIRAAbstractTask {
	private String issue; 
	private String comment;
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	@Override
    public final void execute() throws BuildException {
		JiraClient jiraClient = getJira();
		Issue i = null;
		try {
			i = jiraClient.getIssue(getIssue());
		} catch (JiraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (i==null) throw new BuildException(String.format("Issue %s not found", getIssue()));
		try {
			i.addComment(getComment());
		} catch (JiraException e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

	}

}
