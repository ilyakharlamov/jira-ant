package org.jirant.tasks;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

/**
 * This custom Ant task 
 * @author Gleb Gadyatskiy
 */
public class JIRACounterTask extends JIRAAbstractJQLTask {
	
	private String to;

	protected void validateParams() {
		if (to==null) {
			throw new IllegalArgumentException("Attribute 'jql' must be set");
		}
    }

	public void setTo(String to) {
    	this.to = to;
    }

	@Override
    protected void process(JiraClient client) throws JiraException {
        getProject().setProperty(to, String.valueOf(client.countIssues(getJql())));
    }
	
}
