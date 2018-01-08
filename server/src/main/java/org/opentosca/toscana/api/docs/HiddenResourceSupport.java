package org.opentosca.toscana.api.docs;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

@ApiModel
public class HiddenResourceSupport extends ResourceSupport {

    @Override
    @ApiModelProperty(hidden = true)
    public void add(Link... links) {
        super.add(links);
    }

    @Override
    @ApiModelProperty(hidden = true)
    public List<Link> getLinks() {
        return super.getLinks();
    }
}
