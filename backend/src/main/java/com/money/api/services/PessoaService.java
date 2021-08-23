package com.money.api.services;

import com.money.api.model.Pessoa;
import com.money.api.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;


    public Pessoa pessoaAtualizar(Long codigo, Pessoa pessoa) {

        Optional<Pessoa> pessoaSalva = buscarPessoaCodigo(codigo);

        BeanUtils.copyProperties(pessoa, pessoaSalva.get(), "codigo");

        return pessoaRepository.save(pessoaSalva.get());

    }

    public Optional<Pessoa> buscarPessoaCodigo(Long codigo) {
        Optional<Pessoa> pessoaSalva = pessoaRepository.findById(codigo);
        if (pessoaSalva.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }
        return pessoaSalva;
    }

    public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
        Optional<Pessoa> pessoaSalva = buscarPessoaCodigo(codigo);
        pessoaSalva.get().setAtivo(ativo);
        pessoaRepository.save(pessoaSalva.get());
    }
}
