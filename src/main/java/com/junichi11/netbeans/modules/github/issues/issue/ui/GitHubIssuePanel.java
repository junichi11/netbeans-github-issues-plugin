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
import com.junichi11.netbeans.modules.github.issues.GitHubIssueState;
import com.junichi11.netbeans.modules.github.issues.GitHubIssues;
import static com.junichi11.netbeans.modules.github.issues.GitHubIssues.CLOSED_STATE_COLOR;
import static com.junichi11.netbeans.modules.github.issues.GitHubIssues.OPEN_STATE_COLOR;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssue;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueController.CloseReopenAction;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueController.CommentAction;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueController.SubmitIssueAction;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssueSupport;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.ui.AttributesListCellRenderer;
import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.pegdown.PegDownProcessor;

/**
 *
 * @author junichi11
 */
public class GitHubIssuePanel extends JPanel {

    private static final long serialVersionUID = -4871443269659315479L;

    private GitHubIssue gitHubIssue;
    private CommentsPanel commentsPanel;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final DefaultComboBoxModel<Milestone> milestoneComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<User> assigneeComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultListModel<Label> labelsListModel = new DefaultListModel<>();
    private static final Icon ERROR_ICON = ImageUtilities.loadImageIcon("com/junichi11/netbeans/modules/github/issues/resources/error_icon_16.png", true); // NOI18N
    private static final Icon ICON_32 = ImageUtilities.loadImageIcon("com/junichi11/netbeans/modules/github/issues/resources/icon_32.png", true); // NOI18N
    private static final Logger LOGGER = Logger.getLogger(GitHubIssuePanel.class.getName());
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // NOI18N

    /**
     * Creates new form GitHubIssuePanel
     */
    public GitHubIssuePanel() {
        initComponents();
        init();
    }

    private void init() {
        // set cell renderer
        milestoneComboBox.setRenderer(new AttributesListCellRenderer(milestoneComboBox.getRenderer()));
        assigneeComboBox.setRenderer(new AttributesListCellRenderer(assigneeComboBox.getRenderer()));
        labelsList.setCellRenderer(new AttributesListCellRenderer(labelsList.getCellRenderer()));
        milestoneComboBox.setModel(milestoneComboBoxModel);
        assigneeComboBox.setModel(assigneeComboBoxModel);
        labelsList.setModel(labelsListModel);

        // add document listener
        DefaultDocumentListener documentListener = new DefaultDocumentListener();
        titleTextField.getDocument().addDocumentListener(documentListener);

        // set error
        headerErrorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        setErrorMessage(""); // NOI18N

        // change header title size
        Font font = headerErrorLabel.getFont();
        headerNameLabel.setFont(font.deriveFont((float) (font.getSize() * 1.5)));
        headerNameLabel.setIcon(ICON_32);

        headerStatusLabel.setBorder(new EmptyBorder(3, 5, 3, 5));

        // text bold
        Font bold = new Font(font.getName(), Font.BOLD, font.getSize());
        headerCreatedByUserLabel.setFont(bold);
        headerStatusLabel.setFont(bold);
        commentsPanel = new CommentsPanel();
        mainCommetnsPanel.add(commentsPanel);
    }

    public void setIssue(GitHubIssue gitHubIssue) {
        this.gitHubIssue = gitHubIssue;
    }

    public GitHubIssue getIssue() {
        return gitHubIssue;
    }

