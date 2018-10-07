/*
 * The MIT License
 *
 * Copyright (c) 2018, Nikolas Falco
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.scm.trait.commit.skip;

import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketGitSCMRevision;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource.MercurialRevision;
import com.cloudbees.jenkins.plugins.bitbucket.PullRequestSCMRevision;

import hudson.Extension;
import hudson.Util;
import jenkins.branch.BranchBuildStrategy;
import jenkins.branch.BranchBuildStrategyDescriptor;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceDescriptor;

public class BitbucketSkipCommitBuildStrategy extends BranchBuildStrategy {

    /**
     * The message filter.
     */
    @Nonnull
    private String message;
    /**
     * The author filter.
     */
    @Nonnull
    private String author;

    @DataBoundConstructor
    public BitbucketSkipCommitBuildStrategy(@CheckForNull String message, @CheckForNull String author) {
        this.message = StringUtils.defaultIfBlank(message, "");
        this.author = StringUtils.defaultIfBlank(author, "");
    }

    /**
     * Returns the message pattern of the filter.
     *
     * @return the message filter.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the author pattern filter.
     *
     * @return the author pattern.
     */
    public String getAuthor() {
        return author;
    }

    @Override
    public boolean isAutomaticBuild(SCMSource source, SCMHead head, SCMRevision currRevision, SCMRevision prevRevision) {
        SCMRevision revision = currRevision;

        if (currRevision instanceof PullRequestSCMRevision) {
            PullRequestSCMRevision<?> pr = (PullRequestSCMRevision<?>) currRevision;
            revision = pr.getPull();
        }

        String commitAuthor = null;
        String commitMessage = null;
        if (revision instanceof BitbucketGitSCMRevision) {
            BitbucketGitSCMRevision bbRevision = (BitbucketGitSCMRevision) revision;
            commitAuthor = Util.fixEmpty(bbRevision.getAuthor());
            commitMessage = Util.fixEmpty(bbRevision.getMessage());
        } else if (revision instanceof MercurialRevision) {
            MercurialRevision bbRevision = (MercurialRevision) revision;
            commitAuthor = Util.fixEmpty(bbRevision.getAuthor());
            commitMessage = Util.fixEmpty(bbRevision.getMessage());
        }

        if (commitAuthor != null || commitMessage != null) {
            return !(matches(this.message, commitMessage) || matches(this.author, commitAuthor));
        }

        return true;
    }

    private boolean matches(@Nonnull String pattern, @CheckForNull String value) {
        String fixValue = Util.fixEmpty(value);
        return fixValue != null && Pattern.matches(getPattern(pattern), fixValue);
    }

    /**
     * Returns the pattern corresponding to the branches containing wildcards.
     *
     * @param wildcardPatterns the names wildcards to create a pattern for
     * @return pattern corresponding to the branches containing wildcards
     */
    protected String getPattern(String wildcardPatterns) {
        StringBuilder quotedBranches = new StringBuilder();
        for (String wildcardPattern : wildcardPatterns.split(" ")) {
            StringBuilder quotedPattern = new StringBuilder();
            for (String pattern : wildcardPattern.split("(?=[*])|(?<=[*])")) {
                if (pattern.equals("*")) {
                    quotedPattern.append(".*");
                } else if (!pattern.isEmpty()) {
                    quotedPattern.append(Pattern.quote(pattern));
                }
            }
            if (quotedBranches.length() > 0) {
                quotedBranches.append("|");
            }
            quotedBranches.append(quotedPattern);
        }
        return quotedBranches.toString();
    }

    @Extension
    public static class DescriptorImpl extends BranchBuildStrategyDescriptor {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return Messages.BitbucketSkipCommitBuildStrategy_displayName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isApplicable(SCMSourceDescriptor sourceDescriptor) {
            return sourceDescriptor instanceof BitbucketSCMSource.DescriptorImpl;
        }

    }

}
