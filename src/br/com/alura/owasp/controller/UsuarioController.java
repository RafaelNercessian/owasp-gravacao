package br.com.alura.owasp.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.alura.owasp.dao.UsuarioDao;
import br.com.alura.owasp.model.Role;
import br.com.alura.owasp.model.Usuario;
import br.com.alura.owasp.model.UsuarioDTO;
import br.com.alura.owasp.retrofit.GoogleWebClient;

@Controller
@Transactional
public class UsuarioController {

	@Autowired
	private UsuarioDao dao;

	@Autowired
	private GoogleWebClient cliente;

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
//		webDataBinder.setAllowedFields("email", "senha", "nome", "nomeImagem");
	}

	@RequestMapping("/usuario")
	public String usuario(Model model) {
		Usuario usuario = new Usuario();
		model.addAttribute("usuario", usuario);
		return "usuario";
	}

	@RequestMapping("/usuarioLogado")
	public String usuarioLogado() {
		return "usuarioLogado";
	}

	@RequestMapping(value = "/registrar", method = RequestMethod.POST)
	public String registrar(MultipartFile imagem,
			@ModelAttribute("usuarioRegistro") UsuarioDTO usuarioRegistroDTO,
			RedirectAttributes redirect, HttpServletRequest request,
			Model model, HttpSession session) throws IOException {

		Usuario usuarioRegistro = new UsuarioDTO().montaUsuario();
		
		boolean imagemEhValida = tratarImagem(imagem, usuarioRegistro, request);
		
		if(imagemEhValida){
			usuarioRegistro.getRoles().add(new Role("ROLE_USER"));

			dao.salva(usuarioRegistro);
			session.setAttribute("usuario", usuarioRegistro);
			model.addAttribute("usuario", usuarioRegistro);
			return "usuarioLogado";
		}
		
		redirect.addFlashAttribute("mensagem", "A imagem não é válida!");
		return "redirect:/usuario";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute("usuario") Usuario usuario,
			RedirectAttributes redirect, Model model, HttpSession session,
			HttpServletRequest request) throws IOException {

		String recaptcha = request.getParameter("g-recaptcha-response");

		// Primeira versão contra Mass assignment
		// Usuario usuario = new UsuarioDTO().montaUsuario();
		boolean verifica = cliente.verifica(recaptcha);

		if (verifica) {
			return pesquisaUsuario(usuario, redirect, model, session);
		}

		redirect.addFlashAttribute("mensagem",
				"Por favor, comprove que você é humano!");
		return "redirect:/usuario";

	}

	private String pesquisaUsuario(Usuario usuario,
			RedirectAttributes redirect, Model model, HttpSession session) {
		Usuario usuarioRetornado = dao.procuraUsuario(usuario);
		model.addAttribute("usuario", usuarioRetornado);
		if (usuarioRetornado == null) {
			redirect.addFlashAttribute("mensagem", "Usuário não encontrado");
			return "redirect:/usuario";
		}

		session.setAttribute("usuario", usuarioRetornado);
		return "usuarioLogado";
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("usuario");
		return "usuario";
	}

	private boolean tratarImagem(MultipartFile imagem, Usuario usuario,
			HttpServletRequest request) throws IOException {

		ByteArrayInputStream bytesImagem = new ByteArrayInputStream(
				imagem.getBytes());
		String mime = URLConnection
				.guessContentTypeFromStream(bytesImagem);
		if (mime == null) {
			return false;
		}

		usuario.setNomeImagem(imagem.getOriginalFilename());
		File arquivo = new File(request.getServletContext().getRealPath(
				"/image"), usuario.getNomeImagem());

		imagem.transferTo(arquivo);
		return true;
	}
}
