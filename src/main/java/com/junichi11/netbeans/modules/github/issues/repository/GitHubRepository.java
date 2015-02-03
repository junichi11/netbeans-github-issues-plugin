/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package com.junichi11.netbeans.modules.github.issues.repository;

import com.junichi11.netbeans.modules.github.issues.GitHubCache;
import com.junichi11.netbeans.modules.github.issues.GitHubIssuesConfig;
import com.junichi11.netbeans.modules.github.issues.GitHubIssuesConnector;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams;
import com.junichi11.netbeans.modules.github.issues.egit.SearchService;
import com.junichi11.netbeans.modules.github.issues.issue.CreateIssueParams;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssue;
import com.junichi11.netbeans.modules.github.issues.options.GitHubIssuesOptions;
import com.junichi11.netbeans.modules.github.issues.query.GitHubDefaultQueries;
import com.junichi11.netbeans.modules.github.issues.query.GitHubDefaultQueries.Type;
import com.junichi11.netbeans.modules.github.issues.query.GitHubQuery;
import com.junichi11.netbeans.modules.github.issues.utils.GitHubIssuesUtils;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import com.junichi11.netbeans.modules.github.issues.utils.UiUtils;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.bugtracking.api.RepositoryManager;
import org.netbeans.modules.bugtracking.api.Util;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.netbeans.modules.bugtracking.spi.RepositoryProvider;
import org.openide.util.ImageUtilities;

/**
 *
 * @author junichi11
 */
public class GitHubRepository {

    private RepositoryInfo repositoryInfo;
    private GitHubRepositoryController controller;
    private Repository ghRepository;
    private final Map<String, GitHubIssue> issueCache = Collections.synchronizedMap(new HashMap<String, GitHubIssue>());
    private Boolean isCollaborator = null;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private Set<GitHubQuery> queries = null;
    private static final String PROPERTY_OAUTH_TOKEN = "github.issues.oauth.token"; // NOI18N
    private static final String PROPERTY_REPOSITORY_AUTHOR = "github.issues.repository.author"; // NOI18N
    private static final String PROPERTY_REPOSITORY_NAME = "github.issues.repository.name"; // NOI18N
    private static final String PROPERTY_BOOLEAN_PROPERTY_FILE = "github.issues.boolean.property.file"; // NOI18N
    private static final Image ICON = ImageUtilities.loadImage("com/junichi11/netbeans/modules/github/issues/resources/icon_16.png", false);
    private static final Logger LOGGER = Logger.getLogger(GitHubRepository.class.getName());

    public GitHubRepository() {
    }

    public GitHubRepository(RepositoryInfo repositoryInfo) {
        this.repositoryInfo = repositoryInfo;
    }

    /**
     * Get full repository name. (author/name)
     *
     * @return full repository name.
     */
    public String getFullName() {
        return String.format("%s/%s", getRepositoryAuthor(), getRepositoryName()); // NOI18N
    }

    /**
     * Create a GitHubIssue.
     *
     * @return GitHubIssue
     */
    public GitHubIssue createIssue() {
        return new GitHubIssue(this);
    }

    /**
     * Get controller.
     *
     * @return GitHubRepositoryController
     */
    public GitHubRepositoryController getController() {
        if (controller == null) {
            controller = new GitHubRepositoryController(this);
        }
        return controller;
    }

    /**
     * Get icon.
     *
     * @return icon
     */
    public Image getIcon() {
        return ICON;
    }

    /**
     * Get RepositoryInfo.
     *
     * @return RepositoryInfo
     */
    public RepositoryInfo getRepositoryInfo() {
        return repositoryInfo;
    }

    /**
     * Create GitHubClient.
     *
     * @return GitHubClient
     */
    @CheckForNull
    public GitHubClient createGitHubClient() {
        String oauthToken;
        try {
            if (isPropertyFile()) {
                Properties properties = GitHubIssuesUtils.getProperties();
                oauthToken = properties.getProperty(GitHubIssuesUtils.PROPERTY_OAUTH_TOKEN);
            } else {
                oauthToken = getOAuthToken();
            }
            GitHubClient client = new GitHubClient().setOAuth2Token(oauthToken);
            return client;
        } catch (IOException ex) {
            // show repository panel
            org.netbeans.modules.bugtracking.api.Repository repository = RepositoryManager.getInstance().getRepository(GitHubIssuesConnector.ID, getID());
            Util.edit(repository);
        }
        return null;
    }

