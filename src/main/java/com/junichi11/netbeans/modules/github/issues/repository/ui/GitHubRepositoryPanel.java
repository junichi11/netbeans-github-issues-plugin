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
package com.junichi11.netbeans.modules.github.issues.repository.ui;

import com.junichi11.netbeans.modules.github.issues.GitHubIssues;
import com.junichi11.netbeans.modules.github.issues.options.GitHubIssuesOptions;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.utils.GitHubIssuesUtils;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import com.junichi11.netbeans.modules.github.issues.utils.UiUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public class GitHubRepositoryPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -7859284981818425916L;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private boolean isConnectionSuccessful;
    private boolean isRepositoryValid;
    private boolean isUserNameValid;
    private boolean isDotGithubValid;
    private boolean hasIssues;
    private List<Repository> repositoryCache;

    /**
     * Creates new form GithubRepositoryPanel
     */
    public GitHubRepositoryPanel() {
        initComponents();
        init();

        // #31 set user name and oauth token from .github file
        try {
            String userName = GitHubIssuesUtils.getUserName();
            String oAuthToken = GitHubIssuesUtils.getOAuthToken();
            if (userName != null && oAuthToken != null) {
                setUserName(userName);
                setOAuthToken(oAuthToken);
            }
        } catch (IOException ex) {
            // noop, just set nothing
        }
    }

    public GitHubRepositoryPanel(GitHubRepository repository) {
        this();
        if (repository != null) {
            RepositoryInfo repositoryInfo = repository.getRepositoryInfo();
            if (repositoryInfo != null) {
                setDisplayName(repositoryInfo.getDisplayName());
                setUserName(repositoryInfo.getUsername());
            }
            setOAuthToken(repository.getOAuthToken());
            setRepositoryAuthor(repository.getRepositoryAuthor());
            setRepositoryName(repository.getRepositoryName());
        }
    }

    private void init() {
        // add DocumentListener
        DisplayNameDocumentListener displayNameDocumentListener = new DisplayNameDocumentListener();
        DefaultDocumentListener defaultDocumentListener = new DefaultDocumentListener();
        displayNameTextField.getDocument().addDocumentListener(displayNameDocumentListener);
        userNameTextField.getDocument().addDocumentListener(defaultDocumentListener);
        oauthTokenTextField.getDocument().addDocumentListener(defaultDocumentListener);
        repositoryAuthorTextField.getDocument().addDocumentListener(defaultDocumentListener);
        repositoryNameTextField.getDocument().addDocumentListener(defaultDocumentListener);
        setUserAndOAuthEnabled(!isPropertyFile());

        // TODO
        propertyFileCheckBox.setVisible(false);
    }

    public String getDisplayName() {
        return displayNameTextField.getText().trim();
    }

    public String getUserName() {
        return userNameTextField.getText().trim();
    }

    public String getOAuthToken() {
        return oauthTokenTextField.getText().trim();
    }

    public boolean isPropertyFile() {
        return propertyFileCheckBox.isSelected();
    }

    public String getRepositoryAuthor() {
        return repositoryAuthorTextField.getText().trim();
    }

    public String getRepositoryName() {
        return repositoryNameTextField.getText().trim();
    }

    public boolean isConnectionSuccessful() {
        return isConnectionSuccessful;
    }

    public boolean isRepositoryValid() {
        return isRepositoryValid;
    }

    public boolean isUserNameValid() {
        return isUserNameValid;
    }

    public boolean isDotGithubValid() {
        return isDotGithubValid;
    }

    public boolean hasIssues() {
        return hasIssues;
    }

    private void setConnectButtonEnabled(boolean isEnabled) {
        connectButton.setEnabled(isEnabled);
    }

    private void setAddRepositoryButtonEnabled(boolean isEnabled) {
        addRepositoryButton.setEnabled(isEnabled);
    }

    private void setUserAndOAuthEnabled(boolean isEnabled) {
        userNameTextField.setEnabled(isEnabled);
        oauthTokenTextField.setEnabled(isEnabled);
    }

    private boolean isUserOrOAuthEmpty() {
        return StringUtils.isEmpty(getUserName()) || StringUtils.isEmpty(getOAuthToken());
    }

    private boolean isRepositoryEmpty() {
        return StringUtils.isEmpty(getRepositoryAuthor()) || StringUtils.isEmpty(getRepositoryName());
    }

    private void setDisplayName(String name) {
        displayNameTextField.setText(name);
    }

    private void setUserName(String name) {
        userNameTextField.setText(name);
    }

    private void setOAuthToken(String oauthToken) {
        oauthTokenTextField.setText(oauthToken);
    }

    private void setRepositoryName(String name) {
        repositoryNameTextField.setText(name);
    }

    private void setRepositoryAuthor(String name) {
        repositoryAuthorTextField.setText(name);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void fireChange() {
        if (isPropertyFile()) {
            setConnectButtonEnabled(!isRepositoryEmpty());
        } else {
            setConnectButtonEnabled(!isUserOrOAuthEmpty() && !isRepositoryEmpty());
        }
        setAddRepositoryButtonEnabled(!StringUtils.isEmpty(getOAuthToken()));
        changeSupport.fireChange();
    }

    @NbBundle.Messages({
        "GitHubRepositoryPanel.message.github.file.exist.error=.github file doesn't exist.",
        "GitHubRepositoryPanel.message.github.file.read.error=Can't read .github file.",
        "GitHubRepositoryPanel.message.userName.oauthToken.error=Please confirm whether your user name and OAuth token are correct.",
        "GitHubRepositoryPanel.message.connection.successful=Connection successful!"
    })
    private void connect() {
        setConnectButtonEnabled(false);
        isConnectionSuccessful = false;
        isRepositoryValid = false;
        hasIssues = true;
        isUserNameValid = true;
        String userName = getUserName();
        String oAuthToken = getOAuthToken();
        try {
            if (isPropertyFile()) {
                userName = null;
                oAuthToken = null;
                try {
                    File propertyFile = GitHubIssuesUtils.getDotGithub();
                    if (!propertyFile.exists()) {
                        UiUtils.showErrorDialog(Bundle.GitHubRepositoryPanel_message_github_file_exist_error());
                        return;
                    }
                    Properties properties = GitHubIssuesUtils.getProperties(propertyFile);
                    userName = properties.getProperty(GitHubIssuesUtils.PROPERTY_USER_NAME, null); // NOI18N
                    oAuthToken = properties.getProperty(GitHubIssuesUtils.PROPERTY_OAUTH_TOKEN, null); // NOI18N
                } catch (IOException ex) {
                    UiUtils.showErrorDialog(Bundle.GitHubRepositoryPanel_message_github_file_read_error());
                }
                if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(oAuthToken)) {
                    UiUtils.showErrorDialog(Bundle.GitHubRepositoryPanel_message_userName_oauthToken_error());
                    return;
                }
            }

            // client
            GitHubClient gitHubClient = new GitHubClient().setOAuth2Token(oAuthToken);

            // user
            UserService userService = new UserService(gitHubClient);
            try {
                User user = userService.getUser();
                String login = user.getLogin();
                if (!login.equals(userName)) {
                    isUserNameValid = false;
                    return;
                }
            } catch (IOException ex) {
                UiUtils.showErrorDialog(Bundle.GitHubRepositoryPanel_message_userName_oauthToken_error());
            }

            // repository
            if (gitHubClient != null) {
                RepositoryService repositoryService = new RepositoryService(gitHubClient);
                try {
                    Repository repository = repositoryService.getRepository(getRepositoryAuthor(), getRepositoryName());
                    isRepositoryValid = true;
                    isConnectionSuccessful = true;
                    if (!repository.isHasIssues()) {
                        hasIssues = false;
                        return;
                    }
                } catch (IOException ex) {
                    isRepositoryValid = false;
                }
            }
        } finally {
            fireChange();
        }

        if (isConnectionSuccessful() && isRepositoryValid() && hasIssues() && isUserNameValid()) {
            UiUtils.showPlainDialog(Bundle.GitHubRepositoryPanel_message_connection_successful());
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

        connectButton = new javax.swing.JButton();
        userNameLabel = new javax.swing.JLabel();
        oauthTokenLabel = new javax.swing.JLabel();
        userNameTextField = new javax.swing.JTextField();
        oauthTokenTextField = new javax.swing.JTextField();
        displayNameLabel = new javax.swing.JLabel();
        displayNameTextField = new javax.swing.JTextField();
        propertyFileCheckBox = new javax.swing.JCheckBox();
        repositoryLabel = new javax.swing.JLabel();
        repositoryAuthorTextField = new javax.swing.JTextField();
        repositorySeparationLabel = new javax.swing.JLabel();
        repositoryNameTextField = new javax.swing.JTextField();
        addRepositoryButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(connectButton, org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.connectButton.text")); // NOI18N
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(userNameLabel, org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.userNameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(oauthTokenLabel, org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.oauthTokenLabel.text")); // NOI18N

        userNameTextField.setText(org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.userNameTextField.text")); // NOI18N

        oauthTokenTextField.setText(org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.oauthTokenTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(displayNameLabel, org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.displayNameLabel.text")); // NOI18N

        displayNameTextField.setText(org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.displayNameTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(propertyFileCheckBox, org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.propertyFileCheckBox.text")); // NOI18N
        propertyFileCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertyFileCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(repositoryLabel, org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.repositoryLabel.text")); // NOI18N

        repositoryAuthorTextField.setText(org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.repositoryAuthorTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(repositorySeparationLabel, org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.repositorySeparationLabel.text")); // NOI18N

        repositoryNameTextField.setText(org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.repositoryNameTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addRepositoryButton, org.openide.util.NbBundle.getMessage(GitHubRepositoryPanel.class, "GitHubRepositoryPanel.addRepositoryButton.text")); // NOI18N
        addRepositoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRepositoryButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(oauthTokenLabel)
                    .addComponent(displayNameLabel)
                    .addComponent(repositoryLabel)
                    .addComponent(userNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(repositoryAuthorTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(repositorySeparationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(repositoryNameTextField))
                    .addComponent(userNameTextField)
                    .addComponent(displayNameTextField)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(oauthTokenTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addRepositoryButton))))
            .addGroup(layout.createSequentialGroup()
                .addComponent(propertyFileCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addComponent(connectButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oauthTokenLabel)
                    .addComponent(oauthTokenTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addRepositoryButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userNameLabel)
                    .addComponent(userNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(displayNameLabel)
                    .addComponent(displayNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(repositoryLabel)
                    .addComponent(repositoryAuthorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(repositorySeparationLabel)
                    .addComponent(repositoryNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(propertyFileCheckBox)
                    .addComponent(connectButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
        rp.post(new Runnable() {

            @Override
            public void run() {
                connect();
            }
        });

    }//GEN-LAST:event_connectButtonActionPerformed

    private void propertyFileCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertyFileCheckBoxActionPerformed
        isConnectionSuccessful = false;
        setUserAndOAuthEnabled(!propertyFileCheckBox.isSelected());
        setConnectButtonEnabled(propertyFileCheckBox.isSelected());
        fireChange();
    }//GEN-LAST:event_propertyFileCheckBoxActionPerformed

    @NbBundle.Messages({
        "GitHubRepositoryPanel.addRepositoryButtonAction.error.empty.token=Please set OAuth token.",
        "GitHubRepositoryPanel.addRepositoryButtonAction.error.wrong.token=There is no repository or your OAuth token is wrong.",})
    private void addRepositoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRepositoryButtonActionPerformed
        RequestProcessor rp = GitHubIssues.getInstance().getRequestProcessor();
        rp.post(new Runnable() {

            @Override
            public void run() {
                String oAuthToken = getOAuthToken();
                if (StringUtils.isEmpty(oAuthToken)) {
                    UiUtils.showErrorDialog(Bundle.GitHubRepositoryPanel_addRepositoryButtonAction_error_empty_token());
                    return;
                }
                setAddRepositoryButtonEnabled(false);
                List<Repository> repositories = getRepositories(oAuthToken);
                if (repositories.isEmpty()) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            UiUtils.showErrorDialog(Bundle.GitHubRepositoryPanel_addRepositoryButtonAction_error_wrong_token());
                            setAddRepositoryButtonEnabled(true);
                        }
                    });
                    return;
                }

                final Repository repository = GitHubRepositoryListPanel.showDialog(repositories);

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (repository != null) {
                            String author = repository.getOwner().getLogin();
                            String repositoryName = repository.getName();
                            setUserName(author);
                            setRepositoryAuthor(author);
                            setRepositoryName(repositoryName);
                            if (StringUtils.isEmpty(getDisplayName())) {
                                setDisplayName(String.format("%s/%s", author, repositoryName)); // NOI18N
                            }
                        }
                        setAddRepositoryButtonEnabled(true);
                    }
                });
            }
        });

    }//GEN-LAST:event_addRepositoryButtonActionPerformed

    private List<Repository> getRepositories(String oAuthToken) {
        boolean showParentRepository = GitHubIssuesOptions.getInstance().showParentRepository();
        if (repositoryCache == null) {
            repositoryCache = GitHubRepository.getRepositories(oAuthToken, showParentRepository);
        }
        return repositoryCache;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRepositoryButton;
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel displayNameLabel;
    private javax.swing.JTextField displayNameTextField;
    private javax.swing.JLabel oauthTokenLabel;
    private javax.swing.JTextField oauthTokenTextField;
    private javax.swing.JCheckBox propertyFileCheckBox;
    private javax.swing.JTextField repositoryAuthorTextField;
    private javax.swing.JLabel repositoryLabel;
    private javax.swing.JTextField repositoryNameTextField;
    private javax.swing.JLabel repositorySeparationLabel;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextField userNameTextField;
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

        protected void processUpdate() {
            isConnectionSuccessful = false;
            fireChange();
        }
    }

    private class DisplayNameDocumentListener extends DefaultDocumentListener {

        @Override
        protected void processUpdate() {
            fireChange();
        }
    }
}
