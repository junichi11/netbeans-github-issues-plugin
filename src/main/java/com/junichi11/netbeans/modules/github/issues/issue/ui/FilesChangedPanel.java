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

import com.junichi11.netbeans.modules.github.issues.GitHubIssues;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.EditorKit;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.netbeans.api.progress.BaseProgressUtils;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

/**
 *
 * @author junichi11
 */
final class FilesChangedPanel extends JPanel implements PropertyChangeListener {

    public String diffUrl;
    public String diff;
    private String displayName;
    private TopComponent diffTopComponent;
    private static final String DIFF_MIME_TYPE = "text/x-diff"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(FilesChangedPanel.class.getName());
    private static final long serialVersionUID = 9077936142573482362L;

    /**
     * Creates new form FilesChangedPanel
     */
    public FilesChangedPanel() {
        initComponents();
        init();
    }

    private void init() {
        setMimeType();
    }

    private void setMimeType() {
        EditorKit editorKit = CloneableEditorSupport.getEditorKit(DIFF_MIME_TYPE);
        diffEditorPane.setEditorKit(editorKit);
        diffEditorPane.getDocument().putProperty("mimeType", DIFF_MIME_TYPE); // NOI18N
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortDisplayName() {
        if (displayName == null) {
            return "Diff"; // NOI18N
        }
        int length = displayName.length();
        if (length > 16) {
            return displayName.substring(0, 16) + "..."; // NOI18N
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDetails(PullRequest pullRequest) {
        assert EventQueue.isDispatchThread();
        if (pullRequest == null) {
            return;
        }
        setAdditions(pullRequest.getAdditions());
        setDeletions(pullRequest.getDeletions());
        diffUrl = pullRequest.getDiffUrl();
    }

    public void addFile(CommitFile file, PullRequestMarker marker) {
        assert EventQueue.isDispatchThread();
        if (file == null) {
            return;
        }
        FileChangedPanel fileChangedPanel = new FileChangedPanel(file, marker);
        filesPanel.add(fileChangedPanel);
    }

    public void removeAllFiles() {
        assert EventQueue.isDispatchThread();
        filesPanel.removeAll();
    }

    private void setAdditions(int lines) {
        additionsLabel.setText("+" + String.valueOf(lines)); // NOI18N
        additionsLabel.setForeground(GitHubIssues.GREEN_COLOR);
    }

    private void setDeletions(int lines) {
        deletionsLabel.setText("-" + String.valueOf(lines)); // NOI18N
        deletionsLabel.setForeground(GitHubIssues.RED_COLOR);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        diffPanel = new javax.swing.JPanel();
        diffScrollPane = new javax.swing.JScrollPane();
        diffEditorPane = new javax.swing.JEditorPane();
        diffButton = new javax.swing.JButton();
        deletionsLabel = new javax.swing.JLabel();
        additionsLabel = new javax.swing.JLabel();
        filesPanel = new javax.swing.JPanel();

        diffEditorPane.setEditable(false);
        diffEditorPane.setContentType("text/x-diff"); // NOI18N
        diffScrollPane.setViewportView(diffEditorPane);

        javax.swing.GroupLayout diffPanelLayout = new javax.swing.GroupLayout(diffPanel);
        diffPanel.setLayout(diffPanelLayout);
        diffPanelLayout.setHorizontalGroup(
            diffPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(diffScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );
        diffPanelLayout.setVerticalGroup(
            diffPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(diffScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(diffButton, org.openide.util.NbBundle.getMessage(FilesChangedPanel.class, "FilesChangedPanel.diffButton.text")); // NOI18N
        diffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diffButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deletionsLabel, org.openide.util.NbBundle.getMessage(FilesChangedPanel.class, "FilesChangedPanel.deletionsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(additionsLabel, org.openide.util.NbBundle.getMessage(FilesChangedPanel.class, "FilesChangedPanel.additionsLabel.text")); // NOI18N

        filesPanel.setLayout(new javax.swing.BoxLayout(filesPanel, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(diffButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                        .addComponent(additionsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deletionsLabel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(diffButton)
                    .addComponent(deletionsLabel)
                    .addComponent(additionsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void diffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diffButtonActionPerformed
        AtomicBoolean cancel = new AtomicBoolean();
        if (diffTopComponent != null) {
            diffTopComponent.requestActive();
            return;
        }
        TopComponent.Registry registry = TopComponent.getRegistry();
        registry.addPropertyChangeListener(this);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                final String diffText = fetchDiff();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        diffEditorPane.setText(diffText);
                        diffTopComponent = new DiffTopComponent();
                        diffTopComponent.setDisplayName(getShortDisplayName());
                        diffTopComponent.setToolTipText(getDisplayName());
                        diffTopComponent.setLayout(new BorderLayout());
                        diffTopComponent.add(diffPanel, BorderLayout.CENTER);
                        diffTopComponent.open();
                        diffTopComponent.requestActive();
                    }
                });
            }
        };
        BaseProgressUtils.runOffEventDispatchThread(task, "Fetching diff...", cancel, false);
    }//GEN-LAST:event_diffButtonActionPerformed

    private String fetchDiff() {
        if (diff != null) {
            return diff;
        }
        if (StringUtils.isEmpty(diffUrl)) {
            return ""; // NOI18N
        }
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(diffUrl);
            try (InputStream inputStream = new BufferedInputStream(url.openStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n"); // NOI18N
                }
            }
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        diff = sb.toString();
        return diff;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel additionsLabel;
    private javax.swing.JLabel deletionsLabel;
    private javax.swing.JButton diffButton;
    private javax.swing.JEditorPane diffEditorPane;
    private javax.swing.JPanel diffPanel;
    private javax.swing.JScrollPane diffScrollPane;
    private javax.swing.JPanel filesPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (diffTopComponent == null) {
            return;
        }
        if (evt.getPropertyName().equals(TopComponent.Registry.PROP_TC_CLOSED)) {
            if (evt.getNewValue() == diffTopComponent) {
                TopComponent.Registry registry = TopComponent.getRegistry();
                registry.removePropertyChangeListener(this);
                diffTopComponent.removeAll();
                diffTopComponent = null;
            }
        }
    }
}
