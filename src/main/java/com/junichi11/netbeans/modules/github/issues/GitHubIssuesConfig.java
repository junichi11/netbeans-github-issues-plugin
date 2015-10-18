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

import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssue;
import com.junichi11.netbeans.modules.github.issues.query.GitHubQuery;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider.Status;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public final class GitHubIssuesConfig {

    private static final GitHubIssuesConfig INSTANCE = new GitHubIssuesConfig();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd"); // NOI18N

    // query
    private static final String QUERY = "query"; // NOI18N
    private static final String QUERY_PARAMS = "query.params"; // NOI18N

    // schedule
    private static final String SCHEDULE = "schedule"; // NOI18N
    private static final String SCHEDULE_DUE_DATE = "schedule.due"; // NOI18N
    private static final String SCHEDULE_INTERVAL = "schedule.interval"; // NOI18N

    // template
    private static final String TEMPLATE = "template"; // NOI18N
    public static final String DEFAULT_TEMPLATE_NAME = "default"; // NOI18N

    // status
    private static final String STATUS = "status"; // NOI18N
    private static final String STATUS_FORMAT = "%s::%s"; // NOI18N
    private static final String STATUS_DELIMITER = "::"; // NOI18N

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
        Preferences preferences = getPreferences(repository).node(QUERY);
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
        Preferences preferences = getPreferences(repository).node(QUERY).node(queryName);
        return preferences.get(QUERY_PARAMS, null);
    }

    /**
     * Save parameters for specified query.
     *
     * @param repository repository
     * @param query query
     */
    public void setQueryParams(GitHubRepository repository, GitHubQuery query) {
        Preferences preferences = getPreferences(repository).node(QUERY).node(query.getDisplayName());
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
        Preferences preferences = getPreferences(repository).node(QUERY).node(displayName);
        try {
            preferences.removeNode();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void setScheduleDueDate(GitHubRepository repository, GitHubIssue issue, Date dueDate) {
        Preferences preferences = getPreferences(repository);
        String id = issue.getID();
        if (StringUtils.isEmpty(id)) {
            return;
        }
        preferences.node(SCHEDULE).node(id).put(SCHEDULE_DUE_DATE, DATE_FORMAT.format(dueDate));
    }

    public Date getScheduleDueDate(GitHubRepository repository, GitHubIssue issue) {
        Preferences preferences = getPreferences(repository);
        String id = issue.getID();
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        String dateString = preferences.node(SCHEDULE).node(id).get(SCHEDULE_DUE_DATE, null);
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        try {
            return DateFormat.getDateInstance().parse(dateString);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public void setScheduleInterval(GitHubRepository repository, GitHubIssue issue, int interval) {
        Preferences preferences = getPreferences(repository);
        String id = issue.getID();
        if (StringUtils.isEmpty(id)) {
            return;
        }
        preferences.node(SCHEDULE).node(id).putInt(SCHEDULE_INTERVAL, interval);
    }

    public int getScheduleInterval(GitHubRepository repository, GitHubIssue issue) {
        Preferences preferences = getPreferences(repository);
        String id = issue.getID();
        if (StringUtils.isEmpty(id)) {
            return -1;
        }
        return preferences.node(SCHEDULE).node(id).getInt(SCHEDULE_INTERVAL, -1);
    }

    public void removeSchedule(GitHubRepository repository, GitHubIssue issue) {
        Preferences preferences = getPreferences(repository);
        String id = issue.getID();
        if (StringUtils.isEmpty(id)) {
            return;
        }
        preferences = preferences.node(SCHEDULE).node(id);
        try {
            preferences.removeNode();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Get the template for specified name.
     *
     * @param name the template name
     * @return the template
     */
    @NbBundle.Messages("GitHubIssuesConfig.default.template=#### Overview description\n"
            + "\n"
            + "#### Steps to reproduce\n"
            + "\n"
            + "1. \n"
            + "2. \n"
            + "3. \n"
            + "\n"
            + "#### Actual results\n"
            + "\n"
            + "#### Expected results\n")
    public String getTemplate(String name) {
        return getPreferences().node(TEMPLATE).get(name, Bundle.GitHubIssuesConfig_default_template());
    }

    /**
     * Set template.
     *
     * @param name the template name
     * @param template the template
     */
    public void setTemplate(String name, String template) {
        getPreferences().node(TEMPLATE).put(name, template);
    }

    /**
     * Remove a template. <b>NOTE:</b> Can't remove the default template. But
     * default template will be initialized.
     *
     * @param name the template name
     */
    public void removeTemplate(String name) {
        getPreferences().node(TEMPLATE).remove(name);
    }

    /**
     * Get all template names.
     *
     * @return all template names
     */
    public String[] getTemplateNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add(DEFAULT_TEMPLATE_NAME);
        Preferences preferences = getPreferences().node(TEMPLATE);
        try {
            // contains the default template if it was edited
            String[] childrenNames = preferences.keys();
            int count = 1; // default template
            for (String childName : childrenNames) {
                if (!childName.equals(DEFAULT_TEMPLATE_NAME)) {
                    names.add(childName);
                    count++;
                }
            }
            return names.toArray(new String[count]);
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return names.toArray(new String[1]);
    }

    public Status getStatus(GitHubIssue issue) {
        GitHubRepository repository = issue.getRepository();
        Preferences preferences = getPreferences().node(repository.getID()).node(STATUS);
        String statusTime = preferences.get(issue.getID(), null);
        if (statusTime == null) {
            return IssueStatusProvider.Status.INCOMING_NEW;
        }

        String[] split = statusTime.split(STATUS_DELIMITER);
        if (split.length != 2) {
            return IssueStatusProvider.Status.INCOMING_NEW;
        }

        // TODO CONFLICT, OUTGOING_NEW, OUTGOING_MODIFIED
        IssueStatusProvider.Status status = IssueStatusProvider.Status.valueOf(split[0]);
        long lastUpdated = Long.parseLong(split[1]);
        if (status == IssueStatusProvider.Status.SEEN) {
            long lastUpdatedTime = issue.getLastUpdatedTime();
            if (lastUpdatedTime != -1L) {
                if (lastUpdated < lastUpdatedTime) {
                    setStatus(issue, IssueStatusProvider.Status.INCOMING_MODIFIED);
                    return IssueStatusProvider.Status.INCOMING_MODIFIED;
                }
            }
        }
        return status;
    }

    public void setStatus(GitHubIssue issue, IssueStatusProvider.Status status) {
        long lastUpdatedTime = issue.getLastUpdatedTime();
        if (lastUpdatedTime != -1L) {
            GitHubRepository repository = issue.getRepository();
            Preferences preferences = getPreferences().node(repository.getID()).node(STATUS);
            preferences.put(issue.getID(), String.format(STATUS_FORMAT, status.name(), lastUpdatedTime));
        }
    }

    public void removeRepository(GitHubRepository repository) {
        Preferences preferences = getPreferences(repository);
        try {
            preferences.removeNode();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(GitHubIssuesConfig.class);
    }

    private Preferences getPreferences(GitHubRepository repository) {
        String id = repository.getID();
        return getPreferences().node(id);
    }
}
