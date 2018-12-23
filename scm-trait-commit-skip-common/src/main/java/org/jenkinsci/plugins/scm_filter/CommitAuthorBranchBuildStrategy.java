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
 * A strategy for avoiding automatic builds for commits with authors that contain a specific pattern.
 */
public abstract class CommitAuthorBranchBuildStrategy extends BranchBuildStrategy {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommitAuthorBranchBuildStrategy.class);

    private final String pattern;

    private transient Pattern compiledPattern;

    public static String getDisplayName() {
        return Messages.CommitAuthorBranchBuildStrategy_DisplayName();
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

    public abstract String getAuthor(SCMSource source, SCMRevision revision) throws CouldNotGetCommitDataException;

    public CommitAuthorBranchBuildStrategy(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean isAutomaticBuild(SCMSource source, SCMHead head, SCMRevision currRevision, SCMRevision prevRevision) {
        String author = null;
        try {
            author = getAuthor(source, currRevision);
        } catch (CouldNotGetCommitDataException e) {
            LOGGER.error("Could not attempt to prevent automatic build by commit author pattern "
                    + "because of an error when fetching the commit author", e);
            return true;
        }
        if (author == null) {
            LOGGER.info("Could not attempt to prevent automatic build by commit author pattern "
                    + "because commit author is null");
            return true;
        }
        if (getCompiledPattern().matcher(author).find()) {
            String ownerDisplayName = "Global";
            SCMSourceOwner owner = source.getOwner();
            if (owner != null) {
                ownerDisplayName = owner.getDisplayName();
            }
            LOGGER.info("Automatic build prevented for job [{}] because commit author [{}] "
                    + "matched expression [{}]", ownerDisplayName, author, pattern);
            return false;
        }
        return true;
    }
}
