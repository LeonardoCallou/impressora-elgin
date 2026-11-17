import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Usuario usuario = new Usuario("admin", "1234");
        Impressora impressora = new Impressora();

        if (!fazerLogin(sc, usuario)) {
            return;
        }
        executarMenu(sc, impressora);
        System.out.println("Programa encerrado.");
    }

    // Login com 3 tentativas
    private static boolean fazerLogin(Scanner sc, Usuario usuario) {
        final int MAX_TENTATIVAS = 3;
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            String login = lerTexto(sc, "Usuário: ");
            String senha = lerTexto(sc, "Senha: ");
            if (usuario.validar(login, senha)) {
                System.out.println("Login realizado com sucesso!");
                return true;
            }
            int restantes = MAX_TENTATIVAS - tentativa;
            if (restantes > 0) {
                System.out.println("Usuário ou senha incorretos. Tentativas restantes: " + restantes);
            }
        }
        System.out.println("Número máximo de tentativas atingido. Encerrando o programa.");
        return false;
    }

    // O menu aparece de novo até a pessoa escolher sair
    private static void executarMenu(Scanner sc, Impressora impressora) {
        int opcao;
        do {
            exibirMenu();
            opcao = lerInt(sc, "Escolha uma opção: ");
            switch (opcao) {
                case 1 -> opcaoConfigurar(sc, impressora);
                case 2 -> impressora.abrirConexao();
                case 3 -> impressora.fecharConexao();
                case 4 -> opcaoTexto(sc, impressora);
                case 5 -> opcaoQRCode(sc, impressora);
                case 6 -> opcaoCodigoBarras(sc, impressora);
                case 7 -> opcaoXMLSAT(sc, impressora);
                case 8 -> opcaoXMLCancelamento(sc, impressora);
                case 9 -> {
                    if (impressora.verificarConexao()) {
                        impressora.abrirGaveta();
                    }
                }
                case 10 -> opcaoBip(sc, impressora);
                case 11 -> opcaoAvancarPapel(sc, impressora);
                case 12 -> {
                    if (impressora.verificarConexao()) {
                        impressora.statusPapel();
                    }
                }
                case 0 -> {
                    if (impressora.isConexaoAberta()) {
                        impressora.fecharConexao();
                    }
                    System.out.println("Saindo...");
                }
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println("========== MENU ==========");
        System.out.println("1  - Configurar conexão");
        System.out.println("2  - Abrir conexão");
        System.out.println("3  - Fechar conexão");
        System.out.println("4  - Imprimir texto");
        System.out.println("5  - Imprimir QR Code");
        System.out.println("6  - Imprimir código de barras");
        System.out.println("7  - Imprimir XML SAT");
        System.out.println("8  - Imprimir XML de cancelamento SAT");
        System.out.println("9  - Abrir gaveta");
        System.out.println("10 - Sinal sonoro (bip)");
        System.out.println("11 - Avançar papel");
        System.out.println("12 - Status do papel");
        System.out.println("0  - Sair");
    }

    // Nas opções de impressão, a conexão é verificada antes de qualquer pergunta

    private static void opcaoConfigurar(Scanner sc, Impressora impressora) {
        if (impressora.isConexaoAberta()) {
            System.out.println("Feche a conexão antes de alterar a configuração.");
            return;
        }
        int tipo = lerIntNoIntervalo(sc, "Tipo (1=USB, 2=Serial, 3=Rede, 4=Bluetooth): ", 1, 4);
        String modelo = lerTexto(sc, "Modelo (ex.: i8 ou i9): ");
        String conexao = lerTexto(sc, "Conexão (USB, COM2 ou IP): ");
        int parametro = lerInt(sc, "Parâmetro (0 para USB; velocidade ou porta): ");
        impressora.configurar(tipo, modelo, conexao, parametro);
    }

    private static void opcaoTexto(Scanner sc, Impressora impressora) {
        if (!impressora.verificarConexao()) {
            return;
        }
        String texto = lerTexto(sc, "Texto a imprimir: ");
        int posicao = lerIntNoIntervalo(sc, "Posição (0=esquerda, 1=centro, 2=direita): ", 0, 2);
        int estilo = lerIntNoIntervalo(sc,
                "Estilo (0=fonte A, 1=fonte B, 2=sublinhado, 4=invertido, 8=negrito; some para combinar): ", 0, 15);
        int tamanho = lerIntNoIntervalo(sc, "Tamanho (0=normal, 1 a 7=maior): ", 0, 7);
        int copias = lerIntNoIntervalo(sc, "Quantidade de cópias (1 a 5): ", 1, 5);

        for (int i = 1; i <= copias; i++) {
            impressora.imprimirTexto(texto, posicao, estilo, tamanho);
        }
        impressora.cortarPapel();
    }

    private static void opcaoQRCode(Scanner sc, Impressora impressora) {
        if (!impressora.verificarConexao()) {
            return;
        }
        String dados = lerTexto(sc, "Conteúdo do QR Code: ");
        int tamanho = lerIntNoIntervalo(sc, "Tamanho (1 a 6): ", 1, 6);
        int nivel = lerIntNoIntervalo(sc, "Nível de correção (1 a 4): ", 1, 4);
        impressora.imprimirQRCode(dados, tamanho, nivel);
        impressora.cortarPapel();
    }

    private static void opcaoCodigoBarras(Scanner sc, Impressora impressora) {
        if (!impressora.verificarConexao()) {
            return;
        }
        int tipo = lerIntNoIntervalo(sc, "Tipo (0=UPC-A, 2=EAN13, 3=EAN8, 4=CODE 39, 8=CODE 128): ", 0, 8);
        String dados = lerTexto(sc, "Dados do código: ");
        if (tipo == 8 && !dados.startsWith("{")) {
            dados = "{A" + dados;   // o CODE 128 precisa do prefixo {A
        }
        int altura = lerIntNoIntervalo(sc, "Altura (1 a 255): ", 1, 255);
        int largura = lerIntNoIntervalo(sc, "Largura (1 a 6): ", 1, 6);
        int hri = lerIntNoIntervalo(sc, "Número junto (1=acima, 2=abaixo, 3=os dois, 4=não): ", 1, 4);
        impressora.imprimirCodigoBarras(tipo, dados, altura, largura, hri);
        impressora.cortarPapel();
    }

    private static void opcaoXMLSAT(Scanner sc, Impressora impressora) {
        if (!impressora.verificarConexao()) {
            return;
        }
        String caminho = lerTexto(sc, "Caminho do XML (Enter para XMLSAT.xml): ").trim();
        if (caminho.isEmpty()) {
            caminho = "XMLSAT.xml";
        }
        impressora.imprimirXMLSAT(caminho);
    }

    private static void opcaoXMLCancelamento(Scanner sc, Impressora impressora) {
        if (!impressora.verificarConexao()) {
            return;
        }
        String caminho = lerTexto(sc, "Caminho do XML (Enter para CANC_SAT.xml): ").trim();
        if (caminho.isEmpty()) {
            caminho = "CANC_SAT.xml";
        }
        impressora.imprimirXMLCancelamento(caminho);
    }

    private static void opcaoBip(Scanner sc, Impressora impressora) {
        if (!impressora.verificarConexao()) {
            return;
        }
        int quantidade = lerIntNoIntervalo(sc, "Quantos bips (1 a 5): ", 1, 5);
        for (int i = 1; i <= quantidade; i++) {
            impressora.sinalSonoro(1);
        }
    }

    private static void opcaoAvancarPapel(Scanner sc, Impressora impressora) {
        if (!impressora.verificarConexao()) {
            return;
        }
        int linhas = lerIntNoIntervalo(sc, "Quantas linhas avançar (1 a 255): ", 1, 255);
        impressora.avancarPapel(linhas);
    }

    private static String lerTexto(Scanner sc, String mensagem) {
        System.out.print(mensagem);
        return sc.nextLine();
    }

    // Repete a pergunta até a pessoa digitar um número
    private static int lerInt(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite apenas números.");
            }
        }
    }

    private static int lerIntNoIntervalo(Scanner sc, String mensagem, int min, int max) {
        while (true) {
            int valor = lerInt(sc, mensagem);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.println("Valor fora do intervalo. Digite de " + min + " a " + max + ".");
        }
    }
}