package GUI;

import problemas.*;
import tecnicas.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainMenu extends JFrame {

    private JComboBox<String> comboProblemas;
    private JComboBox<String> comboTecnicas;
    private JButton botonEjecutar;
    private PanelGrafoHilos panelGrafo;
    private PanelDiagramaHilos panelDiagrama;
    private JMenuBar menuBar;
    private JMenu menuArchivo;
    private JMenuItem itemAbrir, itemGuardar, itemLimpiar, itemSalir;


    public MainMenu() {
        setTitle("Simulador de Problemas de Sincronización");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        String[] problemas = {
            "Productor/Consumidor",
            "Filosofos",
            "Barbero Dormilón",
            "Fumadores",
            "Lectores/Escritores"
        };

        String[] tecnicas = {
            "Semáforo",
            "Variable de condición",
            "Monitor",
            "Barrera"
        };

        comboProblemas = new JComboBox<>(problemas);
        comboTecnicas = new JComboBox<>(tecnicas);
        botonEjecutar = new JButton("Ejecutar");
        botonEjecutar.addActionListener(this::ejecutarSimulacion);

        panelGrafo = new PanelGrafoHilos();
        panelGrafo.setPreferredSize(new Dimension(900, 500));
        panelGrafo.setBackground(Color.LIGHT_GRAY);

        panelDiagrama = new PanelDiagramaHilos();
        panelDiagrama.setPreferredSize(new Dimension(900, 100));
        panelDiagrama.setBorder(BorderFactory.createTitledBorder("Diagrama de Hilos"));

        // Asignar paneles a Visualizador para acceso global
        Visualizador.panelDiagrama = panelDiagrama;
        Visualizador.panelGrafo = panelGrafo;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.insets = new Insets(10, 10, 10, 10);
        gbc1.gridx = 0; gbc1.gridy = 0;
        add(new JLabel("Selecciona un problema:"), gbc1);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(10, 10, 10, 10);
        gbc2.gridx = 1; gbc2.gridy = 0;
        add(comboProblemas, gbc2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.insets = new Insets(10, 10, 10, 10);
        gbc3.gridx = 0; gbc3.gridy = 1;
        add(new JLabel("Selecciona una técnica:"), gbc3);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.insets = new Insets(10, 10, 10, 10);
        gbc4.gridx = 1; gbc4.gridy = 1;
        add(comboTecnicas, gbc4);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.insets = new Insets(10, 10, 10, 10);
        gbc5.gridx = 0; gbc5.gridy = 2; gbc5.gridwidth = 2;
        add(botonEjecutar, gbc5);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.weightx = 1.0;
        gbc6.weighty = 0.7;
        gbc6.insets = new Insets(10, 10, 10, 10);
        gbc6.gridx = 0; gbc6.gridy = 3; gbc6.gridwidth = 2;
        add(panelGrafo, gbc6);

        GridBagConstraints gbc7 = new GridBagConstraints();
        gbc7.fill = GridBagConstraints.BOTH;
        gbc7.weightx = 1.0;
        gbc7.weighty = 1.4;
        gbc7.insets = new Insets(10, 10, 10, 10);
        gbc7.gridx = 0; gbc7.gridy = 4; gbc7.gridwidth = 2;
        add(panelDiagrama, gbc7);

        // ---- MENÚ SUPERIOR ----
        menuBar = new JMenuBar();
        menuArchivo = new JMenu("Archivo");

        itemAbrir = new JMenuItem("Abrir");
        itemGuardar = new JMenuItem("Guardar");
        itemLimpiar = new JMenuItem("Limpiar");
        itemSalir = new JMenuItem("Salir");

        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemLimpiar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        menuBar.add(menuArchivo);

        setJMenuBar(menuBar);

        // ---- ACCIONES ----
        itemLimpiar.addActionListener(e -> {
            panelGrafo.limpiarPanel();
            panelDiagrama.repaint();
            JOptionPane.showMessageDialog(this, "Paneles limpiados.");
        });

        itemSalir.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Deseas salir?", "Salir", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) System.exit(0);
        });

        itemGuardar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int opcion = chooser.showSaveDialog(this);
            if (opcion == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
                    writer.write("Procesos: " + panelGrafo.getNumeroProcesos() + "\\n");
                    writer.write("Recursos: " + panelGrafo.getNumeroRecursos() + "\\n");
                    writer.write("Flechas:\\n");
                    writer.write(panelGrafo.obtenerFlechasComoTexto());
                    JOptionPane.showMessageDialog(this, "Guardado exitosamente.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al guardar.");
                }
            }
        });

        itemAbrir.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int opcion = chooser.showOpenDialog(this);
            if (opcion == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    int procesos = 0, recursos = 0;
                    StringBuilder flechas = new StringBuilder();
                    while ((linea = reader.readLine()) != null) {
                        if (linea.startsWith("Procesos:")) {
                            procesos = Integer.parseInt(linea.split(":")[1].trim());
                        } else if (linea.startsWith("Recursos:")) {
                            recursos = Integer.parseInt(linea.split(":")[1].trim());
                        } else if (linea.startsWith("Flechas:")) {
                            while ((linea = reader.readLine()) != null && !linea.isEmpty()) {
                                flechas.append(linea).append("\\n");
                            }
                        }
                    }
                    panelGrafo.limpiarPanel();
                    panelGrafo.agregarProcesosYRecursos(procesos, recursos);
                    panelGrafo.cargarFlechas(flechas.toString());
                    panelDiagrama.repaint();
                    JOptionPane.showMessageDialog(this, "Archivo cargado.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al abrir archivo.");
                }
            }
        });

    }

