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

import com.junichi11.netbeans.modules.github.issues.GitHubIcons;
import com.junichi11.netbeans.modules.github.issues.GitHubIssueState;
import com.junichi11.netbeans.modules.github.issues.utils.GitHubIssuesUtils;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.egit.github.core.Issue;
import org.netbeans.modules.bugtracking.spi.IssuePriorityInfo;
import org.netbeans.modules.bugtracking.spi.IssuePriorityProvider;

/**
 *
 * @author junichi11
 */
public class GitHubIssuePriorityProvider implements IssuePriorityProvider<GitHubIssue> {

    /**
     * GitHub Issues doesn't have priorities. Use this feature to show icons.
     */
    public enum GitHubIssuePriority {
        Open("open", "Open", GitHubIcons.OPEN_ISSUE_IMAGE_16), // NOI18N
        Closed("closed", "Closed", GitHubIcons.CLOSED_ISSUE_IMAGE_16), // NOI18N
        OpenPullRequest("open.pull.request", "Open", GitHubIcons.OPEN_PULL_REQUEST_IMAGE_16), // NOI18N
        ClosedPullRequest("closed.pull.request", "Closed", GitHubIcons.CLOSED_PULL_REQUEST_IMAGE_16), // NOI18N
        MergedPullRequest("merged.pull.request", "Merged", GitHubIcons.MERGED_PULL_REQUEST_IMAGE_16), // NOI18N
        None("", "", null); // NOI18N

        private final String id;
        private final String state;
        private final Image image;

        private GitHubIssuePriority(String id, String state, Image image) {
            this.id = id;
            this.state = state;
            this.image = image;
        }

        public String getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        public Image getImage() {
            return image;
        }

    }

    @Override
    public String getPriorityID(GitHubIssue githubIssue) {
        Issue issue = githubIssue.getIssue();
        if (issue == null) {
            return ""; // NOI18N
        }
        GitHubIssuePriority priority = GitHubIssuePriority.None;
        GitHubIssueState state = GitHubIssueState.toEnum(issue.getState());
        boolean isPullRequest = GitHubIssuesUtils.isPullRequest(issue);
        switch (state) {
            case CLOSED:
                if (isPullRequest) {
                    // XXX merged should be added.
                    // But set the same priority as the closed becase many requests are sent via API.
                    priority = GitHubIssuePriority.ClosedPullRequest;
                } else {
                    priority = GitHubIssuePriority.Closed;
                }
                break;
            case OPEN:
                if (isPullRequest) {
                    priority = GitHubIssuePriority.OpenPullRequest;
                } else {
                    priority = GitHubIssuePriority.Open;
                }
            default:
                break;
        }
        return priority.getId();
    }

    @Override
    public IssuePriorityInfo[] getPriorityInfos() {
        List<IssuePriorityInfo> info = new ArrayList<>();
        for (GitHubIssuePriority priority : GitHubIssuePriority.values()) {
            Image image = priority.getImage();
            if (image != null) {
                info.add(new IssuePriorityInfo(priority.getId(), priority.getState(), image));
            } else {
                info.add(new IssuePriorityInfo(priority.getId(), priority.getState()));
            }
        }
        return info.toArray(new IssuePriorityInfo[info.size()]);
    }

}
