package org.jenkinsci.plugins.scm_filter;

import java.io.IOException;

import org.jenkinsci.plugins.github_branch_source.Connector;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource;
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMRevision;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GitHub;

import hudson.model.Item;
import jenkins.plugins.git.AbstractGitSCMSource;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;

public final class GitHubUtils {

	private GitHubUtils() {
		// utils class, no instances
	}

	public static GHCommit getCommit(SCMSource source,SCMRevision revision) throws CouldNotGetCommitDataException {
		String hash = null;
		if (AbstractGitSCMSource.SCMRevisionImpl.class.isAssignableFrom(revision.getClass())){
			hash = ((AbstractGitSCMSource.SCMRevisionImpl) revision).getHash();
		}
		if (PullRequestSCMRevision.class.isAssignableFrom(revision.getClass())){
			hash = ((PullRequestSCMRevision) revision).getPullHash();
		}
		if (hash == null) {
			throw new CouldNotGetCommitDataException("Unknown revision class ["+revision.getClass()+"] or null hash");
		}

		if (!GitHubSCMSource.class.isAssignableFrom(source.getClass())){
			throw new IllegalArgumentException("SCM Source ["+source.getClass()+"] is not a GitHubSCMSource ");
		}
		GitHubSCMSource ghSource = (GitHubSCMSource) source;

		GitHub gitHub = null;
		try {
			gitHub = Connector.connect(ghSource.getApiUri(), Connector.lookupScanCredentials
					((Item) ghSource.getOwner(), ghSource.getApiUri(), ghSource.getCredentialsId()));
			return gitHub.getRepository(ghSource.getRepoOwner()+"/"+ghSource.getRepository()).getCommit(hash);
		} catch (IOException e){
			throw new CouldNotGetCommitDataException(e);
		}
		finally{
			Connector.release(gitHub);
		}
	}

}
