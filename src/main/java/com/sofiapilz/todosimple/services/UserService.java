package com.sofiapilz.todosimple.services;

import com.sofiapilz.todosimple.models.User;
import com.sofiapilz.todosimple.models.enums.ProfileEnum;
import com.sofiapilz.todosimple.repositories.UserRepository;
import com.sofiapilz.todosimple.security.UserSpringSecurity;
import com.sofiapilz.todosimple.services.exceptions.DataBindingViolationExceptions;
import com.sofiapilz.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User findById(Long id) {
        UserSpringSecurity userSpringSecurity = authenticated();


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

    public UserSpringSecurity authenticated() {
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

}
