package com.money.api.controller;

import com.money.api.event.RecursoCriadoEvent;
import com.money.api.model.Lancamento;
import com.money.api.repository.LancamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    @Autowired
    private LancamentoRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    public List<Lancamento> listar(){
        return repository.findAll();
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Optional<Lancamento>> buscarPeloCodigo(@PathVariable Long codigo) {
        Optional<Lancamento> lancamento = repository.findById(codigo);
        return lancamento != null ? ResponseEntity.ok(lancamento) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
        Lancamento lancamentoSalvo = repository.save(lancamento);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
    }
}
