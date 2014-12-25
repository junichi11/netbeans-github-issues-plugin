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
package com.junichi11.netbeans.modules.github.issues.query;

import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.NonNull;

/**
 *
 * @author junichi11
 */
public final class GitHubDefaultQueries {

    public enum Type {

        // must not change option key name
        ASSIGNED_TO_ME("query.assigned.to.me"), // NOI18N
        CREATED_BY_ME("query.created.by.me"); // NOI18N
        private final String optionKey;

        private Type(String optionKey) {
            this.optionKey = optionKey;
        }

        public String getOptionKey() {
            return optionKey;
        }

    }

    private static final Map<GitHubRepository, GitHubDefaultQueries> DEFAULT_QUERIES = Collections.synchronizedMap(new HashMap<GitHubRepository, GitHubDefaultQueries>());
    private static final Logger LOGGER = Logger.getLogger(GitHubDefaultQueries.class.getName());
    private final Map<Type, GitHubQuery> defaultQueries = new HashMap<>();

    private GitHubDefaultQueries() {
    }

    private static GitHubDefaultQueries create(GitHubRepository repository) {
        GitHubDefaultQueries gitHubDefaultQueries = DEFAULT_QUERIES.get(repository);
        if (gitHubDefaultQueries == null) {
            gitHubDefaultQueries = new GitHubDefaultQueries();
            DEFAULT_QUERIES.put(repository, gitHubDefaultQueries);
        }
        return gitHubDefaultQueries;
    }

    public static GitHubQuery create(GitHubRepository repository, Type type) {
        GitHubDefaultQueries gitHubDefaultQueries = create(repository);
        return gitHubDefaultQueries.getQuery(repository, type);
    }

    public static boolean isDefaultQuery(GitHubQuery query) {
        return query instanceof GitHubDefaultQuery;
    }

    public static void remove(GitHubRepository repository) {
        GitHubDefaultQueries gitHubDefaultQueries = create(repository);
        if (gitHubDefaultQueries == null) {
            return;
        }
        gitHubDefaultQueries.clear();
        DEFAULT_QUERIES.remove(repository);
    }

    private GitHubQuery getQuery(@NonNull GitHubRepository repository, @NonNull Type type) {
        GitHubQuery query = defaultQueries.get(type);
        if (query == null) {
            switch (type) {
                case ASSIGNED_TO_ME:
                    query = new GitHubAssignedToMeQuery(repository);
                    break;
                case CREATED_BY_ME:
                    query = new GitHubCreatedByMeQuery(repository);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "The query type({0}) is not supported!", type.name()); // NOI18N
                    return new GitHubDefaultQuery(repository) {
                    };
            }
            defaultQueries.put(type, query);
        }
        return query;
    }

    private void clear() {
        defaultQueries.clear();
    }

}
