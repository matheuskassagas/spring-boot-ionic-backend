package com.cursonelio.javaspringboot.cursoNelio.controller;

import com.cursonelio.javaspringboot.cursoNelio.controller.util.URL;
import com.cursonelio.javaspringboot.cursoNelio.dto.Response.CategoriaResponse;
import com.cursonelio.javaspringboot.cursoNelio.dto.Response.PedidoResponse;
import com.cursonelio.javaspringboot.cursoNelio.dto.Response.ProdutoResponse;
import com.cursonelio.javaspringboot.cursoNelio.repository.ProdutoRepository;
import com.cursonelio.javaspringboot.cursoNelio.repository.entity.Categoria;
import com.cursonelio.javaspringboot.cursoNelio.repository.entity.Pedido;
import com.cursonelio.javaspringboot.cursoNelio.repository.entity.Produto;
import com.cursonelio.javaspringboot.cursoNelio.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/produtos")
public class ProdutoResource {

    @Autowired
    private ProdutoService service;


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> find(@PathVariable Integer id){
        Produto obj = service.find(id);
        return ResponseEntity.ok().body(obj);
    }

    @RequestMapping(method = RequestMethod.GET)
    public  ResponseEntity<Page<ProdutoResponse>> findPage(
            @RequestParam(value="nome", defaultValue="") String nome,
            @RequestParam(value="categorias", defaultValue="") String categorias,
            @RequestParam(value="page", defaultValue="0") Integer page,
            @RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage,
            @RequestParam(value="orderBy", defaultValue ="nome") String orderBy,
            @RequestParam(value="direction", defaultValue ="ASC") String direction){
        String nomeDecode = URL.decodeParam(nome);
        List<Integer> ids = URL.decodeIntList(categorias);
        Page<Produto> list = service.search(nomeDecode, ids, page, linesPerPage, orderBy, direction);
        Page<ProdutoResponse> listDTO = list.map(obj -> new ProdutoResponse(obj));
        return ResponseEntity.ok().body(listDTO);
    }
}
