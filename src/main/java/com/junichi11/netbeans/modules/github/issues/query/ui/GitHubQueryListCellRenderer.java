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
package com.junichi11.netbeans.modules.github.issues.query.ui;

import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Is;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.No;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Order;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Sort;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.State;
import com.junichi11.netbeans.modules.github.issues.egit.SearchIssuesParams.Type;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author junichi11
 */
public class GitHubQueryListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 5752807710023572203L;

    private final ListCellRenderer renderer;

    public GitHubQueryListCellRenderer(ListCellRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String text = null;
        if (value instanceof Sort) {
            Sort sort = (Sort) value;
            text = sort.getValue();
        } else if (value instanceof Order) {
            Order order = (Order) value;
            text = order.getValue();
        } else if (value instanceof State) {
            State state = (State) value;
            text = state.getValue();
        } else if (value instanceof Type) {
            Type type = (Type) value;
            text = type.getValue();
        } else if (value instanceof Is) {
            Is is = (Is) value;
            text = is.getValue();
        } else if (value instanceof No) {
            No no = (No) value;
            text = no.getValue();
        }
        if (text == null) {
            text = " "; // NOI18N
        }
        return renderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
    }

}
