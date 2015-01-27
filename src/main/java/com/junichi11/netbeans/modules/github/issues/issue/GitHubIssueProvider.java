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

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import org.netbeans.modules.bugtracking.spi.IssueController;
import org.netbeans.modules.bugtracking.spi.IssueProvider;

/**
 *
 * @author junichi11
 */
public class GitHubIssueProvider implements IssueProvider<GitHubIssue> {

    @Override
    public String getDisplayName(GitHubIssue issue) {
        return issue.getDisplayName();
    }

    @Override
    public String getTooltip(GitHubIssue issue) {
        return issue.getTooltip();
    }

    @Override
    public String getID(GitHubIssue issue) {
        return issue.getID();
    }

    @Override
    public Collection<String> getSubtasks(GitHubIssue issue) {
        return issue.getSubtasks();
    }

    @Override
    public String getSummary(GitHubIssue issue) {
        return issue.getSummary();
    }

    @Override
    public boolean isNew(GitHubIssue issue) {
        return issue.isNew();
    }

    @Override
    public boolean isFinished(GitHubIssue issue) {
        return issue.isFinished();
    }

    @Override
    public boolean refresh(GitHubIssue issue) {
        return issue.refresh();
    }

    @Override
    public void addComment(GitHubIssue issue, String comment, boolean resolveAsFixed) {
        issue.addComment(comment, resolveAsFixed);
    }

    @Override
    public void attachFile(GitHubIssue issue, File file, String string, boolean bln) {
        issue.attachFile(file, string, bln);
    }

    @Override
    public IssueController getController(GitHubIssue issue) {
        return issue.getController();
    }

    @Override
    public void addPropertyChangeListener(GitHubIssue issue, PropertyChangeListener listener) {
        issue.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(GitHubIssue issue, PropertyChangeListener listener) {
        issue.removePropertyChangeListener(listener);
    }

}
