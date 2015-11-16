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

import com.junichi11.netbeans.modules.github.issues.GitHubIssues;
import com.junichi11.netbeans.modules.github.issues.GitHubIssuesConnector;
import static com.junichi11.netbeans.modules.github.issues.utils.DateUtils.DEFAULT_DATE_FORMAT;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import org.eclipse.egit.github.core.User;
import org.netbeans.modules.bugtracking.issuetable.IssueNode;
import org.netbeans.modules.bugtracking.issuetable.IssueNode.SummaryProperty;
import org.openide.nodes.Node.Property;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class GitHubIssueNode extends IssueNode<GitHubIssue> {

    public GitHubIssueNode(GitHubIssue issue) {
        super(
                GitHubIssuesConnector.ID,
                issue.getRepository().getID(),
                issue,
                GitHubIssues.getInstance().getIssueProvider(),
                GitHubIssues.getInstance().getIssueStatusProvider(),
                GitHubIssues.getInstance().getChangesProvider());
    }

    @Override
    protected Property<?>[] getProperties() {
        return new Property<?>[]{
            new IDProperty(),
            new SummaryProperty(),
            new CreatedProperty(),
            new UpdatedProperty(),
            new CreatedByProperty(),
            new AssigneeProperty(),
            new StatusProperty()
        };
    }

    @NbBundle.Messages({
        "GitHubIssueNode.id.displayName=ID",
        "GitHubIssueNode.id.shortDescription=ID"
    })
    private class IDProperty extends IssueNode<GitHubIssue>.IssueProperty<String> {

        public IDProperty() {
            super(GitHubIssue.LABEL_NAME_ID, String.class, Bundle.GitHubIssueNode_id_displayName(), Bundle.GitHubIssueNode_id_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return getIssueData().getID();
        }
    }

    @NbBundle.Messages({
        "GitHubIssueNode.created.displayName=Created",
        "GitHubIssueNode.created.shortDescription=Created"
    })
    private class CreatedProperty extends IssueNode<GitHubIssue>.IssueProperty<String> {

        public CreatedProperty() {
            super(GitHubIssue.LABEL_NAME_CREATED, String.class, Bundle.GitHubIssueNode_created_displayName(), Bundle.GitHubIssueNode_created_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            Date created = getIssueData().getCreated();
            if (created == null) {
                return ""; // NOI18N
            }
            return DEFAULT_DATE_FORMAT.format(created);
        }
    }

    @NbBundle.Messages({
        "GitHubIssueNode.updated.displayName=Updated",
        "GitHubIssueNode.updated.shortDescription=Updated"
    })
    private class UpdatedProperty extends IssueNode<GitHubIssue>.IssueProperty<String> {

        public UpdatedProperty() {
            super(GitHubIssue.LABEL_NAME_UPDATED, String.class, Bundle.GitHubIssueNode_updated_displayName(), Bundle.GitHubIssueNode_updated_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            Date updated = getIssueData().getUpdated();
            if (updated == null) {
                return ""; // NOI18N
            }
            return DEFAULT_DATE_FORMAT.format(updated);
        }
    }

    @NbBundle.Messages({
        "GitHubIssueNode.createdBy.displayName=Registered By",
        "GitHubIssueNode.createdBy.shortDescription=Registered By"
    })
    private class CreatedByProperty extends IssueNode<GitHubIssue>.IssueProperty<String> {

        public CreatedByProperty() {
            super(GitHubIssue.LABEL_NAME_CREATED_BY, String.class, Bundle.GitHubIssueNode_createdBy_displayName(), Bundle.GitHubIssueNode_createdBy_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            User user = getIssueData().getCreatedUser();
            if (user == null) {
                return ""; // NOI18N
            }
            return user.getLogin();
        }
    }

    @NbBundle.Messages({
        "GitHubIssueNode.assignee.displayName=Assignee",
        "GitHubIssueNode.assignee.shortDescription=Assignee"
    })
    private class AssigneeProperty extends IssueNode<GitHubIssue>.IssueProperty<String> {

        public AssigneeProperty() {
            super(GitHubIssue.LABEL_NAME_ASSIGNEE, String.class, Bundle.GitHubIssueNode_assignee_displayName(), Bundle.GitHubIssueNode_assignee_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            User user = getIssueData().getAssignee();
            if (user == null) {
                return ""; // NOI18N
            }
            return user.getLogin();
        }
    }

    @NbBundle.Messages({
        "GitHubIssueNode.status.displayName=Status",
        "GitHubIssueNode.status.shortDescription=Status"
    })
    public class StatusProperty extends IssueNode<GitHubIssue>.IssueProperty<String> {

        public StatusProperty() {
            super(GitHubIssue.LABEL_NAME_STATUS, String.class, Bundle.GitHubIssueNode_status_displayName(), Bundle.GitHubIssueNode_status_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            String status = getIssueData().getStatus();
            if (status == null) {
                return ""; // NOI18N
            }
            return status;
        }
    }
}
