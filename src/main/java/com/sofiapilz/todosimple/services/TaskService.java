package com.sofiapilz.todosimple.services;

import com.sofiapilz.todosimple.models.Task;
import com.sofiapilz.todosimple.models.User;
import com.sofiapilz.todosimple.models.enums.ProfileEnum;
import com.sofiapilz.todosimple.models.projection.TaskProjection;
import com.sofiapilz.todosimple.repositories.TaskRepository;
import com.sofiapilz.todosimple.security.UserSpringSecurity;
import com.sofiapilz.todosimple.services.exceptions.AuthorizationException;
import com.sofiapilz.todosimple.services.exceptions.DataBindingViolationExceptions;
import com.sofiapilz.todosimple.services.exceptions.ObjectNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Tarefa não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity
                .hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
            throw  new AuthorizationException("Acesso negado!");

        return task;
    }

    // etorna tds as tasks
    public List<TaskProjection> findAllByUser() {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");

        List<TaskProjection> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }


    @Transactional
    public Task create(Task obj) {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");

        // confere se o user existe
        User user = this.userService.findById(userSpringSecurity.getId()    );
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

    // n precisaria necessariamente pq a Task n eh forenkey em nenhum lugar, n eh q nem no User
    // pq se tu apagar o User sem deletar as Tasks dele da erro
    // entao nnc vai cair no catch pq nenhuma outra entidade do sistema depende de Task
    // mas Task depende do User
    public void delete(Long id) {
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationExceptions("Não é possícel excluir, pois há entidades relacionadas!");
        }
    }

    //verificacao do user da task e do user logado
    private boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}
