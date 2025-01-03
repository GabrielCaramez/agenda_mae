package programadamae;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;

public class SistemaGestao {
    private static final String DATABASE_NAME = "gestao.db";
    private static final String DATABASE_PATH = System.getProperty("user.home") + File.separator + "SistemaMae" + File.separator + DATABASE_NAME;
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            aplicarLookAndFeel();
            inicializarBancoDeDados();
            mostrarTelaLogin();
        });
    }

    private static void criarDiretorioBanco() {
        try {
            File dir = new File(System.getProperty("user.home") + File.separator + "SistemaMae");
            if (!dir.exists()) {
                Files.createDirectories(dir.toPath());
                System.out.println("Diretório do banco de dados criado: " + dir.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao criar o diretório do banco de dados.");
        }
    }

    private static void inicializarBancoDeDados() {
        criarDiretorioBanco();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                // Criação da tabela de usuários
                stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username TEXT NOT NULL, " +
                        "password TEXT NOT NULL)");

                // Criação da tabela Agenda
                stmt.execute("CREATE TABLE IF NOT EXISTS compromissos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "data TEXT NOT NULL, " +
                        "hora TEXT NOT NULL, " +
                        "descricao TEXT NOT NULL)");

                // Criação da tabela Prontuário
                stmt.execute("CREATE TABLE IF NOT EXISTS pacientes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nome TEXT NOT NULL, " +
                        "idade INTEGER NOT NULL, " +
                        "endereco TEXT NOT NULL, " +
                        "descricao TEXT)");

                // Criação da tabela Financeiro
                stmt.execute("CREATE TABLE IF NOT EXISTS financeiro (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "tipo TEXT NOT NULL, " + // 'Receita' ou 'Despesa'
                        "valor REAL NOT NULL, " +
                        "data TEXT NOT NULL)");

                // Criação da tabela Estoque
                stmt.execute("CREATE TABLE IF NOT EXISTS estoque (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "produto TEXT NOT NULL, " +
                        "quantidade INTEGER NOT NULL)");

                System.out.println("Banco de dados inicializado com sucesso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarTelaLogin() {
        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Login");
            loginFrame.setSize(300, 200);
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLayout(new GridLayout(4, 2));

            JLabel userLabel = new JLabel("Usuário:");
            JTextField userField = new JTextField();
            JLabel passLabel = new JLabel("Senha:");
            JPasswordField passField = new JPasswordField();
            JButton loginButton = new JButton("Login");
            JButton createAccountButton = new JButton("Criar Conta");

            loginButton.addActionListener(e -> {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                if (autenticarUsuario(username, password)) {
                    loginFrame.dispose();
                    mostrarTelaPrincipal();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Usuário ou senha inválidos.");
                }
            });

            createAccountButton.addActionListener(e -> {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                if (criarConta(username, password)) {
                    JOptionPane.showMessageDialog(loginFrame, "Conta criada com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Erro ao criar conta.");
                }
            });

            loginFrame.add(userLabel);
            loginFrame.add(userField);
            loginFrame.add(passLabel);
            loginFrame.add(passField);
            loginFrame.add(loginButton);
            loginFrame.add(createAccountButton);

            loginFrame.setVisible(true);
        });
    }

    private static boolean autenticarUsuario(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usuarios WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean criarConta(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuarios (username, password) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void mostrarTelaPrincipal() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Gestão");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JMenuBar menuBar = new JMenuBar();

            JMenu menuAgenda = new JMenu("Agenda");
            JMenu menuProntuario = new JMenu("Prontuário");
            JMenu menuFinanceiro = new JMenu("Financeiro");
            JMenu menuEstoque = new JMenu("Estoque");

            JMenuItem itemAgendar = new JMenuItem("Gerenciar Agenda");
            JMenuItem itemProntuario = new JMenuItem("Gerenciar Prontuário");
            JMenuItem itemFinanceiro = new JMenuItem("Gerenciar Financeiro");
            JMenuItem itemEstoque = new JMenuItem("Gerenciar Estoque");

            menuAgenda.add(itemAgendar);
            menuProntuario.add(itemProntuario);
            menuFinanceiro.add(itemFinanceiro);
            menuEstoque.add(itemEstoque);

            menuBar.add(menuAgenda);
            menuBar.add(menuProntuario);
            menuBar.add(menuFinanceiro);
            menuBar.add(menuEstoque);

            frame.setJMenuBar(menuBar);

            itemAgendar.addActionListener(e -> new TelaAgenda().setVisible(true));
            itemProntuario.addActionListener(e -> new TelaProntuario().setVisible(true));
            itemFinanceiro.addActionListener(e -> new TelaFinanceiro().setVisible(true));
            itemEstoque.addActionListener(e -> new TelaEstoque().setVisible(true));

            frame.setVisible(true);
        });
    }

    private static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void carregarDados(String tabela, DefaultTableModel modelo) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabela)) {
            while (rs.next()) {
                int columnCount = rs.getMetaData().getColumnCount();
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                modelo.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void abrirFormulario(String tabela, DefaultTableModel modelo) {
        JFrame frame = new JFrame("Adicionar Registro");
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(0, 2));

        if (tabela.equals("compromissos")) {
            JTextField dataField = new JTextField();
            JTextField horaField = new JTextField();
            JTextField descricaoField = new JTextField();

            frame.add(new JLabel("Data:"));
            frame.add(dataField);
            frame.add(new JLabel("Hora:"));
            frame.add(horaField);
            frame.add(new JLabel("Descrição:"));
            frame.add(descricaoField);

            JButton salvar = new JButton("Salvar");
            salvar.addActionListener(e -> {
                try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO compromissos (data, hora, descricao) VALUES (?, ?, ?)")) {
                    stmt.setString(1, dataField.getText());
                    stmt.setString(2, horaField.getText());
                    stmt.setString(3, descricaoField.getText());
                    stmt.executeUpdate();
                    carregarDados(tabela, modelo);
                    frame.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            frame.add(salvar);
        } else if (tabela.equals("pacientes")) {
            JTextField nomeField = new JTextField();
            JTextField idadeField = new JTextField();
            JTextField enderecoField = new JTextField();
            JTextField descricaoField = new JTextField();

            frame.add(new JLabel("Nome:"));
            frame.add(nomeField);
            frame.add(new JLabel("Idade:"));
            frame.add(idadeField);
            frame.add(new JLabel("Endereço:"));
            frame.add(enderecoField);
            frame.add(new JLabel("Descrição:"));
            frame.add(descricaoField);

            JButton salvar = new JButton("Salvar");
            salvar.addActionListener(e -> {
                try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO pacientes (nome, idade, endereco, descricao) VALUES (?, ?, ?, ?)")) {
                    stmt.setString(1, nomeField.getText());
                    stmt.setInt(2, Integer.parseInt(idadeField.getText()));
                    stmt.setString(3, enderecoField.getText());
                    stmt.setString(4, descricaoField.getText());
                    stmt.executeUpdate();
                    carregarDados(tabela, modelo);
                    frame.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            frame.add(salvar);
        } else if (tabela.equals("financeiro")) {
            JTextField tipoField = new JTextField();
            JTextField valorField = new JTextField();
            JTextField dataField = new JTextField();

            frame.add(new JLabel("Tipo:"));
            frame.add(tipoField);
            frame.add(new JLabel("Valor:"));
            frame.add(valorField);
            frame.add(new JLabel("Data:"));
            frame.add(dataField);

            JButton salvar = new JButton("Salvar");
            salvar.addActionListener(e -> {
                try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO financeiro (tipo, valor, data) VALUES (?, ?, ?)")) {
                    stmt.setString(1, tipoField.getText());
                    stmt.setDouble(2, Double.parseDouble(valorField.getText()));
                    stmt.setString(3, dataField.getText());
                    stmt.executeUpdate();
                    carregarDados(tabela, modelo);
                    frame.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            frame.add(salvar);
        } else if (tabela.equals("estoque")) {
            JTextField produtoField = new JTextField();
            JTextField quantidadeField = new JTextField();

            frame.add(new JLabel("Produto:"));
            frame.add(produtoField);
            frame.add(new JLabel("Quantidade:"));
            frame.add(quantidadeField);

            JButton salvar = new JButton("Salvar");
            salvar.addActionListener(e -> {
                try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO estoque (produto, quantidade) VALUES (?, ?)")) {
                    stmt.setString(1, produtoField.getText());
                    stmt.setInt(2, Integer.parseInt(quantidadeField.getText()));
                    stmt.executeUpdate();
                    carregarDados(tabela, modelo);
                    frame.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            frame.add(salvar);
        }

        frame.setVisible(true);
    }

    private static void removerRegistro(String tabela, DefaultTableModel modelo, JTable jTable) {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) modelo.getValueAt(selectedRow, 0);
            try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + tabela + " WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                modelo.removeRow(selectedRow);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione uma linha para remover.");
        }
    }

    static class TelaAgenda extends JFrame {
        public TelaAgenda() {
            setTitle("Gerenciar Agenda");
            setSize(600, 400);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Data", "Hora", "Descrição"}, 0);
            JTable tabela = new JTable(modelo);
            JScrollPane scrollPane = new JScrollPane(tabela);

            JButton adicionar = new JButton("Adicionar");
            JButton remover = new JButton("Remover");

            JPanel botoes = new JPanel();
            botoes.add(adicionar);
            botoes.add(remover);

            carregarDados("compromissos", modelo);

            adicionar.addActionListener(e -> abrirFormulario("compromissos", modelo));
            remover.addActionListener(e -> removerRegistro("compromissos", modelo, tabela));

            add(scrollPane, BorderLayout.CENTER);
            add(botoes, BorderLayout.SOUTH);
        }
    }

    static class TelaProntuario extends JFrame {
        public TelaProntuario() {
            setTitle("Gerenciar Prontuário");
            setSize(600, 400);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Idade", "Endereço", "Descrição"}, 0);
            JTable tabela = new JTable(modelo);
            JScrollPane scrollPane = new JScrollPane(tabela);

            JButton adicionar = new JButton("Adicionar");
            JButton remover = new JButton("Remover");

            JPanel botoes = new JPanel();
            botoes.add(adicionar);
            botoes.add(remover);

            carregarDados("pacientes", modelo);

            adicionar.addActionListener(e -> abrirFormulario("pacientes", modelo));
            remover.addActionListener(e -> removerRegistro("pacientes", modelo, tabela));

            add(scrollPane, BorderLayout.CENTER);
            add(botoes, BorderLayout.SOUTH);
        }
    }

    static class TelaFinanceiro extends JFrame {
        public TelaFinanceiro() {
            setTitle("Gerenciar Financeiro");
            setSize(600, 400);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Tipo", "Valor", "Data"}, 0);
            JTable tabela = new JTable(modelo);
            JScrollPane scrollPane = new JScrollPane(tabela);

            JButton adicionar = new JButton("Adicionar");
            JButton remover = new JButton("Remover");

            JPanel botoes = new JPanel();
            botoes.add(adicionar);
            botoes.add(remover);

            carregarDados("financeiro", modelo);

            adicionar.addActionListener(e -> abrirFormulario("financeiro", modelo));
            remover.addActionListener(e -> removerRegistro("financeiro", modelo, tabela));

            add(scrollPane, BorderLayout.CENTER);
            add(botoes, BorderLayout.SOUTH);
        }
    }

    static class TelaEstoque extends JFrame {
        public TelaEstoque() {
            setTitle("Gerenciar Estoque");
            setSize(600, 400);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Produto", "Quantidade"}, 0);
            JTable tabela = new JTable(modelo);
            JScrollPane scrollPane = new JScrollPane(tabela);

            JButton adicionar = new JButton("Adicionar");
            JButton remover = new JButton("Remover");

            JPanel botoes = new JPanel();
            botoes.add(adicionar);
            botoes.add(remover);

            carregarDados("estoque", modelo);

            adicionar.addActionListener(e -> abrirFormulario("estoque", modelo));
            remover.addActionListener(e -> removerRegistro("estoque", modelo, tabela));

            add(scrollPane, BorderLayout.CENTER);
            add(botoes, BorderLayout.SOUTH);
        }
    }
}
