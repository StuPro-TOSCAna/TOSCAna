package org.opentosca.toscana.cli;

import org.opentosca.toscana.cli.commands.AbstractApiCall;

import picocli.CommandLine;

public class CliPropertiesFactory implements CommandLine.IFactory {
    ApiController apiController;

    public CliPropertiesFactory(ApiController apiController) {
        this.apiController = apiController;
    }

    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        AbstractApiCall object = (AbstractApiCall) aClass.newInstance();
        object.createApi(apiController);
        return (K) object;
    }
}
