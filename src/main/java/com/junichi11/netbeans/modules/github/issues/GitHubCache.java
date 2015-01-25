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

import com.junichi11.netbeans.modules.github.issues.repository.GitHubRepository;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.UserService;
import org.netbeans.api.annotations.common.NonNull;

/**
 *
 * @author junichi11
 */
public final class GitHubCache {

    private static final Map<String, GitHubCache> CACHES = Collections.synchronizedMap(new HashMap<String, GitHubCache>());
    private List<User> collaborators;
    private List<Milestone> milestones;
    private List<Label> labels;
    private User myself;
    private final GitHubRepository repository;
    private static final Logger LOGGER = Logger.getLogger(GitHubCache.class.getName());

    private GitHubCache(GitHubRepository repository) {
        this.repository = repository;
    }

    /**
     * Create GitHubCache.
     *
     * @param repository GitHubRepository
     * @return GitHubCache
     */
    public static synchronized GitHubCache create(@NonNull GitHubRepository repository) {
        String id = repository.getID();
        GitHubCache cache = CACHES.get(id);
        if (cache != null) {
            return cache;
        }
        cache = new GitHubCache(repository);
        CACHES.put(id, cache);
        return cache;
    }

    /**
     * Clear cache for specified repository.
     *
     * @param repository GitHubRepository
     */
    public synchronized void clear(GitHubRepository repository) {
        CACHES.remove(repository.getID());
    }

    /**
     * Get collaborators. <i>Note</i> Require the push access.
     *
     * @return collaborators
     */
    public List<User> getCollaborators() {
        if (collaborators == null) {
            Repository ghRepository = repository.getRepository();
            GitHubClient client = repository.createGitHubClient();
            if (client == null) {
                return Collections.emptyList();
            }
            CollaboratorService collaboratorService = new CollaboratorService(client);
            try {
                collaborators = collaboratorService.getCollaborators(ghRepository);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "{0} : Can't get collaborators.", repository.getFullName()); // NOI18N
            }
        }
        return collaborators;
    }

    /**
     * Get milestones.
     *
     * @return milestones
     */
    public List<Milestone> getMilestones() {
        if (milestones == null) {
            Repository gHRepository = repository.getRepository();
            GitHubClient client = repository.createGitHubClient();
            if (client == null) {
                return Collections.emptyList();
            }
            MilestoneService milestoneService = new MilestoneService(client);
            try {
                milestones = milestoneService.getMilestones(gHRepository, "open"); // NOI18N
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "{0} : Can't get milestones.", repository.getFullName()); // NOI18N
            }
        }
        return milestones;
    }

    /**
     * Get labels.
     *
     * @return labels
     */
    public List<Label> getLabels() {
        return getLabels(false);
    }

    /**
     * Get labels.
     *
     * @param force {@code true} if reload labels, otherwise {@code false}
     * @return labels
     */
    public List<Label> getLabels(boolean force) {
        if (labels == null || force) {
            if (labels != null) {
                labels.clear();
            }
            GitHubClient client = repository.createGitHubClient();
            if (client == null) {
                return Collections.emptyList();
            }
            LabelService labelService = new LabelService(client);
            Repository ghRepository = repository.getRepository();
            try {
                labels = labelService.getLabels(ghRepository);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "{0} : Can't get labels.", repository.getRepositoryName()); // NOI18N
            }
        }
        return labels;
    }

    /**
     * Get myself.
     *
     * @return myself
     */
    public User getMySelf() {
        if (myself == null) {
            GitHubClient client = repository.createGitHubClient();
            if (client == null) {
                return null;
            }
            UserService userService = new UserService(client);
            try {
                myself = userService.getUser();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "{0} : Can''t get myself.", repository.getRepositoryName()); // NOI18N
            }
        }
        return myself;
    }

}
