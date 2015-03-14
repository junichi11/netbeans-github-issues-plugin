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
package com.junichi11.netbeans.modules.github.issues.ui;

import com.junichi11.netbeans.modules.github.issues.GitHubCache;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepositoryManager;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;

/**
 *
 * @author junichi11
 */
public class AttributesListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -7361150610203379058L;

    private final ListCellRenderer renderer;
    private final String repositoryId;

    public AttributesListCellRenderer(ListCellRenderer renderer) {
        this(renderer, null);
    }

    public AttributesListCellRenderer(ListCellRenderer renderer, String repositoryId) {
        this.renderer = renderer;
        this.repositoryId = repositoryId;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String text = null;
        Icon icon = null;
        if (value instanceof Milestone) {
            Milestone milestone = (Milestone) value;
            text = milestone.getTitle();
        } else if (value instanceof User) {
            User user = (User) value;
            text = user.getLogin();
            if (text != null && repositoryId != null && !repositoryId.isEmpty()) {
                GitHubRepository repository = GitHubRepositoryManager.getInstance().getRepository(repositoryId);
                if (repository != null) {
                    GitHubCache cache = GitHubCache.create(repository);
                    icon = cache.getUserIcon(user);
                }
            }
        } else if (value instanceof Label) {
            Label label = (Label) value;
            text = label.getName();
            icon = new ColorIcon(String.format("#%s", label.getColor())); // NOI18N
        }
        if (text == null) {
            text = " "; // NOI18N
        }
        JLabel label = (JLabel) renderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        label.setIcon(icon);
        return label;
    }

}
