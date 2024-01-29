package com.sofiapilz.todosimple.controllers;

import com.sofiapilz.todosimple.models.Task;
import com.sofiapilz.todosimple.models.projection.TaskProjection;
import com.sofiapilz.todosimple.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/task")
@Validated
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        Task obj = this.taskService.findById(id);
        return ResponseEntity.ok(obj);
    }


    // retorna tds as tasks de um usuario
    @GetMapping("/user")
    public ResponseEntity<List<TaskProjection>> findAllByUser() {
        List<TaskProjection> objs = this.taskService.findAllByUser();
        return ResponseEntity.ok().body(objs);
    }

//    @PreAuthorize("ROLE_ADMIN")
//    public ResponseEntity<List<Task>> findAll() {
//        List<Task> objs = this.taskService.findById();
//        return ResponseEntity.ok(objs);
//    }


    @PostMapping
    @Validated
    public ResponseEntity<Void> create(@Valid @RequestBody Task obj) {
        this.taskService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody Task obj, @PathVariable Long id) {
        obj.setId(id);
        this.taskService.update(obj);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