    public void update() {
        // header
        setHeader();
        if (gitHubIssue == null) {
            return;
        }

        GitHubRepository repository = gitHubIssue.getRepository();
        if (repository == null) {
            return;
        }

        boolean isCollaborator = repository.isCollaborator();
        // collaborator?
        if (isCollaborator) {
            GitHubCache cache = GitHubCache.create(repository);
            // milestone
            List<Milestone> milestones = cache.getMilestones();
            milestoneComboBoxModel.removeAllElements();
            milestoneComboBoxModel.addElement(null);
            for (Milestone milestone : milestones) {
                milestoneComboBoxModel.addElement(milestone);
            }

            // assignee
            List<User> collaborators = cache.getCollaborators();
            assigneeComboBoxModel.removeAllElements();
            assigneeComboBoxModel.addElement(null);
            for (User collaborator : collaborators) {
                assigneeComboBoxModel.addElement(collaborator);
            }

            // label
            List<Label> labels = cache.getLabels();
            labelsListModel.removeAllElements();
            labelsListModel.addElement(null);
            for (Label label : labels) {
                labelsListModel.addElement(label);
            }
        }

        // existing issue
        if (!gitHubIssue.isNew()) {
            Issue issue = gitHubIssue.getIssue();
            if (issue != null) {
                // set existing info
                // header
                headerCreatedDateLabel.setText(DATE_FORMAT.format(issue.getCreatedAt()));
                headerUpdatedDateLabel.setText(DATE_FORMAT.format(issue.getUpdatedAt()));
                headerCreatedByUserLabel.setText(issue.getUser().getLogin());

                // title
                titleTextField.setText(issue.getTitle());

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
                attributesViewPanel.setAttributes(issue);

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
                PegDownProcessor processor = new PegDownProcessor();
                for (Comment comment : comments) {
                    String body = comment.getBody();
                    String bodyHtml = processor.markdownToHtml(body);
                    comment.setBodyHtml(String.format("<html>%s</html>", bodyHtml)); // NOI18N
                }
                commentsPanel.addComments(comments);
            }
        }

        // visibility
        setNewCommentVisible();
        setCollaboratorsComponentsVisible(isCollaborator);
        attributesViewPanel.setVisible(!gitHubIssue.isNew());

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
    }

    @NbBundle.Messages({
        "GitHubIssuePanel.label.header.name.new=New Issue",
        "GitHubIssuePanel.label.header.submit.button=Submit",
        "GitHubIssuePanel.label.header.submit.button.new=Submit new issue"
    })
    private void setHeader() {
        setErrorMessage(""); // NOI18N
        if (gitHubIssue == null) {
            headerSubmitButton.setVisible(false);
            return;
        }
        boolean isNew = gitHubIssue.isNew();
        if (isNew) {
            headerNameLabel.setText(Bundle.GitHubIssuePanel_label_header_name_new());
            headerSubmitButton.setText(Bundle.GitHubIssuePanel_label_header_submit_button_new());
            headerStatusLabel.setText("");

        } else {
            headerNameLabel.setText(String.format("%s #%s", gitHubIssue.getSummary(), gitHubIssue.getID())); // NOI18N
            headerSubmitButton.setText(Bundle.GitHubIssuePanel_label_header_submit_button());
            headerSubmitButton.setVisible(gitHubIssue.isEditableUser());
            Issue issue = gitHubIssue.getIssue();
            if (issue != null) {
                GitHubIssueState state = GitHubIssueState.toEnum(issue.getState());
                boolean isClosed = state == GitHubIssueState.CLOSED;
                headerStatusLabel.setText(isClosed ? "Closed" : "Open");
                headerStatusLabel.setBackground(isClosed ? CLOSED_STATE_COLOR : OPEN_STATE_COLOR);
                headerStatusLabel.setForeground(Color.WHITE);
            }
        }
        headerStatusLabel.setOpaque(!isNew);
        headerStatusLabel.setVisible(!isNew);
    }

    @NbBundle.Messages({
        "GitHubIssuePanel.label.close.issue=Close issue",
        "GitHubIssuePanel.label.reopen.issue=Reopen issue"
    })
    private void setNewCommentButtonCloseOrReopen(boolean isClosed) {
        if (isClosed) {
            newCommentCloseReopenIssueButton.setText(Bundle.GitHubIssuePanel_label_reopen_issue());
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

    public void setErrorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = ""; // NOI18N
            headerErrorLabel.setIcon(null);
            headerSubmitButton.setEnabled(true);
        } else {
            headerErrorLabel.setIcon(ERROR_ICON);
            headerSubmitButton.setEnabled(false);
        }
        headerErrorLabel.setText(errorMessage);
    }

