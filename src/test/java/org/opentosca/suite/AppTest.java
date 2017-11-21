package org.opentosca.suite;

import java.io.FileNotFoundException;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertTrue;

/**
 * Unit test for simple App.
 */
@RunWith(JUnit4.class)
public class AppTest {

    @Test
    public void serviceTest() throws FileNotFoundException {
        App.main();
        DefaultKubernetesClient client = new DefaultKubernetesClient();
        String namespace = "default";
        ServiceList list = client.services().inNamespace(namespace).list();
        Boolean containsTestService = false;
        for(Service service : list.getItems()) {
            if(service.getMetadata().getName().equals("test-app-service")){
                containsTestService = true;
            }
        }
        assertTrue(containsTestService);
    }

}
