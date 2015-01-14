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

import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author junichi11
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Check whether specified string is {@code null} or empty.
     *
     * @param text text
     * @return {@code true} text is {@code null} or empty, otherwise
     * {@code false}
     */
    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * Combine tokens with a specified separator.
     *
     * @param tokens tokens
     * @param separator separator
     * @return one combined string, empty string if token or separator are
     * {@code null}
     */
    public static String join(List<String> tokens, String separator) {
        StringBuilder sb = new StringBuilder();
        if (tokens != null && separator != null) {
            for (String token : tokens) {
                if (sb.length() > 0) {
                    sb.append(separator);
                }
                sb.append(token);
            }
        }
        return sb.toString();
    }

    /**
     * Convert to quote comment text. Add "> " to the top of each lines.Add line
     * break(\n) the last position.
     *
     * @param comment
     * @return quote comment if comment is not {@code null} and not empty,
     * otherwise empty string
     */
    public static String toQuoteComment(String comment) {
        if (isEmpty(comment)) {
            return ""; // NOI18N
        }
        StringBuilder sb = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(comment, "\n"); // NOI18N
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            sb.append("> ").append(token).append("\n"); // NOI18N
        }
        return sb.toString();
    }
}
