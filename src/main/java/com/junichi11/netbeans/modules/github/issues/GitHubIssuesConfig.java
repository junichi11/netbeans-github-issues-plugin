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
package com.junichi11.netbeans.modules.github.issues;

import com.junichi11.netbeans.modules.github.issues.query.GitHubQuery;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public final class GitHubIssuesConfig {

    private static final GitHubIssuesConfig INSTANCE = new GitHubIssuesConfig();
    private static final String QUERY = "query"; // NOI18N
    private static final String QUERY_PARAMS = "query.params"; // NOI18N

    private GitHubIssuesConfig() {
    }

    public static GitHubIssuesConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Return saved query names.
     *
     * @param repository repository
     * @return saved query names
     */
    public String[] getQueryNames(GitHubRepository repository) {
        Preferences preferences = getPreferences().node(repository.getID()).node(QUERY);
        try {
            return preferences.childrenNames();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return new String[0];
    }

    /**
     * Return parameters for specified name.
     *
     * @param repository repository
     * @param queryName query name
     * @return query parameters if name exists, otherwise {@code null}
     */
    public String getQueryParams(GitHubRepository repository, String queryName) {
        Preferences preferences = getPreferences().node(repository.getID()).node(QUERY).node(queryName);
        return preferences.get(QUERY_PARAMS, null);
    }

    /**
     * Save parameters for specified query.
     *
     * @param repository repository
     * @param query query
     */
    public void setQueryParams(GitHubRepository repository, GitHubQuery query) {
        String id = repository.getID();
        Preferences preferences = getPreferences().node(id).node(QUERY).node(query.getDisplayName());
        preferences.put(QUERY_PARAMS, query.getQueryParam());
    }

    /**
     * Remove configurations for specified query.
     *
     * @param repository repository
     * @param query query
     */
    public void removeQuery(GitHubRepository repository, GitHubQuery query) {
        String displayName = query.getDisplayName();
        if (StringUtils.isEmpty(displayName)) {
            return;
        }
        Preferences preferences = getPreferences().node(repository.getID()).node(QUERY).node(displayName);
        try {
            preferences.removeNode();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(GitHubIssuesConfig.class);
    }
}