    /**
     * Get ID.
     *
     * @return id
     */
    public String getID() {
        if (repositoryInfo == null) {
            return null;
        }
        return repositoryInfo.getID();
    }

    /**
     * Get user name.
     *
     * @return use name
     */
    public String getUserName() {
        if (repositoryInfo == null) {
            return ""; // NOI18N
        }
        return repositoryInfo.getUsername();
    }

    /**
     * Get OAuth token.
     *
     * @return OAuth token
     */
    public String getOAuthToken() {
        return getPropertyValue(PROPERTY_OAUTH_TOKEN);
    }

    /**
     * Get repository author name.
     *
     * @return repository author name
     */
    public String getRepositoryAuthor() {
        return getPropertyValue(PROPERTY_REPOSITORY_AUTHOR);
    }

    /**
     * Get repository name.
     *
     * @return repository name
     */
    public String getRepositoryName() {
        return getPropertyValue(PROPERTY_REPOSITORY_NAME);
    }

    /**
     * Check whether property file is used. property file is ~/.github.
     *
     * @return {@code true} property file is used, otherwise {@code false}
     */
    public boolean isPropertyFile() {
        String propertyValue = getPropertyValue(PROPERTY_BOOLEAN_PROPERTY_FILE);
        if (StringUtils.isEmpty(propertyValue)) {
            return false;
        }
        return Boolean.getBoolean(propertyValue);
    }

    /**
     * Get property value.
     *
     * @param propertyName property name
     * @return value for property name
     */
    private String getPropertyValue(String propertyName) {
        if (repositoryInfo != null) {
            return repositoryInfo.getValue(propertyName);
        }
        return ""; // NOI18N
    }

    /**
     * Get milestones.
     *
     * @return milestones
     */
    public List<Milestone> getMilestones() {
        GitHubCache cache = GitHubCache.create(this);
        return cache.getMilestones();
    }

    /**
     * Check whether user is collaborator of the repository.
     *
     * @return {@code true} if user is collaborator, otherwise {@code false}
     */
    public boolean isCollaborator() {
        if (isCollaborator == null) {
            GitHubClient client = createGitHubClient();
            Repository repository = getRepository();
            if (repository == null) {
                return false;
            }
            CollaboratorService collaboratorService = new CollaboratorService(client);
            try {
                isCollaborator = collaboratorService.isCollaborator(repository, getUserName());
            } catch (IOException ex) {
                isCollaborator = false;
                LOGGER.log(Level.WARNING, "{0} Can''t check whether user is a collaborator.", ex.getMessage()); // NOI18N
            }
        }
        return isCollaborator;
    }

    /**
     * Get collaborators.
     *
     * @return collaborators
     */
    public List<User> getCollaborators() {
        GitHubCache cache = GitHubCache.create(this);
        return cache.getCollaborators();
    }

    // Issues
    /**
     * Get issue labels.
     *
     * @return labels
     */
    public List<Label> getIssueLabels() {
        GitHubCache cache = GitHubCache.create(this);
        return cache.getLabels();
    }

