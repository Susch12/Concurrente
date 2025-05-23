package tecnicas.Fumadores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

public class FumadoresMonitor implements TecnicaSincronizacion {

    private final Mesa mesa = new Mesa();

    class Mesa {
        private int ingrediente = -1;

        public synchronized void poner(int i) throws InterruptedException {
            while (ingrediente != -1) wait();
            ingrediente = i;
            notifyAll();
        }

        public synchronized void tomar(int id) throws InterruptedException {
            while (ingrediente != id) wait();
            ingrediente = -1;
            notifyAll();
        }
    }

    @Override
    public void ejecutar() {
        for (int i = 0; i < 3; i++) {
            final int id = i;
            Thread fumador = new Thread(() -> {
                while (true) {
                    try {
                        mesa.tomar(id);
                        Visualizador.panelDiagrama.actualizarEstado("Fumador " + (id + 1), Estado.ACTIVO);
                        Visualizador.panelGrafo.setEstadoBuffer("Fumador " + (id + 1) + " fumando...");
                        Thread.sleep(1000);
                        Visualizador.panelDiagrama.actualizarEstado("Fumador " + (id + 1), Estado.FINALIZADO);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            fumador.start();
        }

        Thread agente = new Thread(() -> {
            while (true) {
                try {
                    int i = (int) (Math.random() * 3);
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Agente entrega ingrediente " + (i + 1));
                    Thread.sleep(800);
                    Visualizador.panelDiagrama.actualizarEstado("Agente", Estado.FINALIZADO);
                    mesa.poner(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        agente.start();
    }
}