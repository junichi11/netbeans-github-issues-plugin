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
import com.junichi11.netbeans.modules.github.issues.utils.StringUtils;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;

/**
 * Cached data via GitHub API.
 *
 * @author junichi11
 */
public final class GitHubCache {

    // @GuardedBy("this")
    private static final Map<String, GitHubCache> CACHES = Collections.synchronizedMap(new HashMap<String, GitHubCache>());
    // @GuardedBy("this")
    private List<User> collaborators;
    // @GuardedBy("this")
    private final Map<String, List<Milestone>> milestoneMap = Collections.synchronizedMap(new HashMap<String, List<Milestone>>());
    // @GuardedBy("this")
    private List<Label> labels;
    // @GuardedBy("this")
    private List<RepositoryBranch> branches;
    // @GuardedBy("this")
    private List<Repository> forks;
    private User myself;
    // @GuardedBy("this")
    private final Map<String, Icon> userIcons = new HashMap<>();
    private final GitHubRepository repository;
    private static final Logger LOGGER = Logger.getLogger(GitHubCache.class.getName());
    // <OAuth token, User>
    private static final Map<String, User> USERS = Collections.synchronizedMap(new HashMap<String, User>());

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
    public synchronized List<User> getCollaborators() {
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
                LOGGER.log(Level.WARNING, "{0} : Can''t get collaborators. {1}", new Object[]{repository.getFullName(), ex.getMessage()}); // NOI18N
            }
        }
        return collaborators;
    }

    /**
     * Get milestones. If there is a cache, it is returned.
     *
     * @param state open, closed or all
     * @return milestones
     */
    public List<Milestone> getMilestones(String state) {
        return getMilestones(state, false);
    }

    /**
     * Get milestones.
     *
     * @param state open, closed or all
     * @param force {@code true} if don't use cache data, otherwise
     * {@code false}
     * @return milestones
     */
    public synchronized List<Milestone> getMilestones(String state, boolean force) {
        List<Milestone> milestone = milestoneMap.get(state);
        if (milestone == null || force) {
            if (milestone != null) {
                milestone.clear();
            }
            Repository gHRepository = repository.getRepository();
            GitHubClient client = repository.createGitHubClient();
            if (client == null) {
                return Collections.emptyList();
            }
            MilestoneService milestoneService = new MilestoneService(client);
            try {
                milestone = milestoneService.getMilestones(gHRepository, state);
                milestoneMap.put(state, milestone);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "{0} : Can''t get milestones. {1}", new Object[]{repository.getFullName(), ex.getMessage()}); // NOI18N
            }
        }
        return milestone;
    }

    /**
     * Get labels. If there is a cache, it is returned.
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
    public synchronized List<Label> getLabels(boolean force) {
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
                LOGGER.log(Level.WARNING, "{0} : Can''t get labels. {1}", new Object[]{repository.getFullName(), ex.getMessage()}); // NOI18N
            }
        }
        return labels;
    }

    /**
     * Get myself.
     *
     * @return myself
     */
    @CheckForNull
    public synchronized User getMySelf() {
        if (myself == null) {
            GitHubClient client = repository.createGitHubClient();
            if (client == null) {
                return null;
            }
            UserService userService = new UserService(client);
            try {
                myself = userService.getUser();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "{0} : Can''t get myself. {1}", new Object[]{repository.getFullName(), ex.getMessage()}); // NOI18N
            }
        }
        return myself;
    }

    /**
     * Get user icon. Icon size is 16x16.
     *
     * @param user User
     * @return user icon if it was got, otherwise {@code null}
     */
    @CheckForNull
    public synchronized Icon getUserIcon(User user) {
        if (user == null) {
            return null;
        }
        String login = user.getLogin();
        Icon icon = userIcons.get(login);
        if (icon != null) {
            return icon;
        }
        GitHubClient client = repository.createGitHubClient();
        if (client == null) {
            return null;
        }
        UserService userService = new UserService(client);
        try {
            user = userService.getUser(login);
            String avatarUrl = user.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                URL url = new URL(avatarUrl);
                // resize to 16x16
                BufferedImage image = ImageIO.read(url);
                Image resizedImage = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                icon = new ImageIcon(resizedImage);
                userIcons.put(login, icon);
                return icon;
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    /**
     * Get branches on a repository. Get them from a cache.
     *
     * @return RepositoryBranches
     */
    public List<RepositoryBranch> getBranches() {
        return getBranches(false);
    }

    /**
     * Get branches on a repository.
     *
     * @param force {@code true} if reload branches, otherwise {@code false}
     * @return RepositoryBranches
     */
    public synchronized List<RepositoryBranch> getBranches(boolean force) {
        if (branches == null || force) {
            if (branches != null) {
                branches.clear();
            }
            GitHubClient client = repository.createGitHubClient();
            if (client == null) {
                return Collections.emptyList();
            }
            RepositoryService service = new RepositoryService(client);
            Repository ghRepository = repository.getRepository();
            try {
                branches = service.getBranches(ghRepository);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "{0} : Can''t get branches. {1}", new Object[]{repository.getFullName(), ex.getMessage()}); // NOI18N
            }
        }
        return branches;
    }

    /**
     * Get forks for a repository. If there is a cache, it is returned.
     *
     * @return forks
     */
    public List<Repository> getForks() {
        return getForks(false);
    }

    /**
     * Get forks for a repository.
     *
     * @param force {@code true} if don't use the cache, otherwise {@code false}
     * @return forks
     */
    public synchronized List<Repository> getForks(boolean force) {
        if (forks == null || force) {
            if (forks != null) {
                forks.clear();
            }
            GitHubClient client = repository.createGitHubClient();
            if (client == null) {
                return Collections.emptyList();
            }
            RepositoryService service = new RepositoryService(client);
            Repository ghRepository = repository.getRepository();
            try {
                forks = service.getForks(ghRepository);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "{0} : Can''t get forks. {1}", new Object[]{repository.getFullName(), ex.getMessage()}); // NOI18N
            }
        }
        return forks;
    }

    /**
     * Get the user for the OAuth Token.
     *
     * @param oAuthToken OAuth token
     * @return User if it can be got, otherwise {@code null}
     */
    @CheckForNull
    public static synchronized User getUser(String oAuthToken) {
        if (StringUtils.isEmpty(oAuthToken)) {
            return null;
        }

        User user = USERS.get(oAuthToken);
        if (user == null) {
            GitHubClient client = new GitHubClient().setOAuth2Token(oAuthToken);
            UserService userService = new UserService(client);
            try {
                user = userService.getUser();
                if (user != null) {
                    USERS.put(oAuthToken, user);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Can''t get user. {0}", ex.getMessage()); // NOI18N
            }
        }
        return user;
    }
}
