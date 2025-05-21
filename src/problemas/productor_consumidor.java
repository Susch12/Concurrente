package problemas;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.Semaphore;

public class productor_consumidor {

    private static final int BUFFER_SIZE = 5;
    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore items = new Semaphore(0);
    private static final Semaphore espacios = new Semaphore(BUFFER_SIZE);
    private static int buffer = 0;

    public static void iniciar() {
        Visualizador.panelGrafo.limpiarPanel();
        Visualizador.panelGrafo.agregarProcesosYRecursos(2, 1);
        Visualizador.panelGrafo.setEstadoBuffer("Buffer vacío");

        // PRODUCTOR
        Thread productor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.BLOQUEADO);
                    espacios.acquire(); // Esperar espacio

                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.ACTIVO);
                    mutex.acquire();
                    buffer++;
                    Visualizador.panelGrafo.setEstadoBuffer("Produciendo... (" + buffer + ")");
                    Thread.sleep(1000); // Simular producción
                    mutex.release();

                    items.release(); // Incrementar items disponibles
                    Visualizador.panelDiagrama.actualizarEstado("Productor", Estado.FINALIZADO);
                    Thread.sleep(1000); // Simular descanso

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // CONSUMIDOR
        Thread consumidor = new Thread(() -> {
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.BLOQUEADO);
                    items.acquire(); // Esperar item

                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.ACTIVO);
                    mutex.acquire();
                    buffer--;
                    Visualizador.panelGrafo.setEstadoBuffer("Consumiendo... (" + buffer + ")");
                    Thread.sleep(1000); // Simular consumo
                    mutex.release();

                    espacios.release(); // Incrementar espacio disponible
                    Visualizador.panelDiagrama.actualizarEstado("Consumidor", Estado.FINALIZADO);
                    Thread.sleep(1000); // Simular descanso

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        productor.start();
        consumidor.start();
    }
}
