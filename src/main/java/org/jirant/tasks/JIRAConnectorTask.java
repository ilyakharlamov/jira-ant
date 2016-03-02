package org.jirant.tasks;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.TokenCredentials;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * This custom Ant task 
 * @author Gleb Gadyatskiy
 */
public class JIRAConnectorTask extends Task {
	
	private String jira;
	
	private String login;
	
	private String password;
	
	private String token;
	
  private boolean disableSSL;
  
	private static JiraClient jiraClient;

	@Override
    public void execute() throws BuildException {
		if (jira==null) {
			throw new BuildException("Attribute 'jira' must be set");
		}
		try {
	        TokenCredentials credentials;
    			if (token==null) {
            	if (login==null || password==null) {
            		throw new BuildException("Attributes 'login'+'password' or 'token' must be set");
            	}
            	credentials = new TokenCredentials(login,password);
          } else {
            	credentials = new TokenCredentials(token);
          }
    		
    			if (disableSSL) {
    			    XTrustProvider.install();
    			    log("SSL validation is disabled",Project.MSG_WARN);
    			}
    			jiraClient = new JiraClient(jira, credentials);
	        jiraClient.getPriorities();
      } catch (JiraException e) {
          log(e,Project.MSG_ERR);
      	  throw new BuildException("Error connecting to JIRA\r\n"+e.getMessage());
      }
		
  }
	
	/**
	 * Login for JIRA
	 * @param login
	 */
	public void setLogin(String login) {
    	this.login = login;
    }

	/**
	 * Password for JIRA
	 * @param password
	 */
	public void setPassword(String password) {
    	this.password = password;
    }

	/**
	 * Already existing JSESSIONID (as an alternative to login/password)
	 * @param token
	 */
	public void setToken(String token) {
    	this.token = token;
    }

	
	/**
	 * URL to JIRA
	 * @param jira
	 */
	public void setJira(String jira) {
    	this.jira = jira;
	}

	static JiraClient getJiraClient() {
    	return jiraClient;
  }

	/**
	 * Flag to disable SSL certificate validation in case of using self-signed sertificates
	 * @param disableSSL
	 */
  public void setDisableSSL(String disableSSL) {
      this.disableSSL = Boolean.parseBoolean(disableSSL);
  }
  
}
