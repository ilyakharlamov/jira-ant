# jira-ant
posting comments to jira from ant
```xml
    <target name="default" description="description">
       <jiraConnection login="YOUR_JIRA_LOGIN" password="YOUR_JIRA_PASSWORD" jira="http://myjira.mydomain.com/jira"/>
    	<jiraCounter jql="assignee = currentUser() AND resolution = unresolved" to="cnt"/>
    	<echo>There are ${cnt} unresolved issues assigned to you </echo>
    </target>
```
