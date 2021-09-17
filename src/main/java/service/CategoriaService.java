package service;

import domain.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import repository.CategoriaRepository;
import service.exception.ObjectNotFounfException;

import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria find(Integer id) {
       Optional<Categoria> obj = categoriaRepository.findById(id);
       return obj.orElseThrow(() -> new ObjectNotFounfException(("" +
               "Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName())));
    }

}
