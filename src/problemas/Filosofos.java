package problemas;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
import java.util.concurrent.Semaphore;

public class Filosofos {

    private static final int N = 5;
    private static final Semaphore[] tenedores = new Semaphore[N];

    public static void iniciar() {
        for (int i = 0; i < N; i++) {
            tenedores[i] = new Semaphore(1);
        }

        Visualizador.panelGrafo.limpiarPanel();
        Visualizador.panelGrafo.agregarProcesosYRecursos(5, 5);

        for (int i = 0; i < N; i++) {
            final int id = i;
            Thread filosofo = new Thread(() -> cicloFilosofo(id));
            filosofo.setName("Filosofo " + (id + 1));
            filosofo.start();
        }
    }

    private static void cicloFilosofo(int id) {
        int izq = id;
        int der = (id + 1) % N;
        String nombre = "Filosofo " + (id + 1);

        while (true) {
            try {
                // Pensando
                Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.FINALIZADO);
                Visualizador.panelGrafo.setFilosofoActivo(id + 1, false);
                Thread.sleep(1000);

                // Queriendo comer (bloqueado)
                Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.BLOQUEADO);
                tenedores[izq].acquire();
                tenedores[der].acquire();
                Visualizador.panelGrafo.asignarTenedoresFilosofo(id + 1, true);

                // Comiendo (activo)
                Visualizador.panelDiagrama.actualizarEstado(nombre, Estado.ACTIVO);
                Visualizador.panelGrafo.setFilosofoActivo(id + 1, true);
                Thread.sleep(1000);

                // Liberar tenedores
                tenedores[izq].release();
                tenedores[der].release();
                Visualizador.panelGrafo.asignarTenedoresFilosofo(id + 1, false);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
