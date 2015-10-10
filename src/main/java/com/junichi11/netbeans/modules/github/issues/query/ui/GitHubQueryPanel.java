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
package com.junichi11.netbeans.modules.github.issues.query.ui;

import com.junichi11.netbeans.modules.github.issues.GitHubIcons;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Is;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.No;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Order;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Sort;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.State;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Type;
import com.junichi11.netbeans.modules.github.issues.query.GitHubQuery;
import com.junichi11.netbeans.modules.github.issues.query.GitHubQuery.QParam;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class GitHubQueryPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -5303088106606244893L;
    private final GitHubQuery query;
    private final DefaultComboBoxModel<SearchIssuesParams.State> stateComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<SearchIssuesParams.Type> typeComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<SearchIssuesParams.Is> isOpenComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<SearchIssuesParams.Is> isMergedComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<SearchIssuesParams.Is> isIssueComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<SearchIssuesParams.No> noComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<SearchIssuesParams.Sort> sortComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<SearchIssuesParams.Order> orderComboBoxModel = new DefaultComboBoxModel();
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * Creates new form GitHubQueryPanel
     */
    public GitHubQueryPanel(GitHubQuery query, JComponent table) {
        this.query = query;
        initComponents();

        // header
        headerErrorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        setErrorMessage(""); // NOI18N

        Font font = headerErrorLabel.getFont();
        headerNameLabel.setFont(font.deriveFont((float) (font.getSize() * 1.5)));
        headerNameLabel.setIcon(GitHubIcons.GITHUB_ICON_32);

        // state
        stateComboBoxModel.addElement(null);
        for (State state : State.values()) {
            stateComboBoxModel.addElement(state);
        }
        stateComboBox.setModel(stateComboBoxModel);
        stateComboBox.setRenderer(new GitHubQueryListCellRenderer(stateComboBox.getRenderer()));

        // type
        typeComboBoxModel.addElement(null);
        for (Type type : Type.values()) {
            typeComboBoxModel.addElement(type);
        }
        typeComboBox.setModel(typeComboBoxModel);
        typeComboBox.setRenderer(new GitHubQueryListCellRenderer(typeComboBox.getRenderer()));

        // is
        isOpenComboBoxModel.addElement(null);
        isOpenComboBoxModel.addElement(Is.OPEN);
        isOpenComboBoxModel.addElement(Is.CLOSED);
        isOpenComboBox.setModel(isOpenComboBoxModel);
        isOpenComboBox.setRenderer(new GitHubQueryListCellRenderer(isOpenComboBox.getRenderer()));

        isMergedComboBoxModel.addElement(null);
        isMergedComboBoxModel.addElement(Is.MERGED);
        isMergedComboBoxModel.addElement(Is.UNMERGED);
        isMergedComboBox.setModel(isMergedComboBoxModel);
        isMergedComboBox.setRenderer(new GitHubQueryListCellRenderer(isMergedComboBox.getRenderer()));

        isIssueComboBoxModel.addElement(null);
        isIssueComboBoxModel.addElement(Is.ISSUE);
        isIssueComboBoxModel.addElement(Is.PR);
        isIssueComboBox.setModel(isIssueComboBoxModel);
        isIssueComboBox.setRenderer(new GitHubQueryListCellRenderer(isIssueComboBox.getRenderer()));

        // no
        noComboBoxModel.addElement(null);
        for (No no : No.values()) {
            noComboBoxModel.addElement(no);
        }
        noComboBox.setModel(noComboBoxModel);
        noComboBox.setRenderer(new GitHubQueryListCellRenderer(noComboBox.getRenderer()));

        // sort
        sortComboBoxModel.addElement(null);
        for (Sort sort : Sort.values()) {
            sortComboBoxModel.addElement(sort);
        }
        sortComboBox.setModel(sortComboBoxModel);
        sortComboBox.setRenderer(new GitHubQueryListCellRenderer(sortComboBox.getRenderer()));

        // order
        orderComboBoxModel.addElement(null);
        for (Order order : Order.values()) {
            orderComboBoxModel.addElement(order);
        }
        orderComboBox.setModel(orderComboBoxModel);
        orderComboBox.setRenderer(new GitHubQueryListCellRenderer(orderComboBox.getRenderer()));

        // add listener
        addDocumentListener();

        // issue table
        mainIssueTablePanel.add(table);

        resultsLabel.setText(""); // NOI18N

        update();
    }

    private void addDocumentListener() {
        DefaultDocumentListener defaultDocumentListener = new DefaultDocumentListener();
        createdTextField.getDocument().addDocumentListener(defaultDocumentListener);
        updatedTextField.getDocument().addDocumentListener(defaultDocumentListener);
        mergedTextField.getDocument().addDocumentListener(defaultDocumentListener);
        closedTextField.getDocument().addDocumentListener(defaultDocumentListener);
        commentsTextField.getDocument().addDocumentListener(defaultDocumentListener);
        inTextField.getDocument().addDocumentListener(defaultDocumentListener);
    }

    public final void update() {
        setHeaderName();
        if (query == null || !query.isSaved()) {
            return;
        }
        // set existing info
        keywordTextField.setText(query.getParameter(QParam.KEYWORD));
        stateComboBox.setSelectedItem(State.valueOfString(query.getParameter(QParam.STATE)));
        commentsTextField.setText(query.getParameter(QParam.COMMENTS));
        labelsTextField.setText(query.getParameter(QParam.LABELS));
        authorTextField.setText(query.getParameter(QParam.AUTHOR));
        mentionsTextField.setText(query.getParameter(QParam.MENTIONS));
        assigneeTextField.setText(query.getParameter(QParam.ASSIGNEE));
        involvesTextField.setText(query.getParameter(QParam.INVOLVES));
        languageTextField.setText(query.getParameter(QParam.LANGUAGE));
        createdTextField.setText(query.getParameter(QParam.CREATED));
        updatedTextField.setText(query.getParameter(QParam.UPDATED));
        closedTextField.setText(query.getParameter(QParam.CLOSED));
        mergedTextField.setText(query.getParameter(QParam.MERGED));
        inTextField.setText(query.getParameter(QParam.IN));
        noComboBox.setSelectedItem(No.valueOfString(query.getParameter(QParam.NO)));
        isOpenComboBox.setSelectedItem(Is.valueOfString(query.getParameter(QParam.IS_OPEN)));
        isMergedComboBox.setSelectedItem(Is.valueOfString(query.getParameter(QParam.IS_MERGED)));
        isIssueComboBox.setSelectedItem(Is.valueOfString(query.getParameter(QParam.IS_ISSUE)));
        sortComboBox.setSelectedItem(Sort.valueOfString(query.getParameter(GitHubQuery.Param.SORT)));
        orderComboBox.setSelectedItem(Order.valueOfString(query.getParameter(GitHubQuery.Param.ORDER)));
    }

    public final void setErrorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = ""; // NOI18N
            headerErrorLabel.setIcon(null);
        } else {
            headerErrorLabel.setIcon(GitHubIcons.ERROR_ICON_16);
        }
        headerErrorLabel.setText(errorMessage);
    }

    @NbBundle.Messages({
        "# {0} - count",
        "GitHubQueryPanel.message.result.issues=There are {0} issues matching this query.",
        "GitHubQueryPanel.message.result.issue=There is 1 issue matching this query.",
        "GitHubQueryPanel.message.result.empty=There is no issue that matching the query"
    })
    public final void setResultCounts(int count) {
        if (count < 0) {
            resultsLabel.setText(""); // NOI18N
            return;
        }
        if (count == 0) {
            resultsLabel.setText(Bundle.GitHubQueryPanel_message_result_empty());
        } else if (count == 1) {
            resultsLabel.setText(Bundle.GitHubQueryPanel_message_result_issue());
        } else {
            resultsLabel.setText(Bundle.GitHubQueryPanel_message_result_issues(count));
        }
    }

    @NbBundle.Messages({
        "GitHubQueryPanel.new.query.displayName=New query"
    })
    private void setHeaderName() {
        if (!query.isSaved()) {
            headerNameLabel.setText(Bundle.GitHubQueryPanel_new_query_displayName());
            return;
        }
        headerNameLabel.setText(query.getDisplayName());
    }

    public String getKeyword() {
        return keywordTextField.getText().trim();
    }

    public State getState() {
        return (State) stateComboBox.getSelectedItem();
    }

    public String getComments() {
        return commentsTextField.getText().trim();
    }

    public String getLabels() {
        return labelsTextField.getText().trim();
    }

    public String getAuthor() {
        return authorTextField.getText().trim();
    }

    public String getMentions() {
        return mentionsTextField.getText().trim();
    }

    public String getAssignee() {
        return assigneeTextField.getText().trim();
    }

    public String getInvolves() {
        return involvesTextField.getText().trim();
    }

    public String getLanguage() {
        return languageTextField.getText().trim();
    }

    public String getCreated() {
        return createdTextField.getText().trim();
    }

    public String getUpdated() {
        return updatedTextField.getText().trim();
    }

    public String getMerged() {
        return mergedTextField.getText().trim();
    }

    public String getClosed() {
        return closedTextField.getText().trim();
    }

    public String getIn() {
        return inTextField.getText().replaceAll("\\s", "").trim(); // NOI18N
    }

    public Type getType() {
        return (Type) typeComboBox.getSelectedItem();
    }

    public No getNo() {
        return (No) noComboBox.getSelectedItem();
    }

    public Is getIsOpen() {
        return (Is) isOpenComboBox.getSelectedItem();
    }

    public Is getIsMerged() {
        return (Is) isMergedComboBox.getSelectedItem();
    }

    public Is getIsIssue() {
        return (Is) isIssueComboBox.getSelectedItem();
    }

    public Sort getSort() {
        return (Sort) sortComboBox.getSelectedItem();
    }

    public Order getOrder() {
        return (Order) orderComboBox.getSelectedItem();
    }

    public boolean isSearch(Object source) {
        return source == searchButton;
    }

    public boolean isSave(Object source) {
        return source == saveButton;
    }

    public void addSaveAction(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    public void removeSaveAction(ActionListener listener) {
        saveButton.removeActionListener(listener);
    }

    public void addSearchAction(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void removeSearchAction(ActionListener listener) {
        searchButton.removeActionListener(listener);
    }

    public void setButtonsEnabled(boolean isEnabled) {
        searchButton.setEnabled(isEnabled);
        saveButton.setEnabled(isEnabled);
        resetButton.setEnabled(isEnabled);
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

        stateLabel = new javax.swing.JLabel();
        stateComboBox = new javax.swing.JComboBox<State>();
        keywordLabel = new javax.swing.JLabel();
        keywordTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        commentsLabel = new javax.swing.JLabel();
        commentsTextField = new javax.swing.JTextField();
        labelsLabel = new javax.swing.JLabel();
        labelsTextField = new javax.swing.JTextField();
        authorLabel = new javax.swing.JLabel();
        authorTextField = new javax.swing.JTextField();
        mentionsLabel = new javax.swing.JLabel();
        mentionsTextField = new javax.swing.JTextField();
        assigneeLabel = new javax.swing.JLabel();
        assigneeTextField = new javax.swing.JTextField();
        involvesLabel = new javax.swing.JLabel();
        involvesTextField = new javax.swing.JTextField();
        inLabel = new javax.swing.JLabel();
        inTextField = new javax.swing.JTextField();
        noLabel = new javax.swing.JLabel();
        isLabel = new javax.swing.JLabel();
        languageLabel = new javax.swing.JLabel();
        languageTextField = new javax.swing.JTextField();
        createdLabel = new javax.swing.JLabel();
        updatedLabel = new javax.swing.JLabel();
        mergedLabel = new javax.swing.JLabel();
        closedLabel = new javax.swing.JLabel();
        createdTextField = new javax.swing.JTextField();
        updatedTextField = new javax.swing.JTextField();
        mergedTextField = new javax.swing.JTextField();
        closedTextField = new javax.swing.JTextField();
        sortLabel = new javax.swing.JLabel();
        sortComboBox = new javax.swing.JComboBox<Sort>();
        orderLabel = new javax.swing.JLabel();
        orderComboBox = new javax.swing.JComboBox<Order>();
        headerPanel = new javax.swing.JPanel();
        headerNameLabel = new javax.swing.JLabel();
        headerErrorLabel = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        mainIssueTablePanel = new javax.swing.JPanel();
        resultsLabel = new javax.swing.JLabel();
        isOpenComboBox = new javax.swing.JComboBox<Is>();
        isMergedComboBox = new javax.swing.JComboBox<Is>();
        isIssueComboBox = new javax.swing.JComboBox<Is>();
        noComboBox = new javax.swing.JComboBox<No>();
        typeLabel = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<Type>();

        org.openide.awt.Mnemonics.setLocalizedText(stateLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.stateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(keywordLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.keywordLabel.text")); // NOI18N

        keywordTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.keywordTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(searchButton, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.searchButton.text")); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(saveButton, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.saveButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(commentsLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.commentsLabel.text")); // NOI18N

        commentsTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.commentsTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelsLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.labelsLabel.text")); // NOI18N

        labelsTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.labelsTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(authorLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.authorLabel.text")); // NOI18N

        authorTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.authorTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(mentionsLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.mentionsLabel.text")); // NOI18N

        mentionsTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.mentionsTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(assigneeLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.assigneeLabel.text")); // NOI18N

        assigneeTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.assigneeTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(involvesLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.involvesLabel.text")); // NOI18N

        involvesTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.involvesTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(inLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.inLabel.text")); // NOI18N

        inTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.inTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(noLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.noLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(isLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.isLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(languageLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.languageLabel.text")); // NOI18N

        languageTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.languageTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(createdLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.createdLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(updatedLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.updatedLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(mergedLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.mergedLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(closedLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.closedLabel.text")); // NOI18N

        createdTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.createdTextField.text")); // NOI18N

        updatedTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.updatedTextField.text")); // NOI18N

        mergedTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.mergedTextField.text")); // NOI18N

        closedTextField.setText(org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.closedTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sortLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.sortLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(orderLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.orderLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerNameLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.headerNameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerErrorLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.headerErrorLabel.text")); // NOI18N

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(headerNameLabel)
                    .addComponent(headerErrorLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(headerErrorLabel)
                .addContainerGap())
        );

        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.resetButton.text")); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        mainIssueTablePanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(resultsLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.resultsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(typeLabel, org.openide.util.NbBundle.getMessage(GitHubQueryPanel.class, "GitHubQueryPanel.typeLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mentionsLabel)
                    .addComponent(assigneeLabel)
                    .addComponent(authorLabel)
                    .addComponent(involvesLabel)
                    .addComponent(labelsLabel)
                    .addComponent(languageLabel)
                    .addComponent(keywordLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(authorTextField)
                            .addComponent(mentionsTextField)
                            .addComponent(assigneeTextField)
                            .addComponent(involvesTextField)
                            .addComponent(labelsTextField)
                            .addComponent(languageTextField))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(createdLabel)
                            .addComponent(updatedLabel)
                            .addComponent(mergedLabel)
                            .addComponent(closedLabel)
                            .addComponent(inLabel)
                            .addComponent(commentsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(createdTextField)
                            .addComponent(updatedTextField)
                            .addComponent(mergedTextField)
                            .addComponent(closedTextField)
                            .addComponent(inTextField)
                            .addComponent(commentsTextField)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(keywordTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(mainIssueTablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(isLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(isOpenComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(isMergedComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(isIssueComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(noLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(noComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultsLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sortLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(orderLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(orderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keywordLabel)
                    .addComponent(keywordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stateLabel)
                    .addComponent(stateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authorLabel)
                    .addComponent(authorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createdLabel)
                    .addComponent(createdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mentionsLabel)
                    .addComponent(mentionsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updatedLabel)
                    .addComponent(updatedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(assigneeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(assigneeLabel)
                    .addComponent(mergedLabel)
                    .addComponent(mergedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(involvesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(involvesLabel)
                            .addComponent(closedLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inLabel)
                            .addComponent(labelsLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(closedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(languageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(languageLabel))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(commentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(commentsLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(isLabel)
                    .addComponent(isOpenComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(isMergedComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(isIssueComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(noLabel)
                    .addComponent(noComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeLabel)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortLabel)
                    .addComponent(sortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(orderLabel)
                    .addComponent(orderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchButton)
                    .addComponent(saveButton)
                    .addComponent(resetButton)
                    .addComponent(resultsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainIssueTablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
    }//GEN-LAST:event_searchButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        update();
    }//GEN-LAST:event_resetButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel assigneeLabel;
    private javax.swing.JTextField assigneeTextField;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JTextField authorTextField;
    private javax.swing.JLabel closedLabel;
    private javax.swing.JTextField closedTextField;
    private javax.swing.JLabel commentsLabel;
    private javax.swing.JTextField commentsTextField;
    private javax.swing.JLabel createdLabel;
    private javax.swing.JTextField createdTextField;
    private javax.swing.JLabel headerErrorLabel;
    private javax.swing.JLabel headerNameLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel inLabel;
    private javax.swing.JTextField inTextField;
    private javax.swing.JLabel involvesLabel;
    private javax.swing.JTextField involvesTextField;
    private javax.swing.JComboBox<Is> isIssueComboBox;
    private javax.swing.JLabel isLabel;
    private javax.swing.JComboBox<Is> isMergedComboBox;
    private javax.swing.JComboBox<Is> isOpenComboBox;
    private javax.swing.JLabel keywordLabel;
    private javax.swing.JTextField keywordTextField;
    private javax.swing.JLabel labelsLabel;
    private javax.swing.JTextField labelsTextField;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JTextField languageTextField;
    private javax.swing.JPanel mainIssueTablePanel;
    private javax.swing.JLabel mentionsLabel;
    private javax.swing.JTextField mentionsTextField;
    private javax.swing.JLabel mergedLabel;
    private javax.swing.JTextField mergedTextField;
    private javax.swing.JComboBox<No> noComboBox;
    private javax.swing.JLabel noLabel;
    private javax.swing.JComboBox<Order> orderComboBox;
    private javax.swing.JLabel orderLabel;
    private javax.swing.JButton resetButton;
    private javax.swing.JLabel resultsLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JComboBox<Sort> sortComboBox;
    private javax.swing.JLabel sortLabel;
    private javax.swing.JComboBox<State> stateComboBox;
    private javax.swing.JLabel stateLabel;
    private javax.swing.JComboBox<Type> typeComboBox;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JLabel updatedLabel;
    private javax.swing.JTextField updatedTextField;
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
