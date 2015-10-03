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
package com.junichi11.netbeans.modules.github.issues.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.eclipse.egit.github.core.Issue;
import org.netbeans.api.annotations.common.CheckForNull;

/**
 *
 * @author junichi11
 */
public final class GitHubIssuesUtils {

    public static final String PROPERTY_USER_NAME = "login"; // NOI18N
    public static final String PROPERTY_OAUTH_TOKEN = "oauth"; // NOI18N
    private static final String DOT_GITHUB_NAME = ".github"; // NOI18N

    private GitHubIssuesUtils() {
    }

    public static boolean isPullRequest(Issue issue) {
        return issue != null && issue.getPullRequest() != null;
    }

    public static File getDotGithub() {
        File homeDir = new File(System.getProperty("user.home")); // NOI18N
        return new File(homeDir, DOT_GITHUB_NAME);
    }

    public static Properties getProperties() throws IOException {
        return getProperties(null);
    }

    public static Properties getProperties(File propertyFile) throws IOException {
        if (propertyFile == null) {
            propertyFile = getDotGithub();
        }
        Properties properties = new Properties();
        if (!propertyFile.exists()) {
            return properties;
        }
        try (FileInputStream in = new FileInputStream(propertyFile)) {
            properties.load(in);
        }
        return properties;
    }

    /**
     * Get a user name from a .github file. The .github file must be put the
     * user home directory.
     *
     * @return user name if .github file exists and name is set, otherwise
     * {@code null}
     * @throws IOException
     */
    @CheckForNull
    public static String getUserName() throws IOException {
        Properties properties = getProperties();
        return properties.getProperty(GitHubIssuesUtils.PROPERTY_USER_NAME, null);
    }

    /**
     * Get an OAuth token from a .github file. The .github file must be put the
     * user home directory.
     *
     * @return OAuth token if .github file exists and the token is set,
     * otherwise {@code null}
     * @throws IOException
     */
    @CheckForNull
    public static String getOAuthToken() throws IOException {
        Properties properties = getProperties();
        return properties.getProperty(GitHubIssuesUtils.PROPERTY_OAUTH_TOKEN, null);
    }

}
