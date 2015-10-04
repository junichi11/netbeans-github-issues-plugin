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

import com.junichi11.netbeans.modules.github.issues.issue.ui.CreatePullRequestPanel;
import com.junichi11.netbeans.modules.github.issues.GitHubCache;
import com.junichi11.netbeans.modules.github.issues.GitHubIssues;
import com.junichi11.netbeans.modules.github.issues.issue.ui.CommentTabbedPanel;
import com.junichi11.netbeans.modules.github.issues.issue.ui.CommentsPanel;
import com.junichi11.netbeans.modules.github.issues.issue.ui.GitHubIssuePanel;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import com.junichi11.netbeans.modules.github.issues.utils.UiUtils;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.netbeans.modules.bugtracking.spi.IssueController;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
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
    private final String repositoryId;

    public GitHubIssueController(GitHubIssue gitHubIssue) {
        repositoryId = gitHubIssue.getRepository().getID();
        getPanel().setIssue(gitHubIssue);
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                getPanel().update();
            }
        };
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
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
            panel = new GitHubIssuePanel(repositoryId);
            panel.addChangeListener(this);
            panel.addAction(getSubmitIssueAction());
            panel.addAction(getCommentAction());
            panel.addAction(getCloseReopenAction());
            panel.addAction(getCreatePullRequestAction());
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

    private CreatePullRequestAction getCreatePullRequestAction() {
        return new CreatePullRequestAction();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case CommentsPanel.PROP_COMMENT_QUOTE:
                quoteComment();
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

    private void quoteComment() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                GitHubIssuePanel p = getPanel();
                String quoteComment = StringUtils.toQuoteComment(p.getQuoteComment()) + "\n"; // NOI18N
                p.appendNewComment(quoteComment);
            }
        });
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

    @NbBundle.Messages({
        "CreatePullRequestAction.confirmation.message=Do you want to change this issue to Pull Request?",
        "CreatePullRequestAction.error.message.same.branch=Another branch must be set."
    })
    public class CreatePullRequestAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GitHubIssuePanel p = getPanel();
            p.setCreatePullRequestButtonEnabled(false);
            final GitHubIssue issue = p.getIssue();
            RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
            rp.post(new Runnable() {
                @Override
                public void run() {
                    final GitHubRepository repository = issue.getRepository();
                    GitHubCache cache = GitHubCache.create(repository);
                    final User mySelf = cache.getMySelf();
                    final List<RepositoryBranch> baseBranches = cache.getBranches(true);
                    final List<RepositoryBranch> compareBranches = getCompareBranches(cache, repository, baseBranches);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (baseBranches.isEmpty() || compareBranches.isEmpty()) {
                                    return;
                                }

                                // create descriptor
                                final CreatePullRequestPanel createPullRequestPanel = new CreatePullRequestPanel(baseBranches, compareBranches);
                                // XXX NbMessage
                                createPullRequestPanel.setMessage(Bundle.CreatePullRequestAction_confirmation_message());
                                final NotifyDescriptor.Confirmation descriptor = new NotifyDescriptor.Confirmation(
                                        createPullRequestPanel,
                                        "Change to Pull Request",
                                        NotifyDescriptor.OK_CANCEL_OPTION,
                                        NotifyDescriptor.QUESTION_MESSAGE
                                );

                                // add change listener
                                ChangeListener changeListener = null;
                                if (repository.getRepositoryAuthor().equals(mySelf.getLogin())) {
                                    changeListener = new ChangeListener() {
                                        @Override
                                        public void stateChanged(ChangeEvent e) {
                                            // validate
                                            RepositoryBranch selectedBaseBranch = createPullRequestPanel.getSelectedBaseBranch();
                                            RepositoryBranch selectedCompareBranch = createPullRequestPanel.getSelectedCompareBranch();
                                            if (selectedCompareBranch.getName().equals(selectedBaseBranch.getName())) {
                                                createPullRequestPanel.setErrorMessage(Bundle.CreatePullRequestAction_error_message_same_branch());
                                                descriptor.setValid(false);
                                                return;
                                            }
                                            createPullRequestPanel.setErrorMessage(""); // NOI18N
                                            descriptor.setValid(true);
                                        }
                                    };
                                    createPullRequestPanel.addChangeListener(changeListener);
                                    changeListener.stateChanged(null);
                                }

                                // show dialog
                                if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION) {
                                    RepositoryBranch selectedBaseBranch = createPullRequestPanel.getSelectedBaseBranch();
                                    RepositoryBranch selectedCompareBranch = createPullRequestPanel.getSelectedCompareBranch();
                                    String baseBranch = selectedBaseBranch.getName();
                                    String compareBranch = mySelf.getLogin() + ":" + selectedCompareBranch.getName(); // NOI18N
                                    PullRequest pullRequest;
                                    try {
                                        pullRequest = issue.createPullRequest(compareBranch, baseBranch);
                                        if (pullRequest != null) {
                                            getPanel().refresh();
                                        }
                                    } catch (IOException ex) {
                                        UiUtils.showErrorDialog("Can't create a pull request:" + ex.getMessage()); // NOI18N
                                    }
                                }
                                if (changeListener != null) {
                                    createPullRequestPanel.removeChangeListener(changeListener);
                                }
                            } finally {
                                getPanel().setCreatePullRequestButtonEnabled(true);
                            }
                        }
                    });
                }
            });

        }

        private List<RepositoryBranch> getCompareBranches(GitHubCache cache, GitHubRepository repository, List<RepositoryBranch> baseBranches) {
            List<RepositoryBranch> myBranches;
            final User mySelf = cache.getMySelf();
            String repositoryAuthor = repository.getRepositoryAuthor();
            if (repositoryAuthor.equals(mySelf.getLogin())) {
                myBranches = baseBranches;
            } else {
                List<Repository> forks = cache.getForks();
                Repository myRepository = null;
                for (Repository fork : forks) {
                    User owner = fork.getOwner();
                    if (owner.getLogin().equals(mySelf.getLogin())) {
                        myRepository = fork;
                        break;
                    }
                }
                if (myRepository == null) {
                    return Collections.emptyList();
                }
                // get my branches
                GitHubClient client = repository.createGitHubClient();
                myBranches = getMyRepositoryBranches(client, myRepository);
            }
            return myBranches;
        }

        private List<RepositoryBranch> getMyRepositoryBranches(GitHubClient client, Repository myRepository) {
            RepositoryService service = new RepositoryService(client);
            try {
                return service.getBranches(myRepository);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            return Collections.emptyList();
        }
    }
}
