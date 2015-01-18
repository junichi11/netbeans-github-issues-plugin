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
import com.junichi11.netbeans.modules.github.issues.issue.ui.CommentTabbedPanel;
import com.junichi11.netbeans.modules.github.issues.issue.ui.CommentsPanel;
import com.junichi11.netbeans.modules.github.issues.issue.ui.GitHubIssuePanel;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import com.junichi11.netbeans.modules.github.issues.utils.UiUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;
import org.netbeans.modules.bugtracking.spi.IssueController;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public class GitHubIssueController implements IssueController, ChangeListener, PropertyChangeListener {

    private GitHubIssuePanel panel;
    private String errorMessage;

    public GitHubIssueController(GitHubIssue gitHubIssue) {
        getPanel().setIssue(gitHubIssue);
        getPanel().update();
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public void opened() {
    }

    @Override
    public void closed() {
    }

    @Override
    public boolean saveChanges() {
        return true;
    }

    @Override
    public boolean discardUnsavedChanges() {
        return true;
    }

    @Override
    public boolean isChanged() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPanel().getIssue().addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPanel().getIssue().removePropertyChangeListener(listener);
    }

    private GitHubIssuePanel getPanel() {
        if (panel == null) {
            panel = new GitHubIssuePanel();
            panel.addChangeListener(this);
            panel.addAction(getSubmitIssueAction());
            panel.addAction(getCommentAction());
            panel.addAction(getCloseReopenAction());
            panel.addCommentsChangeListener(this);
        }
        return panel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        validate();
    }

    private void validate() {
        if (!isValid()) {
            getPanel().setErrorMessage(errorMessage);
        } else {
            getPanel().setErrorMessage(""); // NOI18N
        }

    }

    @NbBundle.Messages({
        "GitHubIssueController.message.empty.title=Title must be set."
    })
    private boolean isValid() {
        // title
        String title = getPanel().getTitle();
        if (StringUtils.isEmpty(title)) {
            errorMessage = Bundle.GitHubIssueController_message_empty_title();
            return false;
        }

        // everything ok
        errorMessage = null;
        return true;
    }

    private SubmitIssueAction getSubmitIssueAction() {
        return new SubmitIssueAction();
    }

    private CommentAction getCommentAction() {
        return new CommentAction();
    }

    private CloseReopenAction getCloseReopenAction() {
        return new CloseReopenAction();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case CommentsPanel.PROP_COMMENT_QUOTE:
                GitHubIssuePanel p = getPanel();
                String quoteComment = StringUtils.toQuoteComment(p.getQuoteComment()) + "\n"; // NOI18N
                p.appendNewComment(quoteComment);
                break;
            case CommentsPanel.PROP_COMMENT_EDITED:
                editComment();
                break;
            case CommentsPanel.PROP_COMMENT_DELETED:
                deleteComment();
                break;
            default:
                break;
        }
    }

    @NbBundle.Messages({
        "GitHubIssueController.edit.comment.title=Edit Comment",
        "GitHubIssueController.edit.comment.fail=Can't edit this comment."
    })
    private void editComment() {
        final Comment comment = getPanel().getEditedComment();
        final String editedBody = CommentTabbedPanel.showDialog(Bundle.GitHubIssueController_edit_comment_title(), comment.getBody());
        if (editedBody != null) {
            final GitHubIssue issue = getPanel().getIssue();
            if (issue != null) {
                RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
                rp.post(new Runnable() {

                    @Override
                    public void run() {
                        Comment editedComment = issue.editComment(comment, editedBody);
                        if (editedComment == null) {
                            UiUtils.showErrorDialog(Bundle.GitHubIssueController_edit_comment_fail());
                            return;
                        }
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                getPanel().loadComments();
                            }
                        });
                    }
                });
            }
        }
    }

    @NbBundle.Messages({
        "GitHubIssueController.delete.comment.fail=Can't delete this issue."
    })
    private void deleteComment() {
        final Comment deletedComment = getPanel().getDeletedComment();
        if (deletedComment == null) {
            return;
        }
        RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
        rp.post(new Runnable() {

            @Override
            public void run() {
                GitHubRepository repository = getPanel().getIssue().getRepository();
                final boolean success = GitHubIssueSupport.deleteComment(repository, deletedComment);

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (success) {
                            // remove comment panel
                            getPanel().removeDeletedComment();
                        } else {
                            // show error message
                            UiUtils.showErrorDialog(Bundle.GitHubIssueController_delete_comment_fail());
                        }
                    }
                });
            }
        });
    }

    //~ inner classes
    public class SubmitIssueAction implements ActionListener {

        private SubmitIssueAction() {
        }

        @NbBundle.Messages({
            "SubmitIssueAction.message.issue.added.fail=The issue has not been added.",
            "SubmitIssueAction.message.issue.updated.fail=The issue has not been updated."
        })
        @Override
        public void actionPerformed(ActionEvent e) {
            final GitHubIssuePanel p = getPanel();
            p.setSubmitButtonEnabled(false);
            RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
            rp.post(new Runnable() {

                @Override
                public void run() {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            GitHubIssue issue = p.getIssue();
                            CreateIssueParams issueParams = getCreateIssueParams(issue.isNew(), p);
                            if (issue.isNew()) {
                                // add issue
                                Issue newIssue = issue.submitNewIssue(issueParams);
                                if (newIssue != null) {
                                    p.update();
                                } else {
                                    // show dialog
                                    UiUtils.showErrorDialog(Bundle.SubmitIssueAction_message_issue_added_fail());
                                }
                            } else {
                                // edit issue
                                Issue editIssue = issue.editIssue(issueParams);
                                if (editIssue != null) {
                                    p.update();
                                } else {
                                    // show dialog
                                    UiUtils.showErrorDialog(Bundle.SubmitIssueAction_message_issue_updated_fail());
                                }
                            }
                            p.setSubmitButtonEnabled(true);
                        }
                    });
                }
            });

        }

        private CreateIssueParams getCreateIssueParams(boolean isNew, GitHubIssuePanel p) {
            User assignee = p.getAssignee();
            if (!isNew && assignee == null) {
                assignee = new User();
                assignee.setLogin(""); // NOI18N
            }
            Milestone milestone = p.getMilestone();
            if (milestone == null) {
                milestone = new Milestone();
            }
            CreateIssueParams createIssueParams = new CreateIssueParams(p.getTitle())
                    .body(p.getDescription())
                    .milestone(milestone)
                    .labels(p.getLabels());
            if (assignee != null) {
                createIssueParams = createIssueParams.assignee(assignee);
            }
            return createIssueParams;
        }
    }

    public class CommentAction implements ActionListener {

        private CommentAction() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final GitHubIssuePanel p = getPanel();
            GitHubIssues gitHubIssues = GitHubIssues.getInstance();
            RequestProcessor rp = gitHubIssues.getRequestProcessor();
            p.setNewCommentEnabled(false);
            rp.post(new Runnable() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String comment = p.getNewComment();
                                if (StringUtils.isEmpty(comment)) {
                                    return;
                                }
                                comment(comment);
                                p.update();
                            } finally {
                                p.setNewCommentEnabled(true);
                            }
                        }
                    });
                }
            });
        }

        @NbBundle.Messages({
            "CommentAction.message.comment.added=Comment has been added",
            "CommentAction.message.comment.added.fail=Comment has not been added."
        })
        protected Comment comment(String comment) {
            GitHubIssuePanel p = getPanel();
            if (StringUtils.isEmpty(comment)) {
                return null;
            }
            Comment newComment = GitHubIssueSupport.comment(p.getIssue(), comment);
            if (newComment != null) {
                p.setNewComment(""); // NOI18N
                StatusDisplayer.getDefault().setStatusText(Bundle.CommentAction_message_comment_added());
            } else {
                UiUtils.showErrorDialog(Bundle.CommentAction_message_comment_added_fail());
            }
            return newComment;
        }

    }

    public class CloseReopenAction extends CommentAction {

        private CloseReopenAction() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final GitHubIssuePanel p = getPanel();
            p.setNewCommentEnabled(false);

            GitHubIssues gitHubIssues = GitHubIssues.getInstance();
            RequestProcessor rp = gitHubIssues.getRequestProcessor();
            rp.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        String comment = p.getNewComment();
                        if (StringUtils.isEmpty(comment)) {
                            closeReopen();
                        } else {
                            Comment newComment = comment(comment);
                            if (newComment != null) {
                                closeReopen();
                            }
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                p.update();
                            }
                        });
                    } finally {
                        p.setNewCommentEnabled(true);
                    }
                }
            });
        }

        private boolean closeReopen() {
            return GitHubIssueSupport.toggleState(getPanel().getIssue());
        }

    }
}
