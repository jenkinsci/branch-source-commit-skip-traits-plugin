package org.jenkinsci.plugins.scm_filter;

import jenkins.scm.api.trait.SCMSourceTraitDescriptor;

/**
 * @author avolanis
 */
public abstract class TitleSkipTrait extends CommitSkipTrait {

    protected TitleSkipTrait(String tokens) {
        super(tokens);
    }

    /**
     * Our descriptor.
     */
    public abstract static class TitleSkipTraitDescriptorImpl extends SCMSourceTraitDescriptor {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return Messages.TitleSkipTrait_DisplayName();
        }
    }
}
