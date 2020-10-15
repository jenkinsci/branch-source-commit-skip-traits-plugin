package org.jenkinsci.plugins.scm_filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jenkins.branch.BranchBuildStrategy;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceOwner;
import jenkins.scm.api.mixin.ChangeRequestSCMHead;

public abstract class PullRequestTitleBuildStrategy extends BranchBuildStrategy {
    private final static Logger LOGGER = LoggerFactory.getLogger(PullRequestTitleBuildStrategy.class);

    public static String getDisplayName() {
        return Messages.PullRequestTitleBranchBuildStrategy_DisplayName();
    }

    private final String tokens;

    public PullRequestTitleBuildStrategy(String tokens) {
        this.tokens = tokens;
    }

    public String getTokens() {
        return tokens;
    }

    private transient TokenListMatcher matcher;

    private boolean containsSkipToken(String title) {
        if (matcher == null) {
            matcher = new TokenListMatcher(getTokens());
        }
        return matcher.containsSkipToken(title);
    }

    protected abstract String getTitle(SCMSource source, SCMHead head) throws CouldNotGetCommitDataException;

    @Override
    public boolean isAutomaticBuild(SCMSource source, SCMHead head, SCMRevision currRevision,
            SCMRevision prevRevision) {
        if (!(head instanceof ChangeRequestSCMHead)) {
            return false;
        }
        String title = null;
        try {
            title = getTitle(source, head);
        } catch (CouldNotGetCommitDataException e) {
            LOGGER.error("Could not attempt to prevent automatic build by pull request title "
                    + "because of an error when fetching the pull request", e);
            return true;
        }
        if (title == null) {
            LOGGER.info(
                    "Could not attempt to prevent automatic build by by pull request title " + "because title is null");
            return true;
        }
        title = title.toLowerCase();
        if (containsSkipToken(title)) {
            String ownerDisplayName = "Global";
            SCMSourceOwner owner = source.getOwner();
            if (owner != null) {
                ownerDisplayName = owner.getDisplayName();
            }
            LOGGER.info(
                    "Automatic build prevented for job [{}] because pull request title [{}] " + "contained one of [{}]",
                    ownerDisplayName, title, tokens);
            return false;
        }
        return true;
    }
}
