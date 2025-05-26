package tecnicas.Filosofos;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;

import java.awt.Color;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class FilosofoCondicion implements TecnicaSincronizacion {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition[] condiciones = new Condition[5];
    private final boolean[] tenedorDisponible = {true, true, true, true, true};

    public FilosofoCondicion() {
        for (int i = 0; i < 5; i++) {
            condiciones[i] = lock.newCondition();
        }
    }

    @Override
    public void ejecutar() {
        Visualizador.getPanelDiagrama().iniciarTick();

        for (int i = 0; i < 5; i++) {
            final int id = i;
            final int displayId = id + 1; // ✅ Moverlo fuera del lambda

            Thread filosofo = new Thread(() -> {
                while (true) {
                    int izq = id;
                    int der = (id + 1) % 5;

                    try {
                        // Pensando
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + displayId, Estado.BLOQUEADO);
                        Visualizador.getPanelGrafo().setFilosofoActivo(displayId, false);
                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(displayId, false);
                        Visualizador.getPanelGrafo().setEstadoFilosofo(displayId, "PENSANDO", Color.BLUE);
                        Thread.sleep((long)(Math.random() * 2000));

                        // Intentar comer
                        lock.lock();
                        while (!tenedorDisponible[izq] || !tenedorDisponible[der]) {
                            condiciones[id].await();
                        }
                        tenedorDisponible[izq] = false;
                        tenedorDisponible[der] = false;
                        lock.unlock();

                        // Comer
                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(displayId, true);
                        Visualizador.getPanelGrafo().setFilosofoActivo(displayId, true);
                        Visualizador.getPanelGrafo().setEstadoFilosofo(displayId, "COMIENDO", Color.GREEN);
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + displayId, Estado.ACTIVO);
                        Thread.sleep(1000);

                        // Terminar
                        lock.lock();
                        tenedorDisponible[izq] = true;
                        tenedorDisponible[der] = true;
                        condiciones[(id + 1) % 5].signal();
                        condiciones[(id + 4) % 5].signal();
                        lock.unlock();

                        Visualizador.getPanelGrafo().asignarTenedoresFilosofo(displayId, false);
                        Visualizador.getPanelGrafo().setFilosofoActivo(displayId, false);
                        Visualizador.getPanelGrafo().setEstadoFilosofo(displayId, "FINALIZADO", Color.LIGHT_GRAY);
                        Visualizador.getPanelDiagrama().actualizarEstado("F" + displayId, Estado.FINALIZADO);
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        if (lock.isHeldByCurrentThread()) lock.unlock();
                    }
                }
            }, "Filosofo-" + displayId); // ✅ Ya puedes usarlo aquí

            filosofo.start();
        }
    }
}
