package com.cursonelio.javaspringboot.cursoNelio.service;


import com.cursonelio.javaspringboot.cursoNelio.dto.Response.PedidoResponse;
import com.cursonelio.javaspringboot.cursoNelio.repository.*;
import com.cursonelio.javaspringboot.cursoNelio.repository.entity.*;
import com.cursonelio.javaspringboot.cursoNelio.repository.entity.enuns.EstadoPagamento;
import com.cursonelio.javaspringboot.cursoNelio.security.UserSS;
import com.cursonelio.javaspringboot.cursoNelio.service.EmailService.EmailService;
import com.cursonelio.javaspringboot.cursoNelio.service.exception.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.cursonelio.javaspringboot.cursoNelio.service.exception.ObjectNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private BoletoService boletoService;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmailService emailService;

    public Pedido create (Pedido obj){
        obj.setId(null);
        obj.setInstante(new Date());
        obj.setCliente(clienteRepository.findById(obj.getCliente().getId()).get());
        obj.getPagamento().setEstadoPagamento(EstadoPagamento.PENDENTE);
        obj.getPagamento().setPedido(obj);
        if (obj.getPagamento() instanceof PagamentoComBoleto){ //if my class pagamento it will be pagamentoComBoleto, do it....
            PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
            boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
        }
        obj = repository.save(obj);
        pagamentoRepository.save(obj.getPagamento());
        for (ItemPedido ip : obj.getItens()){
            ip.setDesconto(0.0);
            ip.setProduto(produtoRepository.findById(ip.getProduto().getId()).get());
            ip.setPreco(ip.getProduto().getPreco());
            ip.setPedido(obj);
        }
        itemPedidoRepository.saveAll(obj.getItens());
        emailService.sendOrderConfirmationHtmlEmail(obj);
        //emailService.sendOrderConfimationEmail(obj);
        return obj;
    }

    @Transactional(readOnly = true)
    public Pedido find (Integer id){
        Optional<Pedido> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(("" +
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName())));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findAll(){
        return repository.findAll().stream().map(pedido -> new PedidoResponse().toResponse(pedido)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponse findPedidoId (Integer id){
        Optional<Pedido> pedido = repository.findById(id);
        return new PedidoResponse().toResponse(pedido.get());
    }

    @Transactional(readOnly = true)
    public Page<Pedido> findPage (Integer page, Integer linesPerPage, String orderBy, String direction){
        UserSS user = UserService.authenticated();
        if (user == null) {
            throw new AuthorizationException("Acesso Negado");
        }
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
           Cliente cliente = clienteRepository.findById(user.getId()).get();
           return repository.findByCliente(cliente, pageRequest);
        }
    }

