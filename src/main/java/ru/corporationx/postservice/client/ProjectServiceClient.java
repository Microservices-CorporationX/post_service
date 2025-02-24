package ru.corporationx.postservice.client;


import ru.corporationx.postservice.dto.project.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}/${project-service.version}")
public interface ProjectServiceClient {

    @GetMapping("/projects/{projectId}")
    ProjectDto getProject(@PathVariable long projectId);

}
