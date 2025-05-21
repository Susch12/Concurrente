package tecnicas.Barbero;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.LinkedList;
import java.util.Queue;

public class BarberoMonitor implements TecnicaSincronizacion {

    private final Barberia barberia = new Barberia();

    class Barberia {
        private final Queue<Integer> clientes = new LinkedList<>();
        private final int maxSillas = 3;

        public synchronized boolean entrarCliente(int id) {
            if (clientes.size() < maxSillas) {
                clientes.add(id);
                notify();
                return true;
            }
            return false;
        }

        public synchronized int esperarCliente() throws InterruptedException {
            while (clientes.isEmpty()) wait();
            return clientes.poll();
        }

        public synchronized int contarClientes() {
            return clientes.size();
        }
    }

    @Override
    public void ejecutar() {
        Thread barbero = new Thread(() -> {
            while (true) {
                try {
                    int id = barberia.esperarCliente();
                    Visualizador.panelGrafo.setEstadoBarbero("CORTANDO");
                    Visualizador.panelDiagrama.actualizarEstado("Barbero", Estado.ACTIVO);
                    Visualizador.panelGrafo.actualizarSillasEspera(barberia.clientes.stream().toList());
                    Thread.sleep(1000);
                    Visualizador.panelDiagrama.actualizarEstado("Barbero", Estado.FINALIZADO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        barbero.start();

        for (int i = 0; i < 5; i++) {
            final int cid = i;
            Thread cliente = new Thread(() -> {
                while (true) {
                    try {
                        if (barberia.entrarCliente(cid)) {
                            Visualizador.panelGrafo.actualizarSillasEspera(barberia.clientes.stream().toList());
                        }
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            cliente.start();
        }
    }
}