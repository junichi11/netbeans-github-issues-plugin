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
package com.junichi11.netbeans.modules.github.issues.repository;

import com.junichi11.netbeans.modules.github.issues.GitHubIssuesConnector;
import com.junichi11.netbeans.modules.github.issues.repository.ui.GitHubRepositoryPanel;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.api.RepositoryManager;
import org.netbeans.modules.bugtracking.spi.RepositoryController;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class GitHubRepositoryController implements RepositoryController {

    private final GitHubRepository repository;
    private GitHubRepositoryPanel panel;
    private String errorMessage;

    public GitHubRepositoryController(GitHubRepository repository) {
        this.repository = repository;
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
    public boolean isValid() {
        validate();
        return errorMessage == null;
    }

    @Override
    public void populate() {
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void applyChanges() {
        GitHubRepositoryPanel p = getPanel();
        GitHubRepositoryInfo gitHubRepositoryInfo = new GitHubRepositoryInfo()
                .setHostname(p.getHostname())
                .setDisplayName(p.getDisplayName())
                .setUserName(p.getUserName())
                .setOAuthToken(p.getOAuthToken())
                .setPropertyFile(p.isPropertyFile())
                .setRepositoryAuthor(p.getRepositoryAuthor())
                .setRepositoryName(p.getRepositoryName());
        repository.setRepositoryInfo(gitHubRepositoryInfo);
        GitHubRepositoryManager.getInstance().add(repository);
    }

    @Override
    public void cancelChanges() {
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        getPanel().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        getPanel().removeChangeListener(listener);
    }

    private GitHubRepositoryPanel getPanel() {
        if (panel == null) {
            RepositoryInfo repositoryInfo = repository.getRepositoryInfo();
            if (repositoryInfo != null) {
                panel = new GitHubRepositoryPanel(repository);
            } else {
                panel = new GitHubRepositoryPanel();
            }
        }
        return panel;
    }

    @NbBundle.Messages({
        "# {0} - name",
        "GithubRepositoryController.message.empty.error={0} must be set.",
        "GithubRepositoryController.message.displayName.alredy.exists.error=The name already exists.",
        "GithubRepositoryController.message.connection.error=Please try to click Connect button or confirm your user name and OAuth token.",
        "GithubRepositoryController.message.repository.error=Repository is invalid.",
        "GithubRepositoryController.message.user.name.error=User name is invalid.",
        "GithubRepositoryController.message.dotgithub.error=Please try to check .github (login and oauth).",
        "GithubRepositoryController.message.has.issues.error=Repository doesn't have Issues.",
        "GithubRepositoryController.label.displayName=Name",
        "GithubRepositoryController.label.userName=User Name",
        "GithubRepositoryController.label.oauthToken=OAuth Token",
        "GithubRepositoryController.label.repositoryAuthor=Repository Author",
        "GithubRepositoryController.label.repositoryName=Repository Name",})
    private void validate() {
        GitHubRepositoryPanel repositoryPanel = getPanel();
        if (!repositoryPanel.isPropertyFile()) {
            // oauth token
            if (StringUtils.isEmpty(repositoryPanel.getOAuthToken())) {
                errorMessage = Bundle.GithubRepositoryController_message_empty_error(Bundle.GithubRepositoryController_label_oauthToken());
                return;
            }

            // user name
            if (StringUtils.isEmpty(repositoryPanel.getUserName())) {
                errorMessage = Bundle.GithubRepositoryController_message_empty_error(Bundle.GithubRepositoryController_label_userName());
                return;
            }
        }

        // display name
        String displayName = repositoryPanel.getDisplayName();
        if (StringUtils.isEmpty(displayName)) {
            errorMessage = Bundle.GithubRepositoryController_message_empty_error(Bundle.GithubRepositoryController_label_displayName());
            return;
        }
        // already exists?
        RepositoryManager manager = RepositoryManager.getInstance();
        Collection<Repository> repositories = manager.getRepositories(GitHubIssuesConnector.ID);
        String id = repository.getID();
        for (Repository r : repositories) {
            if (id != null && id.equals(r.getId())) {
                continue;
            }
            String repositoryName = r.getDisplayName();
            if (repositoryName.equals(displayName)) {
                errorMessage = Bundle.GithubRepositoryController_message_displayName_alredy_exists_error();
                return;
            }
        }

        // repository
        if (StringUtils.isEmpty(repositoryPanel.getRepositoryAuthor())) {
            errorMessage = Bundle.GithubRepositoryController_message_empty_error(Bundle.GithubRepositoryController_label_repositoryAuthor());
            return;
        }
        if (StringUtils.isEmpty(repositoryPanel.getRepositoryName())) {
            errorMessage = Bundle.GithubRepositoryController_message_empty_error(Bundle.GithubRepositoryController_label_repositoryName());
            return;
        }

        // connecton
        if (!repositoryPanel.isConnectionSuccessful()) {
            errorMessage = Bundle.GithubRepositoryController_message_connection_error();
            return;
        }
        if (!repositoryPanel.isRepositoryValid()) {
            errorMessage = Bundle.GithubRepositoryController_message_repository_error();
            return;
        }

        // has issues?
        if (!repositoryPanel.hasIssues()) {
            errorMessage = Bundle.GithubRepositoryController_message_has_issues_error();
            return;
        }

        if (!repositoryPanel.isUserNameValid()) {
            errorMessage = Bundle.GithubRepositoryController_message_user_name_error();
            return;
        }

        // everything ok
        errorMessage = null;
    }
}
