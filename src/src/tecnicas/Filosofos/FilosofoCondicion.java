package tecnicas.Filosofos;

import GUI.Visualizador;
import GUI.PanelDiagramaHilos.Estado;
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
        for (int i = 0; i < 5; i++) {
            final int id = i;
            Thread filosofo = new Thread(() -> {
                while (true) {
                    int izq = id;
                    int der = (id + 1) % 5;

                    lock.lock();
                    try {
                        while (!tenedorDisponible[izq] || !tenedorDisponible[der]) {
                            condiciones[id].await();
                        }

                        tenedorDisponible[izq] = false;
                        tenedorDisponible[der] = false;

                        Visualizador.panelGrafo.asignarTenedoresFilosofo(id + 1, true);
                        Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.ACTIVO);
                        lock.unlock();

                        Thread.sleep(1000);

                        lock.lock();
                        tenedorDisponible[izq] = true;
                        tenedorDisponible[der] = true;
                        Visualizador.panelGrafo.asignarTenedoresFilosofo(id + 1, false);
                        Visualizador.panelDiagrama.actualizarEstado("F" + (id+1), Estado.FINALIZADO);
                        condiciones[(id + 1) % 5].signal();
                        condiciones[(id + 4) % 5].signal();
                        lock.unlock();

                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        if (lock.isHeldByCurrentThread()) lock.unlock();
                    }
                }
            });
            filosofo.start();
        }
    }
}