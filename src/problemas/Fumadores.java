package problemas;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Fumadores {

    private static final int N = 3;
    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore[] puedeFumar = new Semaphore[N];
    private static final String[] ingredientes = {"Papel", "Tabaco", "Cerillos"};
    private static int faltante;

    public static void iniciar() {
        for (int i = 0; i < N; i++) {
            puedeFumar[i] = new Semaphore(0);
        }

        Visualizador.panelGrafo.limpiarPanel();
        Visualizador.panelGrafo.setNombresProcesos(
            java.util.List.of("Agente", "Fumador 1", "Fumador 2", "Fumador 3")
        );
        Visualizador.panelGrafo.agregarProcesosYRecursos(4, 0); // sin recursos

        // Agente
        Thread agente = new Thread(() -> {
            Random rand = new Random();
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.BLOQUEADO);
                    Thread.sleep(1000);

                    mutex.acquire();
                    faltante = rand.nextInt(3);
                    String entregado = "Entrega " + ingredientes[(faltante + 1) % 3] + " y " + ingredientes[(faltante + 2) % 3];
                    Visualizador.panelGrafo.setEstadoBuffer(entregado);
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.ACTIVO);
                    puedeFumar[faltante].release(); // Despierta al fumador que tiene el faltante
                    mutex.release();

                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.FINALIZADO);
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Agente");

        agente.start();

        // Fumadores
        for (int i = 0; i < N; i++) {
            final int id = i;
            Thread fumador = new Thread(() -> {
                String nombre = "Fumador " + (id + 1);
                while (true) {
                    try {
                        Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.BLOQUEADO);
                        puedeFumar[id].acquire(); // Espera a tener ingredientes

                        Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.ACTIVO);
                        Visualizador.panelGrafo.setEstadoBuffer(nombre + " armando cigarro");
                        Thread.sleep(1000);

                        Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.FINALIZADO);
                        Visualizador.panelGrafo.setEstadoBuffer(nombre + " fumando...");
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Fumador " + (i + 1));

            fumador.start();
        }
    }
}
