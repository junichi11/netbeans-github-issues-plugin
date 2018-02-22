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
import com.junichi11.netbeans.modules.github.issues.GitHubIssuesConfig;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.utils.DateUtils;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import com.junichi11.netbeans.modules.github.issues.utils.UiUtils;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.User;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.bugtracking.commons.UIUtils;
import org.netbeans.modules.bugtracking.issuetable.ColumnDescriptor;
import org.netbeans.modules.bugtracking.issuetable.IssueNode;
import org.netbeans.modules.bugtracking.spi.IssueController;
import org.netbeans.modules.bugtracking.spi.IssueProvider;
import org.netbeans.modules.bugtracking.spi.IssueScheduleInfo;
import org.netbeans.modules.bugtracking.spi.IssueScheduleProvider;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider.Status;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class GitHubIssue {

    private final GitHubRepository repository;
    private Issue issue;
    private IssueNode node;
    private GitHubIssueController controller;
    private IssueScheduleInfo scheduleInfo;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static final String LABEL_NAME_ID = "github.issue.id"; // NOI18N
    public static final String LABEL_NAME_STATUS = "github.issue.status"; // NOI18N
    public static final String LABEL_NAME_CREATED = "github.issue.created"; // NOI18N
    public static final String LABEL_NAME_UPDATED = "github.issue.updated"; // NOI18N
    public static final String LABEL_NAME_CREATED_BY = "github.issue.created.by"; // NOI18N
    public static final String LABEL_NAME_ASSIGNEE = "github.issue.assignee"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(GitHubIssue.class.getName());

    public GitHubIssue(GitHubRepository repository) {
        this(repository, null);
    }

    public GitHubIssue(GitHubRepository repository, Issue issue) {
        this.repository = repository;
        this.issue = issue;
    }

    public GitHubRepository getRepository() {
        return repository;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Issue getIssue() {
        return issue;
    }

    @NbBundle.Messages({
        "GitHubIssue.new.issue.displayName=New Issue"
    })
    public String getDisplayName() {
        if (isNew()) {
            return Bundle.GitHubIssue_new_issue_displayName();
        }
        return String.format("%s - %s", issue.getNumber(), issue.getTitle()); // NOI18N
    }

    @NbBundle.Messages({
        "GitHubIssue.LBL.assignee=Assignee",
        "GitHubIssue.LBL.created=Created",
        "GitHubIssue.LBL.createdBy=Created by",
        "GitHubIssue.LBL.dueDate=Due date",
        "GitHubIssue.LBL.milestone=Milestone"
    })
    public String getTooltip() {
        // XXX improve
        StringBuilder sb = new StringBuilder();
        String title = String.format("%s [%s]", getDisplayName(), repository.getFullName()); // NOI18N
        sb.append("<html>"); // NOI18N
        sb.append("<b>").append(title).append("</b>"); // NOI18N
        sb.append("<hr>"); // NOI18N
        Date created = getCreated();
        Date dueDate = getDueDate();
        User assignee = getAssignee();
        User createdUser = getCreatedUser();
        Milestone milestone = getMilestone();
        if (created != null) {
            sb.append(Bundle.GitHubIssue_LBL_created()).append(" : ") // NOI18N
                    .append(DateUtils.DEFAULT_DATE_FORMAT.format(created)).append("<br>"); // NOI18N
        }
        if (dueDate != null) {
            sb.append(Bundle.GitHubIssue_LBL_dueDate()).append(" : ") // NOI18N
                    .append(DateUtils.DEFAULT_DATE_FORMAT.format(dueDate)).append("<br>"); // NOI18N
        }
        if (createdUser != null) {
            sb.append(Bundle.GitHubIssue_LBL_createdBy()).append(" : ") // NOI18N
                    .append(createdUser.getLogin()).append("<br>"); // NOI18N
        }
        if (assignee != null) {
            sb.append(Bundle.GitHubIssue_LBL_assignee()).append(" : ") // NOI18N
                    .append(assignee.getLogin()).append("<br>"); // NOI18N
        }
        if (milestone != null) {
            sb.append(Bundle.GitHubIssue_LBL_milestone()).append(" : ") // NOI18N
                    .append(milestone.getTitle()).append("<br>"); // NOI18N
        }
        sb.append("</html>"); // NOI18N
        return sb.toString();
    }

    public Status getIssueStatus() {
        return GitHubIssuesConfig.getInstance().getStatus(this);
    }

    public void setIssueStatus(Status status) {
        GitHubIssuesConfig.getInstance().setStatus(this, status);
        fireStatusChange();
    }

    public boolean isNew() {
        return issue == null;
    }

    public String getID() {
        if (issue != null) {
            return String.valueOf(issue.getNumber());
        }
        return null;
    }

    public Collection<String> getSubtasks() {
        return Collections.emptyList();
    }

    public String getSummary() {
        if (issue != null) {
            return issue.getTitle();
        }
        return null;
    }

    public String getStatus() {
        if (issue != null) {
            return issue.getState();
        }
        return null;
    }

    public User getAssignee() {
        if (issue != null) {
            return issue.getAssignee();
        }
        return null;
    }

    public User getCreatedUser() {
        if (issue != null) {
            return issue.getUser();
        }
        return null;
    }

    public Date getUpdated() {
        if (issue != null) {
            return issue.getUpdatedAt();
        }
        return null;
    }

    public Date getCreated() {
        if (issue != null) {
            return issue.getCreatedAt();
        }
        return null;
    }

    public Date getClosed() {
        if (issue != null) {
            return issue.getClosedAt();
        }
        return null;
    }

    public Milestone getMilestone() {
        if (issue != null) {
            return issue.getMilestone();
        }
        return null;
    }

    public boolean isFinished() {
        if (issue == null) {
            return false;
        }
        return "closed".equals(issue.getState()); // NOI18N
    }

    public boolean refresh() {
        return true;
    }

    public void refreshIssue() {
        getRepository().refresh(this);
        fireStatusChange();
    }

    public void addComment(String comment, boolean resolveAsFixed) {
        if (resolveAsFixed) {
            // close an issue
            Issue i = getIssue();
            if (i != null) {
                GitHubIssueState state = GitHubIssueState.toEnum(i.getState());
                if (state != GitHubIssueState.OPEN) {
                    LOGGER.log(Level.INFO, "This issue({0} #{1}) state is already closed.", new Object[]{i.getTitle(), i.getNumber()}); // NOI18N
                    return;
                }
                GitHubIssueSupport.toggleState(this);
            }
        }
    }

    public void attachFile(File file, String string, boolean bln) {
        // TODO
    }

    public GitHubIssueController getController() {
        if (controller == null) {
            controller = new GitHubIssueController(this);
        }
        return controller;
    }

    public Issue submitNewIssue(CreateIssueParams params) {
        Issue newIssue = repository.submitNewIssue(params);
        setNewIssue(newIssue);
        return newIssue;
    }

    /**
     * Set a new issue. Add GitHubIssue to the issue cache.
     *
     * @param newIssue a new Issue
     */
    private void setNewIssue(Issue newIssue) {
        if (newIssue != null) {
            setIssue(newIssue);
            // add to cache
            repository.addIssue(this);
            scheduleInfo = createScheduleInfo();
            fireChange();
            fireDataChange();
            fireScheduleChange();
            setIssueStatus(Status.SEEN);
        }
    }

    public Issue editIssue(CreateIssueParams params) {
        Issue editIssue = repository.editIssue(this, params);
        if (editIssue != null) {
            setIssue(editIssue);
            scheduleInfo = createScheduleInfo();
            fireChange();
            fireDataChange();
            fireScheduleChange();
        } else {
            repository.refresh(this);
        }
        return editIssue;
    }

    public Comment editComment(Comment comment, String editedBody) {
        if (editedBody != null) {
            String originalBody = comment.getBody();
            if (issue != null) {
                comment.setBody(editedBody);
                Comment editComment = GitHubIssueSupport.editComment(getRepository(), comment);
                if (editComment != null) {
                    String body = editComment.getBody();
                    String bodyHtml = StringUtils.markdownToHtml(body);
                    comment.setBodyHtml(String.format("<html>%s</html>", bodyHtml)); // NOI18N
                    comment.setBody(body);
                    comment.setUpdatedAt(editComment.getUpdatedAt());
                } else {
                    comment.setBody(originalBody);
                }
                return editComment;
            }
        }
        return null;
    }

    /**
     * Create a pull request from an existing issue.
     *
     * @param head head username:branch
     * @param base base branch name
     * @return PullRequest if it was created successfully, otherwise
     * {@code null}
     * @throws IOException
     */
    @CheckForNull
    public PullRequest createPullRequest(String head, String base) throws IOException {
        GitHubRepository repo = getRepository();
        PullRequest pullRequest = repo.createPullRequest(getIssue().getNumber(), head, base);
        return pullRequest;
    }

    /**
     * Create a new pull request. Title, body, base and head can be set.
     *
     * @param pullRequest PullRequest
     * @return PullRequest if new pull request has been created, otherwise
     * {@code null}
     * @throws IOException
     */
    @CheckForNull
    public PullRequest createPullRequest(PullRequest pullRequest) throws IOException {
        GitHubRepository repo = getRepository();
        PullRequest newPullRequest = repo.createPullRequest(pullRequest);
        if (newPullRequest != null) {
            Issue newIssue = repo.getIssue(newPullRequest.getNumber());
            setNewIssue(newIssue);
        }
        return newPullRequest;
    }

    public boolean isCreatedUser() {
        if (issue == null) {
            return false;
        }
        User user = issue.getUser();
        return user.getLogin().equals(repository.getUserName());
    }

    public List<Comment> getComments() {
        if (isNew()) {
            return Collections.emptyList();
        }
        return repository.getComments(issue.getNumber());
    }

    // schedule
    @NbBundle.Messages({
        "GitHubIssue.MSG.setSchedule=Set a due date to your milestone"
    })
    public void setSchedule(IssueScheduleInfo scheduleInfo) {
        UiUtils.showPlainDialog(Bundle.GitHubIssue_MSG_setSchedule());
    }

    public Date getDueDate() {
        String status = getStatus();
        if (status == null || GitHubIssueState.toEnum(status) == GitHubIssueState.CLOSED) {
            return null;
        }
        Milestone milestone = getMilestone();
        if (milestone != null) {
            return milestone.getDueOn();
        }
        return null;
    }

    public IssueScheduleInfo getSchedule() {
        String status = getStatus();
        if (status == null || GitHubIssueState.toEnum(status) == GitHubIssueState.CLOSED) {
            return null;
        }
        if (scheduleInfo == null) {
            scheduleInfo = createScheduleInfo();
        }
        return scheduleInfo;
    }

    private IssueScheduleInfo createScheduleInfo() {
        Milestone milestone = getMilestone();
        if (milestone != null) {
            Date dueDate = milestone.getDueOn();
            if (dueDate != null) {
                return new IssueScheduleInfo(dueDate, 1);
            }
        }
        return null;
    }

    public long getLastUpdatedTime() {
        Date updated = this.getUpdated();
        if (updated != null) {
            long time = updated.getTime();
            return time;
        }
        return -1L;
    }

    @NbBundle.Messages({
        "GitHubIssue.column.descriptor.issueType.displayName=Issue Type",
        "GitHubIssue.column.descriptor.summary.displayName=Summary",
        "GitHubIssue.column.descriptor.priority.displayName=Priority",
        "GitHubIssue.column.descriptor.created.displayName=Created",
        "GitHubIssue.column.descriptor.dueDate.displayName=Due Date",
        "GitHubIssue.column.descriptor.updated.displayName=Updated",
        "GitHubIssue.column.descriptor.createdBy.displayName=Created by",
        "GitHubIssue.column.descriptor.assignee.displayName=Assignee",
        "GitHubIssue.column.descriptor.status.displayName=Status",
        "GitHubIssue.column.descriptor.attachment.displayName=Attachment",
        "GitHubIssue.column.descriptor.sharedFile.displayName=Shared File"
    })
    public static ColumnDescriptor<String>[] getColumnDescriptors() {
        List<ColumnDescriptor<String>> descriptors = new LinkedList<>();
        JTable table = new JTable();
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ID, String.class, "ID", "ID", UIUtils.getColumnWidthInPixels(6, table)));
        descriptors.add(new ColumnDescriptor<>(IssueNode.LABEL_NAME_SUMMARY, String.class, Bundle.GitHubIssue_column_descriptor_summary_displayName(), Bundle.GitHubIssue_column_descriptor_summary_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_CREATED, String.class, Bundle.GitHubIssue_column_descriptor_created_displayName(), Bundle.GitHubIssue_column_descriptor_created_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_UPDATED, String.class, Bundle.GitHubIssue_column_descriptor_updated_displayName(), Bundle.GitHubIssue_column_descriptor_updated_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_CREATED_BY, String.class, Bundle.GitHubIssue_column_descriptor_createdBy_displayName(), Bundle.GitHubIssue_column_descriptor_createdBy_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ASSIGNEE, String.class, Bundle.GitHubIssue_column_descriptor_assignee_displayName(), Bundle.GitHubIssue_column_descriptor_assignee_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_STATUS, String.class, Bundle.GitHubIssue_column_descriptor_status_displayName(), Bundle.GitHubIssue_column_descriptor_status_displayName()));
        return descriptors.toArray(new ColumnDescriptor[descriptors.size()]);
    }

    /**
     * Get issue node. Use an issue node to add to an issue table.
     *
     * @return issue node
     */
    public IssueNode getIssueNode() {
        if (node == null) {
            node = createIssueNode();
        }
        return node;
    }

    /**
     * Get recent changes.
     *
     * @return changes
     */
    public String getRecentChanges() {
        return ""; // NOI18N
    }

    private IssueNode createIssueNode() {
        return new GitHubIssueNode(this);
    }

    public boolean isEditableUser() {
        return isCreatedUser() || repository.isCollaborator();
    }

    void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    void fireChange() {
        propertyChangeSupport.firePropertyChange(IssueController.PROP_CHANGED, null, null);
    }

    void fireDataChange() {
        propertyChangeSupport.firePropertyChange(IssueProvider.EVENT_ISSUE_DATA_CHANGED, null, null);
    }

    void fireStatusChange() {
        propertyChangeSupport.firePropertyChange(IssueStatusProvider.EVENT_STATUS_CHANGED, null, null);
    }

    void fireScheduleChange() {
        propertyChangeSupport.firePropertyChange(IssueScheduleProvider.EVENT_ISSUE_SCHEDULE_CHANGED, null, null);
    }

}