    /**
     * Submit a new issue.
     *
     * @param params
     * @return Issue if an issue has been submitted, otherwise {@code null}
     */
    @CheckForNull
    public Issue submitNewIssue(CreateIssueParams params) {
        Issue issue = null;
        if (params == null) {
            return issue;
        }
        String title = params.getTitle();
        if (StringUtils.isEmpty(title)) {
            return issue;
        }
        issue = new Issue().setTitle(params.getTitle())
                .setBody(params.getBody())
                .setAssignee(params.getAssignee())
                .setMilestone(params.getMilestone())
                .setLabels(params.getLabels());
        Repository repository = getRepository();
        IssueService issueService = new IssueService(createGitHubClient());
        Issue createdIssue = null;
        try {
            createdIssue = issueService.createIssue(repository, issue);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "{0} Can't create an issue.", ex.getMessage()); // NOI18N
        }
        return createdIssue;
    }

    /**
     * Edit an issue.
     *
     * @param gitHubIssue GitHubIssue
     * @param params CreateIssueParams
     * @return Issue if issue has been edited, otherwise {@code null}
     */
    @CheckForNull
    public Issue editIssue(GitHubIssue gitHubIssue, CreateIssueParams params) {
        Issue issue = gitHubIssue.getIssue();
        if (params == null) {
            return issue;
        }
        String title = params.getTitle();
        if (StringUtils.isEmpty(title)) {
            return issue;
        }
        User user = new User();
        issue.setTitle(title)
                .setBody(params.getBody())
                .setAssignee(params.getAssignee())
                .setMilestone(params.getMilestone())
                .setLabels(params.getLabels());
        Repository repository = getRepository();
        IssueService issueService = new IssueService(createGitHubClient());
        Issue updatedIssue = null;
        try {
            updatedIssue = issueService.editIssue(repository, issue);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "{0} Can't edit an issue.", ex.getMessage()); // NOI18N
        }
        return updatedIssue;
    }

    /**
     * Create GitHubIssue for an Issue.
     *
     * @param issue Issue
     * @return GitHubIssue
     */
    public synchronized GitHubIssue createIssue(Issue issue) {
        String id = String.valueOf(issue.getNumber());
        GitHubIssue gitHubIssue = issueCache.get(id);
        if (gitHubIssue != null) {
            return gitHubIssue;
        }
        gitHubIssue = new GitHubIssue(this, issue);
        issueCache.put(id, gitHubIssue);
        return gitHubIssue;
    }

    /**
     * Add a GitHubIssue to cache.
     *
     * @param issue GitHubIssue
     */
    public synchronized void addIssue(GitHubIssue issue) {
        if (issue == null) {
            return;
        }
        String id = issue.getID();
        if (StringUtils.isEmpty(id)) {
            return;
        }
        GitHubIssue i = issueCache.get(id);
        if (i != null) {
            return;
        }
        issueCache.put(id, issue);
    }

    /**
     * Get an Issue of specified id number.
     *
     * @param id ID
     * @return Issue if the issue exists, otherwise {@code null}
     */
    @CheckForNull
    public Issue getIssue(int id) {
        Repository repository = getRepository();
        if (repository == null) {
            return null;
        }
        try {
            GitHubClient client = createGitHubClient();
            IssueService issueService = new IssueService(client);
            return issueService.getIssue(repository, id);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "{0} Can't get an issue of the id number.", ex.getMessage()); // NOI18N
        }
        return null;
    }

    /**
     * Get Issues.
     *
     * @param filter
     * @return GitHubIssues
     */
    public List<GitHubIssue> getIssues(Map<String, String> filter) {
        Repository repository = getRepository();
        if (repository == null) {
            return null;
        }
        try {
            GitHubClient client = createGitHubClient();
            IssueService issueService = new IssueService(client);
            List<Issue> issues = issueService.getIssues(repository, filter);
            ArrayList<GitHubIssue> gitHubIssues = new ArrayList<>(issues.size());
            for (Issue issue : issues) {
                GitHubIssue createIssue = createIssue(issue);
                gitHubIssues.add(createIssue);
            }
            return gitHubIssues;
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "{0}: {1} Can't get issues.", new Object[]{getFullName(), ex.getMessage()}); // NOI18N
        }
        return Collections.emptyList();
    }

    /**
     * Get Issues of id numbers.
     *
     * @param ids
     * @return GitHubIssues
     */
    public List<GitHubIssue> getIssues(String... ids) {
        ArrayList<GitHubIssue> issues = new ArrayList<>();
        for (String id : ids) {
            GitHubIssue issue = getIssue(id);
            if (issue != null) {
                issues.add(issue);
            }
        }
        return issues;
    }

    /**
     * Get GitHubIssue.
     *
     * @param id
     * @return GitHubIssue if the issue exists, otherwise {@code null}
     */
    @CheckForNull
    private GitHubIssue getIssue(String id) {
        GitHubIssue issue;
        synchronized (this) {
            issue = issueCache.get(id);
        }
        if (issue != null) {
            return issue;
        }
        GitHubClient client = createGitHubClient();
        IssueService issueService = new IssueService(client);
        try {
            Issue i = issueService.getIssue(getRepository(), id);
            if (i != null) {
                return createIssue(i);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "{0} : Can't get an issue.", getFullName()); // NOI18N
        }

        return null;
    }

    /**
     * Refresh GitHubIssue.
     *
     * @param issue GitHubIssue
     */
    public void refresh(GitHubIssue issue) {
        if (issue == null || issue.isNew()) {
            return;
        }
        int id = Integer.parseInt(issue.getID());
        Issue refreshed = getIssue(id);
        if (refreshed == null) {
            return;
        }
        issue.setIssue(refreshed);
    }

    /**
     * Search issues.
     *
     * @param params SearchIssuesParams
     * @return GitHubIssues
     */
    public List<GitHubIssue> searchIssues(SearchIssuesParams params) {
        GitHubClient client = createGitHubClient();
        SearchService searchService = new SearchService(client);
        String repositoryName = getFullName();
        params = params.repo(repositoryName);
        try {
            List<Issue> searchIssues = searchService.searchIssues(params);
            ArrayList<GitHubIssue> issues = new ArrayList<>(searchIssues.size());
            for (Issue searchIssue : searchIssues) {
                issues.add(createIssue(searchIssue));
            }
            return issues;
        } catch (IOException ex) {
            // show dialog
            UiUtils.showErrorDialog(ex.getMessage());
            LOGGER.log(Level.WARNING, "{0} - {1}", new Object[]{repositoryName, ex.getMessage()}); // NOI18N
        }
        return Collections.emptyList();
    }

    /**
     * Search issues online.
     *
     * @param keyword keyword
     * @return GitHubIssues
     */
    public Collection<GitHubIssue> simpleSearch(String keyword) {
        ArrayList<GitHubIssue> issues = new ArrayList<>();
        if (keyword.matches("\\A\\d+\\z")) { // NOI18N
            issues.addAll(getIssues(keyword));
        }
        SearchIssuesParams params = new SearchIssuesParams();
        params.keyword(keyword);
        issues.addAll(searchIssues(params));
        return issues;
    }

    /**
     * Get comments of specified issue number.
     *
     * @param issueNumber issue number
     * @return Comments
     */
    public List<Comment> getComments(int issueNumber) {
        GitHubClient client = createGitHubClient();
        IssueService issueService = new IssueService(client);
        Repository repository = getRepository();
        if (repository == null) {
            return Collections.emptyList();
        }
        try {
            return issueService.getComments(repository, issueNumber);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "{0} - {1}", new Object[]{getFullName(), ex.getMessage()}); // NOI18N
        }
        return Collections.emptyList();
    }

    // Queries
    /**
     * Create a GitHubQuery.
     *
     * @return GitHubQuery
     */
    public GitHubQuery createQuery() {
        return new GitHubQuery(this);
    }

    /**
     * Get GitHubQueries.
     *
     * @return GitHubQueries
     */
    public Collection<GitHubQuery> getQueries() {
        if (queries == null) {
            queries = new HashSet<>();
            // add default queries
            GitHubIssuesOptions options = GitHubIssuesOptions.getInstance();
            Map<Type, Boolean> defaultQueryOptions = options.getDefaultQueryOptions();
            for (Map.Entry<Type, Boolean> entrySet : defaultQueryOptions.entrySet()) {
                Type type = entrySet.getKey();
                Boolean isEnabled = entrySet.getValue();
                if (!isEnabled) {
                    continue;
                }
                switch (type) {
                    case ASSIGNED_TO_ME:
                        if (!isCollaborator()) {
                            continue;
                        }
                        break;
                    default:
                        break;
                }
                addQuery(GitHubDefaultQueries.create(this, type));
            }

            // add user queries
            String[] queryNames = GitHubIssuesConfig.getInstance().getQueryNames(this);
            for (String queryName : queryNames) {
                String queryParams = GitHubIssuesConfig.getInstance().getQueryParams(this, queryName);
                if (StringUtils.isEmpty(queryParams)) {
                    continue;
                }
                GitHubQuery backlogQuery = new GitHubQuery(this, queryName, queryParams);
                backlogQuery.setSaved(true);
                addQuery(backlogQuery);
            }
        }
        return queries;
    }

    /**
     * Add a query.
     *
     * @param query a query
     */
    public void addQuery(GitHubQuery query) {
        getQueries().add(query);
    }

    /**
     * Remove a query.
     *
     * @param query a query
     */
    public void removeQuery(GitHubQuery query) {
        // remove configurations
        if (!GitHubDefaultQueries.isDefaultQuery(query)) {
            removeQueryConfig(query);
        }
        getQueries().remove(query);
        fireQueryListChanged();
    }

    private void removeQueryConfig(GitHubQuery query) {
        GitHubIssuesConfig.getInstance().removeQuery(this, query);
    }

    /**
     * Save a query.
     *
     * @param query a query
     */
    public void saveQuery(GitHubQuery query) {
        String displayName = query.getDisplayName();
        if (StringUtils.isEmpty(displayName)) {
            return;
        }
        GitHubIssuesConfig.getInstance().setQueryParams(this, query);
        addQuery(query);
        fireQueryListChanged();
    }

    // options
    public void optionsChanged() {
        GitHubIssuesOptions options = GitHubIssuesOptions.getInstance();
        Map<Type, Boolean> defaultQueryOptions = options.getDefaultQueryOptions();
        for (Map.Entry<Type, Boolean> entrySet : defaultQueryOptions.entrySet()) {
            Type type = entrySet.getKey();
            Boolean isEnabled = entrySet.getValue();
            GitHubQuery query = GitHubDefaultQueries.create(this, type);
            setDefaultQuery(query, isEnabled);
        }
        fireQueryListChanged();
    }

    private void setDefaultQuery(GitHubQuery query, boolean isEnabled) {
        if (isEnabled) {
            if (!getQueries().contains(query)) {
                getQueries().add(query);
            }
        } else {
            if (getQueries().contains(query)) {
                getQueries().remove(query);
            }
        }
    }

    void removed() {
        GitHubIssuesConfig.getInstance().removeRepository(this);
        if (queries != null) {
            queries.clear();
        }
    }

    /**
     * Add label.
     *
     * @param label Label
     * @return Label if label was added, otherwise {@code null}
     */
    @CheckForNull
    public Label addLabel(Label label) {
        GitHubClient client = createGitHubClient();
        if (client == null || ghRepository == null) {
            return null;
        }
        LabelService service = new LabelService(client);
        try {
            return service.createLabel(ghRepository, label);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    /**
     * Add a milestone.
     *
     * @param milestone milestone
     * @return Milestone if a milestone was added, otherwise {@code null}
     */
    @CheckForNull
    public Milestone addMilestone(Milestone milestone) {
        GitHubClient client = createGitHubClient();
        if (client == null || ghRepository == null) {
            return null;
        }
        MilestoneService service = new MilestoneService(client);
        try {
            return service.createMilestone(ghRepository, milestone);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    // Repository
    @CheckForNull
    public Repository getRepository() {
        if (ghRepository == null) {
            GitHubClient client = createGitHubClient();
            if (client == null) {
                return null;
            }
            try {
                RepositoryService service = new RepositoryService(client);
                ghRepository = service.getRepository(getRepositoryAuthor(), getRepositoryName());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Invalid repository:{0}", getFullName()); // NOI18N
            }
        }
        return ghRepository;
    }

    public void setRepositoryInfo(GitHubRepositoryInfo githubRepositoryInfo) {
        String url = String.format("https://github.com/%s/%s/issues/", githubRepositoryInfo.getRepositoryAuthor(), githubRepositoryInfo.getRepositoryName()); // NOI18N
        repositoryInfo = createRepositoryInfo(githubRepositoryInfo, url, githubRepositoryInfo.getUserName(), null, null, null);
        setProperties(githubRepositoryInfo);
    }

    private void setProperties(GitHubRepositoryInfo githubRepositoryInfo) {
        if (repositoryInfo != null) {
            repositoryInfo.putValue(PROPERTY_OAUTH_TOKEN, githubRepositoryInfo.getOAuthToken());
            repositoryInfo.putValue(PROPERTY_BOOLEAN_PROPERTY_FILE, String.valueOf(githubRepositoryInfo.isPropertyFile()));
            repositoryInfo.putValue(PROPERTY_REPOSITORY_AUTHOR, githubRepositoryInfo.getRepositoryAuthor());
            repositoryInfo.putValue(PROPERTY_REPOSITORY_NAME, githubRepositoryInfo.getRepositoryName());
        }
    }

    private RepositoryInfo createRepositoryInfo(GitHubRepositoryInfo githubRepositoryInfo, String url, String user, String httpUser, char[] password, char[] httpPassword) {
        String displayName = githubRepositoryInfo.getDisplayName();
        String id = repositoryInfo != null ? repositoryInfo.getID() : displayName + System.currentTimeMillis();
        RepositoryInfo info = new RepositoryInfo(
                id,
                GitHubIssuesConnector.ID,
                url,
                displayName, // display name
                displayName, // tooltip
                user,
                httpUser,
                password,
                httpPassword
        );
        return info;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void fireQueryListChanged() {
        propertyChangeSupport.firePropertyChange(RepositoryProvider.EVENT_QUERY_LIST_CHANGED, null, null);
    }

    private void fireUnsubmittedIssueChanged() {
        propertyChangeSupport.firePropertyChange(RepositoryProvider.EVENT_UNSUBMITTED_ISSUES_CHANGED, null, null);
    }

    public static List<Repository> getRepositories(String oauthToken) {
        GitHubClient client = new GitHubClient().setOAuth2Token(oauthToken);
        RepositoryService repositoryService = new RepositoryService(client);
        try {
            return repositoryService.getRepositories();
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
        return Collections.emptyList();
    }
}
