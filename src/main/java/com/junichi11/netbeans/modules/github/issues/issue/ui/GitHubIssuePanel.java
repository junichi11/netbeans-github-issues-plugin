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
package com.junichi11.netbeans.modules.github.issues.issue.ui;

import com.junichi11.netbeans.modules.github.issues.GitHubCache;
import com.junichi11.netbeans.modules.github.issues.GitHubIcons;
import com.junichi11.netbeans.modules.github.issues.GitHubIssueState;
import com.junichi11.netbeans.modules.github.issues.GitHubIssues;
import static com.junichi11.netbeans.modules.github.issues.GitHubIssues.CLOSED_STATE_COLOR;
import static com.junichi11.netbeans.modules.github.issues.GitHubIssues.MERGED_STATE_COLOR;
import static com.junichi11.netbeans.modules.github.issues.GitHubIssues.OPEN_STATE_COLOR;
import com.junichi11.netbeans.modules.github.issues.GitHubIssuesConfig;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssue;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueController.CloseReopenAction;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueController.CommentAction;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueController.CreatePullRequestAction;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueController.SubmitIssueAction;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueSupport;
import com.junichi11.netbeans.modules.github.issues.options.GitHubIssuesOptions;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.ui.AttributesListCellRenderer;
import com.junichi11.netbeans.modules.github.issues.utils.GitHubIssuesUtils;
import com.junichi11.netbeans.modules.github.issues.utils.UiUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.MergeStatus;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.IssueService;
import org.netbeans.api.annotations.common.CheckForNull;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.pegdown.PegDownProcessor;

/**
 *
 * @author junichi11
 */
public class GitHubIssuePanel extends JPanel {

    private static final long serialVersionUID = -4871443269659315479L;

    private PullRequest newPullRequest;
    private GitHubIssue gitHubIssue;
    private CommentsPanel commentsPanel;
    private FilesChangedPanel filesChangedPanel;
    private CommitsPanel commitsPanel;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final DefaultComboBoxModel<Milestone> milestoneComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<User> assigneeComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultListModel<Label> labelsListModel = new DefaultListModel<>();
    private static final Logger LOGGER = Logger.getLogger(GitHubIssuePanel.class.getName());
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // NOI18N
    private final String repositoryId;

    // manage templates options
    private static final String TEMPLATES_ADD_OPTION = Bundle.GitHubIssuePanel_manage_templates_add_option();
    private static final String TEMPLATES_EDIT_OPTION = Bundle.GitHubIssuePanel_manage_templates_edit_option();
    private static final String TEMPLATES_DUPLICATE_OPTION = Bundle.GitHubIssuePanel_manage_templates_duplicate_option();
    private static final String TEMPLATES_REMOVE_OPTION = Bundle.GitHubIssuePanel_manage_templates_remove_option();
    private static final String TEMPLATES_CLOSE_OPTION = Bundle.GitHubIssuePanel_manage_templates_close_option();

    /**
     * Creates new form GitHubIssuePanel
     */
    public GitHubIssuePanel(String repositoryId) {
        this.repositoryId = repositoryId;
        initComponents();
        init();
    }

    private void init() {
        // set cell renderer
        milestoneComboBox.setRenderer(new AttributesListCellRenderer(milestoneComboBox.getRenderer()));
        assigneeComboBox.setRenderer(new AttributesListCellRenderer(assigneeComboBox.getRenderer(), repositoryId));
        labelsList.setCellRenderer(new AttributesListCellRenderer(labelsList.getCellRenderer()));
        milestoneComboBox.setModel(milestoneComboBoxModel);
        assigneeComboBox.setModel(assigneeComboBoxModel);
        labelsList.setModel(labelsListModel);

        // add document listener
        DefaultDocumentListener documentListener = new DefaultDocumentListener();
        titleTextField.getDocument().addDocumentListener(documentListener);

        // add property change listener
        PropertyChangeListener mergeChangeListener = new MergePropertyChangeListener();
        mergePanel.addPropertyChangeListener(mergeChangeListener);

        // set error
        headerErrorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        setErrorMessage(""); // NOI18N

        // change header title size
        Font font = headerErrorLabel.getFont();
        headerNameLabel.setFont(font.deriveFont((float) (font.getSize() * 1.5)));
        headerNameLabel.setIcon(GitHubIcons.GITHUB_ICON_32);

        headerStatusLabel.setBorder(new EmptyBorder(3, 5, 3, 5));

        // text bold
        Font bold = new Font(font.getName(), Font.BOLD, font.getSize());
        headerCreatedByUserLabel.setFont(bold);
        headerStatusLabel.setFont(bold);
        commentsPanel = new CommentsPanel();
        ((GroupLayout) mainCommentsPanel.getLayout()).replace(dummyCommentsPanel, commentsPanel);
        filesChangedPanel = new FilesChangedPanel();
        ((GroupLayout) mainFilesChangedPanel.getLayout()).replace(dummyFilesChangedPanel, filesChangedPanel);
        commitsPanel = new CommitsPanel();
        ((GroupLayout) mainCommitsPanel.getLayout()).replace(dummyCommitsPanel, commitsPanel);
    }

    public boolean isNew() {
        assert gitHubIssue != null;
        return gitHubIssue.isNew();
    }

