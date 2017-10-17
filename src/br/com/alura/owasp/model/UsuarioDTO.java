package br.com.alura.owasp.model;

import org.springframework.web.multipart.MultipartFile;

public class UsuarioDTO {

	private String email;
	private String senha;
	private String nome;
	private MultipartFile imagem;
	private String nomeImagem;
	
	public void setEmail(String email) {
		this.email = email;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public MultipartFile getImagem() {
		return imagem;
	}

	public void setImagem(MultipartFile imagem) {
		this.imagem = imagem;
	}

	public void setNomeImagem(String nomeImagem) {
		this.nomeImagem = nomeImagem;
	}

	public Usuario montaUsuario() {
		Usuario usuario = new Usuario(email,nome,imagem,senha,nomeImagem);
		return usuario;
	}
}
