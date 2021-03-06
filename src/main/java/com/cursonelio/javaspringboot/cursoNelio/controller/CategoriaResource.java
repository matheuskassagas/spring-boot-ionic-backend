package com.cursonelio.javaspringboot.cursoNelio.controller;


import com.cursonelio.javaspringboot.cursoNelio.dto.Response.CategoriaResponse;
import com.cursonelio.javaspringboot.cursoNelio.dto.Response.ClienteResponse;
import com.cursonelio.javaspringboot.cursoNelio.repository.entity.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cursonelio.javaspringboot.cursoNelio.service.CategoriaService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController//classe controladora
@RequestMapping(value="/categorias")//nome do end point rest
public class CategoriaResource {

    @Autowired
    private CategoriaService service;

    //simples requisicao GET - verbo http obtendo dados (pegando dados)
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ResponseEntity<?> find(@PathVariable Integer id){
        Categoria obj = service.find(id);
        return ResponseEntity.ok().body(obj);
    }

    @RequestMapping(value = "/categoria/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findCategoria (@PathVariable Integer id){
        CategoriaResponse obj = service.findCategoriaId(id);
        return ResponseEntity.ok().body(obj);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CategoriaResponse>> findAll(){
        return ResponseEntity.ok().body(service.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@Valid @RequestBody Categoria categoria){
        categoria = service.create(categoria);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(categoria.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@Valid @PathVariable Integer id, @RequestBody Categoria categoria){
        categoria.setId(id);
        service.update(categoria);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Integer id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public  ResponseEntity<Page<CategoriaResponse>> findPage(
            @RequestParam(value="page", defaultValue="0") Integer page,
            @RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage,
            @RequestParam(value="orderBy", defaultValue ="nome") String orderBy,
            @RequestParam(value="direction", defaultValue ="ASC") String direction){
        Page<Categoria> list = service.findPage(page, linesPerPage, orderBy, direction);
        Page<CategoriaResponse> listDTO = list.map(obj -> new CategoriaResponse(obj));
        return ResponseEntity.ok().body(listDTO);
    }

}
