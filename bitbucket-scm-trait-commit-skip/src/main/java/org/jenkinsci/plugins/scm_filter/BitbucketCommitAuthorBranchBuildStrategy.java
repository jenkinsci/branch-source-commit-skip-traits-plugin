package org.jenkinsci.plugins.scm_filter;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketGitSCMRevision;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.PullRequestSCMRevision;

import hudson.Extension;
import hudson.Util;
import hudson.util.FormValidation;
import jenkins.branch.BranchBuildStrategyDescriptor;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceDescriptor;

public class BitbucketCommitAuthorBranchBuildStrategy extends CommitAuthorBranchBuildStrategy {

	@DataBoundConstructor
	public BitbucketCommitAuthorBranchBuildStrategy(String pattern) {
		super(pattern);
	}

	@Override
	public String getAuthor(SCMSource source, SCMRevision currRevision) throws CouldNotGetCommitDataException {
		SCMRevision revision = currRevision;
		if (currRevision instanceof PullRequestSCMRevision) {
			PullRequestSCMRevision<?> pr = (PullRequestSCMRevision<?>) currRevision;
			revision = pr.getPull();
		}
		if (revision instanceof BitbucketGitSCMRevision) {
			BitbucketGitSCMRevision bbRevision = (BitbucketGitSCMRevision) revision;
			return Util.fixEmpty(bbRevision.getAuthor());
		} else if (revision instanceof BitbucketSCMSource.MercurialRevision) {
			BitbucketSCMSource.MercurialRevision bbRevision = (BitbucketSCMSource.MercurialRevision) revision;
			return Util.fixEmpty(bbRevision.getAuthor());
		}

		throw new CouldNotGetCommitDataException("Revision class is not a BitbucketGitSCMRevision or BitbucketSCMSource.MercurialRevision");
	}

	@Extension
	public static class DescriptorImpl extends RegexFilterBranchBuildStrategyDescriptor {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public @Nonnull
		String getDisplayName() {
			return CommitAuthorBranchBuildStrategy.getDisplayName();
		}

		/**
		 * {@inheritDoc}
		 * this is currently never called for organization folders, see JENKINS-54468
		 */
		@Override
		public boolean isApplicable(@Nonnull SCMSourceDescriptor sourceDescriptor) {
			return BitbucketSCMSource.DescriptorImpl.class.isAssignableFrom(sourceDescriptor.getClass());
		}

	}
}