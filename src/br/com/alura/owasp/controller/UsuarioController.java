package br.com.alura.owasp.controller;

import java.io.File;
import java.io.IOException;

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
import org.springframework.web.context.request.WebRequest;
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

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder, WebRequest request) {
		webDataBinder.setAllowedFields("email", "senha", "nome", "imagem",
				"nomeImagem");
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
	public String registrar(
			@ModelAttribute("usuarioRegistro") Usuario usuarioRegistro,
			RedirectAttributes redirect, HttpServletRequest request,
			Model model, HttpSession session) {

		tratarImagem(usuarioRegistro, request);
		usuarioRegistro.getRoles().add(new Role("ROLE_USER"));

		dao.salva(usuarioRegistro);
		session.setAttribute("usuario", usuarioRegistro);
		model.addAttribute("usuario", usuarioRegistro);
		return "usuarioLogado";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute("usuario") Usuario usuario,
			RedirectAttributes redirect, Model model, HttpSession session,
			HttpServletRequest request) throws IOException {

		String recaptcha = request.getParameter("g-recaptcha-response");

		//Primeira vers�o contra Mass assignment
		//Usuario usuario = new UsuarioDTO().montaUsuario();
		boolean verifica = new GoogleWebClient().verifica(recaptcha);

		if (verifica) {
			return pesquisaUsuario(usuario, redirect, model, session);
		}

		redirect.addFlashAttribute("mensagem",
				"Por favor, comprove que voc� � humano!");
		return "redirect:/usuario";

	}

	private String pesquisaUsuario(Usuario usuario,
			RedirectAttributes redirect, Model model, HttpSession session) {
		Usuario usuarioRetornado = dao.procuraUsuario(usuario);
		model.addAttribute("usuario", usuarioRetornado);
		if (usuarioRetornado == null) {
			redirect.addFlashAttribute("mensagem", "Usu�rio n�o encontrado");
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

	private void tratarImagem(Usuario usuario, HttpServletRequest request) {
		usuario.setNomeImagem(usuario.getImagem().getOriginalFilename());
		File arquivo = new File(request.getServletContext().getRealPath(
				"/image"), usuario.getNomeImagem());
		try {
			usuario.getImagem().transferTo(arquivo);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
