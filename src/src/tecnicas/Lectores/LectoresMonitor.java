package tecnicas.Lectores;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

public class LectoresMonitor implements TecnicaSincronizacion {

    private final RecursoCompartido recurso = new RecursoCompartido();

    class RecursoCompartido {
        private int lectores = 0;
        private boolean escribiendo = false;

        public synchronized void empezarLeer(int id) throws InterruptedException {
            while (escribiendo) wait();
            lectores++;
        }

        public synchronized void terminarLeer(int id) {
            lectores--;
            if (lectores == 0) notifyAll();
        }

        public synchronized void empezarEscribir() throws InterruptedException {
            while (lectores > 0 || escribiendo) wait();
            escribiendo = true;
        }

        public synchronized void terminarEscribir() {
            escribiendo = false;
            notifyAll();
        }
    }

    @Override
    public void ejecutar() {
        for (int i = 1; i <= 2; i++) {
            final int id = i;
            Thread lector = new Thread(() -> {
                while (true) {
                    try {
                        recurso.empezarLeer(id);
                        Visualizador.panelDiagrama.actualizarEstado("Lector " + id, Estado.ACTIVO);
                        Visualizador.panelGrafo.setEstadoBuffer("Lector " + id + " leyendo...");
                        Thread.sleep(1000);
                        Visualizador.panelDiagrama.actualizarEstado("Lector " + id, Estado.FINALIZADO);
                        recurso.terminarLeer(id);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            lector.start();
        }

        Thread escritor = new Thread(() -> {
            while (true) {
                try {
                    recurso.empezarEscribir();
                    Visualizador.panelDiagrama.actualizarEstado("Escritor", Estado.ACTIVO);
                    Visualizador.panelGrafo.setEstadoBuffer("Escribiendo...");
                    Thread.sleep(1200);
                    Visualizador.panelDiagrama.actualizarEstado("Escritor", Estado.FINALIZADO);
                    recurso.terminarEscribir();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        escritor.start();
    }
}