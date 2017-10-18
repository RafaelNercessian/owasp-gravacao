package br.com.alura.owasp.retrofit;

import org.springframework.stereotype.Component;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

@Component
public interface GoogleService {

	@POST("siteverify")
	Call<Resposta> enviaToken(@Query("secret") String secret,
			@Query("response") String resposta);

}
