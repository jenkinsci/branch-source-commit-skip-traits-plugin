package org.jenkinsci.plugins.scm_filter;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.github_branch_source.Connector;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GitHub;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.model.Item;
import hudson.util.FormValidation;
import jenkins.branch.BranchBuildStrategyDescriptor;
import jenkins.plugins.git.AbstractGitSCMSource;
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
	public String getMessage(SCMSource source, SCMRevision revision) throws CouldNotGetCommitMessageException {
		GitHubSCMSource ghSource = (GitHubSCMSource) source;
		AbstractGitSCMSource.SCMRevisionImpl gitRevision = (AbstractGitSCMSource.SCMRevisionImpl) revision;
		GitHub gitHub = null;
		try {
			gitHub = Connector.connect(ghSource.getApiUri(), Connector.lookupScanCredentials
					((Item) ghSource.getOwner(), ghSource.getApiUri(), ghSource.getCredentialsId()));
			GHCommit commit = gitHub.getRepository(ghSource.getRepoOwner()+"/"+ghSource.getRepository()).getCommit(gitRevision.getHash());
			return commit.getCommitShortInfo().getMessage();
		} catch (IOException e){
			throw new CouldNotGetCommitMessageException(e);
		}
		finally{
			Connector.release(gitHub);
		}
	}

	@Extension
	public static class DescriptorImpl extends BranchBuildStrategyDescriptor {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public @Nonnull String getDisplayName() {
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

		public FormValidation doCheckPattern(@QueryParameter String value) {
			if (StringUtils.isBlank(value)) {
				return FormValidation.error("Cannot be empty");
			}
			return FormValidation.ok();
		}

	}
}
