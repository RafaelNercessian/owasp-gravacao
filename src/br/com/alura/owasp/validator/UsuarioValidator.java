package br.com.alura.owasp.validator;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.com.alura.owasp.model.Usuario;

public class UsuarioValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Usuario.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Usuario usuario = (Usuario) target;
		
		try {
			ImageIO.read(usuario.getImagem().getInputStream()).toString();
		} catch (Exception e) {
			errors.rejectValue("imagem", "erro.imagem");
		}
	}

}
