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
package com.junichi11.netbeans.modules.github.issues;

import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssue;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueFinder;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssuePriorityProvider;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueProvider;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueScheduleProvider;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueStatusProvider;
import com.junichi11.netbeans.modules.github.issues.query.GitHubQuery;
import com.junichi11.netbeans.modules.github.issues.query.GitHubQueryProvider;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepositoryProvider;
import java.awt.Color;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.netbeans.modules.bugtracking.issuetable.IssueNode;
import org.netbeans.modules.bugtracking.spi.BugtrackingSupport;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public final class GitHubIssues {

    private BugtrackingSupport<GitHubRepository, GitHubQuery, GitHubIssue> bugtrackingSupport;
    private GitHubIssueFinder issueFinder;
    private GitHubIssuePriorityProvider issuePriorityProvider;
    private GitHubIssueProvider issueProvider;
    private GitHubIssueScheduleProvider issueScheduleProvider;
    private GitHubIssueStatusProvider issueStatusProvider;
    private GitHubQueryProvider queryProvider;
    private GitHubRepositoryProvider repositoryProvider;
    private IssueNode.ChangesProvider<GitHubIssue> changesProvider;
    private RequestProcessor rp;
    // colors
    public static final Color GREEN_COLOR = Color.decode("#6cc644"); // NOI18N
    public static final Color RED_COLOR = Color.decode("#bd2c00"); // NOI18N
    public static final Color OPEN_STATE_COLOR = Color.decode("#6cc644"); // NOI18N
    public static final Color CLOSED_STATE_COLOR = Color.decode("#bd2c00"); // NOI18N
    public static final Color MERGED_STATE_COLOR = Color.decode("#6e5494"); // NOI18N

    public static final String DEFAULT_HOSTNAME = IGitHubConstants.HOST_API;
    // url formats
    /**
     * https://[hostname]/[owner]/[repository]/raw/[sha]/[file name]
     */
    public static final String RAW_URL_FORMAT = "https://%s/%s/%s/raw/%s/%s"; // NOI18N

    private static final GitHubIssues INSTANCE = new GitHubIssues();

    private GitHubIssues() {
    }

    public static GitHubIssues getInstance() {
        return INSTANCE;
    }

    public BugtrackingSupport<GitHubRepository, GitHubQuery, GitHubIssue> getBugtrackingSupport() {
        if (bugtrackingSupport == null) {
            bugtrackingSupport = new BugtrackingSupport<>(getRepositoryProvider(), getQueryProvider(), getIssueProvider());
        }
        return bugtrackingSupport;
    }

    public RequestProcessor getRequestProcessor() {
        if (rp == null) {
            rp = new RequestProcessor("GithubIssues", 1, true); // NOI18N
        }
        return rp;
    }

    public GitHubIssueProvider getIssueProvider() {
        if (issueProvider == null) {
            issueProvider = new GitHubIssueProvider();
        }
        return issueProvider;
    }

    public GitHubIssueStatusProvider getIssueStatusProvider() {
        if (issueStatusProvider == null) {
            issueStatusProvider = new GitHubIssueStatusProvider();
        }
        return issueStatusProvider;
    }

    public GitHubIssuePriorityProvider getIssuePriorityProvider() {
        if (issuePriorityProvider == null) {
            issuePriorityProvider = new GitHubIssuePriorityProvider();
        }
        return issuePriorityProvider;
    }

    public GitHubIssueScheduleProvider getIssueScheduleProvider() {
        if (issueScheduleProvider == null) {
            issueScheduleProvider = new GitHubIssueScheduleProvider();
        }
        return issueScheduleProvider;
    }

    public GitHubIssueFinder getIssueFinder() {
        if (issueFinder == null) {
            issueFinder = new GitHubIssueFinder();
        }
        return issueFinder;
    }

    public GitHubQueryProvider getQueryProvider() {
        if (queryProvider == null) {
            queryProvider = new GitHubQueryProvider();
        }
        return queryProvider;
    }

    public GitHubRepositoryProvider getRepositoryProvider() {
        if (repositoryProvider == null) {
            repositoryProvider = new GitHubRepositoryProvider();
        }
        return repositoryProvider;
    }

    public IssueNode.ChangesProvider<GitHubIssue> getChangesProvider() {
        if (changesProvider == null) {
            changesProvider = new IssueNode.ChangesProvider<GitHubIssue>() {
                @Override
                public String getRecentChanges(GitHubIssue issue) {
                    return issue.getRecentChanges();
                }
            };
        }
        return changesProvider;
    }

}
