package org.jenkinsci.plugins.scm_filter;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource;
import org.kohsuke.github.GHCommit;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceDescriptor;

public class GitHubCommitMessageBranchBuildStrategy extends CommitMessageBranchBuildStrategy {

    @DataBoundConstructor
    public GitHubCommitMessageBranchBuildStrategy(@CheckForNull String pattern) {
        super(pattern);
    }

    @Override
    @CheckForNull
    public String getMessage(SCMSource source, SCMRevision revision) throws CouldNotGetCommitDataException {
        GHCommit commit = GitHubUtils.getCommit(source, revision);
        try {
            return commit.getCommitShortInfo().getMessage();
        } catch (IOException e) {
            throw new CouldNotGetCommitDataException(e);
        }
    }

    @Extension
    public static class DescriptorImpl extends RegexFilterBranchBuildStrategyDescriptor {

        @Override
        @Nonnull
        public String getDisplayName() {
            return CommitMessageBranchBuildStrategy.getDisplayName();
        }

        /**
         * {@inheritDoc}
         * this is currently never called for organization folders, see JENKINS-54468
         */
        @Override
        public boolean isApplicable(@Nonnull SCMSourceDescriptor sourceDescriptor) {
            return GitHubSCMSource.DescriptorImpl.class.isAssignableFrom(sourceDescriptor.getClass());
        }
    }
}
