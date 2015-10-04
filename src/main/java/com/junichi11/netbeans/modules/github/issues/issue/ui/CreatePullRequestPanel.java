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

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.parboiled.common.StringUtils;

/**
 *
 * @author junichi11
 */
public final class CreatePullRequestPanel extends JPanel {

    private static final long serialVersionUID = 5695111005863831097L;
    private static final Icon ERROR_ICON = ImageUtilities.loadImageIcon("com/junichi11/netbeans/modules/github/issues/resources/error_icon_16.png", true); // NOI18N
    private final DefaultComboBoxModel<RepositoryBranch> baseComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<RepositoryBranch> compareComboBoxModel = new DefaultComboBoxModel();
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * Creates new form CreatePullRequestPanel
     */
    public CreatePullRequestPanel(List<RepositoryBranch> baseBranches, List<RepositoryBranch> compareBranches) {
        initComponents();

        baseComboBox.setRenderer(new PullRequestListCellRenderer(baseComboBox.getRenderer()));
        compareComboBox.setRenderer(new PullRequestListCellRenderer(compareComboBox.getRenderer()));
        for (RepositoryBranch baseBranche : baseBranches) {
            baseComboBoxModel.addElement(baseBranche);
        }
        for (RepositoryBranch compareBranch : compareBranches) {
            compareComboBoxModel.addElement(compareBranch);
        }
        baseComboBox.setModel(baseComboBoxModel);
        compareComboBox.setModel(compareComboBoxModel);

        init();
    }

    private void init() {
        setMessage(""); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        setErrorMessage(""); // NOI18N
        // add listener
        DefaultItemListener itemListener = new DefaultItemListener();
        baseComboBox.addItemListener(itemListener);
        compareComboBox.addItemListener(itemListener);
    }

    public RepositoryBranch getSelectedBaseBranch() {
        return (RepositoryBranch) baseComboBox.getSelectedItem();
    }

    public RepositoryBranch getSelectedCompareBranch() {
        return (RepositoryBranch) compareComboBox.getSelectedItem();
    }

    public void setMessage(String message) {
        messageLabel.setVisible(!StringUtils.isEmpty(message));
        messageLabel.setText(message);
    }

    public void setErrorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorLabel.setIcon(null);
        } else {
            errorLabel.setIcon(ERROR_ICON);
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
        baseComboBox = new javax.swing.JComboBox<RepositoryBranch>();
        compareLabel = new javax.swing.JLabel();
        compareComboBox = new javax.swing.JComboBox<RepositoryBranch>();
        messageLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(baseLabel, org.openide.util.NbBundle.getMessage(CreatePullRequestPanel.class, "CreatePullRequestPanel.baseLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(compareLabel, org.openide.util.NbBundle.getMessage(CreatePullRequestPanel.class, "CreatePullRequestPanel.compareLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(messageLabel, org.openide.util.NbBundle.getMessage(CreatePullRequestPanel.class, "CreatePullRequestPanel.messageLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(CreatePullRequestPanel.class, "CreatePullRequestPanel.errorLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(baseLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(baseComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(compareLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(compareComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(messageLabel)
                            .addComponent(errorLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addComponent(compareLabel)
                    .addComponent(compareComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(errorLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<RepositoryBranch> baseComboBox;
    private javax.swing.JLabel baseLabel;
    private javax.swing.JComboBox<RepositoryBranch> compareComboBox;
    private javax.swing.JLabel compareLabel;
    private javax.swing.JLabel errorLabel;
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
