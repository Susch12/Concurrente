package problemas;

import tecnicas.PC.TecnicaSincronizacion;

public class productor_consumidor {

    private static TecnicaSincronizacion estrategia;

    public static void setEstrategia(TecnicaSincronizacion e) {
        estrategia = e;
    }

    public static void iniciar() {
        if (estrategia == null) {
            System.err.println("⚠ No se ha establecido una estrategia para Productor/Consumidor.");
            return;
        }

        System.out.println("✅ Iniciando Productor/Consumidor con: " + estrategia.getClass().getSimpleName());
        estrategia.ejecutar();
    }
}
