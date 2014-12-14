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

import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.service.IssueService;

/**
 *
 * @author junichi11
 */
public class GetIssuesParams {

    public enum State {

        OPEN("open"), // NOI18N
        CLOSED("closed"), // NOI18N
        ALL("all"); // NOI18N
        private final String name;

        private State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Sort {

        CREATED("created"), // NOI18N
        UPDATED("updated"), // NOI18N
        COMMENTS("comments"); // NOI18N
        private final String name;

        private Sort(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Direction {

        ASC("asc"), // NOI18N
        DESC("desc"); // NOI18N
        private final String name;

        private Direction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private Milestone milestone;
    private State state;
    private String assignee;
    private String creator;
    private String mentioned;
    private List<Label> labels;
    private Sort sort;
    private Direction direction;
    private Date since;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-ddHH:MM:SSZ"); // NOI18N

    public GetIssuesParams milestone(Milestone milestone) {
        this.milestone = milestone;
        return this;
    }

    public GetIssuesParams assignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public GetIssuesParams creator(String creator) {
        this.creator = creator;
        return this;
    }

    public GetIssuesParams mentioned(String mentioned) {
        this.mentioned = mentioned;
        return this;
    }

    public GetIssuesParams state(State state) {
        this.state = state;
        return this;
    }

    public GetIssuesParams labels(List<Label> labels) {
        this.labels = labels;
        return this;
    }

    public GetIssuesParams sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public GetIssuesParams direction(Direction direction) {
        this.direction = direction;
        return this;
    }

    public GetIssuesParams since(Date since) {
        this.since = since;
        return this;
    }

    public Map<String, String> toFilterMap() {
        HashMap<String, String> filterMap = new HashMap<>();

        if (milestone != null) {
            filterMap.put(IssueService.FILTER_MILESTONE, String.valueOf(milestone.getNumber())); // NOI18N
        }

        if (!StringUtils.isEmpty(assignee)) {
            filterMap.put(IssueService.FILTER_ASSIGNEE, assignee); // NOI18N
        }

        if (!StringUtils.isEmpty(creator)) {
            filterMap.put("creator", creator); // NOI18N
        }

        if (!StringUtils.isEmpty(mentioned)) {
            filterMap.put(IssueService.FILTER_MENTIONED, mentioned); // NOI18N
        }

        if (state != null) {
            filterMap.put(IssueService.FILTER_STATE, state.getName()); // NOI18N
        }

        if (labels != null) {
            StringBuilder sb = new StringBuilder();
            for (Label label : labels) {
                if (sb.length() > 0) {
                    sb.append(","); // NOI18N
                }
                sb.append(label.getName());
            }
            String labelsWithComma = sb.toString();
            if (!StringUtils.isEmpty(labelsWithComma)) {
                filterMap.put(IssueService.FILTER_LABELS, labelsWithComma); // NOI18N
            }
        }

        if (sort != null) {
            filterMap.put(IssueService.FIELD_SORT, sort.getName()); // NOI18N
        }

        if (direction != null) {
            filterMap.put(IssueService.FIELD_DIRECTION, direction.getName()); // NOI18N
        }

        if (since != null) {
            filterMap.put(IssueService.FIELD_SINCE, DATE_FORMAT.format(since)); // NOI18N
        }

        return filterMap;
    }

}
