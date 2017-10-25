package org.opentosca.toscana.core.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opentosca.toscana.core.transformation.artifacts.ArtifactManagementService;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.Relation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/artifacts")
public class ArtifactController {

    @Value("${toscana.mappings.enable-artifact-list}")
    public boolean enableArtifactList;

    private final ArtifactManagementService ams;

    @Autowired
    public ArtifactController(ArtifactManagementService ams) {
        this.ams = ams;
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET, produces = "application/hal+json")
    public ResponseEntity<Resources<FileResource>> listFiles(HttpServletRequest request) throws Exception {
        if (!enableArtifactList) {
            throw new IllegalAccessException("This operation has been disabled by the administrator");
        }
        List<FileResource> resources = new ArrayList<>();
        for (File file : ams.getArtifactDir().listFiles()) {
            if (file.isFile()) {
                resources.add(
                    new FileResource(
                        file.getName(),
                        file.length(),
                        request.getRequestURL()
                            .toString()
                            .replace("/artifacts/", "")
                            .replace("/artifacts", "")
                    )
                );
            }
        }
        Resources<FileResource> res = new Resources<>(resources);
        return ResponseEntity.ok(res);
    }

    @RequestMapping(value = "/{filename:.+}", method = RequestMethod.GET)
    public void getArtifact(
        @PathVariable("filename") String filename,
        HttpServletResponse response
    ) throws IOException {
        File f = new File(ams.getArtifactDir(), filename);
        if (!f.exists()) {
            throw new FileNotFoundException(f.getName() + " not found!");
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"");
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Length", f.length() + "");

        FileInputStream in = new FileInputStream(f);
        OutputStream out = response.getOutputStream();

        int read = 0;
        byte[] buff = new byte[8192];
        while ((read = in.read(buff)) != -1) {
            out.write(buff, 0, read);
        }
        out.flush();
        in.close();
        out.close();
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN,
        reason = "Operation has been disabled by the administrator")
    @ExceptionHandler(IllegalAccessException.class)
    public void handleDisabledList() {

    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND,
        reason = "File Not Found")
    @ExceptionHandler(FileNotFoundException.class)
    public void handleNotFoundList() {

    }

    @Relation(collectionRelation = "file")
    public static class FileResource extends ResourceSupport {
        private String filename;
        private Long filesize;

        public FileResource(
            @JsonProperty("filename") String filename,
            @JsonProperty("length") Long filesize,
            String baseUrl
        ) {
            this.filename = filename;
            this.filesize = filesize;
            add(new Link(baseUrl + "/artifacts/" + filename, "self"));
        }

        @JsonProperty("filename")
        public String getFilename() {
            return filename;
        }

        @JsonProperty("length")
        public Long getFilesize() {
            return filesize;
        }
    }
}