    public void setIssue(GitHubIssue gitHubIssue) {
        this.gitHubIssue = gitHubIssue;

        // insert the default template
        if (isNew()) {
            if (GitHubIssuesOptions.getInstance().insertDefaultTemplate()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String template = GitHubIssuesConfig.getInstance().getTemplate(GitHubIssuesConfig.DEFAULT_TEMPLATE_NAME);
                        descriptionTabbedPanel.setText(template);
                    }
                });
            }
        }
    }

    public GitHubIssue getIssue() {
        return gitHubIssue;
    }

    private GitHubRepository getRepository() {
        if (gitHubIssue == null) {
            return null;
        }
        return gitHubIssue.getRepository();
    }

    @CheckForNull
    private PullRequest getPullRequest() {
        if (isPullRequest()) {
            GitHubIssue issue = getIssue();
            GitHubRepository repository = getRepository();
            if (repository != null) {
                return repository.getPullRequest(issue.getIssue().getNumber(), false);
            }
        }
        return null;
    }

    private boolean isPullRequest() {
        GitHubIssue issue = getIssue();
        return GitHubIssuesUtils.isPullRequest(issue.getIssue());
    }

    private boolean isMerged() {
        if (isPullRequest()) {
            PullRequest pullRequest = getPullRequest();
            if (pullRequest != null) {
                return pullRequest.isMerged();
            }
        }
        return false;
    }

    private boolean isMergeable() {
        if (isPullRequest()) {
            PullRequest pullRequest = getPullRequest();
            if (pullRequest != null) {
                return pullRequest.isMergeable()
                        && !pullRequest.isMerged()
                        && pullRequest.getState().equals("open"); // NOI18N
            }
        }
        return false;
    }

    /**
     * Check whether the button to set a new pull request is selected.
     *
     * @return {@code true} if the button is selected, otherwise {@code false}
     */
    public boolean isNewPullRequestSelected() {
        return newPullRequestToggleButton.isSelected();
    }

    /**
     * Set whether the button is selected.
     *
     * @param isSelected {@code true} the button is selected, otherwise
     * {@code false}
     */
    public void setNewPullRequestSelected(boolean isSelected) {
        newPullRequestToggleButton.setSelected(isSelected);
    }

    /**
     * Set a new PullRequest. Change the texts for the issue name label and the
     * submit button.
     *
     * @param pullRequest PullRequest must have title, body, base and head,
     * {@code null} if create a new issue.
     */
    public void setNewPullRequest(PullRequest pullRequest) {
        assert EventQueue.isDispatchThread();
        this.newPullRequest = pullRequest;
        setPullRequestHeader();
        if (pullRequest != null) {
            // pull request
            headerNameLabel.setText(Bundle.GitHubIssuePanel_label_header_name_new_pull_request());
            headerSubmitButton.setText(Bundle.GitHubIssuePanel_label_header_submit_button_new_pull_request());
        } else {
            // issue
            headerNameLabel.setText(Bundle.GitHubIssuePanel_label_header_name_new());
            headerSubmitButton.setText(Bundle.GitHubIssuePanel_label_header_submit_button_new());
        }
    }

    /**
     * Get a new PullRequest.
     *
     * @return a new PullRequest
     */
    @CheckForNull
    public PullRequest getNewPullRequest() {
        return newPullRequest;
    }

    @NbBundle.Messages({
        "# {0} - count",
        "GitHubIssuePanel.comment.count=Comment({0})",
        "# {0} - count",
        "GitHubIssuePanel.files.changed.count=Files Changed({0})",
        "# {0} - count",
        "GitHubIssuePanel.commit.count=Commits({0})"
    })
    public void update() {
        assert EventQueue.isDispatchThread();
        // header
        setHeader();
        if (gitHubIssue == null) {
            return;
        }

        GitHubRepository repository = getRepository();
        if (repository == null) {
            return;
        }

        boolean isCollaborator = repository.isCollaborator();
        // collaborator?
        if (isCollaborator) {
            GitHubCache cache = GitHubCache.create(repository);
            // milestone
            updateMilestones(cache, false);

            // assignee
            List<User> collaborators = cache.getCollaborators();
            assigneeComboBoxModel.removeAllElements();
            assigneeComboBoxModel.addElement(null);
            for (User collaborator : collaborators) {
                assigneeComboBoxModel.addElement(collaborator);
            }

            // label
            updateLables(cache, false);
        }

        // existing issue
        boolean isExistingIssue = !isNew();
        boolean isPullRequest = isPullRequest();
        if (isExistingIssue) {
            Issue issue = gitHubIssue.getIssue();
            if (issue != null) {
                // set existing info
                // user infomation
                User user = issue.getUser();
                GitHubCache cache = GitHubCache.create(repository);
                Icon userIcon = cache.getUserIcon(user);

                // header
                headerCreatedDateLabel.setText(DATE_FORMAT.format(issue.getCreatedAt()));
                headerUpdatedDateLabel.setText(DATE_FORMAT.format(issue.getUpdatedAt()));
                headerCreatedByUserLabel.setText(user.getLogin());
                headerCreatedByUserLabel.setIcon(userIcon);

                // title
                titleTextField.setText(issue.getTitle());
                Dimension dim = titleTextField.getPreferredSize();
                titleTextField.setMinimumSize(new Dimension(0, dim.height));
                titleTextField.setPreferredSize(new Dimension(0, dim.height));

                // description
                descriptionTabbedPanel.setText(issue.getBody());

                // assignee
                User assignee = issue.getAssignee();
                if (assignee != null) {
                    setAssigneeSelected(assignee);
                }

                // milestone
                Milestone milestone = issue.getMilestone();
                if (milestone != null) {
                    setMilestoneSelected(milestone);
                }

                // labels
                List<Label> labels = issue.getLabels();
                if (!labels.isEmpty()) {
                    setLabelsSelected(labels);
                }

                // set attributes
                attributesViewPanel.setAttributes(issue, repository);

                // new comment
                GitHubIssueState state = GitHubIssueState.toEnum(issue.getState());
                setNewCommentButtonCloseOrReopen(state == GitHubIssueState.CLOSED);

                // editable
                boolean isEditable = gitHubIssue.isEditableUser();
                titleTextField.setEditable(isEditable);
                descriptionTabbedPanel.setEditable(isEditable);

                // add comments
                commentsPanel.removeAllComments();
                List<Comment> comments = gitHubIssue.getComments();
                // set count
                commentsCollapsibleSectionPanel.setLabel(Bundle.GitHubIssuePanel_comment_count(comments.size()));
                PegDownProcessor processor = GitHubIssues.getInstance().getPegDownProcessor();
                for (Comment comment : comments) {
                    String body = comment.getBody();
                    String bodyHtml = processor.markdownToHtml(body);
                    comment.setBodyHtml(String.format("<html>%s</html>", bodyHtml)); // NOI18N
                }
                commentsPanel.addComments(comments, repository);

                // PR
                if (isPullRequest) {
                    PullRequest pullRequest = getPullRequest();
                    PullRequestMarker base = pullRequest.getBase();
                    int id = getIssue().getIssue().getNumber();
                    String summary = getIssue().getSummary();

                    // commits
                    List<RepositoryCommit> commits = repository.getCommits(id);
                    commitsPanel.removeAllCommits();
                    commitsCollapsibleSectionPanel.setLabel(Bundle.GitHubIssuePanel_commit_count(commits.size()));
                    for (RepositoryCommit commit : commits) {
                        Icon commiterIcon = cache.getUserIcon(commit.getCommitter());
                        commitsPanel.addCommit(commit.getCommit(), commiterIcon);
                    }

                    // files changed
                    List<CommitFile> pullRequestsFiles = repository.getPullRequestsFiles(issue.getNumber());
                    filesChangedPanel.setDisplayName(String.format("[Diff] #%s - %s", id, summary)); // NOI18N
                    filesChangedPanel.removeAllFiles();
                    for (CommitFile file : pullRequestsFiles) {
                        filesChangedPanel.addFile(file, base);
                    }
                    filesChangedPanel.setDetails(pullRequest);
                    filesChangedcollapsibleSectionPanel.setLabel(Bundle.GitHubIssuePanel_files_changed_count(pullRequestsFiles.size()));

                    // mergeable?
                    boolean isMergeable = isMergeable();
                    mergePanel.setMergeButtonEnabled(isMergeable);
                    if (isMergeable) {
                        mergePanel.setCommitMessage(summary);
                    }
                }
            }
        }

        // visibility
        setCommentsSectionVisible(isExistingIssue);
        setNewCommentVisible(isExistingIssue);
        setCollaboratorsComponentsVisible(isCollaborator);
        attributesViewPanel.setVisible(isExistingIssue);
        // PR
        commitsCollapsibleSectionPanel.setVisible(isPullRequest);
        filesChangedcollapsibleSectionPanel.setVisible(isPullRequest);
        mergePanel.setVisible(isPullRequest && isCollaborator);

        fireChange();
    }

    private void updateMilestones(GitHubCache cache, boolean force) {
        List<Milestone> milestones = cache.getMilestones(IssueService.STATE_OPEN, force);
        milestoneComboBoxModel.removeAllElements();
        milestoneComboBoxModel.addElement(null);
        for (Milestone milestone : milestones) {
            milestoneComboBoxModel.addElement(milestone);
        }
    }

    private void updateLables(GitHubCache cache, boolean force) {
        List<Label> labels = cache.getLabels(force);
        labelsListModel.removeAllElements();
        labelsListModel.addElement(null);
        for (Label label : labels) {
            labelsListModel.addElement(label);
        }
    }

    public void loadComments() {
        commentsPanel.loadComments(getRepository());
        fireChange();
    }

    private void setAssigneeSelected(User assignee) {
        int size = assigneeComboBoxModel.getSize();
        for (int i = 0; i < size; i++) {
            User user = assigneeComboBoxModel.getElementAt(i);
            if (user == null) {
                continue;
            }
            if (user.getLogin().equals(assignee.getLogin())) {
                assigneeComboBox.setSelectedItem(user);
                break;
            }
        }
    }

    private void setMilestoneSelected(Milestone milestone) {
        if (milestone == null) {
            return;
        }
        int size = milestoneComboBoxModel.getSize();
        for (int i = 0; i < size; i++) {
            Milestone m = milestoneComboBoxModel.getElementAt(i);
            if (m == null) {
                continue;
            }
            if (m.getNumber() == milestone.getNumber()) {
                milestoneComboBox.setSelectedItem(m);
                break;
            }
        }

        // closed milestone
        milestoneComboBoxModel.addElement(milestone);
        milestoneComboBox.setSelectedItem(milestone);
    }

    private void setLabelsSelected(List<Label> labels) {
        int size = labels.size();
        int[] indices = new int[size];
        int labelIndex = 0;
        for (Label label : labels) {
            for (int i = 0; i < labelsListModel.getSize(); i++) {
                Label l = labelsListModel.getElementAt(i);
                if (l == null) {
                    continue;
                }
                if (l.getName().equals(label.getName())) {
                    indices[labelIndex] = i;
                    break;
                }
            }
            labelIndex++;
        }
        labelsList.setSelectedIndices(indices);
    }

    private void setCollaboratorsComponentsVisible(boolean isVisible) {
        labelsLabel.setVisible(isVisible);
        labelsScrollPane.setVisible(isVisible);
        milestoneLabel.setVisible(isVisible);
        milestoneComboBox.setVisible(isVisible);
        assigneeLabel.setVisible(isVisible);
        assigneeComboBox.setVisible(isVisible);
        assignYourselfLinkButton.setVisible(isVisible);
        newLabelButton.setVisible(isVisible);
        newMilestoneButton.setVisible(isVisible);
    }

    @NbBundle.Messages({
        "GitHubIssuePanel.label.header.name.new=New Issue",
        "GitHubIssuePanel.label.header.name.new.pull.request=New Pull Request",
        "GitHubIssuePanel.label.header.submit.button=Submit",
        "GitHubIssuePanel.label.header.submit.button.new=Submit new issue",
        "GitHubIssuePanel.label.header.submit.button.new.pull.request=Submit new pull request"
    })
    private void setHeader() {
        setErrorMessage(""); // NOI18N
        if (gitHubIssue == null) {
            headerSubmitButton.setVisible(false);
            return;
        }
        boolean isNew = isNew();
        GitHubIssueState state = GitHubIssueState.NEW;

        // new pull request
        newPullRequestToggleButton.setVisible(isNew);

        if (isNew) {
            headerNameLabel.setText(Bundle.GitHubIssuePanel_label_header_name_new());
            headerSubmitButton.setText(Bundle.GitHubIssuePanel_label_header_submit_button_new());
        } else {
            String summary = gitHubIssue.getSummary();
            headerNameLabel.setText(String.format("%s #%s", summary, gitHubIssue.getID())); // NOI18N
            Dimension dim = headerNameLabel.getPreferredSize();
            headerNameLabel.setMinimumSize(new Dimension(0, dim.height));
            headerNameLabel.setPreferredSize(new Dimension(0, dim.height));

            headerSubmitButton.setText(Bundle.GitHubIssuePanel_label_header_submit_button());
            headerSubmitButton.setVisible(gitHubIssue.isEditableUser());
            Issue issue = gitHubIssue.getIssue();
            if (issue != null) {
                state = GitHubIssueState.toEnum(issue.getState());
            }
        }
        setHeaderStatus(state);

        setPullRequestHeader();
    }

    private void setPullRequestHeader() {
        boolean isPullRequest = isPullRequest();
        boolean isNewPullRequestSelected = isNewPullRequestSelected();
        if (isNew()) {
            changeToPullRequestButton.setVisible(false);
            if (isNewPullRequestSelected) {
                setPullRequestBaseHeadLabel(getNewPullRequest());
            } else {
                setPullRequestBaseHeadLabel(null);
            }
        } else {
            // existing issue
            headerPrBaseHeadLabel.setVisible(isPullRequest);
            if (isPullRequest) {
                setPullRequestBaseHeadLabel(getPullRequest());
                changeToPullRequestButton.setVisible(false);
            } else {
                headerPrBaseHeadLabel.setText(" "); // NOI18N
                changeToPullRequestButton.setVisible(getIssue().isCreatedUser());
            }
        }

    }

    private void setPullRequestBaseHeadLabel(PullRequest pullRequest) {
        if (pullRequest != null) {
            PullRequestMarker base = pullRequest.getBase();
            PullRequestMarker head = pullRequest.getHead();
            setPullRequestBaseHeadLabel(base.getLabel(), head.getLabel());
        } else {
            setPullRequestBaseHeadLabel("", ""); // NOI18N
        }
        headerPrBaseHeadLabel.setVisible(pullRequest != null);
    }

    private void setPullRequestBaseHeadLabel(String base, String head) {
        headerPrBaseHeadLabel.setText(String.format("<html>Base: <b>%s</b> Head: <b>%s</b>", base, head)); // NOI18N
    }

    private void setHeaderStatus(GitHubIssueState status) {
        String text = ""; // NOI18N
        Icon icon = null;
        boolean opaque = false;
        boolean visible = false;
        Color background = getBackground();
        Color foreground = getForeground();

        switch (status) {
            case NEW:
                // noop
                break;
            case OPEN:
                GitHubIssue issue = getIssue();
                boolean isPR = GitHubIssuesUtils.isPullRequest(issue.getIssue());
                text = "Open"; // NOI18N
                icon = isPR ? GitHubIcons.GIT_PULL_REQUEST_ICON_16 : GitHubIcons.ISSUE_OPENED_ICON_16;
                opaque = true;
                visible = true;
                background = OPEN_STATE_COLOR;
                foreground = Color.WHITE;
                break;
            case CLOSED:
                issue = getIssue();
                isPR = GitHubIssuesUtils.isPullRequest(issue.getIssue());
                PullRequest pullRequest = getPullRequest();
                text = isMerged() ? "Merged" : "Closed"; // NOI18N
                icon = isPR ? GitHubIcons.GIT_PULL_REQUEST_ICON_16 : GitHubIcons.ISSUE_CLOSED_ICON_16;
                opaque = true;
                visible = true;
                background = (isPR && pullRequest != null && pullRequest.isMerged()) ? MERGED_STATE_COLOR : CLOSED_STATE_COLOR;
                foreground = Color.WHITE;
                break;
            default:
                throw new AssertionError();
        }

        headerStatusLabel.setText(text);
        headerStatusLabel.setIcon(icon);
        headerStatusLabel.setBackground(background);
        headerStatusLabel.setForeground(foreground);
        headerStatusLabel.setOpaque(opaque);
        headerStatusLabel.setVisible(visible);
    }

    @NbBundle.Messages({
        "GitHubIssuePanel.label.close.issue=Close issue",
        "GitHubIssuePanel.label.reopen.issue=Reopen issue",
        "GitHubIssuePanel.label.close.pull.request=Close pull request",
        "GitHubIssuePanel.label.reopen.pull.request=Reopen pull request"
    })
    private void setNewCommentButtonCloseOrReopen(boolean isClosed) {
        if (isClosed) {
            if (isPullRequest()) {
                newCommentCloseReopenIssueButton.setText(Bundle.GitHubIssuePanel_label_reopen_pull_request());
            } else {
                newCommentCloseReopenIssueButton.setText(Bundle.GitHubIssuePanel_label_reopen_issue());
            }
            return;
        }
        if (isPullRequest()) {
            newCommentCloseReopenIssueButton.setText(Bundle.GitHubIssuePanel_label_close_pull_request());
        } else {
            newCommentCloseReopenIssueButton.setText(Bundle.GitHubIssuePanel_label_close_issue());
        }
    }

    public String getTitle() {
        return titleTextField.getText().trim();
    }

    public String getDescription() {
        return descriptionTabbedPanel.getText();
    }

    public User getAssignee() {
        return (User) assigneeComboBox.getSelectedItem();
    }

    public Milestone getMilestone() {
        return (Milestone) milestoneComboBox.getSelectedItem();
    }

    public List<Label> getLabels() {
        List<Label> selectedValuesList = labelsList.getSelectedValuesList();
        ArrayList<Label> labels = new ArrayList<>();
        for (Label l : selectedValuesList) {
            if (l != null) {
                labels.add(l);
            }
        }
        return labels;
    }

    public String getNewComment() {
        return newCommentTabbedPanel.getText();
    }

    public void setNewComment(String comment) {
        newCommentTabbedPanel.setText(comment);
    }

    public void appendNewComment(String comment) {
        if (comment == null) {
            return;
        }
        newCommentTabbedPanel.appendText(comment);
    }

    public String getQuoteComment() {
        return commentsPanel.getQuoteComment();
    }

    public Comment getEditedComment() {
        return commentsPanel.getEditedComment();
    }

    public Comment getDeletedComment() {
        return commentsPanel.getDeletedComment();
    }

    public void removeDeletedComment() {
        commentsPanel.removeDeletedCommlent();
    }

    public void addCommentsChangeListener(PropertyChangeListener listener) {
        commentsPanel.addPropertyChangeListener(listener);
    }

    public void removeCommentsChangeListener(PropertyChangeListener listener) {
        commentsPanel.removePropertyChangeListener(listener);
    }

    public void setErrorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = ""; // NOI18N
            headerErrorLabel.setIcon(null);
            headerSubmitButton.setEnabled(true);
        } else {
            headerErrorLabel.setIcon(GitHubIcons.ERROR_ICON_16);
            headerSubmitButton.setEnabled(false);
        }
        headerErrorLabel.setText(errorMessage);
    }

    private void setNewCommentVisible(boolean isVisible) {
        newCommentLabel.setVisible(isVisible);
        newCommentTabbedPanel.setVisible(isVisible);
        newCommentButton.setVisible(isVisible);
        if (isVisible) {
            newCommentCloseReopenIssueButton.setVisible(gitHubIssue.isEditableUser() && !isMerged());
        } else {
            newCommentCloseReopenIssueButton.setVisible(false);
        }
    }

    private void setCommentsSectionVisible(boolean isVisible) {
        commentsCollapsibleSectionPanel.setVisible(isVisible);
    }

    public void setNewCommentEnabled(boolean isEnabled) {
        newCommentTabbedPanel.setEnabled(isEnabled);
        newCommentButton.setEnabled(isEnabled);
        newCommentCloseReopenIssueButton.setEnabled(isEnabled);
    }

    public void setSubmitButtonEnabled(boolean isEnabled) {
        headerSubmitButton.setEnabled(isEnabled);
    }

    public void setCreatePullRequestButtonEnabled(boolean isEnabled) {
        changeToPullRequestButton.setEnabled(isEnabled);
    }

    public void addAction(SubmitIssueAction listener) {
        headerSubmitButton.addActionListener(listener);
    }

    public void addAction(CommentAction listener) {
        newCommentButton.addActionListener(listener);
    }

    public void addAction(CloseReopenAction listener) {
        newCommentCloseReopenIssueButton.addActionListener(listener);
    }

    public void addAction(CreatePullRequestAction listener) {
        changeToPullRequestButton.addActionListener(listener);
        newPullRequestToggleButton.addActionListener(listener);
    }

    public void removeAction(SubmitIssueAction listener) {
        headerSubmitButton.removeActionListener(listener);
    }

    public void removeAction(CommentAction listener) {
        newCommentButton.removeActionListener(listener);
    }

    public void removeAction(CloseReopenAction listener) {
        newCommentCloseReopenIssueButton.removeActionListener(listener);
    }

    public void removeAction(CreatePullRequestAction listener) {
        changeToPullRequestButton.removeActionListener(listener);
        newPullRequestToggleButton.removeActionListener(listener);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainCommentsPanel = new javax.swing.JPanel();
        dummyCommentsPanel = new javax.swing.JPanel();
        mainFilesChangedPanel = new javax.swing.JPanel();
        dummyFilesChangedPanel = new javax.swing.JPanel();
        mainCommitsPanel = new javax.swing.JPanel();
        dummyCommitsPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        headerSubmitButton = new javax.swing.JButton();
        headerNameLabel = new javax.swing.JLabel();
        headerErrorLabel = new javax.swing.JLabel();
        headerCreatedLabel = new javax.swing.JLabel();
        headerCreatedDateLabel = new javax.swing.JLabel();
        headerUpdatedLabel = new javax.swing.JLabel();
        headerUpdatedDateLabel = new javax.swing.JLabel();
        headerCreatedByLabel = new javax.swing.JLabel();
        headerCreatedByUserLabel = new javax.swing.JLabel();
        headerStatusLabel = new javax.swing.JLabel();
        headerShowInBrowserLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        jSeparator1 = new javax.swing.JSeparator();
        refreshLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        newLabelButton = new javax.swing.JButton();
        newMilestoneButton = new javax.swing.JButton();
        headerPrBaseHeadLabel = new javax.swing.JLabel();
        changeToPullRequestButton = new javax.swing.JButton();
        newPullRequestToggleButton = new javax.swing.JToggleButton();
        mainScrollPane = new javax.swing.JScrollPane();
        mainPanel = new javax.swing.JPanel();
        assigneeLabel = new javax.swing.JLabel();
        assigneeComboBox = new javax.swing.JComboBox<User>();
        titleTextField = new javax.swing.JTextField();
        milestoneLabel = new javax.swing.JLabel();
        milestoneComboBox = new javax.swing.JComboBox<Milestone>();
        descriptionLabel = new javax.swing.JLabel();
        titleLabel = new javax.swing.JLabel();
        labelsLabel = new javax.swing.JLabel();
        labelsScrollPane = new javax.swing.JScrollPane();
        labelsList = new javax.swing.JList<Label>();
        descriptionTabbedPanel = new com.junichi11.netbeans.modules.github.issues.issue.ui.CommentTabbedPanel();
        newCommentLabel = new javax.swing.JLabel();
        newCommentTabbedPanel = new com.junichi11.netbeans.modules.github.issues.issue.ui.CommentTabbedPanel();
        newCommentButton = new javax.swing.JButton();
        newCommentCloseReopenIssueButton = new javax.swing.JButton();
        attributesViewPanel = new com.junichi11.netbeans.modules.github.issues.issue.ui.AttributesViewPanel();
        assignYourselfLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        commentsCollapsibleSectionPanel = new org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel();
        insertTemplateButton = new javax.swing.JButton();
        manageTemplatesButton = new javax.swing.JButton();
        filesChangedcollapsibleSectionPanel = new org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel();

        dummyCommentsPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout mainCommentsPanelLayout = new javax.swing.GroupLayout(mainCommentsPanel);
        mainCommentsPanel.setLayout(mainCommentsPanelLayout);
        mainCommentsPanelLayout.setHorizontalGroup(
            mainCommentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dummyCommentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE)
        );
        mainCommentsPanelLayout.setVerticalGroup(
            mainCommentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dummyCommentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 13, Short.MAX_VALUE)
        );

        dummyFilesChangedPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout mainFilesChangedPanelLayout = new javax.swing.GroupLayout(mainFilesChangedPanel);
        mainFilesChangedPanel.setLayout(mainFilesChangedPanelLayout);
        mainFilesChangedPanelLayout.setHorizontalGroup(
            mainFilesChangedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 791, Short.MAX_VALUE)
            .addGroup(mainFilesChangedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(dummyFilesChangedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE))
        );
        mainFilesChangedPanelLayout.setVerticalGroup(
            mainFilesChangedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
            .addGroup(mainFilesChangedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(dummyFilesChangedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dummyCommitsPanelLayout = new javax.swing.GroupLayout(dummyCommitsPanel);
        dummyCommitsPanel.setLayout(dummyCommitsPanelLayout);
        dummyCommitsPanelLayout.setHorizontalGroup(
            dummyCommitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        dummyCommitsPanelLayout.setVerticalGroup(
            dummyCommitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainCommitsPanelLayout = new javax.swing.GroupLayout(mainCommitsPanel);
        mainCommitsPanel.setLayout(mainCommitsPanelLayout);
        mainCommitsPanelLayout.setHorizontalGroup(
            mainCommitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dummyCommitsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainCommitsPanelLayout.setVerticalGroup(
            mainCommitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dummyCommitsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(headerSubmitButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerSubmitButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerNameLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerNameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerErrorLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerErrorLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerCreatedLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerCreatedLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerCreatedDateLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerCreatedDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerUpdatedLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerUpdatedLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerUpdatedDateLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerUpdatedDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerCreatedByLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerCreatedByLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerCreatedByUserLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerCreatedByUserLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerStatusLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerStatusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerShowInBrowserLinkButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerShowInBrowserLinkButton.text")); // NOI18N
        headerShowInBrowserLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                headerShowInBrowserLinkButtonActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(refreshLinkButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.refreshLinkButton.text")); // NOI18N
        refreshLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(newLabelButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.newLabelButton.text")); // NOI18N
        newLabelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newLabelButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(newMilestoneButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.newMilestoneButton.text")); // NOI18N
        newMilestoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMilestoneButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(headerPrBaseHeadLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.headerPrBaseHeadLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(changeToPullRequestButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.changeToPullRequestButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(newPullRequestToggleButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.newPullRequestToggleButton.text")); // NOI18N

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                        .addComponent(headerErrorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(newPullRequestToggleButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(changeToPullRequestButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newMilestoneButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newLabelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerSubmitButton))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addComponent(headerNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(refreshLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerShowInBrowserLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(headerPanelLayout.createSequentialGroup()
                                .addComponent(headerStatusLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(headerCreatedLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(headerCreatedDateLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(headerUpdatedLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(headerUpdatedDateLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(headerCreatedByLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(headerCreatedByUserLabel))
                            .addComponent(headerPrBaseHeadLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(headerShowInBrowserLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(refreshLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(headerNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerCreatedLabel)
                    .addComponent(headerCreatedDateLabel)
                    .addComponent(headerUpdatedLabel)
                    .addComponent(headerUpdatedDateLabel)
                    .addComponent(headerCreatedByLabel)
                    .addComponent(headerCreatedByUserLabel)
                    .addComponent(headerStatusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(headerPrBaseHeadLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerSubmitButton)
                    .addComponent(headerErrorLabel)
                    .addComponent(newLabelButton)
                    .addComponent(newMilestoneButton)
                    .addComponent(changeToPullRequestButton)
                    .addComponent(newPullRequestToggleButton)))
        );

        mainPanel.setAutoscrolls(true);

        org.openide.awt.Mnemonics.setLocalizedText(assigneeLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.assigneeLabel.text")); // NOI18N

        titleTextField.setText(org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.titleTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(milestoneLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.milestoneLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(descriptionLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.descriptionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(titleLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.titleLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelsLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.labelsLabel.text")); // NOI18N

        labelsScrollPane.setViewportView(labelsList);

        org.openide.awt.Mnemonics.setLocalizedText(newCommentLabel, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.newCommentLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(newCommentButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.newCommentButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(newCommentCloseReopenIssueButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.newCommentCloseReopenIssueButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(assignYourselfLinkButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.assignYourselfLinkButton.text")); // NOI18N
        assignYourselfLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assignYourselfLinkButtonActionPerformed(evt);
            }
        });

        commentsCollapsibleSectionPanel.setContent(mainCommentsPanel);
        commentsCollapsibleSectionPanel.setLabel(org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.commentsCollapsibleSectionPanel.label")); // NOI18N

        insertTemplateButton.setIcon(new javax.swing.ImageIcon("/home/junichi11/NetBeansProjects/netbeans-github-issues/src/main/resources/com/junichi11/netbeans/modules/github/issues/resources/template_16.png")); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(insertTemplateButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.insertTemplateButton.text")); // NOI18N
        insertTemplateButton.setToolTipText(org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.insertTemplateButton.toolTipText")); // NOI18N
        insertTemplateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertTemplateButtonActionPerformed(evt);
            }
        });

        manageTemplatesButton.setIcon(new javax.swing.ImageIcon("/home/junichi11/NetBeansProjects/netbeans-github-issues/src/main/resources/com/junichi11/netbeans/modules/github/issues/resources/template_settings_16.png")); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(manageTemplatesButton, org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.manageTemplatesButton.text")); // NOI18N
        manageTemplatesButton.setToolTipText(org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.manageTemplatesButton.toolTipText")); // NOI18N
        manageTemplatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageTemplatesButtonActionPerformed(evt);
            }
        });

        filesChangedcollapsibleSectionPanel.setContent(mainFilesChangedPanel);
        filesChangedcollapsibleSectionPanel.setExpanded(false);
        filesChangedcollapsibleSectionPanel.setLabel(org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.filesChangedcollapsibleSectionPanel.label")); // NOI18N
        filesChangedcollapsibleSectionPanel.setMaximumSize(new java.awt.Dimension(800, 31));

        commitsCollapsibleSectionPanel.setContent(mainCommitsPanel);
        commitsCollapsibleSectionPanel.setExpanded(false);
        commitsCollapsibleSectionPanel.setLabel(org.openide.util.NbBundle.getMessage(GitHubIssuePanel.class, "GitHubIssuePanel.commitsCollapsibleSectionPanel.label")); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(attributesViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descriptionLabel)
                            .addComponent(titleLabel)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(insertTemplateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(manageTemplatesButton)))
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descriptionTabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(titleTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelsLabel)
                            .addComponent(labelsScrollPane)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(assigneeLabel)
                                .addGap(19, 19, 19)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(assignYourselfLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(assigneeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(milestoneLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(milestoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(newCommentLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newCommentTabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(newCommentCloseReopenIssueButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newCommentButton))
                    .addComponent(commentsCollapsibleSectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filesChangedcollapsibleSectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mergePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(commitsCollapsibleSectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(assigneeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(assigneeLabel)
                    .addComponent(titleLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                        .addComponent(assignYourselfLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(milestoneLabel)
                            .addComponent(milestoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelsScrollPane))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(descriptionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(insertTemplateButton)
                            .addComponent(manageTemplatesButton))
                        .addGap(237, 237, 237))
                    .addComponent(descriptionTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributesViewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(commitsCollapsibleSectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filesChangedcollapsibleSectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(commentsCollapsibleSectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mergePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newCommentLabel)
                    .addComponent(newCommentTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newCommentButton)
                    .addComponent(newCommentCloseReopenIssueButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainScrollPane.setViewportView(mainPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainScrollPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainScrollPane))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void headerShowInBrowserLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_headerShowInBrowserLinkButtonActionPerformed
        RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
        rp.post(new Runnable() {

            @Override
            public void run() {
                GitHubIssueSupport.showInBrowser(gitHubIssue);
            }
        });
    }//GEN-LAST:event_headerShowInBrowserLinkButtonActionPerformed

    private void refreshLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshLinkButtonActionPerformed
        refresh();
    }//GEN-LAST:event_refreshLinkButtonActionPerformed

    @NbBundle.Messages({
        "GitHubIssuePanel.message.addLabel.error=Can't add a label."
    })
    private void newLabelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newLabelButtonActionPerformed
        assert gitHubIssue != null;
        Enumeration<Label> elements = labelsListModel.elements();
        Label label = LabelPanel.showDialog(Collections.list(elements));
        if (label != null) {
            GitHubRepository repository = getRepository();
            if (repository == null) {
                return;
            }
            Label newLable = repository.addLabel(label);
            if (newLable == null) {
                // show dialog
                UiUtils.showErrorDialog(Bundle.GitHubIssuePanel_message_addLabel_error());
                return;
            }
            GitHubCache cache = GitHubCache.create(repository);
            updateLables(cache, true);
            Issue issue = getIssue().getIssue();
            if (issue != null) {
                setLabelsSelected(issue.getLabels());
            }
        }
    }//GEN-LAST:event_newLabelButtonActionPerformed

    @NbBundle.Messages({
        "GitHubIssuePanel.message.addMilestone.error=Can't add a milestone"
    })
    private void newMilestoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMilestoneButtonActionPerformed
        assert gitHubIssue != null;
        GitHubRepository repository = getRepository();
        if (repository == null) {
            return;
        }
        Milestone milestone = MilestonePanel.showDialog(repository.getMilestones("all", false)); // NOI18N
        if (milestone != null) {
            Milestone newMilestone = repository.addMilestone(milestone);
            if (newMilestone == null) {
                // show dialog
                UiUtils.showErrorDialog(Bundle.GitHubIssuePanel_message_addMilestone_error());
                return;
            }
            GitHubCache cache = GitHubCache.create(repository);
            updateMilestones(cache, true);
            Issue issue = getIssue().getIssue();
            if (issue != null) {
                setMilestoneSelected(issue.getMilestone());
            }
        }
    }//GEN-LAST:event_newMilestoneButtonActionPerformed

    private void assignYourselfLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assignYourselfLinkButtonActionPerformed
        GitHubRepository repository = getRepository();
        if (repository == null) {
            return;
        }
        GitHubCache cache = GitHubCache.create(repository);
        User myself = cache.getMySelf();
        if (myself == null) {
            LOGGER.log(Level.WARNING, "{0} : Can''t get myself.", repository.getFullName()); // NOI18N
            return;
        }
        setAssigneeSelected(myself);
    }//GEN-LAST:event_assignYourselfLinkButtonActionPerformed

    @NbBundle.Messages("GitHubIssuePanel.insert.template.title=Insert Template")
    private void insertTemplateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertTemplateButtonActionPerformed
        assert EventQueue.isDispatchThread();
        String[] templateNames = GitHubIssuesConfig.getInstance().getTemplateNames();
        InsertTemplatePanel insertTemplatePanel = new InsertTemplatePanel();
        insertTemplatePanel.setTemplates(templateNames);
        NotifyDescriptor.Confirmation message = new NotifyDescriptor.Confirmation(
                insertTemplatePanel,
                Bundle.GitHubIssuePanel_insert_template_title(),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE);
        if (DialogDisplayer.getDefault().notify(message) == NotifyDescriptor.OK_OPTION) {
            // insert
            String selectedTemplateName = insertTemplatePanel.getSelectedTemplateName();
            String template = GitHubIssuesConfig.getInstance().getTemplate(selectedTemplateName);
            if (template == null || template.isEmpty()) {
                return;
            }
            descriptionTabbedPanel.setText(descriptionTabbedPanel.getText() + template);
        }
    }//GEN-LAST:event_insertTemplateButtonActionPerformed

    @NbBundle.Messages({
        "GitHubIssuePanel.manage.templates.title=Manage Templates",
        "GitHubIssuePanel.manage.templates.add.option=Add",
        "GitHubIssuePanel.manage.templates.remove.option=Remove",
        "GitHubIssuePanel.manage.templates.edit.option=Edit",
        "GitHubIssuePanel.manage.templates.duplicate.option=Duplicate",
        "GitHubIssuePanel.manage.templates.close.option=Close"
    })
    private void manageTemplatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageTemplatesButtonActionPerformed
        assert EventQueue.isDispatchThread();
        final ManageTemplatesPanel manageTemplatesPanel = new ManageTemplatesPanel();
        final DialogDescriptor descriptor = new DialogDescriptor(
                manageTemplatesPanel, // message
                Bundle.GitHubIssuePanel_manage_templates_title(), // title
                true, // modal
                null, // options
                null, // initial value
                DialogDescriptor.RIGHT_ALIGN,
                null, // help
                null // action listener
        );
        descriptor.setOptions(new String[]{
            TEMPLATES_ADD_OPTION,
            TEMPLATES_EDIT_OPTION,
            TEMPLATES_DUPLICATE_OPTION,
            TEMPLATES_REMOVE_OPTION,
            TEMPLATES_CLOSE_OPTION
        });
        descriptor.setClosingOptions(new String[]{TEMPLATES_CLOSE_OPTION});
        descriptor.setButtonListener(new ManageTemplateButtonListener(descriptor, manageTemplatesPanel));
        DialogDisplayer.getDefault().notify(descriptor);
    }//GEN-LAST:event_manageTemplatesButtonActionPerformed

    public void refresh() {
        RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
        rp.post(new Runnable() {
            @Override
            public void run() {
                if (isNew()) {
                    return;
                }
                gitHubIssue.refreshIssue();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.bugtracking.commons.LinkButton assignYourselfLinkButton;
    private javax.swing.JComboBox<User> assigneeComboBox;
    private javax.swing.JLabel assigneeLabel;
    private com.junichi11.netbeans.modules.github.issues.issue.ui.AttributesViewPanel attributesViewPanel;
    private javax.swing.JButton changeToPullRequestButton;
    private org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel commentsCollapsibleSectionPanel;
    private final org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel commitsCollapsibleSectionPanel = new org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel();
    private javax.swing.JLabel descriptionLabel;
    private com.junichi11.netbeans.modules.github.issues.issue.ui.CommentTabbedPanel descriptionTabbedPanel;
    private javax.swing.JPanel dummyCommentsPanel;
    private javax.swing.JPanel dummyCommitsPanel;
    private javax.swing.JPanel dummyFilesChangedPanel;
    private org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel filesChangedcollapsibleSectionPanel;
    private javax.swing.JLabel headerCreatedByLabel;
    private javax.swing.JLabel headerCreatedByUserLabel;
    private javax.swing.JLabel headerCreatedDateLabel;
    private javax.swing.JLabel headerCreatedLabel;
    private javax.swing.JLabel headerErrorLabel;
    private javax.swing.JLabel headerNameLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel headerPrBaseHeadLabel;
    private org.netbeans.modules.bugtracking.commons.LinkButton headerShowInBrowserLinkButton;
    private javax.swing.JLabel headerStatusLabel;
    private javax.swing.JButton headerSubmitButton;
    private javax.swing.JLabel headerUpdatedDateLabel;
    private javax.swing.JLabel headerUpdatedLabel;
    private javax.swing.JButton insertTemplateButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelsLabel;
    private javax.swing.JList<Label> labelsList;
    private javax.swing.JScrollPane labelsScrollPane;
    private javax.swing.JPanel mainCommentsPanel;
    private javax.swing.JPanel mainCommitsPanel;
    private javax.swing.JPanel mainFilesChangedPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane mainScrollPane;
    private javax.swing.JButton manageTemplatesButton;
    private final com.junichi11.netbeans.modules.github.issues.issue.ui.MergePanel mergePanel = new com.junichi11.netbeans.modules.github.issues.issue.ui.MergePanel();
    private javax.swing.JComboBox<Milestone> milestoneComboBox;
    private javax.swing.JLabel milestoneLabel;
    private javax.swing.JButton newCommentButton;
    private javax.swing.JButton newCommentCloseReopenIssueButton;
    private javax.swing.JLabel newCommentLabel;
    private com.junichi11.netbeans.modules.github.issues.issue.ui.CommentTabbedPanel newCommentTabbedPanel;
    private javax.swing.JButton newLabelButton;
    private javax.swing.JButton newMilestoneButton;
    private javax.swing.JToggleButton newPullRequestToggleButton;
    private org.netbeans.modules.bugtracking.commons.LinkButton refreshLinkButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField titleTextField;
    // End of variables declaration//GEN-END:variables

    private class DefaultDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }
    }

    private class MergePropertyChangeListener implements PropertyChangeListener {

        public MergePropertyChangeListener() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(MergePanel.PROP_MERGE_CHANGED)) {
                // merge
                RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
                rp.post(new Runnable() {
                    @Override
                    public void run() {
                        final MergeStatus mergeStatus = getRepository().merge(
                                getIssue().getIssue().getNumber(),
                                mergePanel.getCommitMessage());
                        if (mergeStatus != null && mergeStatus.isMerged()) {
                            refresh();
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showPlainDialog(mergeStatus.getMessage());
                            }
                        });
                    }
                });
            }
        }
    }

    //~ inner class
    private static class ManageTemplateButtonListener implements ActionListener {

        private final DialogDescriptor descriptor;
        private final ManageTemplatesPanel manageTemplatesPanel;

        public ManageTemplateButtonListener(DialogDescriptor descriptor, ManageTemplatesPanel manageTemplatesPanel) {
            this.descriptor = descriptor;
            this.manageTemplatesPanel = manageTemplatesPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object value = descriptor.getValue();
            if (value == TEMPLATES_ADD_OPTION) {
                add();
            } else if (value == TEMPLATES_EDIT_OPTION) {
                edit();
            } else if (value == TEMPLATES_DUPLICATE_OPTION) {
                duplicate();
            } else if (value == TEMPLATES_REMOVE_OPTION) {
                remove();
            }
        }

        @NbBundle.Messages("ManageTemplateButtonListener.add.title=Add Template")
        private void add() {
            showDialog(TEMPLATES_ADD_OPTION, Bundle.ManageTemplateButtonListener_add_title());
        }

        @NbBundle.Messages("ManageTemplateButtonListener.edit.title=Edit Template")
        private void edit() {
            showDialog(TEMPLATES_EDIT_OPTION, Bundle.ManageTemplateButtonListener_edit_title());
        }

        @NbBundle.Messages("ManageTemplateButtonListener.duplicate.title=Duplicate Template")
        private void duplicate() {
            showDialog(TEMPLATES_DUPLICATE_OPTION, Bundle.ManageTemplateButtonListener_duplicate_title());
        }

        @NbBundle.Messages({
            "# {0} - name",
            "ManageTemplateButtonListener.remove.message=Do you really want to remove {0}?"
        })
        private void remove() {
            String selectedTemplateName = manageTemplatesPanel.getSelectedTemplateName();
            if (selectedTemplateName == null || selectedTemplateName.isEmpty()) {
                return;
            }
            if (UiUtils.showQuestionDialog(Bundle.ManageTemplateButtonListener_remove_message(selectedTemplateName))) {
                GitHubIssuesConfig.getInstance().removeTemplate(selectedTemplateName);
                manageTemplatesPanel.resetTemplateNameList();
            }
        }

        private void showDialog(String option, String title) {
            if (!option.equals(TEMPLATES_ADD_OPTION)
                    && !option.equals(TEMPLATES_EDIT_OPTION)
                    && !option.equals(TEMPLATES_DUPLICATE_OPTION)) {
                return;
            }

            // create panel
            final TemplatePanel templatePanel = new TemplatePanel();
            String selectedTemplateName = manageTemplatesPanel.getSelectedTemplateName();
            if (!option.equals(TEMPLATES_ADD_OPTION)) {
                if (selectedTemplateName == null || selectedTemplateName.isEmpty()) {
                    return;
                }
                templatePanel.setTemplateNameEditable(!option.equals(TEMPLATES_EDIT_OPTION));
                templatePanel.setTemplateName(selectedTemplateName);
                templatePanel.setTemplate(GitHubIssuesConfig.getInstance().getTemplate(selectedTemplateName));
            }
            final NotifyDescriptor.Confirmation notify = new NotifyDescriptor.Confirmation(
                    templatePanel,
                    title,
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.PLAIN_MESSAGE);

            // add listener
            ChangeListener listener = null;
            if (option.equals(TEMPLATES_ADD_OPTION) || option.equals(TEMPLATES_DUPLICATE_OPTION)) {
                final List<String> existingNames = new ArrayList<>(Arrays.asList(GitHubIssuesConfig.getInstance().getTemplateNames()));
                listener = new TemplatePanelChangeListener(templatePanel, notify, existingNames);
                templatePanel.addChangeListener(listener);
                templatePanel.fireChange();
            }

            // show dialog
            if (DialogDisplayer.getDefault().notify(notify) == NotifyDescriptor.OK_OPTION) {
                String templateName = templatePanel.getTemplateName();
                if (templateName != null && !templateName.isEmpty()) {
                    String template = templatePanel.getTemplate();
                    GitHubIssuesConfig.getInstance().setTemplate(templateName, template);
                    if (option.equals(TEMPLATES_EDIT_OPTION)) {
                        manageTemplatesPanel.setSelectedTemplateName(selectedTemplateName);
                    } else {
                        manageTemplatesPanel.resetTemplateNameList();
                    }
                }
            }

            if (listener != null) {
                templatePanel.removeChangeListener(listener);
            }
        }
    }

    private static class TemplatePanelChangeListener implements ChangeListener {

        private final TemplatePanel templatePanel;
        private final NotifyDescriptor.Confirmation notify;
        private final List<String> existingNames;

        public TemplatePanelChangeListener(TemplatePanel templatePanel, NotifyDescriptor.Confirmation notify, List<String> existingNames) {
            this.templatePanel = templatePanel;
            this.notify = notify;
            this.existingNames = existingNames;
        }

        @Override
        @NbBundle.Messages({
            "TemplatePanelChangeListener.invalid.empty=Name must be set.",
            "TemplatePanelChangeListener.invalid.existing=It already exisits."
        })
        public void stateChanged(ChangeEvent e) {
            // validate
            String templateName = templatePanel.getTemplateName();
            if ((templateName == null || templateName.isEmpty())) {
                notify.setValid(false);
                templatePanel.setErrorMessage(Bundle.TemplatePanelChangeListener_invalid_empty());
                return;
            }
            if (existingNames.contains(templateName)) {
                notify.setValid(false);
                templatePanel.setErrorMessage(Bundle.TemplatePanelChangeListener_invalid_existing());
                return;
            }

            // everything ok
            notify.setValid(true);
            templatePanel.setErrorMessage(" "); // NOI18N
        }
    }

}
