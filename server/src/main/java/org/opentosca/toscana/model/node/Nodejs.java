package org.opentosca.toscana.model.node;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Nodejs extends WebServer {

    public static final String GITHUB_URL_DEFAULT = "https://github.com/mmm/testnode.git";
    /**
     Defaults to {@link #GITHUB_URL_DEFAULT}
     */
    public static ToscaKey<String> GITHUB_URL = new ToscaKey<>(PROPERTIES, "github_url");

    public Nodejs(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(GITHUB_URL, GITHUB_URL_DEFAULT);
    }

    /**
     @return {@link #GITHUB_URL}
     */
    public String getGithubUrl() {
        return get(GITHUB_URL);
    }

    /**
     Sets {@link #GITHUB_URL}
     */
    public Nodejs setGithubUrl(String githubUrl) {
        set(GITHUB_URL, githubUrl);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
