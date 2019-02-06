package org.jenkinsci.plugins.scm_filter;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jenkins.branch.BranchBuildStrategy;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceOwner;

/**
 * A strategy for avoiding automatic builds for commits with messages that contain a specific pattern.
 */
public abstract class CommitMessageBranchBuildStrategy extends BranchBuildStrategy {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommitMessageBranchBuildStrategy.class);

    private final String pattern;

    private transient Pattern compiledPattern;

    public static String getDisplayName() {
        return Messages.CommitMessageBranchBuildStrategy_DisplayName();
    }

    public String getPattern() {
        return pattern;
    }

    public Pattern getCompiledPattern() {
        if (compiledPattern == null) {
            compiledPattern = Pattern.compile(pattern);
        }
        return compiledPattern;
    }

    public abstract String getMessage(SCMSource source, SCMRevision revision) throws CouldNotGetCommitDataException;

    public CommitMessageBranchBuildStrategy(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean isAutomaticBuild(SCMSource source, SCMHead head, SCMRevision currRevision, SCMRevision prevRevision) {
        String message = null;
        try {
            message = getMessage(source, currRevision);
        } catch (CouldNotGetCommitDataException e) {
            LOGGER.error("Could not attempt to prevent automatic build by commit message pattern "
                    + "because of an error when fetching the commit message", e);
            return true;
        }
        if (message == null) {
            LOGGER.info("Could not attempt to prevent automatic build by commit message pattern "
                    + "because commit message is null");
            return true;
        }
        if (getCompiledPattern().matcher(message).find()) {
            String ownerDisplayName = "Global";
            SCMSourceOwner owner = source.getOwner();
            if (owner != null) {
                ownerDisplayName = owner.getDisplayName();
            }
            LOGGER.info("Automatic build prevented for job [{}] because commit message [{}] "
                    + "matched expression [{}]", ownerDisplayName, message, pattern);
            return false;
        }
        return true;
    }
}
