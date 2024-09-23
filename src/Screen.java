import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class Product {
    String code, name, quantity, description, lotNumber;

    public Product(String code, String name, String quantity, String description, String lotNumber) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.lotNumber = lotNumber;
    }
    @Override
    public String toString() {
        return String.format("Código: %s\nNome: %s\nQuantidade: %s\nDescrição: %s\nNúmero do Lote: %s\n",
                code, name, quantity, description, lotNumber);
    }
}
class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(173, 216, 230), 0, getHeight(), Color.WHITE);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
class Screen {
    private JTextField codeTextField, nameTextField, quantityTextField, descriptionTextField, lotNumberTextField;
    private ArrayList<Product> productList = new ArrayList<>();
    private JButton addButton, saveButton;

    public Screen() {
        JFrame jFrame = new JFrame("Formulário de Produto");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(400, 300);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        codeTextField = new JTextField();
        nameTextField = new JTextField();
        quantityTextField = new JTextField();
        descriptionTextField = new JTextField();
        lotNumberTextField = new JTextField();

        addLabelAndTextField(panel, gbc, "Código do Produto:", codeTextField, 0);
        addLabelAndTextField(panel, gbc, "Nome:", nameTextField, 1);
        addLabelAndTextField(panel, gbc, "Quantidade:", quantityTextField, 2);
        addLabelAndTextField(panel, gbc, "Descrição:", descriptionTextField, 3);
        addLabelAndTextField(panel, gbc, "Número do Lote:", lotNumberTextField, 4);

        addButton = addButton(panel, gbc, "Adicionar Produto", e -> addProduct(), 5);
        saveButton = addButton(panel, gbc, "Salvar Lista", e -> saveDataToFile(), 6);

        setupEnterKeyBindings(panel);

        jFrame.add(panel);
        jFrame.setVisible(true);
    }
    private void addLabelAndTextField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        textField.setColumns(15);
        panel.add(textField, gbc);
        addEnterKeyListener(textField);
    }
    private JButton addButton(JPanel panel, GridBagConstraints gbc, String buttonText, ActionListener action, int y) {
        JButton button = new JButton(buttonText);
        button.addActionListener(action);
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(button, gbc);
        return button;
    }
    private void setupEnterKeyBindings(JPanel panel) {
        InputMap im = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = panel.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterPressed");
        am.put("enterPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton focusedButton = getFocusedButton();
                if (focusedButton != null) {
                    focusedButton.doClick();
                }
            }
        });
    }
    private void addProduct() {
        if (validateFields()) {
            productList.add(new Product(
                    codeTextField.getText(),
                    nameTextField.getText(),
                    quantityTextField.getText(),
                    descriptionTextField.getText(),
                    lotNumberTextField.getText()
            ));
            clearFields();
            showMessage("Produto adicionado com sucesso!");
        } else {
            showMessage("Por favor, preencha todos os campos.");
        }
    }
    private boolean validateFields() {
        return !codeTextField.getText().isEmpty() &&
                !nameTextField.getText().isEmpty() &&
                !quantityTextField.getText().isEmpty() &&
                !descriptionTextField.getText().isEmpty() &&
                !lotNumberTextField.getText().isEmpty();
    }
    private void saveDataToFile() {
        if (productList.isEmpty()) {
            showMessage("A lista de produtos está vazia. Adicione produtos antes de salvar.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Lista de Produtos");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto", "txt"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(addTxtExtension(fileChooser.getSelectedFile())))) {
                for (Product product : productList) {
                    writer.write(product.toString());
                    writer.newLine();
                }
                showMessage("Lista de produtos salva com sucesso!");
            } catch (IOException e) {
                showMessage("Erro ao salvar a lista de produtos: " + e.getMessage());
            }
        }
    }

    private File addTxtExtension(File file) {
        String filePath = file.getAbsolutePath();
        return filePath.endsWith(".txt") ? file : new File(filePath + ".txt");
    }

    private void clearFields() {
        codeTextField.setText("");
        nameTextField.setText("");
        quantityTextField.setText("");
        descriptionTextField.setText("");
        lotNumberTextField.setText("");
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private void addEnterKeyListener(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Component nextComponent = textField.getNextFocusableComponent();
                    if (nextComponent != null) {
                        nextComponent.requestFocus();
                    }
                }
            }
        });
    }
    private JButton getFocusedButton() {
        Component focusedComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusedComponent instanceof JButton) {
            return (JButton) focusedComponent;
        }
        return null;
    }
}