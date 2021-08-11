package com.money.api.controller;

import com.money.api.event.RecursoCriadoEvent;
import com.money.api.model.Pessoa;
import com.money.api.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping
    public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
        Pessoa pessoaSalva = pessoaRepository.save(pessoa);

        publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));

        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Pessoa> buscar(@PathVariable Long codigo){
        Optional<Pessoa> categoria = pessoaRepository.findById(codigo);
        if (categoria.isPresent()){
            return ResponseEntity.ok().body(categoria.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long codigo){
        pessoaRepository.deleteById(codigo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa){
        Optional<Pessoa> pessoaSalva = pessoaRepository.findById(codigo);

        if(pessoaSalva.isEmpty()){
            throw new EmptyResultDataAccessException(1);
        }

        if (pessoaSalva.isPresent()){
            BeanUtils.copyProperties(pessoa, pessoaSalva.get(), "codigo");

            Pessoa pessoaAtual = pessoaRepository.save(pessoaSalva.get());
            return ResponseEntity.ok(pessoaSalva.get());
        }
        return ResponseEntity.badRequest().build();

    }
}
