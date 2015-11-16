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

import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Is;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.No;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Order;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Sort;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.State;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Type;
import com.junichi11.netbeans.modules.github.issues.issue.GitHubIssue;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.netbeans.modules.bugtracking.issuetable.ColumnDescriptor;
import org.netbeans.modules.bugtracking.spi.QueryProvider;

/**
 *
 * @author junichi11
 */
public class GitHubQuery {

    public enum QParam {

        KEYWORD("keyword", "keyword"), // NOI18N
        MILESTONE("milestone", "milestone"), // NOI18N
        TYPE("type", "type"), // NOI18N
        IN("in", "in"), // NOI18N
        AUTHOR("author", "author"), // NOI18N
        ASSIGNEE("assignee", "assignee"), // NOI18N
        MENTIONS("mentions", "mentions"), // NOI18N
        COMMENTER("commenter", "commenter"), // NOI18N
        INVOLVES("involves", "involves"), // NOI18N
        STATE("state", "state"), // NOI18N
        LABELS("labels", "labels"), // NOI18N
        NO("no", "no"), // NOI18N
        LANGUAGE("language", "language"), // NOI18N
        IS("is", "is"), // NOI18N
        IS_OPEN("isOpen", "is"), // NOI18N
        IS_MERGED("isMerged", "is"), // NOI18N
        IS_ISSUE("isIssue", "is"), // NOI18N
        CREATED("created", "created"), // NOI18N
        UPDATED("updated", "updated"), // NOI18N
        MERGED("merged", "merged"), // NOI18N
        CLOSED("closed", "closed"), // NOI18N
        COMMENTS("comments", "comments"); // NOI18N
        private final String key;
        private final String value;

        private QParam(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }

    public enum Param {

        SORT("sort"), // NOI18N
        ORDER("order"); // NOI18N
        private final String value;

        private Param(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private String name;
    private String queryParam;
    private GitHubRepository repository;
    private GitHubQueryController controller;
    private ColumnDescriptor[] columnDescriptors;
    private boolean isSaved;
    private QueryProvider.IssueContainer<GitHubIssue> issueContainer;

    private String keyword;
    private final Map<String, String> qParamsMap = new HashMap<>();
    private final Map<String, String> paramsMap = new HashMap<>();
    private State state;

    public GitHubQuery(GitHubRepository repository) {
        this(repository, null, null);
    }

    public GitHubQuery(GitHubRepository repository, String name, String queryParam) {
        this.name = name;
        this.repository = repository;
        this.queryParam = queryParam;
        if (queryParam != null) {
            isSaved = true;
        }
        parseParam();
    }

    public GitHubRepository getRepository() {
        return this.repository;
    }

    public void setDisplayName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return name;
    }

    public String getTooltip() {
        return getDisplayName();
    }

    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(SearchIssuesParams params) {
        if (params == null) {
            queryParam = null;
            return;
        }
        this.queryParam = params.getParameters(false);
        parseParam();
    }

    public Map<String, String> getParamsMap() {
        return new HashMap<>(paramsMap);
    }

    public Map<String, String> getQParamsMap() {
        return new HashMap<>(qParamsMap);
    }

    public void save() {
        repository.saveQuery(this);
    }

    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public GitHubQueryController getController() {
        if (controller == null) {
            controller = createController();
        }
        return controller;
    }

    private GitHubQueryController createController() {
        return new GitHubQueryController(this);
    }

    public boolean canRemove() {
        return true;
    }

    public void remove() {
        repository.removeQuery(this);
    }

    public boolean canRename() {
        return true;
    }

    public void rename(String string) {
    }

    public void setIssueContainer(QueryProvider.IssueContainer<GitHubIssue> issueContainer) {
        this.issueContainer = issueContainer;
    }

    /**
     * Refresh issues.
     */
    public void refresh() {
        try {
            if (issueContainer != null) {
                issueContainer.refreshingStarted();
                issueContainer.clear();
                for (GitHubIssue issue : getIssues(true)) {
                    issueContainer.add(issue);
                }
            }
        } finally {
            fireFinished();
        }
    }

    void fireFinished() {
        if (issueContainer != null) {
            issueContainer.refreshingFinished();
        }
    }

    public List<GitHubIssue> getIssues(boolean isRefresh) {
        Map<String, String> filter = getFilter();
        if (filter.isEmpty()) {
            if (StringUtils.isEmpty(queryParam)) {
                return Collections.emptyList();
            }
            return searchIssues(createSearchIssuesParams(), isRefresh);
        }
        return repository.getIssues(filter, isRefresh);
    }

