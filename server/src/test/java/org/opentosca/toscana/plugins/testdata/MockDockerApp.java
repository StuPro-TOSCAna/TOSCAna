package org.opentosca.toscana.plugins.testdata;

import java.util.LinkedList;
import java.util.List;

import org.opentosca.toscana.plugins.model.DockerApp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockDockerApp {

    private List<String> dependencies;
    private String[] tag;
    private String[] identifier;


    public MockDockerApp() {
        tag = new String[] {"tag for docker image", "simple-task-app"};
        identifier = new String[] {"identifier of docker container", "simple-task-app-1"};
        dependencies = new LinkedList<>();
        dependencies.add("simple-task-app/Dockerfile");
        dependencies.add("simple-task-app/createdb.sql");
        dependencies.add("simple-task-app/index.php");
        dependencies.add("simple-task-app/mysql-credentials.php");
    }

    public DockerApp validDockerApp() {
        return generateMock();
    }

    public DockerApp invalidDockerApp() {
        dependencies.remove(0);
        return generateMock();
    }

    private DockerApp generateMock() {
        DockerApp app = mock(DockerApp.class);
        when(app.getDependencies()).thenReturn(dependencies);
        when(app.getTag()).thenReturn(tag);
        when(app.getIdentifier()).thenReturn(identifier);

        return app;
    }
}
