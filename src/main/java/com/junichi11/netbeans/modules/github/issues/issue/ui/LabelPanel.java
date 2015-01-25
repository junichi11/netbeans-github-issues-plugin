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

import java.awt.Color;
import java.awt.Dialog;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.eclipse.egit.github.core.Label;
import org.netbeans.api.annotations.common.CheckForNull;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class LabelPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -9122329677222779787L;
    private static final Icon ERROR_ICON = ImageUtilities.loadImageIcon("com/junichi11/netbeans/modules/github/issues/resources/error_icon_16.png", true); // NOI18N
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private String errorMessage;
    private final List<Label> filter;

    /**
     * Creates new form LabelPanel
     */
    public LabelPanel(List<Label> filter) {
        if (filter == null) {
            this.filter = Collections.emptyList();
        } else {
            this.filter = filter;
        }
        initComponents();
        DocumentListenerImpl documentListener = new DocumentListenerImpl();
        nameTextField.getDocument().addDocumentListener(documentListener);
        colorTextField.getDocument().addDocumentListener(documentListener);
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        setError("");
    }

    /**
     * Show dialog.
     *
     * @param filter list of existing labels
     * @return Label if name and color are input, otherwise {@code null}
     */
    @NbBundle.Messages({
        "LabelPanel.title=Add label"
    })
    @CheckForNull
    public static Label showDialog(List<Label> filter) {
        final LabelPanel panel = new LabelPanel(filter);
        final DialogDescriptor descriptor = new DialogDescriptor(panel, Bundle.LabelPanel_title(), true, DialogDescriptor.OK_CANCEL_OPTION, null, null);
        ChangeListener changeListener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                descriptor.setValid(panel.getErrorMessage() == null);
                panel.setError(panel.getErrorMessage());
            }
        };
        panel.addChangeListener(changeListener);
        panel.fireChange();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();
        panel.removeChangeListener(changeListener);
        if (descriptor.getValue() == DialogDescriptor.OK_OPTION) {
            return new Label()
                    .setName(panel.getLabelName())
                    .setColor(panel.getLabelColor());
        }
        return null;
    }

    public String getLabelName() {
        return nameTextField.getText().trim();
    }

    public String getLabelColor() {
        return colorTextField.getText();
    }

    public final void setError(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorLabel.setText(""); // NOI18N
            errorLabel.setIcon(null);
            return;
        }
        errorLabel.setText(errorMessage);
        errorLabel.setIcon(ERROR_ICON);
    }

    void fireChange() {
        validateInputValues();
        changeSupport.fireChange();
    }

    void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    @NbBundle.Messages({
        "LabelPanel.validate.name.empty=Name must be set.",
        "# {0} - label name",
        "LabelPanel.validate.name.alreadyExists={0} already exists.",
        "LabelPanel.validate.color.empty=Color must be set"
    })
    private void validateInputValues() {
        String labelName = getLabelName();
        if (labelName.isEmpty()) {
            errorMessage = Bundle.LabelPanel_validate_name_empty();
            return;
        }

        // already exist?
        for (Label label : filter) {
            if (label == null) {
                continue;
            }
            if (label.getName().equals(labelName)) {
                errorMessage = Bundle.LabelPanel_validate_name_alreadyExists(labelName);
                return;
            }
        }

        String labelColor = getLabelColor();
        if (labelColor.isEmpty()) {
            errorMessage = Bundle.LabelPanel_validate_color_empty();
            return;
        }
        errorMessage = null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectColorButton = new javax.swing.JButton();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        colorLabel = new javax.swing.JLabel();
        colorTextField = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(selectColorButton, org.openide.util.NbBundle.getMessage(LabelPanel.class, "LabelPanel.selectColorButton.text")); // NOI18N
        selectColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectColorButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(LabelPanel.class, "LabelPanel.nameLabel.text")); // NOI18N

        nameTextField.setText(org.openide.util.NbBundle.getMessage(LabelPanel.class, "LabelPanel.nameTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(colorLabel, org.openide.util.NbBundle.getMessage(LabelPanel.class, "LabelPanel.colorLabel.text")); // NOI18N

        colorTextField.setEditable(false);
        colorTextField.setText(org.openide.util.NbBundle.getMessage(LabelPanel.class, "LabelPanel.colorTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(LabelPanel.class, "LabelPanel.errorLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameLabel)
                            .addComponent(colorLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(colorTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectColorButton))
                            .addComponent(nameTextField)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(errorLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectColorButton)
                    .addComponent(colorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages({
        "LabelPanel.colorChooser.title=Select color"
    })
    private void selectColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectColorButtonActionPerformed
        Color color = JColorChooser.showDialog(null, Bundle.LabelPanel_colorChooser_title(), null);
        if (color != null) {
            colorTextField.setText(Integer.toHexString(color.getRGB()).substring(2));
        }
    }//GEN-LAST:event_selectColorButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel colorLabel;
    private javax.swing.JTextField colorTextField;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton selectColorButton;
    // End of variables declaration//GEN-END:variables

    private class DocumentListenerImpl implements DocumentListener {

        public DocumentListenerImpl() {
        }

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
