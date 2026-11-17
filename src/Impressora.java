import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class Impressora {
    // Assinatura do QR Code do XML de cancelamento
    private static final String ASS_QRCODE = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

    private int tipo = 1;
    private String modelo = "i9";
    private String conexao = "USB";
    private int parametro = 0;
    private boolean conexaoAberta = false;

    public boolean isConexaoAberta() {
        return conexaoAberta;
    }

    public void configurar(int tipo, String modelo, String conexao, int parametro) {
        this.tipo = tipo;
        this.modelo = modelo;
        this.conexao = conexao;
        this.parametro = parametro;
        System.out.println("Configuração salva: tipo=" + tipo + ", modelo=" + modelo
                + ", conexão=" + conexao + ", parâmetro=" + parametro);
    }

    public void abrirConexao() {
        if (conexaoAberta) {
            System.out.println("A conexão já está aberta.");
            return;
        }
        try {
            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
            if (retorno == 0) {
                conexaoAberta = true;
                System.out.println("Conexão aberta com sucesso!");
            } else {
                System.out.println("Erro ao abrir a conexão. Código: " + retorno);
            }
        } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
            // a DLL não foi encontrada ou não carregou
            System.out.println("Não foi possível carregar a DLL: " + e.getMessage());
        }
    }

    public void fecharConexao() {
        if (!conexaoAberta) {
            System.out.println("A conexão já está fechada.");
            return;
        }
        if (conferir(ImpressoraDLL.INSTANCE.FechaConexaoImpressora(), "Fechar conexão")) {
            conexaoAberta = false;
        }
    }

    // Avisa se a conexão está fechada
    public boolean verificarConexao() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes.");
            return false;
        }
        return true;
    }

    // 0 = sucesso, outro valor = erro
    private boolean conferir(int retorno, String acao) {
        if (retorno == 0) {
            System.out.println(acao + ": sucesso!");
            return true;
        }
        System.out.println("Erro em " + acao + ". Código: " + retorno);
        return false;
    }

    // Nesse comando, só número negativo é erro
    public void imprimirTexto(String dados, int posicao, int estilo, int tamanho) {
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto(dados, posicao, estilo, tamanho);
        if (retorno < 0) {
            System.out.println("Erro ao imprimir texto. Código: " + retorno);
        } else {
            System.out.println("Texto enviado com sucesso!");
        }
    }

    public void cortarPapel() {
        conferir(ImpressoraDLL.INSTANCE.Corte(5), "Corte do papel");
    }

    public void imprimirQRCode(String dados, int tamanho, int nivelCorrecao) {
        conferir(ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, tamanho, nivelCorrecao), "Impressão do QR Code");
    }

    public void imprimirCodigoBarras(int tipoCodigo, String dados, int altura, int largura, int hri) {
        conferir(ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(tipoCodigo, dados, altura, largura, hri),
                "Impressão do código de barras");
    }

    public void imprimirXMLSAT(String caminho) {
        String xml = lerArquivo(caminho);
        if (xml == null) {
            return;
        }
        conferir(ImpressoraDLL.INSTANCE.ImprimeXMLSAT(xml, 0), "Impressão do XML SAT");
    }

    public void imprimirXMLCancelamento(String caminho) {
        String xml = lerArquivo(caminho);
        if (xml == null) {
            return;
        }
        conferir(ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(xml, ASS_QRCODE, 0),
                "Impressão do XML de cancelamento");
    }

    public void abrirGaveta() {
        conferir(ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10), "Abertura da gaveta");
    }

    public void sinalSonoro(int quantidade) {
        conferir(ImpressoraDLL.INSTANCE.SinalSonoro(quantidade, 50, 5), "Sinal sonoro");
    }

    public void avancarPapel(int linhas) {
        conferir(ImpressoraDLL.INSTANCE.AvancaPapel(linhas), "Avanço do papel");
    }

    public void statusPapel() {
        int retorno = ImpressoraDLL.INSTANCE.StatusImpressora(3);
        switch (retorno) {
            case 5 -> System.out.println("Papel: tem papel.");
            case 6 -> System.out.println("Papel: está acabando.");
            case 7 -> System.out.println("Papel: sem papel.");
            default -> System.out.println("Não foi possível ler o sensor de papel (retorno " + retorno
                    + "). Nem todo modelo suporta esse sensor.");
        }
    }

    // Lê o XML como texto; se não existir, avisa e devolve null
    private String lerArquivo(String caminho) {
        try {
            return new String(Files.readAllBytes(Paths.get(caminho)), StandardCharsets.UTF_8);
        } catch (IOException | InvalidPathException e) {
            System.out.println("Arquivo não encontrado ou ilegível: " + caminho);
            return null;
        }
    }
}