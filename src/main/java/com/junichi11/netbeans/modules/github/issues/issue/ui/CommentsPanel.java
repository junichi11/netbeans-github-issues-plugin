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
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.User;

/**
 *
 * @author junichi11
 */
public class CommentsPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 3131157719093603387L;

    private final List<CommentPanel> commentPanels = Collections.synchronizedList(new ArrayList<CommentPanel>());
    private CommentPanel quoteCommentPanel;
    private CommentPanel deletedCommentPanel;
    private CommentPanel editedCommentPanel;
    public static final String PROP_COMMENT_DELETED = "github.issues.comment.deleted"; // NOI18N
    public static final String PROP_COMMENT_QUOTE = "github.issues.comment.quote"; // NOI18N
    public static final String PROP_COMMENT_EDITED = "github.issues.comment.edited"; // NOI18N

    public CommentsPanel() {
        initComponents();
    }

    public List<Comment> getComments() {
        ArrayList<Comment> comments = new ArrayList<>(commentPanels.size());
        synchronized (commentPanels) {
            for (CommentPanel commentPanel : commentPanels) {
                comments.add(commentPanel.getComment());
            }
        }
        return comments;
    }

    public void addComments(Collection<Comment> comments, GitHubRepository repository) {
        for (Comment comment : comments) {
            addComment(comment, repository);
        }
    }

    public void addComment(Comment comment, GitHubRepository repository) {
        if (comment == null || repository == null) {
            return;
        }
        GitHubCache cache = GitHubCache.create(repository);
        User owner = comment.getUser();
        String loginName = repository.getUserName();
        CommentPanel newPanel = new CommentPanel(comment, cache.getUserIcon(owner));
        boolean isMyself = isMyself(owner, loginName);
        newPanel.setEditEnabled(isMyself);
        newPanel.setDeleteEnabled(isMyself);
        newPanel.addPropertyChangeListener(this);
        commentPanels.add(newPanel);
        add(newPanel);
    }

    private boolean isMyself(User owner, String me) {
        if (owner == null || StringUtils.isEmpty(me)) {
            return false;
        }
        return owner.getLogin().equals(me);
    }

    public void removeAllComments() {
        synchronized (commentPanels) {
            for (CommentPanel comment : commentPanels) {
                removeComment(comment);
            }
            commentPanels.clear();
        }
    }

    private void removeComment(CommentPanel comment) {
        if (comment == null) {
            return;
        }
        comment.removePropertyChangeListener(this);
        remove(comment);
    }

    public String getQuoteComment() {
        if (quoteCommentPanel != null) {
            String selectedText = quoteCommentPanel.getSelectedText();
            if (StringUtils.isEmpty(selectedText)) {
                return quoteCommentPanel.getComment().getBody();
            }
            return selectedText;
        }
        return ""; // NOI18N
    }

    public Comment getDeletedComment() {
        return deletedCommentPanel == null ? null : deletedCommentPanel.getComment();
    }

    public Comment getEditedComment() {
        return editedCommentPanel == null ? null : editedCommentPanel.getComment();
    }

    public void resetChangedPanels() {
        quoteCommentPanel = null;
        deletedCommentPanel = null;
        editedCommentPanel = null;
    }

    public void loadComments(GitHubRepository repository) {
        assert repository != null;
        GitHubCache cache = GitHubCache.create(repository);
        synchronized (commentPanels) {
            for (CommentPanel commentPanel : commentPanels) {
                Comment comment = commentPanel.getComment();
                User user = comment.getUser();
                commentPanel.load(cache.getUserIcon(user));
            }
        }
    }

    public void removeDeletedCommlent() {
        if (deletedCommentPanel != null) {
            deletedCommentPanel.setVisible(false);
            removeComment(deletedCommentPanel);
            deletedCommentPanel = null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PROP_COMMENT_QUOTE:
                fireQuotePropertyChanged();
                break;
            case PROP_COMMENT_DELETED:
                fireDeletedPropertyChanged();
                break;
            case PROP_COMMENT_EDITED:
                fireEditedPropertyChanged();
                break;
            default:
                break;
        }
    }

    private void fireQuotePropertyChanged() {
        synchronized (commentPanels) {
            quoteCommentPanel = null;
            for (CommentPanel comment : commentPanels) {
                if (comment.isQuote()) {
                    quoteCommentPanel = comment;
                    comment.resetProperties();
                    firePropertyChange(PROP_COMMENT_QUOTE, null, null);
                    break;
                }
            }
        }
    }

    private void fireDeletedPropertyChanged() {
        synchronized (commentPanels) {
            deletedCommentPanel = null;
            for (CommentPanel comment : commentPanels) {
                if (comment.isDeleted()) {
                    deletedCommentPanel = comment;
                    comment.resetProperties();
                    firePropertyChange(PROP_COMMENT_DELETED, null, null);
                    break;
                }
            }
        }
    }

    private void fireEditedPropertyChanged() {
        synchronized (commentPanels) {
            editedCommentPanel = null;
            for (CommentPanel comment : commentPanels) {
                if (comment.isEdited()) {
                    editedCommentPanel = comment;
                    comment.resetProperties();
                    firePropertyChange(PROP_COMMENT_EDITED, null, null);
                    break;
                }
            }
        }
    }

}