    private void setNewCommentVisible() {
        if (gitHubIssue == null) {
            return;
        }
        boolean isVisible = !gitHubIssue.isNew(); // existing issue
        newCommentLabel.setVisible(isVisible);
        newCommentTabbedPanel.setVisible(isVisible);
        newCommentButton.setVisible(isVisible);
        if (isVisible) {
            newCommentCloseReopenIssueButton.setVisible(gitHubIssue.isEditableUser());
        } else {
            newCommentCloseReopenIssueButton.setVisible(false);
        }
    }

    public void setNewCommentEnabled(boolean isEnabled) {
        newCommentTabbedPanel.setEnabled(isEnabled);
        newCommentButton.setEnabled(isEnabled);
        newCommentCloseReopenIssueButton.setEnabled(isEnabled);
    }

    public void setSubmitButtonEnabled(boolean isEnabled) {
        headerSubmitButton.setEnabled(isEnabled);
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

    public void removeAction(SubmitIssueAction listener) {
        headerSubmitButton.removeActionListener(listener);
    }

    public void removeAction(CommentAction listener) {
        newCommentButton.removeActionListener(listener);
    }

    public void removeAction(CloseReopenAction listener) {
        newCommentCloseReopenIssueButton.removeActionListener(listener);
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
        mainCommetnsPanel = new javax.swing.JPanel();

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
                        .addComponent(headerSubmitButton))
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
                        .addComponent(headerCreatedByUserLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addComponent(headerNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refreshLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerShowInBrowserLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerSubmitButton)
                    .addComponent(headerErrorLabel)))
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

        mainCommetnsPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descriptionLabel)
                            .addComponent(titleLabel))
                        .addGap(37, 37, 37)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descriptionTabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                            .addComponent(titleTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelsLabel)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(milestoneLabel)
                                    .addComponent(assigneeLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(milestoneComboBox, 0, 173, Short.MAX_VALUE)
                                    .addComponent(assigneeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(labelsScrollPane)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(newCommentLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newCommentTabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(newCommentCloseReopenIssueButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newCommentButton)))
                .addContainerGap())
            .addComponent(attributesViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainCommetnsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(milestoneLabel)
                            .addComponent(milestoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(descriptionLabel)
                        .addGap(271, 271, 271))
                    .addComponent(descriptionTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributesViewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainCommetnsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
        rp.post(new Runnable() {

            @Override
            public void run() {
                if (gitHubIssue.isNew()) {
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

    }//GEN-LAST:event_refreshLinkButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<User> assigneeComboBox;
    private javax.swing.JLabel assigneeLabel;
    private com.junichi11.netbeans.modules.github.issues.issue.ui.AttributesViewPanel attributesViewPanel;
    private javax.swing.JLabel descriptionLabel;
    private com.junichi11.netbeans.modules.github.issues.issue.ui.CommentTabbedPanel descriptionTabbedPanel;
    private javax.swing.JLabel headerCreatedByLabel;
    private javax.swing.JLabel headerCreatedByUserLabel;
    private javax.swing.JLabel headerCreatedDateLabel;
    private javax.swing.JLabel headerCreatedLabel;
    private javax.swing.JLabel headerErrorLabel;
    private javax.swing.JLabel headerNameLabel;
    private javax.swing.JPanel headerPanel;
    private org.netbeans.modules.bugtracking.commons.LinkButton headerShowInBrowserLinkButton;
    private javax.swing.JLabel headerStatusLabel;
    private javax.swing.JButton headerSubmitButton;
    private javax.swing.JLabel headerUpdatedDateLabel;
    private javax.swing.JLabel headerUpdatedLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelsLabel;
    private javax.swing.JList<Label> labelsList;
    private javax.swing.JScrollPane labelsScrollPane;
    private javax.swing.JPanel mainCommetnsPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane mainScrollPane;
    private javax.swing.JComboBox<Milestone> milestoneComboBox;
    private javax.swing.JLabel milestoneLabel;
    private javax.swing.JButton newCommentButton;
    private javax.swing.JButton newCommentCloseReopenIssueButton;
    private javax.swing.JLabel newCommentLabel;
    private com.junichi11.netbeans.modules.github.issues.issue.ui.CommentTabbedPanel newCommentTabbedPanel;
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
}
