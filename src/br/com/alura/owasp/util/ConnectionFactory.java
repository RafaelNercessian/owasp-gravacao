package br.com.alura.owasp.util;

import java.sql.Connection;
import java.sql.DriverManager;

//Essa classe não será mais usada depois da primeira aula
public class ConnectionFactory {

	public Connection getConnection() {
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost/owasp?createDatabaseIfNotExist=true",
					"root", "");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
