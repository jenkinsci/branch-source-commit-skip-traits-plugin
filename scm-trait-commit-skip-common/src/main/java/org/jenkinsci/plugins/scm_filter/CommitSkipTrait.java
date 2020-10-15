package org.jenkinsci.plugins.scm_filter;

import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;

/**
 * @author witokondoria
 */
public abstract class CommitSkipTrait extends SCMSourceTrait {

    private final String tokens;

    /**
     * Constructor for stapler.
     */
    protected CommitSkipTrait(String tokens) {
        this.tokens = tokens;
    }

    public String getTokens() {
        return tokens;
    }

    @Override
    protected abstract void decorateContext(SCMSourceContext<?, ?> context);

    /**
     * Our descriptor.
     */
    abstract static class CommitSkipTraitDescriptorImpl extends SCMSourceTraitDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.CommitSkipTrait_DisplayName();
        }
    }
}
