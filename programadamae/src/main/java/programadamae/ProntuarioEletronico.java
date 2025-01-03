package programadamae;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProntuarioEletronico {
    private static final String DATABASE_URL = "jdbc:sqlite:gestao.db";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Prontuário Eletrônico");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel(new GridLayout(6, 2));
            frame.add(panel);

            // Campos do formulário
            JTextField nomeField = new JTextField();
            JTextField idadeField = new JTextField();
            JTextField enderecoField = new JTextField();
            JTextArea descricaoArea = new JTextArea(5, 20);
            JScrollPane scrollPane = new JScrollPane(descricaoArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            // Adiciona os campos ao painel
            panel.add(new JLabel("Nome:"));
            panel.add(nomeField);
            panel.add(new JLabel("Idade:"));
            panel.add(idadeField);
            panel.add(new JLabel("Endereço:"));
            panel.add(enderecoField);
            panel.add(new JLabel("Descrição:"));
            panel.add(scrollPane);

            JButton salvarBtn = new JButton("Salvar");
            panel.add(salvarBtn);

            salvarBtn.addActionListener(e -> {
                String nome = nomeField.getText().trim();
                String idadeStr = idadeField.getText().trim();
                String endereco = enderecoField.getText().trim();
                String descricao = descricaoArea.getText().trim();

                if (nome.isEmpty() || idadeStr.isEmpty() || endereco.isEmpty() || descricao.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Todos os campos devem ser preenchidos.");
                    return;
                }

                int idade;
                try {
                    idade = Integer.parseInt(idadeStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Idade deve ser um número inteiro.");
                    return;
                }

                try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO pacientes (nome, idade, endereco, descricao) VALUES (?, ?, ?, ?)")) {
                    stmt.setString(1, nome);
                    stmt.setInt(2, idade);
                    stmt.setString(3, endereco);
                    stmt.setString(4, descricao);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Paciente cadastrado com sucesso!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro ao salvar dados: " + ex.getMessage());
                }
            });

            frame.setVisible(true);
        });
    }
}
