package com.example.application.service;

import com.example.application.data.entity.Person;
import com.example.application.data.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudService;

public class PersonService extends CrudService<Person, Integer> {

private PersonRepository repository;

public PersonService(@Autowired PersonRepository repository) {
        this.repository = repository;
        }

@Override
protected PersonRepository getRepository() {
        return repository;
        }
}
