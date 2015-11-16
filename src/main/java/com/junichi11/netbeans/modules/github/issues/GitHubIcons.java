/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package com.junichi11.netbeans.modules.github.issues;

import java.awt.Image;
import javax.swing.Icon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;

/**
 * Icons.
 *
 * @author junichi11
 */
public final class GitHubIcons {

    @StaticResource
    public static final String CLOSED_ISSUE_16 = "com/junichi11/netbeans/modules/github/issues/resources/closed_issue_16.png"; // NOI18N
    @StaticResource
    public static final String CLOSED_PULL_REQUEST_16 = "com/junichi11/netbeans/modules/github/issues/resources/closed_pull_request_16.png"; // NOI18N
    @StaticResource
    public static final String ERROR_16 = "com/junichi11/netbeans/modules/github/issues/resources/error_16.png"; // NOI18N
    @StaticResource
    public static final String GIT_MERGE_16 = "com/junichi11/netbeans/modules/github/issues/resources/git_merge_16.png"; // NOI18N
    @StaticResource
    public static final String GIT_PULL_REQUEST_16 = "com/junichi11/netbeans/modules/github/issues/resources/git_pull_request_16.png"; // NOI18N
    @StaticResource
    public static final String GITHUB_16 = "com/junichi11/netbeans/modules/github/issues/resources/icon_16.png"; // NOI18N
    @StaticResource
    public static final String GITHUB_32 = "com/junichi11/netbeans/modules/github/issues/resources/icon_32.png"; // NOI18N
    @StaticResource
    public static final String ISSUE_CLOSED_16 = "com/junichi11/netbeans/modules/github/issues/resources/issue_closed_16.png"; // NOI18N
    @StaticResource
    public static final String ISSUE_OPENED_16 = "com/junichi11/netbeans/modules/github/issues/resources/issue_opened_16.png"; // NOI18N
    @StaticResource
    public static final String MERGED_PULL_REQUEST_16 = "com/junichi11/netbeans/modules/github/issues/resources/merged_pull_request_16.png"; // NOI18N
    @StaticResource
    public static final String OPEN_ISSUE_16 = "com/junichi11/netbeans/modules/github/issues/resources/open_issue_16.png"; // NOI18N
    @StaticResource
    public static final String OPEN_PULL_REQUEST_16 = "com/junichi11/netbeans/modules/github/issues/resources/open_pull_request_16.png"; // NOI18N
    @StaticResource
    public static final String TEMPLATE_16 = "com/junichi11/netbeans/modules/github/issues/resources/template_16.png"; // NOI18N
    @StaticResource
    public static final String TEMPLATE_SETTINGS_16 = "com/junichi11/netbeans/modules/github/issues/resources/template_settings_16.png"; // NOI18N

    // icons
    public static final Icon CLOSED_ISSUE_ICON_16 = ImageUtilities.loadImageIcon(CLOSED_ISSUE_16, true);
    public static final Icon CLOSED_PULL_REQUEST_ICON_16 = ImageUtilities.loadImageIcon(CLOSED_PULL_REQUEST_16, true);
    public static final Icon ERROR_ICON_16 = ImageUtilities.loadImageIcon(ERROR_16, true);
    public static final Icon GIT_MERGE_ICON_16 = ImageUtilities.loadImageIcon(GIT_MERGE_16, true);
    public static final Icon GIT_PULL_REQUEST_ICON_16 = ImageUtilities.loadImageIcon(GIT_PULL_REQUEST_16, true);
    public static final Icon GITHUB_ICON_16 = ImageUtilities.loadImageIcon(GITHUB_16, true);
    public static final Icon GITHUB_ICON_32 = ImageUtilities.loadImageIcon(GITHUB_32, true);
    public static final Icon ISSUE_OPENED_ICON_16 = ImageUtilities.loadImageIcon(ISSUE_OPENED_16, true);
    public static final Icon ISSUE_CLOSED_ICON_16 = ImageUtilities.loadImageIcon(ISSUE_CLOSED_16, true);
    public static final Icon MERGED_PULL_REQUEST_ICON_16 = ImageUtilities.loadImageIcon(MERGED_PULL_REQUEST_16, true);
    public static final Icon OPEN_ISSUE_ICON_16 = ImageUtilities.loadImageIcon(OPEN_ISSUE_16, true);
    public static final Icon OPEN_PULL_REQUEST_ICON_16 = ImageUtilities.loadImageIcon(OPEN_PULL_REQUEST_16, true);
    public static final Icon TEMPLATE_ICON_16 = ImageUtilities.loadImageIcon(TEMPLATE_16, true);
    public static final Icon TEMPLATE_SETTINGS_ICON_16 = ImageUtilities.loadImageIcon(TEMPLATE_SETTINGS_16, true);

    // images
    public static final Image CLOSED_ISSUE_IMAGE_16 = ImageUtilities.loadImage(CLOSED_ISSUE_16, true);
    public static final Image CLOSED_PULL_REQUEST_IMAGE_16 = ImageUtilities.loadImage(CLOSED_PULL_REQUEST_16, true);
    public static final Image ERROR_IMAGE_16 = ImageUtilities.loadImage(ERROR_16, true);
    public static final Image GIT_MERGE_IMAGE_16 = ImageUtilities.loadImage(GIT_MERGE_16, true);
    public static final Image GIT_PULL_REQUEST_IMAGE_16 = ImageUtilities.loadImage(GIT_PULL_REQUEST_16, true);
    public static final Image GITHUB_IMAGE_16 = ImageUtilities.loadImage(GITHUB_16, true);
    public static final Image GITHUB_IMAGE_32 = ImageUtilities.loadImage(GITHUB_32, true);
    public static final Image ISSUE_OPENED_IMAGE_16 = ImageUtilities.loadImage(ISSUE_OPENED_16, true);
    public static final Image ISSUE_CLOSED_IMAGE_16 = ImageUtilities.loadImage(ISSUE_CLOSED_16, true);
    public static final Image MERGED_PULL_REQUEST_IMAGE_16 = ImageUtilities.loadImage(MERGED_PULL_REQUEST_16, true);
    public static final Image OPEN_ISSUE_IMAGE_16 = ImageUtilities.loadImage(OPEN_ISSUE_16, true);
    public static final Image OPEN_PULL_REQUEST_IMAGE_16 = ImageUtilities.loadImage(OPEN_PULL_REQUEST_16, true);
    public static final Image TEMPLATE_IMAGE_16 = ImageUtilities.loadImage(TEMPLATE_16, true);
    public static final Image TEMPLATE_SETTINGS_IMAGE_16 = ImageUtilities.loadImage(TEMPLATE_SETTINGS_16, true);

    private GitHubIcons() {
    }
}
