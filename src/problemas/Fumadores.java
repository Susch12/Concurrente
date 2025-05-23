package problemas;

import tecnicas.Fumadores.TecnicaSincronizacion;

public class Fumadores {

    private static TecnicaSincronizacion estrategia;

    public static void setEstrategia(TecnicaSincronizacion e) {
        estrategia = e;
    }

    public static void iniciar() {
        if (estrategia == null) {
            System.err.println("⚠ No se ha establecido una estrategia para Fumadores.");
            return;
        }

        System.out.println("✅ Iniciando Fumadores con: " + estrategia.getClass().getSimpleName());
        estrategia.ejecutar();
    }
}
