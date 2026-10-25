import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Logger simples para o TP1, inspirado no estilo visual de logs do Node.js.
 * Suporta níveis, contexto, cores ANSI e uma barra de progresso.
 */
public final class Logs {

    private static final DateTimeFormatter HORARIO = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int LARGURA_NIVEL = 7;
    private static final int LARGURA_BARRA = 24;

    private static final String RESET = "\u001B[0m";
    private static final String CINZA = "\u001B[90m";
    private static final String AZUL = "\u001B[34m";
    private static final String CIANO = "\u001B[36m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARELO = "\u001B[33m";
    private static final String VERMELHO = "\u001B[31m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String NEGRITO = "\u001B[1m";

    private static volatile boolean debugAtivado;
    private static volatile boolean coresAtivadas = detectarSuporteACores();

    private Logs() {
        // Classe utilitaria: nao deve ser instanciada.
    }

    /** Ativa ou desativa mensagens de nivel DEBUG. */
    public static void setDebug(boolean ativado) {
        debugAtivado = ativado;
    }

    /** Permite forcar a exibicao ou a remocao das cores ANSI. */
    public static void setCores(boolean ativadas) {
        coresAtivadas = ativadas;
    }

    public static boolean isDebugAtivado() {
        return debugAtivado;
    }

    public static void info(String contexto, String mensagem) {
        escrever("INFO", contexto, mensagem, CIANO);
    }

    public static void success(String contexto, String mensagem) {
        escrever("SUCCESS", contexto, mensagem, VERDE);
    }

    public static void debug(String contexto, String mensagem) {
        if (debugAtivado) {
            escrever("DEBUG", contexto, mensagem, MAGENTA);
        }
    }

    public static void warn(String contexto, String mensagem) {
        escrever("WARN", contexto, mensagem, AMARELO);
    }

    public static void error(String contexto, String mensagem) {
        escrever("ERROR", contexto, mensagem, VERMELHO);
    }

    /** Atalhos usando o contexto padrao "TP1". */
    public static void info(String mensagem) {
        info("TP1", mensagem);
    }

    public static void success(String mensagem) {
        success("TP1", mensagem);
    }

    public static void debug(String mensagem) {
        debug("TP1", mensagem);
    }

    public static void warn(String mensagem) {
        warn("TP1", mensagem);
    }

    public static void error(String mensagem) {
        error("TP1", mensagem);
    }

    /**
     * Registra o progresso atual. A barra e a mensagem so aparecem em modo debug.
     */
    public static void progress(
        String contexto,
        long concluidas,
        long total,
        String decorrido,
        String restante
    ) {
        if (!debugAtivado) {
            return;
        }

        double percentual = total <= 0 ? 100.0 : Math.min(100.0, concluidas * 100.0 / total);
        int preenchidas = (int) Math.round(LARGURA_BARRA * percentual / 100.0);
        StringBuilder barra = new StringBuilder(LARGURA_BARRA);

        for (int i = 0; i < LARGURA_BARRA; i++) {
            barra.append(i < preenchidas ? '#' : '.');
        }

        String mensagem = String.format(
            Locale.ROOT,
            "%s %5.1f%%  %d/%d  decorrido %s  restante ~%s",
            barra,
            percentual,
            concluidas,
            total,
            decorrido,
            restante
        );
        escrever("PROGRESS", contexto, mensagem, AZUL);
    }

    /** Imprime uma linha divisoria para separar etapas do experimento. */
    public static void divider(String contexto, String titulo) {
        String nome = titulo == null ? "" : titulo.trim();
        int largura = 56;
        if (nome.isEmpty()) {
            info(contexto, repetir('-', largura));
            return;
        }

        String texto = " " + nome + " ";
        int espacos = Math.max(2, largura - texto.length());
        int esquerda = espacos / 2;
        int direita = espacos - esquerda;
        info(contexto, repetir('-', esquerda) + texto + repetir('-', direita));
    }

    private static synchronized void escrever(
        String nivel,
        String contexto,
        String mensagem,
        String cor
    ) {
        String horario = LocalTime.now().format(HORARIO);
        String nomeContexto = contexto == null || contexto.trim().isEmpty() ? "TP1" : contexto.trim();
        String texto = mensagem == null ? "" : mensagem;
        String nivelFormatado = String.format(Locale.ROOT, "%-" + LARGURA_NIVEL + "s", nivel);

        String horarioFormatado = colorir("[" + horario + "]", CINZA);
        String nivelColorido = colorir(nivelFormatado, cor + NEGRITO);
        String contextoColorido = colorir("[" + nomeContexto + "]", AZUL + NEGRITO);

        System.out.println(horarioFormatado + " " + nivelColorido + " " + contextoColorido + " " + texto);
    }

    private static String repetir(char caractere, int quantidade) {
        StringBuilder resultado = new StringBuilder(Math.max(0, quantidade));
        for (int i = 0; i < quantidade; i++) {
            resultado.append(caractere);
        }
        return resultado.toString();
    }

    private static String colorir(String texto, String codigo) {
        return coresAtivadas ? codigo + texto + RESET : texto;
    }

    private static boolean detectarSuporteACores() {
        String semCores = System.getenv("NO_COLOR");
        String terminal = System.getenv("TERM");
        return semCores == null && (terminal == null || !"dumb".equalsIgnoreCase(terminal));
    }
}
