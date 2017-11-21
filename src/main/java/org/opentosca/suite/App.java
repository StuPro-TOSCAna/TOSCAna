package org.opentosca.suite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main() throws FileNotFoundException {
        String stream = App.class.getClassLoader().getResource("resource.yaml").getFile();
        DefaultKubernetesClient client = new DefaultKubernetesClient();
        String namespace = client.getNamespace();
        System.out.println("Namespace: " + namespace);
        List<HasMetadata> refreshed = client.load(new FileInputStream(stream)).get();
        System.out.println(refreshed.size());
        for(HasMetadata metadata : refreshed) {
            if(metadata instanceof Service) {
                Service service = (Service) metadata;
                System.out.println(service.getMetadata().getName());
                client.services().inNamespace("default").create(service);
            }
        }
        System.out.println( "Hello World!" );
    }
}
