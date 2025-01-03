package programadamae;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ControleFinanceiro {
    private static final String DATABASE_URL = "jdbc:sqlite:gestao.db";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Controle Financeiro");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Dataset para o gráfico
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(5000, "Receitas", "Janeiro");
            dataset.addValue(3000, "Despesas", "Janeiro");
            dataset.addValue(7000, "Receitas", "Fevereiro");
            dataset.addValue(4000, "Despesas", "Fevereiro");

            // Criação do gráfico
            JFreeChart chart = ChartFactory.createBarChart(
                    "Fluxo de Caixa", "Mês", "Valor", dataset);

            // Adiciona o gráfico à janela
            ChartPanel chartPanel = new ChartPanel(chart);
            frame.add(chartPanel, BorderLayout.CENTER);

            // Adiciona botão de rolagem para o campo de descrição
            JTextArea textArea = new JTextArea(5, 20);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane, BorderLayout.SOUTH);

            // Botão para salvar dados financeiros
            JButton salvarBtn = new JButton("Salvar");
            frame.add(salvarBtn, BorderLayout.NORTH);

            salvarBtn.addActionListener(e -> {
                String descricao = textArea.getText().trim();
                if (descricao.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Descrição não pode estar vazia.");
                    return;
                }

                try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO financeiro (tipo, valor, data) VALUES (?, ?, ?)")) {
                    stmt.setString(1, "Receita"); // ou "Despesa"
                    stmt.setDouble(2, 1000.0); // valor exemplo
                    stmt.setString(3, "2023-10-01"); // data exemplo
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Dados financeiros salvos com sucesso!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro ao salvar dados: " + ex.getMessage());
                }
            });

            frame.setVisible(true);
        });
    }
}
