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
package com.junichi11.netbeans.modules.github.issues.egit;

import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public class SearchIssuesParams {

    public enum Type {

        PR("pr"), // NOI18N
        ISSUE("issue"); // NOI18N
        private final String value;

        private Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public static Type valueOfString(String type) {
            for (Type value : values()) {
                if (value.getValue().equals(type)) {
                    return value;
                }
            }
            return null;
        }
    }

    public enum In {

        TITLE("title"), // NOI18N
        BODY("body"), // NOI18N
        COMMENT("comment"); // NOI18N
        private final String value;

        private In(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum State {

        OPEN("open"), // NOI18N
        CLOSED("closed"); // NOI18N
        private final String value;

        private State(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static State valueOfString(String state) {
            for (State value : values()) {
                if (value.getValue().equals(state)) {
                    return value;
                }
            }
            return null;
        }
    }

    public enum No {

        LABEL("label"), // NOI18N
        MILESTONE("milestone"), // NOI18N
        ASSIGNEE("assignee"); // NOI18N
        private final String value;

        private No(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static No valueOfString(String no) {
            for (No value : values()) {
                if (value.getValue().equals(no)) {
                    return value;
                }
            }
            return null;
        }

    }

    public enum Is {

        OPEN("open"), // NOI18N
        CLOSED("closed"), // NOI18N
        MERGED("merged"), // NOI18N
        UNMERGED("unmerged"), // NOI18N
        PR("pr"), // NOI18N
        ISSUE("issue"); // NOI18N
        private final String value;

        private Is(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Is valueOfString(String is) {
            for (Is value : values()) {
                if (value.getValue().equals(is)) {
                    return value;
                }
            }
            return null;
        }
    }

    public enum Sort {

        COMMENTS("comments"), // NOI18N
        CREATED("created"), // NOI18N
        UPDATED("updated"); // NOI18N
        private final String value;

        private Sort(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Sort valueOfString(String name) {
            for (Sort value : values()) {
                if (value.getValue().equals(name)) {
                    return value;
                }
            }
            return null;
        }
    }

    public enum Order {

        ASC("asc"), // NOI18N
        DESC("desc"); // NOI18N

        private final String value;

        private Order(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Order valueOfString(String name) {
            for (Order value : values()) {
                if (value.getValue().equals(name)) {
                    return value;
                }
            }
            return null;
        }
    }

    private final List<Pair<String, String>> parameters = new ArrayList<>();
    private final List<Pair<String, String>> qParameters = new ArrayList<>();

    private String qKeyword = ""; // NOI18N

    public SearchIssuesParams() {
    }

    public SearchIssuesParams keyword(String keyword) {
        if (keyword == null) {
            keyword = ""; // NOI18N
        }
        this.qKeyword = keyword;
        return this;
    }

    public SearchIssuesParams milestone(Milestone milestone) {
        if (milestone != null) {
            qParameters.add(Pair.of("milestone", milestone.getTitle())); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams milestone(String milestone) {
        if (milestone != null) {
            qParameters.add(Pair.of("milestone", milestone)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams type(Type type) {
        if (type != null) {
            qParameters.add(Pair.of("type", type.getValue())); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams in(List<In> ins) {
        StringBuilder sb = new StringBuilder();
        for (In in : ins) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(in.getValue());
        }
        String inString = sb.toString();
        if (!StringUtils.isEmpty(inString)) {
            qParameters.add(Pair.of("in", inString)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams in(String in) {
        if (!StringUtils.isEmpty(in)) {
            qParameters.add(Pair.of("in", in)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams is(Is is) {
        if (is != null) {
            qParameters.add(Pair.of("is", is.getValue())); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams author(String author) {
        if (!StringUtils.isEmpty(author)) {
            qParameters.add(Pair.of("author", author)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams assignee(String assignee) {
        if (!StringUtils.isEmpty(assignee)) {
            qParameters.add(Pair.of("assignee", assignee)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams mentions(String mentions) {
        if (!StringUtils.isEmpty(mentions)) {
            qParameters.add(Pair.of("mentions", mentions)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams commenter(String commenter) {
        if (!StringUtils.isEmpty(commenter)) {
            qParameters.add(Pair.of("commenter", commenter)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams involves(String involves) {
        if (!StringUtils.isEmpty(involves)) {
            qParameters.add(Pair.of("involves", involves)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams team(String team) {
        if (!StringUtils.isEmpty(team)) {
            qParameters.add(Pair.of("team", team)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams state(State state) {
        if (state != null) {
            qParameters.add(Pair.of("state", state.getValue())); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams labels(String labels) {
        if (!StringUtils.isEmpty(labels)) {
            String[] splits = labels.split(","); // NOI18N
            for (String s : splits) {
                if (s.isEmpty()) {
                    continue;
                }
                qParameters.add(Pair.of("label", s.trim())); // NOI18N
            }
        }
        return this;
    }

    public SearchIssuesParams labels(List<Label> labels) {
        for (Label label : labels) {
            qParameters.add(Pair.of("label", label.getName())); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams no(No no) {
        if (no != null) {
            qParameters.add(Pair.of("no", no.getValue())); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams language(String language) {
        if (!StringUtils.isEmpty(language)) {
            qParameters.add(Pair.of("language", language)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams is(List<Is> ises) {
        for (Is is : ises) {
            qParameters.add(Pair.of("is", is.getValue())); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams created(String created) {
        if (!StringUtils.isEmpty(created)) {
            qParameters.add(Pair.of("created", created)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams updated(String updated) {
        if (!StringUtils.isEmpty(updated)) {
            qParameters.add(Pair.of("updated", updated)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams merged(String merged) {
        if (!StringUtils.isEmpty(merged)) {
            qParameters.add(Pair.of("merged", merged)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams closed(String closed) {
        if (!StringUtils.isEmpty(closed)) {
            qParameters.add(Pair.of("closed", closed)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams comments(String comments) {
        if (!StringUtils.isEmpty(comments)) {
            qParameters.add(Pair.of("comments", comments)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams user(String user) {
        if (!StringUtils.isEmpty(user)) {
            qParameters.add(Pair.of("user", user)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams repo(String repo) {
        if (!StringUtils.isEmpty(repo)) {
            qParameters.add(Pair.of("repo", repo)); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams sort(Sort sort) {
        if (sort != null) {
            parameters.add(Pair.of("sort", sort.getValue())); // NOI18N
        }
        return this;
    }

    public SearchIssuesParams order(Order order) {
        if (order != null) {
            parameters.add(Pair.of("order", order.getValue())); // NOI18N
        }
        return this;
    }

    public String getParameters(boolean asParameter) {
        StringBuilder sb = new StringBuilder();
        if (asParameter) {
            sb.append("?"); // NOI18N
        }
        sb.append(getQParameters(asParameter));
        for (Pair parameter : parameters) {
            sb.append("&"); // NOI18N
            sb.append(parameter.first()).append("=").append(parameter.second()); // NOI18N
        }
        return sb.toString();
    }

    private String getQParameters(boolean asParameter) {
        StringBuilder sb = new StringBuilder();
        sb.append("q="); // NOI18N
        if (!StringUtils.isEmpty(qKeyword)) {
            String kwd = qKeyword;
            if (asParameter) {
                kwd = kwd.replaceAll(" ", "+"); // NOI18N
            }
            sb.append(kwd);
        }
        boolean first = true;
        for (Pair<String, String> qParameter : qParameters) {
            if (first) {
                if (!StringUtils.isEmpty(qKeyword)) {
                    sb.append("+"); // NOI18N
                }
                first = false;
            } else {
                sb.append("+"); // NOI18N
            }
            sb.append(qParameter.first()).append(":").append(qParameter.second()); // NOI18N
        }
        return sb.toString();
    }

}
