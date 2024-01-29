package com.sofiapilz.todosimple.services;

import com.sofiapilz.todosimple.models.User;
import com.sofiapilz.todosimple.models.dto.UserCreateDTO;
import com.sofiapilz.todosimple.models.dto.UserUpdateDTO;
import com.sofiapilz.todosimple.models.enums.ProfileEnum;
import com.sofiapilz.todosimple.repositories.UserRepository;
import com.sofiapilz.todosimple.security.UserSpringSecurity;
import com.sofiapilz.todosimple.services.exceptions.AuthorizationException;
import com.sofiapilz.todosimple.services.exceptions.DataBindingViolationExceptions;
import com.sofiapilz.todosimple.services.exceptions.ObjectNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    // salva a senha do usuario ja criptografada

    @Autowired
    private UserRepository userRepository;


    //verificaçao antes d buscar o usuario
    //so passa se tiver buscando o proprio id ou adm
    //se o id q esta sendo pesquisado n eh o ms do q esta logado, n ai poder pesquisar
    public User findById(Long id) {
        UserSpringSecurity userSpringSecurity = authenticated();
        if (!Objects.nonNull(userSpringSecurity)
                || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId()))
            throw new AuthorizationException("Acesso negado!");

        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
                "Usuário não encontrado! Id:" + id + ", Tipo: " + User.class.getName()));
    }

    @Transactional
    public User create(User obj) {
        obj.setId(null); // garante q vai ser criado um novo id
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword())); //momento q criptografa a senha e salva
        //lista pra garantir q o usuario será criado com o código de numero 2 (normal)
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet())); //garante q td usuario criado sera do tipo 2
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj) {
        User newobj = findById(obj.getId());
        newobj.setPassword(obj.getPassword());
        newobj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword())); //momento q criptografa a senha e salva
        return this.userRepository.save(newobj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationExceptions("Não é possícel excluir, pois há entidades relacionadas!");
        }
    }

    public static UserSpringSecurity authenticated() {
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    public User fromDTO(@Valid UserCreateDTO obj) {
        User user = new User();
        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());
        return user;
    }

    public User fromDTO(@Valid UserUpdateDTO obj) {
        User user = new User();
        user.setId(obj.getId());
        user.setPassword(obj.getPassword());
        return user;
    }


}
