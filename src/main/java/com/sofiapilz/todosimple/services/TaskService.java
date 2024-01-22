package com.sofiapilz.todosimple.services;

import com.sofiapilz.todosimple.models.Task;
import com.sofiapilz.todosimple.models.User;
import com.sofiapilz.todosimple.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException
                ("Tarefa não encontrada! id:" + id + ", Tipo: " + Task.class.getName()));
    }

    // etorna tds as tasks
    public List<Task> findAllByUserId(Long userId) {
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
    }


    @Transactional
    public Task create(Task obj) {
        // confere se o user existe
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj) {
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }


    public void delete(Long id) {
        findById(id);
        // n precisaria necessariamente pq a Task n eh forenkey em nenhum lugar, n eh q nem no User
        // pq se tu apagar o User sem deletar as Tasks dele da erro
        // entao nnc vai cair no catch pq nenhuma outra entidade do sistema depende de Task
        // mas Task depende do User
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possícel excluir, pois há entidades relacionadas!");
        }


    }
}