    protected Map<String, String> getFilter() {
        return Collections.emptyMap();
    }

    public List<GitHubIssue> searchIssues(SearchIssuesParams params, boolean isRefresh) {
        return repository.searchIssues(params, isRefresh);
    }

    public String getKeyword() {
        return keyword;
    }

    public State getState() {
        return state;
    }

    public String getParameter(QParam param) {
        if (param == null) {
            return null;
        }
        String value = qParamsMap.get(param.getKey());
        return value == null ? "" : value; // NOI18N
    }

    public String getParameter(Param param) {
        if (param == null) {
            return null;
        }
        String value = paramsMap.get(param.getValue());
        return value == null ? "" : value; // NOI18N
    }

    private SearchIssuesParams createSearchIssuesParams() {
        return new SearchIssuesParams()
                .keyword(getParameter(QParam.KEYWORD))
                .milestone(getParameter(QParam.MILESTONE))
                .assignee(getParameter(QParam.ASSIGNEE))
                .author(getParameter(QParam.AUTHOR))
                .commenter(getParameter(QParam.COMMENTER))
                .mentions(getParameter(QParam.MENTIONS))
                .created(getParameter(QParam.CREATED))
                .updated(getParameter(QParam.UPDATED))
                .merged(getParameter(QParam.MERGED))
                .closed(getParameter(QParam.CLOSED))
                .involves(getParameter(QParam.INVOLVES))
                .language(getParameter(QParam.LANGUAGE))
                .state(State.valueOfString(getParameter(QParam.STATE)))
                .type(Type.valueOfString(getParameter(QParam.TYPE)))
                .in(getParameter(QParam.IN))
                .no(No.valueOfString(getParameter(QParam.NO)))
                .is(Is.valueOfString(getParameter(QParam.IS_OPEN)))
                .is(Is.valueOfString(getParameter(QParam.IS_MERGED)))
                .is(Is.valueOfString(getParameter(QParam.IS_ISSUE)))
                .comments(getParameter(QParam.COMMENTS))
                .labels(getParameter(QParam.LABELS))
                .sort(Sort.valueOfString(getParameter(Param.SORT)))
                .order(Order.valueOfString(getParameter(Param.ORDER)));
    }

    private void clearParams() {
        qParamsMap.clear();
        paramsMap.clear();
    }

    private void parseParam() {
        clearParams();
        if (StringUtils.isEmpty(queryParam)) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(queryParam, "&"); // NOI18N
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String[] split = token.split("="); // NOI18N
            if (split.length != 2) {
                continue;
            }
            String key = split[0];
            String value = split[1];
            paramsMap.put(key, value);
            switch (key) {
                case "q": // NOI18N
                    parseQ(value);
                    break;
                case "sort": // NOI18N
                    break;
                case "order": // NOI18N
                    break;
                default:
                    break;
            }
        }
    }

    private void parseQ(String q) {
        if (StringUtils.isEmpty(q)) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(q, "+"); // NOI18N
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String[] split = token.split(":"); // NOI18N
            if (split.length == 1) {
                qParamsMap.put(QParam.KEYWORD.getKey(), token);
                continue;
            }
            if (split.length != 2) {
                continue;
            }
            String key = split[0];
            String value = split[1];
            if (key.equals("is")) { // NOI18N
                switch (value) {
                    case "merged": // NOI18N
                    case "unmerged": // NOI18N
                        qParamsMap.put(QParam.IS_MERGED.getKey(), value);
                        break;
                    case "issue": // NOI18N
                    case "pr": // NOI18N
                        qParamsMap.put(QParam.IS_ISSUE.getKey(), value);
                        break;
                    case "open": // NOI18N
                    case "closed": // NOI18N
                        qParamsMap.put(QParam.IS_OPEN.getKey(), value);
                        break;
                    default:
                        break;
                }
            } else if (key.equals("label")) { // NOI18N
                String labels = qParamsMap.get(QParam.LABELS.getKey());
                if (!StringUtils.isEmpty(labels)) {
                    labels = String.format("%s,%s", labels, value); // NOI18N
                } else {
                    labels = value;
                }
                qParamsMap.put(QParam.LABELS.getKey(), labels);
            } else {
                qParamsMap.put(key, value);
            }
        }
    }

    /**
     * Get ColumnDescriptors.
     *
     * @return ColumnDescriptors
     */
    public ColumnDescriptor[] getColumnDescriptors() {
        if (columnDescriptors == null) {
            columnDescriptors = GitHubIssue.getColumnDescriptors();
        }
        return columnDescriptors;
    }

}
