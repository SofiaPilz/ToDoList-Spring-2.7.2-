package com.sofiapilz.todosimple.services;

import com.sofiapilz.todosimple.models.User;
import com.sofiapilz.todosimple.repositories.TaskRepository;
import com.sofiapilz.todosimple.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new RuntimeException
                ("Usuário não encontrado! Id:" + id + ", Tipo: " + User.class.getName()));
    }

    @Transactional
    public User create(User obj) {
        // garante q vai ser criado um novo id
        obj.setId(null);
        obj = this.userRepository.save(obj);
        this.taskRepository.saveAll(obj.getTasks());
        return obj;
    }

    @Transactional
    public User update(User obj) {
        User newobj = findById(obj.getId());
        newobj.setPassword(obj.getPassword());
        return this.userRepository.save(newobj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possícel excluir, pois há entidades relacionadas!");
        }


    }
}
