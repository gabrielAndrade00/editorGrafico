package comunicacao;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;

import database.DesenhoDAO;
import database.IDesenhoDAO;

public class SupervisoraDeConexao extends Thread {
    private double valor = 0;

    private Parceiro cliente;
    private Socket conexao;

    private ArrayList<Parceiro> clientes;

    private IDesenhoDAO desenhoDAO;

    public SupervisoraDeConexao (Socket conexao, ArrayList<Parceiro> clientes) throws Exception {
        if (conexao == null)
            throw new Exception ("Conexão ausente");

        if (clientes == null)
            throw new Exception ("Clientes ausentes");

        this.conexao  = conexao;
        this.clientes = clientes;

        this.desenhoDAO = new DesenhoDAO();
    }

    public void run() {
        ObjectOutputStream transmissor;

        try {
            transmissor = new ObjectOutputStream(this.conexao.getOutputStream());
        }
        catch (Exception erro) {
            return;
        }
        
        ObjectInputStream receptor = null;

        try {
            receptor = new ObjectInputStream(this.conexao.getInputStream());
        }
        catch (Exception err0) {
            try {
                transmissor.close();
            }
            catch (Exception falha) {} // so tentando fechar antes de acabar a thread
            
            return;
        }

        try {
            this.cliente = new Parceiro(this.conexao, receptor, transmissor);
        }
        catch (Exception erro) {} // sei que passei os parametros corretos

        try {
            synchronized(this.clientes) {
                this.clientes.add(this.cliente);
                System.out.println("Novo cliente conectado!");
            }

            for(;;) {
                Comunicado comunicado = null;

                if (this.cliente != null) {
                    comunicado = this.cliente.envie();
                }
                else {
                    continue;
                }

                if (comunicado == null)
                    return;

                else if (comunicado instanceof PedidoDesenhos) {
                    System.out.println("Pedido de desenhos recebido do cliente " + cliente.getIp());

                    List<database.Desenho> desenhos = new ArrayList<database.Desenho>();

                    desenhos = desenhoDAO.buscarDesenhos();

                    Desenhos comunicadoDesenhos = new Desenhos();

                    for (database.Desenho desenhoDb : desenhos) {
                        Desenho desenho = new Desenho(desenhoDb.getNome(), desenhoDb.getIpAtualizacao(), desenhoDb.getDataUltimaAtualizacao(), desenhoDb.getDataCriacao());

                        for (String figura : desenhoDb.getFiguras()) {
                            desenho.addFigura(figura);
                        }

                        comunicadoDesenhos.addDesenho(desenho);
                    }

                    cliente.receba(comunicadoDesenhos);
                }
                else if (comunicado instanceof PedidoSalvamento) {
                    System.out.println("Pedido de salvamento recebido do cliente " + cliente.getIp());

                    PedidoSalvamento pedidoSalvamento = (PedidoSalvamento)comunicado;
                    
                    database.Desenho desenho = new database.Desenho();

                    desenho.setIpAtualizacao(cliente.getIp());
                    desenho.setNome(pedidoSalvamento.getDesenho().getNome());
                    desenho.setFiguras(pedidoSalvamento.getDesenho().getFiguras());
                    desenho.setDataCriacao(LocalDateTime.now());
                    desenho.setDataUltimaAtualizacao(LocalDateTime.now());

                    System.out.println("Conteúdo do desenho '" + pedidoSalvamento.getDesenho().getNome() + "'");
                        
                    for (String figura : desenho.getFiguras()) {
                        System.out.println(figura);
                    }

                    desenhoDAO.salvarDesenho(desenho);

                    System.out.println("O desenho '" + pedidoSalvamento.getDesenho().getNome() + " foi salvo com sucesso");
                }
                else if (comunicado instanceof EncerrarConexao) {
                    synchronized (this.clientes) {
                        this.clientes.remove(this.cliente);
                    }

                    this.cliente.adeus();
                    System.out.println("Cliente desconectado!");

                    this.cliente = null;
                }
            }
        }
        catch (Exception erro) {
            try {
                transmissor.close();
                receptor.close();
                System.out.println("Erro!");
            }
            catch (Exception falha) {} // so tentando fechar antes de acabar a thread

            return;
        }
    }
}
