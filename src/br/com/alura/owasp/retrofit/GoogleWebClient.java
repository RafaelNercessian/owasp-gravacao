package br.com.alura.owasp.retrofit;

import java.io.IOException;

import retrofit2.Call;

public class GoogleWebClient {

	public boolean verifica(String resposta) throws IOException {
		String secret="6LeF1DMUAAAAADlfRa9Y-BuroxGZMX31iSTkgglU";
		Call<Resposta> call = new RetrofitInicializador().getGoogleService().enviaToken(secret, resposta);
		return call.execute().body().isSuccess();
	}
}
