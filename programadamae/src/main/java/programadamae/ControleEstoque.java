package programadamae;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ControleEstoque {
    public void conectarBanco() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gestao.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM estoque")) {

            // Se a conexão foi bem-sucedida
            System.out.println("Conexão com o banco de dados estabelecida.");

            while (rs.next()) {
                // Exemplo de manipulação de dados
                System.out.println(rs.getString("produto"));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
}
