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
package com.junichi11.netbeans.modules.github.issues.options;

import com.junichi11.netbeans.modules.github.issues.query.GitHubDefaultQueries;
import com.junichi11.netbeans.modules.github.issues.query.GitHubDefaultQueries.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public final class GitHubIssuesOptions {

    public static final String SUB_PATH = "Team/GitHubIssues"; // NOI18N
    private static final String PREFERENCES_PATH = "github.issues"; // NOI18N
    private static final String QUERY_OPEN = "query.open"; // NOI18N
    private static final GitHubIssuesOptions INSTANCE = new GitHubIssuesOptions();

    private GitHubIssuesOptions() {
    }

    public static GitHubIssuesOptions getInstance() {
        return INSTANCE;
    }

    public void setOpenQuery(boolean isEnabled) {
        getPreferences().putBoolean(QUERY_OPEN, isEnabled);
    }

    public boolean isOpenQuery() {
        return getPreferences().getBoolean(QUERY_OPEN, true);
    }

    public void setAssignedToMeQuery(boolean isEnabled) {
        getPreferences().putBoolean(Type.ASSIGNED_TO_ME.getOptionKey(), isEnabled);
    }

    public boolean isAssignedToMeQuery() {
        return isDefaultQuery(Type.ASSIGNED_TO_ME);
    }

    public void setCreatedByMeQuery(boolean isEnabled) {
        getPreferences().putBoolean(Type.CREATED_BY_ME.getOptionKey(), isEnabled);
    }

    public boolean isCreatedByMeQuery() {
        return isDefaultQuery(Type.CREATED_BY_ME);
    }

    private boolean isDefaultQuery(Type type) {
        boolean defaultValue = false;
        return getPreferences().getBoolean(type.getOptionKey(), defaultValue);
    }

    public Map<GitHubDefaultQueries.Type, Boolean> getDefaultQueryOptions() {
        Map<GitHubDefaultQueries.Type, Boolean> map = new HashMap<>();
        for (GitHubDefaultQueries.Type type : GitHubDefaultQueries.Type.values()) {
            map.put(type, isDefaultQuery(type));
        }
        return map;
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(GitHubIssuesOptions.class).node(PREFERENCES_PATH);
    }

}
