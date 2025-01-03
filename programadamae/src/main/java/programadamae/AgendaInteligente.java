package programadamae;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AgendaInteligente {
    private static final String DATABASE_URL = "jdbc:sqlite:gestao.db";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Agenda Inteligente");
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Modelo da Tabela
            String[] colunas = {"ID", "Data", "Hora", "Descrição"};
            DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
            JTable tabela = new JTable(modelo);

            // ScrollPane para a tabela
            JScrollPane scrollPane = new JScrollPane(tabela);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Botão para adicionar compromisso
            JButton adicionarBtn = new JButton("Adicionar");
            frame.add(adicionarBtn, BorderLayout.SOUTH);

            adicionarBtn.addActionListener(e -> abrirFormulario(modelo));

            // Conexão com Banco de Dados
            try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM compromissos")) {

                // Preenche a tabela
                while (rs.next()) {
                    modelo.addRow(new Object[]{rs.getInt("id"), rs.getString("data"), rs.getString("hora"), rs.getString("descricao")});
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            frame.setVisible(true);
        });
    }

    private static void abrirFormulario(DefaultTableModel modelo) {
        JFrame frame = new JFrame("Adicionar Compromisso");
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(0, 2));

        JTextField dataField = new JTextField();
        JTextField horaField = new JTextField();
        JTextArea descricaoArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(descricaoArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.add(new JLabel("Data:"));
        frame.add(dataField);
        frame.add(new JLabel("Hora:"));
        frame.add(horaField);
        frame.add(new JLabel("Descrição:"));
        frame.add(scrollPane);

        JButton salvarBtn = new JButton("Salvar");
        salvarBtn.addActionListener(e -> {
            String data = dataField.getText().trim();
            String hora = horaField.getText().trim();
            String descricao = descricaoArea.getText().trim();

            if (data.isEmpty() || hora.isEmpty() || descricao.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Todos os campos devem ser preenchidos.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO compromissos (data, hora, descricao) VALUES (?, ?, ?)")) {
                stmt.setString(1, data);
                stmt.setString(2, hora);
                stmt.setString(3, descricao);
                stmt.executeUpdate();
                carregarDados(modelo);
                frame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao salvar dados: " + ex.getMessage());
            }
        });

        frame.add(salvarBtn);
        frame.setVisible(true);
    }

    private static void carregarDados(DefaultTableModel modelo) {
        modelo.setRowCount(0); // Limpa a tabela
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM compromissos")) {

            // Preenche a tabela
            while (rs.next()) {
                modelo.addRow(new Object[]{rs.getInt("id"), rs.getString("data"), rs.getString("hora"), rs.getString("descricao")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
