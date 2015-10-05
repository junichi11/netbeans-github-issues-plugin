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
package com.junichi11.netbeans.modules.github.issues.issue;

import com.junichi11.netbeans.modules.github.issues.GitHubIssueState;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import com.junichi11.netbeans.modules.github.issues.utils.UiUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.netbeans.api.annotations.common.CheckForNull;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author junichi11
 */
public final class GitHubIssueSupport {

    private static final Logger LOGGER = Logger.getLogger(GitHubIssueSupport.class.getName());

    private GitHubIssueSupport() {
    }

    /**
     * Close or reopen an issue.
     *
     * @param gitHubIssue
     * @return {@code true} if state was changed, otherwise {@code false}
     */
    public static boolean toggleState(GitHubIssue gitHubIssue) {
        if (gitHubIssue == null) {
            return false;
        }
        Issue issue = gitHubIssue.getIssue();
        if (issue == null) {
            return false;
        }
        GitHubIssueState state = GitHubIssueState.toEnum(issue.getState());
        try {
            GitHubRepository repository = gitHubIssue.getRepository();
            IssueService issueService = createIssueService(repository);

            if (issueService != null) {
                switch (state) {
                    case CLOSED:
                        issue.setState(GitHubIssueState.OPEN.toString());
                        break;
                    case OPEN:
                        issue.setState(GitHubIssueState.CLOSED.toString());
                        break;
                    default:
                        throw new AssertionError();
                }
                Issue editedIssue = issueService.editIssue(repository.getRepository(), issue);
                if (editedIssue != null) {
                    gitHubIssue.setIssue(editedIssue);
                    StatusDisplayer.getDefault().setStatusText("Status has been changed.");
                    gitHubIssue.fireDataChange();
                    return true;
                }
            }
        } catch (IOException ex) {
            // set original state
            issue.setState(state.toString());
            LOGGER.log(Level.WARNING, ex.getMessage());
            UiUtils.showErrorDialog("Can't change issue status.");
        }
        return false;
    }

    /**
     * Add comment.
     *
     * @param gitHubIssue GitHubIssue
     * @param comment comment
     * @return Comment
     */
    public static Comment comment(GitHubIssue gitHubIssue, String comment) {
        if (StringUtils.isEmpty(comment)) {
            return null;
        }
        Issue issue = gitHubIssue.getIssue();
        if (issue != null) {
            try {
                GitHubRepository gitHubRepository = gitHubIssue.getRepository();
                Comment createdComment = createComment(gitHubRepository, issue, comment);
                if (createdComment != null) {
                    return createdComment;
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
        return null;
    }

    private static Comment createComment(GitHubRepository gitHubRepository, Issue issue, String comment) throws IOException {
        IssueService issueService = createIssueService(gitHubRepository);
        if (issueService == null) {
            return null;
        }
        return issueService.createComment(gitHubRepository.getRepository(), issue.getNumber(), comment);
    }

    /**
     * Edit comment.
     *
     * @param repository GitHubRepository
     * @param comment Comment
     * @return comment if editing successfully, otherwise {@code null}
     */
    @CheckForNull
    public static Comment editComment(GitHubRepository repository, Comment comment) {
        IssueService issueService = createIssueService(repository);
        if (issueService == null) {
            return null;
        }
        try {
            return issueService.editComment(repository.getRepository(), comment);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    /**
     * Delete comment.
     *
     * @param repository
     * @param comment
     * @return {@code true} if comment is deleted, otherwise {@code false}
     */
    public static boolean deleteComment(GitHubRepository repository, Comment comment) {
        IssueService issueService = createIssueService(repository);
        if (issueService == null) {
            return false;
        }
        boolean success = true;
        try {
            issueService.deleteComment(repository.getRepository(), comment.getId());
        } catch (IOException ex) {
            success = false;
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return success;
    }

    /**
     * Show in browser.
     *
     * @param gitHubIssue GitHubIssue
     */
    public static void showInBrowser(GitHubIssue gitHubIssue) {
        if (!gitHubIssue.isNew()) {
            Issue issue = gitHubIssue.getIssue();
            String htmlUrl = issue.getHtmlUrl();
            try {
                URL url = new URL(htmlUrl);
                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.WARNING, "Can't open the browser:{0}", ex.getMessage()); // NOI18N
            }
        }

    }

    @CheckForNull
    private static IssueService createIssueService(GitHubRepository gitHubRepository) {
        GitHubClient client = gitHubRepository.createGitHubClient();
        if (client == null) {
            return null;
        }
        return new IssueService(client);
    }

}
