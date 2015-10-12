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
package com.junichi11.netbeans.modules.github.issues.query;

import com.junichi11.netbeans.modules.github.issues.GitHubIssues;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssue;
import com.junichi11.netbeans.modules.github.issues.query.ui.GitHubQueryPanel;
import com.junichi11.netbeans.modules.github.issues.ui.IssueTableCellRenderer;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import com.junichi11.netbeans.modules.github.issues.utils.ValidateUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.bugtracking.commons.SaveQueryPanel;
import org.netbeans.modules.bugtracking.issuetable.IssueNode;
import org.netbeans.modules.bugtracking.issuetable.IssueTable;
import org.netbeans.modules.bugtracking.issuetable.QueryTableCellRenderer;
import org.netbeans.modules.bugtracking.spi.QueryController;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public class GitHubQueryController implements QueryController, ActionListener, ChangeListener {

    private GitHubQueryPanel panel;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final GitHubQuery query;
    private IssueTable issueTable;
    private String errorMessage;

    public GitHubQueryController(GitHubQuery query) {
        this.query = query;
    }

    @Override
    public boolean providesMode(QueryMode queryMode) {
        if (query instanceof GitHubDefaultQuery) {
            return false;
        }
        return true;
    }

    @Override
    public JComponent getComponent(QueryMode queryMode) {
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
    public boolean saveChanges(String string) {
        return false;
    }

    @Override
    public boolean discardUnsavedChanges() {
        return false;
    }

    @Override
    public boolean isChanged() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener instanceof IssueTable) {
            return;
        }
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener instanceof IssueTable) {
            return;
        }
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    void firePropertyChange() {
        propertyChangeSupport.firePropertyChange(QueryController.PROP_CHANGED, null, null);
    }

    private GitHubQueryPanel getPanel() {
        if (panel == null) {
            // issue table
            issueTable = new IssueTable(query.getRepository().getID(), query.getDisplayName(), this, query.getColumnDescriptors(), false);
            issueTable.setRenderer(new IssueTableCellRenderer((QueryTableCellRenderer) issueTable.getRenderer()));
            issueTable.initColumns();
            panel = new GitHubQueryPanel(query, issueTable.getComponent());
            panel.addSaveAction(this);
            panel.addSearchAction(this);
            panel.addChangeListener(this);
        }
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final Object source = event.getSource();
        final GitHubQueryPanel p = getPanel();
        p.setButtonsEnabled(false);

        RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
        rp.post(new Runnable() {

            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if (p.isSave(source)) {
                                save();
                            } else if (p.isSearch(source)) {
                                search();
                            }
                        } finally {
                            p.setButtonsEnabled(true);
                        }
                    }
                });
            }
        });
    }

    @NbBundle.Messages({
        "GitHubQueryController.message.error.empty.name=Query name must be set.",
        "GitHubQueryController.message.error.already.exists=Already exists.",
        "GitHubQueryController.message.saved=Query has been saved."
    })
    private void save() {
        SearchIssuesParams issuesParams = createSearchIssuesParams();
        if (issuesParams == null) {
            return;
        }
        if (!query.isSaved()) {
            SaveQueryPanel.QueryNameValidator validator = new SaveQueryPanel.QueryNameValidator() {
                @Override
                public String isValid(String name) {
                    if (StringUtils.isEmpty(name)) {
                        return Bundle.GitHubQueryController_message_error_empty_name();
                    }
                    Collection<GitHubQuery> queries = query.getRepository().getQueries();
                    for (GitHubQuery query : queries) {
                        if (query.getDisplayName().equals(name)) {
                            return Bundle.GitHubQueryController_message_error_already_exists();
                        }
                    }
                    return null;
                }
            };
            // if cancel button is pressed, null value will be returned
            String queryName = SaveQueryPanel.show(validator, HelpCtx.DEFAULT_HELP);
            if (StringUtils.isEmpty(queryName)) {
                return;
            }
            query.setDisplayName(queryName);
        }
        query.setQueryParam(issuesParams);
        query.save();
        query.setSaved(true);
        getPanel().update();
        StatusDisplayer.getDefault().setStatusText(Bundle.GitHubQueryController_message_saved());
        query.refresh();
        firePropertyChange();
    }

    private void search() {
        issueTable.started();
        SearchIssuesParams params = createSearchIssuesParams();
        List<GitHubIssue> searchIssues = query.searchIssues(params, false);
        getPanel().setResultCounts(searchIssues.size());
        for (GitHubIssue searchIssue : searchIssues) {
            IssueNode issueNode = searchIssue.getIssueNode();
            issueTable.addNode(issueNode);
        }
    }

    private SearchIssuesParams createSearchIssuesParams() {
        GitHubQueryPanel p = getPanel();
        return new SearchIssuesParams()
                .keyword(p.getKeyword())
                .milestone(p.getMilestone())
                .state(p.getState())
                .type(p.getType())
                .comments(p.getComments())
                .assignee(p.getAssignee())
                .author(p.getAuthor())
                .involves(p.getInvolves())
                .mentions(p.getMentions())
                .created(p.getCreated())
                .updated(p.getUpdated())
                .merged(p.getMerged())
                .closed(p.getClosed())
                .language(p.getLanguage())
                .labels(p.getLabels())
                .is(p.getIsOpen())
                .is(p.getIsMerged())
                .is(p.getIsIssue())
                .no(p.getNo())
                .in(p.getIn())
                .sort(p.getSort())
                .order(p.getOrder());
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        validate();
    }

    private void validate() {
        getPanel().setButtonsEnabled(isValid());
        getPanel().setErrorMessage(errorMessage);
    }

    @NbBundle.Messages({
        "# {0} - name",
        "GitHubQueryController.message.date.format.error={0} is invalid format.(e.g. 2014-12-05, <=2014-12-05)",
        "GitHubQueryController.message.comments.format.error=Comments is invalid format.(e.g. <100, 100..200)",
        "GitHubQueryController.message.in.format.error=You can just use body, title, comments or any combination of these.",
        "GitHubQueryController.label.created=Created",
        "GitHubQueryController.label.updated=Updated",
        "GitHubQueryController.label.merged=Merged",
        "GitHubQueryController.label.closed=Closed"
    })
    private boolean isValid() {
        GitHubQueryPanel p = getPanel();
        if (!ValidateUtils.isQueryDateFormat(p.getCreated())) {
            errorMessage = Bundle.GitHubQueryController_message_date_format_error(Bundle.GitHubQueryController_label_created());
            return false;
        }

        if (!ValidateUtils.isQueryDateFormat(p.getUpdated())) {
            errorMessage = Bundle.GitHubQueryController_message_date_format_error(Bundle.GitHubQueryController_label_updated());
            return false;
        }

        if (!ValidateUtils.isQueryDateFormat(p.getMerged())) {
            errorMessage = Bundle.GitHubQueryController_message_date_format_error(Bundle.GitHubQueryController_label_merged());
            return false;
        }

        if (!ValidateUtils.isQueryDateFormat(p.getClosed())) {
            errorMessage = Bundle.GitHubQueryController_message_date_format_error(Bundle.GitHubQueryController_label_closed());
            return false;
        }

        if (!ValidateUtils.isQueryCommentsFormat(p.getComments())) {
            errorMessage = Bundle.GitHubQueryController_message_comments_format_error();
            return false;
        }

        if (!ValidateUtils.isQueryInFormat(p.getIn())) {
            errorMessage = Bundle.GitHubQueryController_message_in_format_error();
            return false;
        }

        // everything ok
        errorMessage = null;
        return true;
    }

}