private void ejecutarSimulacion(ActionEvent e) {
    String problema = (String) comboProblemas.getSelectedItem();
    String tecnica = (String) comboTecnicas.getSelectedItem();

    panelGrafo.limpiarPanel();

    // Mensaje de confirmación
    JOptionPane.showMessageDialog(this,
        "Simulando: " + problema + "\nCon técnica: " + tecnica,
        "Simulación iniciada",
        JOptionPane.INFORMATION_MESSAGE);

    switch (problema) {
        case "Filosofos" -> {
            panelGrafo.agregarProcesosYRecursos(5, 5);
            switch (tecnica) {
                case "Semáforo" -> Filosofos.setTecnica(new tecnicas.Filosofos.FilosofoSemaforo());
                case "Variable de condición" -> Filosofos.setTecnica(new tecnicas.Filosofos.FilosofoCondicion());
                case "Monitor" -> Filosofos.setTecnica(new tecnicas.Filosofos.FilosofoMonitor());
                case "Barrera" -> Filosofos.setTecnica(new tecnicas.Filosofos.FilosofoBarrera());
                default -> {
                    JOptionPane.showMessageDialog(this, "Técnica no soportada para Filósofos.");
                    return;
                }
            }
            Filosofos.iniciar();
        }

        case "Productor/Consumidor" -> {
            panelGrafo.agregarProcesosYRecursos(2, 1);
            switch (tecnica) {
                case "Semáforo" -> productor_consumidor.setEstrategia(new tecnicas.PC.PC_Semaforo());
                case "Variable de condición" -> productor_consumidor.setEstrategia(new tecnicas.PC.PC_Condicion());
                case "Monitor" -> productor_consumidor.setEstrategia(new tecnicas.PC.PC_Monitor());
                case "Barrera" -> productor_consumidor.setEstrategia(new tecnicas.PC.PC_Barrera());
                default -> {
                    JOptionPane.showMessageDialog(this, "Técnica no soportada para Productor/Consumidor.");
                    return;
                }
            }
            productor_consumidor.iniciar();
        }

        case "Barbero Dormilón" -> {
            panelGrafo.agregarProcesosYRecursos(6, 1);
            switch (tecnica) {
                case "Semáforo" -> BarberoDormilon.setEstrategia(new tecnicas.Barbero.BarberoSemaforo());
                case "Variable de condición" -> BarberoDormilon.setEstrategia(new tecnicas.Barbero.BarberoCondicion());
                case "Monitor" -> BarberoDormilon.setEstrategia(new tecnicas.Barbero.BarberoMonitor());
                case "Barrera" -> BarberoDormilon.setEstrategia(new tecnicas.Barbero.BarberoBarrera());
                default -> {
                    JOptionPane.showMessageDialog(this, "Técnica no soportada para Barbero Dormilón.");
                    return;
                }
            }
            BarberoDormilon.iniciar();
        }

        case "Fumadores" -> {
            panelGrafo.agregarProcesosYRecursos(4, 0);
            switch (tecnica) {
                case "Semáforo" -> Fumadores.setEstrategia(new tecnicas.Fumadores.FumadoresSemaforo());
                case "Variable de condición" -> Fumadores.setEstrategia(new tecnicas.Fumadores.FumadoresCondicion());
                case "Monitor" -> Fumadores.setEstrategia(new tecnicas.Fumadores.FumadoresMonitor());
                case "Barrera" -> Fumadores.setEstrategia(new tecnicas.Fumadores.FumadoresBarrera());
                default -> {
                    JOptionPane.showMessageDialog(this, "Técnica no soportada para Fumadores.");
                    return;
                }
            }
            Fumadores.iniciar();
        }

        case "Lectores/Escritores" -> {
            panelGrafo.agregarProcesosYRecursos(3, 1);
            switch (tecnica) {
                case "Semáforo" -> LectoresEscritores.setEstrategia(new tecnicas.Lectores.LectoresSemaforo());
                case "Variable de condición" -> LectoresEscritores.setEstrategia(new tecnicas.Lectores.LectoresCondicion());
                case "Monitor" -> LectoresEscritores.setEstrategia(new tecnicas.Lectores.LectoresMonitor());
                case "Barrera" -> LectoresEscritores.setEstrategia(new tecnicas.Lectores.LectoresBarrera());
                default -> {
                    JOptionPane.showMessageDialog(this, "Técnica no soportada para Lectores/Escritores.");
                    return;
                }
            }
            LectoresEscritores.iniciar();
        }

        default -> JOptionPane.showMessageDialog(this, "Problema no soportado aún.");
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
