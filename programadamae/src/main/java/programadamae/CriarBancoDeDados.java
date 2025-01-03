package programadamae;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CriarBancoDeDados {
    private static final String DATABASE_NAME = "gestao.db";
    private static final String DATABASE_PATH = System.getProperty("user.home") + File.separator + "SistemaMae" + File.separator + DATABASE_NAME;

    public static void main(String[] args) {
        criarDiretorioBanco();
        criarBancoDeDados();
    }

    private static void criarDiretorioBanco() {
        try {
            File dir = new File(System.getProperty("user.home") + File.separator + "SistemaMae");
            if (!dir.exists()) {
                Files.createDirectories(dir.toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void criarBancoDeDados() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute("CREATE TABLE IF NOT EXISTS compromissos (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT NOT NULL, hora TEXT NOT NULL, descricao TEXT NOT NULL)");
                stmt.execute("CREATE TABLE IF NOT EXISTS pacientes (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, idade INTEGER NOT NULL, endereco TEXT NOT NULL, descricao TEXT)");
                stmt.execute("CREATE TABLE IF NOT EXISTS financeiro (id INTEGER PRIMARY KEY AUTOINCREMENT, tipo TEXT NOT NULL, valor REAL NOT NULL, data TEXT NOT NULL)");
                stmt.execute("CREATE TABLE IF NOT EXISTS estoque (id INTEGER PRIMARY KEY AUTOINCREMENT, produto TEXT NOT NULL, quantidade INTEGER NOT NULL)");
                System.out.println("Banco de dados criado com sucesso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}