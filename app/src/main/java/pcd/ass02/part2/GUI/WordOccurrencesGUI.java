package pcd.ass02.part2.GUI;

import pcd.ass02.part2.lib.EventLoop.WordOccurrencesEventLoop;
import pcd.ass02.part2.lib.WordOccurrences;
import pcd.ass02.part2.lib.WordOccurrencesVirtualThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WordOccurrencesGUI extends JFrame {
    private JTextField webAddress, wordToFind, depth;
    private JTextArea resultArea;
    private JButton startButton;

    public WordOccurrencesGUI() {
        setTitle("Word Counter");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        inputPanel.add(new JLabel("Web address:"));
        webAddress = new JTextField();
        inputPanel.add(webAddress);

        inputPanel.add(new JLabel("Word to find:"));
        wordToFind = new JTextField();
        inputPanel.add(wordToFind);

        inputPanel.add(new JLabel("Depth:"));
        depth = new JTextField();
        inputPanel.add(depth);

        startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            String webAddressText = webAddress.getText().isEmpty() ? "http://Fracarlucci.github.io/Rancorrank" : webAddress.getText();
            String wordToFindText = wordToFind.getText().isEmpty() ? "http://Fracarlucci.github.io/Rancorrank" : wordToFind.getText();
            int depthText;
            try {
                depthText = Integer.parseInt(depth.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CompletableFuture<Map<String, Integer>> future = CompletableFuture.supplyAsync(() -> {
                final WordOccurrences wordOccVT = new WordOccurrencesVirtualThread();
                return wordOccVT.getWordOccurences(webAddressText, wordToFindText, depthText);
            });
            Map<String, Integer> report = null;
            try {
                report = future.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
            final int viewedLinks = report.keySet().size();
            final int wordsFound = report.values().stream().mapToInt(Integer::intValue).sum();

//            System.out.println("*************** REPORT ***************");
            resultArea.setText("Occurrences of \"" + wordToFindText + "\" : link");

            report.forEach((k, v) -> resultArea.append(v + " : " + k));
            resultArea.append("Links: " + viewedLinks);
            resultArea.append("Words found: " + wordsFound);
        });
        inputPanel.add(startButton);

        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordOccurrencesGUI gui = new WordOccurrencesGUI();
            gui.setVisible(true);
        });
    }
}
