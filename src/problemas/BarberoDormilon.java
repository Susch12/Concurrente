package problemas;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BarberoDormilon {

    private static final int SILLAS_ESPERA = 3;  // ajustable
    private static final int TOTAL_CLIENTES = 10;
    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore clientes = new Semaphore(0);
    private static final Semaphore barbero = new Semaphore(0);
    private static final Queue<Integer> filaClientes = new LinkedList<>();
    private static int idCliente = 1;

    public static void iniciar() {
        Visualizador.panelGrafo.limpiarPanel();
        Visualizador.panelGrafo.configurarBarberoDormilon(SILLAS_ESPERA + 1, false);

        Thread barberoThread = new Thread(() -> {
            String nombre = "Barbero";
            while (true) {
                try {
                    Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.BLOQUEADO);
                    Visualizador.panelGrafo.setEstadoBarbero("Durmiendo");
                    clientes.acquire(); // Esperar cliente

                    Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.ACTIVO);
                    mutex.acquire();
                    int cliente = filaClientes.poll();
                    Visualizador.panelGrafo.actualizarSillasEspera(new LinkedList<>(filaClientes));
                    Visualizador.panelGrafo.setEstadoBarbero("Cortando a Cliente " + cliente);
                    mutex.release();

                    Visualizador.panelGrafo.setEstadoSillaBarbero(true);
                    Thread.sleep(1500); // cortar pelo
                    Visualizador.panelGrafo.setEstadoSillaBarbero(false);

                    Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.FINALIZADO);
                    Thread.sleep(1000); // descanso

                    barbero.release(); // liberar al cliente

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Barbero");

        barberoThread.start();

        // Lanzar clientes
        for (int i = 1; i <= TOTAL_CLIENTES; i++) {
            final int clienteId = idCliente++;
            Thread cliente = new Thread(() -> {
                try {
                    mutex.acquire();
                    if (filaClientes.size() < SILLAS_ESPERA) {
                        filaClientes.add(clienteId);
                        Visualizador.panelGrafo.actualizarSillasEspera(new LinkedList<>(filaClientes));
                        mutex.release();

                        clientes.release(); // notificar barbero
                        barbero.acquire();  // esperar corte

                    } else {
                        // No hay sillas
                        mutex.release();
                        System.out.println("Cliente " + clienteId + " se fue (sin silla)");
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Cliente " + clienteId);
            cliente.start();

            try {
                Thread.sleep(1000); // tiempo entre llegadas
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
