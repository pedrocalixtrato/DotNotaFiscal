package br.com.pdv.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.pdv.Enum.FormaPagamento;
import br.com.pdv.Enum.StatusPedido;


@Entity
public class Pedido implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Date dataCriacao;
	private String observacao;
	private Date dataEntrega;
	private BigDecimal valorFrete = BigDecimal.ZERO;
	private BigDecimal valorDesconto = BigDecimal.ZERO;
	private BigDecimal valorTotal = BigDecimal.ZERO;
	private StatusPedido status = StatusPedido.ORCAMENTO;
	private FormaPagamento formaPagamento;	
	private Clientes cliente ;	
	private Usuario vendedor;
	private EnderecoEntrega enderecoEntrega;
	private List<ItemPedido> itens = new ArrayList<>();
	
	@Id		
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	@Temporal(TemporalType.DATE)
	public Date getDataEntrega() {
		return dataEntrega;
	}
	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}
	
	public BigDecimal getValorFrete() {
		return valorFrete;
	}
	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}
	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	@Enumerated(EnumType.STRING)
	public StatusPedido getStatus() {
		return status;
	}
	public void setStatus(StatusPedido status) {
		this.status = status;
	}
	@Enumerated(EnumType.STRING)
	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}
	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
	
	
	@ManyToOne
	@JoinColumn(name = "cliente_id", nullable = false)
	public Clientes getCliente() {
		return cliente;
	}
	public void setCliente(Clientes cliente) {
		this.cliente = cliente;
	}
	
	@ManyToOne
	@JoinColumn(name = "vendedor_id")
	public Usuario getVendedor() {
		return vendedor;
	}

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY )
	public List<ItemPedido> getItens() {
		return itens;
	}
	public void setItens(List<ItemPedido> itens) {
		this.itens = itens;
	}
	
	public void setVendedor(Usuario vendedor) {
		this.vendedor = vendedor;
	}

	@Embedded
	public EnderecoEntrega getEnderecoEntrega() {
		return enderecoEntrega;
	}
	public void setEnderecoEntrega(EnderecoEntrega enderecoEntrega) {
		this.enderecoEntrega = enderecoEntrega;
	}
	
		
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pedido other = (Pedido) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	@Transient
	public boolean isNovo() {
		return getDataCriacao() == null;
	}
	
	@Transient
	public boolean isExistente() {
		return !isNovo();
	}
	@Transient
	public BigDecimal getValorSubTotal(){
		
		return this.getValorTotal().subtract(this.getValorFrete().add(this.getValorDesconto()));
		
	}
	
	public void recalcularValorTotal() {
		BigDecimal total = BigDecimal.ZERO;
		
		total = total.add(this.getValorFrete()).subtract(this.getValorDesconto());
		
		for(ItemPedido item : this.getItens()){
			if(item.getProduto() != null && item.getProduto().getCodigo() != null)
			total = total.add(item.getValorTotal());
		}
		
		this.setValorTotal(total);
	}
	public void adicionarItemVazio() {
		if(this.isOrcamento()){		
		Produto produto = new Produto();
		
		
		ItemPedido item = new ItemPedido();		
		item.setProduto(produto);		
		item.setPedido(this);
		this.getItens().add(0, item);
		}
	}
	
	@Transient
	public boolean isOrcamento(){
		return StatusPedido.ORCAMENTO.equals(this.getStatus());
	}

}
