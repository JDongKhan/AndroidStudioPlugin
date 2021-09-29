package com.jd.dart.ui;

import com.jd.dart.utils.FileGenerator;

import javax.swing.*;
import java.awt.event.*;

public class DartModelGeneratorDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField modelClassName;
    private String filePath;

    public DartModelGeneratorDialog(String filePath) {
        this.filePath = filePath;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        //居中显示
        this.setLocationRelativeTo(null);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        String fileName = modelClassName.getText();
        String templatesFilePath = "/templates/model.dart";
        FileGenerator.generateClassFile(fileName,filePath,templatesFilePath);
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        DartModelGeneratorDialog dialog = new DartModelGeneratorDialog(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
