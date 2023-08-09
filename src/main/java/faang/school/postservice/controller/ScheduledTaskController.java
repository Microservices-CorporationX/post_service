package faang.school.postservice.controller;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.scheduledexecutor.ScheduledTaskExecutor;
import faang.school.postservice.util.validator.ScheduledTaskControllerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduled")
@Slf4j
public class ScheduledTaskController {

    private final Map<List<String>, ScheduledTaskExecutor> scheduledTaskExecutors;
    private final ScheduledTaskControllerValidator validator;

    @PostMapping("/")
    void addTaskBySchedule(@Valid @RequestBody ScheduledTaskDto dto) {
        log.info("Entity type: {}, task type: {}", dto.entityType(), dto.taskType());

        validator.addTaskBySchedule(scheduledTaskExecutors, dto);

        var scheduledTaskExecutor = scheduledTaskExecutors.get(
                List.of(dto.entityType().toString(), dto.taskType().toString())
        );

        scheduledTaskExecutor.execute(dto);
    }
}
