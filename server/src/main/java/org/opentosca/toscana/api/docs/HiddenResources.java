package org.opentosca.toscana.api.docs;

import java.util.List;

import io.swagger.annotations.ApiModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

@ApiModel
public class HiddenResources<T> extends Resources<T> {

    public HiddenResources(Iterable<T> content, Link... links) {
        super(content, links);
    }

    @Override
//    @ApiModelProperty(hidden = true)
    public void add(Iterable<Link> links) {
        super.add(links);
    }

    @Override
//    @ApiModelProperty(hidden = true)
    public List<Link> getLinks() {
        return super.getLinks();
    }

    @Override
//    @ApiModelProperty(hidden = true)
    public void add(Link... links) {
        super.add(links);
    }
}
