/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package com.junichi11.netbeans.modules.github.issues.issue.ui;

import com.junichi11.netbeans.modules.github.issues.GitHubIcons;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.openide.util.ChangeSupport;
import org.parboiled.common.StringUtils;

/**
 *
 * @author junichi11
 */
public final class CreatePullRequestPanel extends JPanel {

    private static final long serialVersionUID = 5695111005863831097L;
    private final DefaultComboBoxModel<Repository> baseRepositoryComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<RepositoryBranch> baseComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<Repository> headRepositoryComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<RepositoryBranch> headComboBoxModel = new DefaultComboBoxModel();
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final Map<Repository, List<RepositoryBranch>> baseRepositories;
    private final Map<Repository, List<RepositoryBranch>> headRepositories;

    public static final String PROP_COMPARE_PULL_REQUEST = "github.issues.pull.request.compare"; // NOI18N

    /**
     * Creates new form CreatePullRequestPanel
     */
    public CreatePullRequestPanel(Map<Repository, List<RepositoryBranch>> baseRepositories, Map<Repository, List<RepositoryBranch>> headRepositories) {
        this.baseRepositories = baseRepositories;
        this.headRepositories = headRepositories;
        initComponents();

        baseRepositoryComboBox.setRenderer(new PullRequestListCellRenderer(baseRepositoryComboBox.getRenderer()));
        headRepositoryComboBox.setRenderer(new PullRequestListCellRenderer(headRepositoryComboBox.getRenderer()));
        baseComboBox.setRenderer(new PullRequestListCellRenderer(baseComboBox.getRenderer()));
        headComboBox.setRenderer(new PullRequestListCellRenderer(headComboBox.getRenderer()));

        addRepositories(baseRepositoryComboBoxModel, baseComboBoxModel, baseRepositories);
        addRepositories(headRepositoryComboBoxModel, headComboBoxModel, headRepositories);

        baseRepositoryComboBox.setModel(baseRepositoryComboBoxModel);
        headRepositoryComboBox.setModel(headRepositoryComboBoxModel);
        baseComboBox.setModel(baseComboBoxModel);
        headComboBox.setModel(headComboBoxModel);

        init();
    }

    private void addRepositories(DefaultComboBoxModel<Repository> repositoryModel, DefaultComboBoxModel<RepositoryBranch> branchModel, Map<Repository, List<RepositoryBranch>> repositories) {
        boolean first = true;
        for (Repository repository : repositories.keySet()) {
            repositoryModel.addElement(repository);
            if (first) {
                first = false;
                addBranches(branchModel, repositories.get(repository));
            }
        }
    }

    private void addBranches(DefaultComboBoxModel<RepositoryBranch> model, List<RepositoryBranch> branches) {
        model.removeAllElements();
        for (RepositoryBranch branch : branches) {
            model.addElement(branch);
        }
    }

    private void init() {
        setMessage(""); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        setErrorMessage(""); // NOI18N

        // add listener
        DefaultItemListener itemListener = new DefaultItemListener();
        baseComboBox.addItemListener(itemListener);
        headComboBox.addItemListener(itemListener);
        baseRepositoryComboBox.addItemListener(itemListener);
        headRepositoryComboBox.addItemListener(itemListener);
        baseRepositoryComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Repository repository = getSelectedBaseRepository();
                addBranches(baseComboBoxModel, CreatePullRequestPanel.this.baseRepositories.get(repository));
            }
        });
        headRepositoryComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Repository repository = getSelectedHeadRepository();
                addBranches(baseComboBoxModel, CreatePullRequestPanel.this.headRepositories.get(repository));
            }
        });
    }

    public void setCompareButtonEnabled(boolean isEnabled) {
        compareButton.setEnabled(isEnabled);
    }

    public Repository getSelectedBaseRepository() {
        return (Repository) baseRepositoryComboBox.getSelectedItem();
    }

    public RepositoryBranch getSelectedBaseBranch() {
        return (RepositoryBranch) baseComboBox.getSelectedItem();
    }

    public Repository getSelectedHeadRepository() {
        return (Repository) headRepositoryComboBox.getSelectedItem();
    }

    public RepositoryBranch getSelectedHeadBranch() {
        return (RepositoryBranch) headComboBox.getSelectedItem();
    }

    public void setMessage(String message) {
        messageLabel.setVisible(!StringUtils.isEmpty(message));
        messageLabel.setText(message);
    }

    public void setErrorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorLabel.setIcon(null);
        } else {
            errorLabel.setIcon(GitHubIcons.ERROR_ICON_16);
        }
        errorLabel.setText(errorMessage);
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

        baseLabel = new javax.swing.JLabel();
        headLabel = new javax.swing.JLabel();
        messageLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();
        compareButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(baseLabel, org.openide.util.NbBundle.getMessage(CreatePullRequestPanel.class, "CreatePullRequestPanel.baseLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headLabel, org.openide.util.NbBundle.getMessage(CreatePullRequestPanel.class, "CreatePullRequestPanel.headLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(messageLabel, org.openide.util.NbBundle.getMessage(CreatePullRequestPanel.class, "CreatePullRequestPanel.messageLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(CreatePullRequestPanel.class, "CreatePullRequestPanel.errorLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(compareButton, org.openide.util.NbBundle.getMessage(CreatePullRequestPanel.class, "CreatePullRequestPanel.compareButton.text")); // NOI18N
        compareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compareButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(messageLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(errorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(compareButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(baseLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(baseRepositoryComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(baseComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(headLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headRepositoryComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(baseComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(baseLabel)
                    .addComponent(headLabel)
                    .addComponent(headComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(baseRepositoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headRepositoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(errorLabel)
                    .addComponent(compareButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void compareButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compareButtonActionPerformed
        firePropertyChange(PROP_COMPARE_PULL_REQUEST, null, null);
    }//GEN-LAST:event_compareButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JComboBox<RepositoryBranch> baseComboBox = new javax.swing.JComboBox<RepositoryBranch>();
    private javax.swing.JLabel baseLabel;
    private final javax.swing.JComboBox<Repository> baseRepositoryComboBox = new javax.swing.JComboBox<Repository>();
    private javax.swing.JButton compareButton;
    private javax.swing.JLabel errorLabel;
    private final javax.swing.JComboBox<RepositoryBranch> headComboBox = new javax.swing.JComboBox<RepositoryBranch>();
    private javax.swing.JLabel headLabel;
    private final javax.swing.JComboBox<Repository> headRepositoryComboBox = new javax.swing.JComboBox<Repository>();
    private javax.swing.JLabel messageLabel;
    // End of variables declaration//GEN-END:variables

    private static class PullRequestListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -8351981614538451529L;

        private final ListCellRenderer renderer;

        public PullRequestListCellRenderer(ListCellRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String text = null;
            if (value instanceof RepositoryBranch) {
                RepositoryBranch branch = (RepositoryBranch) value;
                text = branch.getName();
            } else if (value instanceof Repository) {
                Repository repository = (Repository) value;
                text = repository.getOwner().getLogin() + "/" + repository.getName(); // NOI18N
            }
            if (text == null) {
                text = " "; // NOI18N
            }
            JLabel label = (JLabel) renderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            return label;
        }
    }

    private class DefaultItemListener implements ItemListener {

        public DefaultItemListener() {
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            fireChange();
        }
    }
}
