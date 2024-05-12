package pcd.ass02.part2.GUI;

import io.vertx.core.Vertx;
import pcd.ass02.part2.lib.EventLoop.VerticleFinder;
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
    private JButton stopButton;

    public WordOccurrencesGUI() {
        setTitle("Word Occurrences");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        inputPanel.add(new JLabel("Web address:"));
        webAddress = new JTextField("https://virtuale.unibo.it");
        inputPanel.add(webAddress);

        inputPanel.add(new JLabel("Word to find:"));
        wordToFind = new JTextField("virtuale");
        inputPanel.add(wordToFind);

        inputPanel.add(new JLabel("Depth:"));
        depth = new JTextField("2");
        inputPanel.add(depth);

        startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            startButton.setText("Finding words...");
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            String webAddressText = webAddress.getText().isEmpty() ? "https://fracarlucci.github.io/RancorRank/" : webAddress.getText();
            String wordToFindText = wordToFind.getText().isEmpty() ? "hello" : wordToFind.getText();
            int depthText;
            try {
                depthText = Integer.parseInt(depth.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            final WordOccurrencesEventLoop wordOccEventLoop = new WordOccurrencesEventLoop();
//            wordOccEventLoop.getWordOccurences(webAddressText, wordToFindText, depthText);
            Map<String, Integer> result = new HashMap<>();
            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(new VerticleFinder(webAddressText, wordToFindText, depthText, res -> {
//                result.putAll(res);
                resultArea.setText(result.values().stream().mapToInt(Integer::intValue).sum() + " occurrences found\n");
                result.putAll(res);
//                System.out.println(result.values().stream().mapToInt(Integer::intValue).sum() + " occurrences found");
            })).onComplete(res -> {

//            System.out.println("*************** REPORT ***************");
                resultArea.append("\nOccurrences of \"" + wordToFindText + "\" : link\n");

                result.forEach((k, v) -> resultArea.append(v + " : " + k + "\n"));
                final int viewedLinks = result.keySet().size();
                final int wordsFound = result.values().stream().mapToInt(Integer::intValue).sum();
                resultArea.append("Links: " + viewedLinks + "\n");
                resultArea.append("Words found: " + wordsFound + "\n");
                startButton.setText("Words found!");
                stopButton.setEnabled(false);
                vertx.close();
            });
        });
        stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> {

        });
        inputPanel.add(startButton);
        inputPanel.add(stopButton);

        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        Font font = new Font("Arial", Font.PLAIN, 14);
        resultArea.setFont(font);
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
