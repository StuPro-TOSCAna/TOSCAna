package org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform;

import java.util.Optional;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;

public class ImageMappingVisitor implements NodeVisitor {

    private final BaseImageMapper mapper;

    private String baseImage = null;
    private boolean hasInstallScripts = false;

    public ImageMappingVisitor(BaseImageMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void visit(Compute node) {
        baseImage = mapper.mapToBaseImage(node.getOs());
        hasInstallScripts = hasCreateScript(node) || hasConfigureScript(node);
    }

    private boolean hasCreateScript(RootNode node) {
        return node.getStandardLifecycle().getCreate().isPresent() && node.getStandardLifecycle().getCreate().get().getArtifact().isPresent();
    }

    private boolean hasConfigureScript(RootNode node) {
        return node.getStandardLifecycle().getConfigure().isPresent();
    }

    @Override
    public void visit(Apache node) {
        if (!hasInstallScripts) {
            if (!hasCreateScript(node)) {
                baseImage = "library/php:apache";
            }
        }
    }

    @Override
    public void visit(MysqlDbms node) {
        if (!hasInstallScripts) {
            if (!hasCreateScript(node)) {
                baseImage = "library/mysql:latest";
            }
        }
    }

    @Override
    public void visit(WebApplication node) {
        // Parent has to be visited to determine image
    }

    @Override
    public void visit(MysqlDatabase node) {
        // Parent has to be visited to determine image
    }

    public Optional<String> getBaseImage() {
        return Optional.ofNullable(baseImage);
    }
}
