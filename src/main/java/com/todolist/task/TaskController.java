package com.todolist.task;

import com.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/create")
    private ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        var userId = request.getAttribute("userId");
        taskModel.setUserId((UUID) userId);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio/final da task deve ser maior que a data atual");
        }

        var taskCreated = taskRepository.save(taskModel);
        return  ResponseEntity.status(201).body(taskCreated);

    }

@GetMapping("getByUser")
    public List<TaskModel> list(HttpServletRequest request){
        var userId = request.getAttribute("userId");
    return taskRepository.findByUserId((UUID) userId);
    }

    @PutMapping( "/update/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        var userId = request.getAttribute("userId");

        var task = taskRepository.findById(id).orElse(null);

        if(task == null){
            return ResponseEntity.status(404).body("Tarefa nao encontrada");
        }

        if(!task.getUserId().equals(userId)){
            return ResponseEntity.status(400).body("Usuario nao tem permissão para editar a tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);
        var taskUpd = taskRepository.save(task);

        return ResponseEntity.status(200).body(taskUpd);
    }
}
