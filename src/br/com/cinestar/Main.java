package br.com.cinestar;

import br.com.cinestar.model.Filme;
import br.com.cinestar.model.Genero;
import br.com.cinestar.repository.FilmeRepository;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner;
    private static final FilmeRepository repository = new FilmeRepository();

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(
                new FileOutputStream(FileDescriptor.out),
                true,
                StandardCharsets.UTF_8.name()
        ));
        scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        boolean executando = true;
        repository.carregar();

        System.out.println("+--------------------------------------+");
        System.out.println("|           CINESTAR - FILMES          |");
        System.out.println("+--------------------------------------+");
        System.out.println(repository.listarTodos().size() + " filme(s) carregado(s).");


        while (executando) {
            exibirMenu();
            int opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    cadastrarFilme();
                    break;
                case 2:
                    listarFilmes(repository.listarTodos());
                    break;
                case 3:
                    buscarPorTitulo();
                    break;
                case 4:
                    buscarPorGenero();
                    break;
                case 5:
                    buscarPorId();
                    break;
                case 6:
                    editarFilme();
                    break;
                case 7:
                    excluirFilme();
                    break;
                case 0:
                    executando = false;
                    System.out.println("\n Obrigado por usar o CineStar!");
                    break;
                default:
                    System.out.println("\n Opção inválida. Escolha uma opção do menu.");
            }

            if (executando) {
                if (desejaContinuar()) {
                    limparTela();
                } else {
                    executando = false;
                    System.out.println("\nObrigado por usar o CineStar!");
                }
            }
        }

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n--------------------------------------");
        System.out.println(" MENU PRINCIPAL");
        System.out.println("--------------------------------------");
        System.out.println("1.  Cadastrar filme");
        System.out.println("2.  Listar todos os filmes");
        System.out.println("3.  Buscar por título");
        System.out.println("4.  Buscar por gênero");
        System.out.println("5.  Buscar por ID");
        System.out.println("6.  Editar filme");
        System.out.println("7.  Excluir filme");
        System.out.println("0.  Sair");
        System.out.println("--------------------------------------");
    }

    private static void cadastrarFilme() throws IOException {
        System.out.println("\n CADASTRO DE FILME");
        String titulo = lerTituloObrigatorio("Título:");
        String diretor = lerTextoObrigatorio("Diretor(a): ");
        int ano = lerAno("Ano de lançamento: ");
        Genero genero = escolherGenero(null);

        Filme filme = repository.salvar(titulo, diretor, ano, genero);
        System.out.println("\n Filme cadastrado com sucesso!");
        System.out.println(filme);
    }

    private static void buscarPorTitulo() {
        System.out.println("\n BUSCA POR TÍTULO");
        String titulo = lerTextoObrigatorio("Digite parte ou todo o título: ");
        listarFilmes(repository.buscarPorTitulo(titulo));
    }

    private static void buscarPorGenero() {
        System.out.println("\n BUSCA POR GÊNERO");
        Genero genero = escolherGenero(null);
        listarFilmes(repository.buscarPorGenero(genero));
    }

    private static void buscarPorId() {
        System.out.println("\n BUSCA POR ID");
        int id = lerInteiro("Digite o ID do filme: ");
        Filme filme = repository.buscarPorId(id);

        if (filme == null) {
            System.out.println(" Nenhum filme encontrado com esse ID.");
        } else {
            System.out.println("\n Filme encontrado:");
            System.out.println(filme);
        }
    }

    private static void editarFilme() throws IOException {
        System.out.println("\nEDIÇÃO DE FILME");
        int id = lerInteiro("Digite o ID do filme que deseja editar: ");
        Filme filme = repository.buscarPorId(id);

        if (filme == null) {
            System.out.println("Nenhum filme encontrado com esse ID.");
            return;
        }

        System.out.println("\nFilme atual:");
        System.out.println(filme);
        System.out.println("\nPressione Enter para manter o valor atual.");

        String titulo = lerTituloOpcional(
                "Título [" + filme.getTitulo() + "] (Enter mantém; números por extenso): "
        );
        String diretor = lerTextoOpcional("Diretor(a) [" + filme.getDiretor() + "]: ");
        Integer ano = lerAnoOpcional("Ano [" + filme.getAno() + "]: ");
        String novoTitulo = titulo.isEmpty() ? filme.getTitulo() : titulo;
        String novoDiretor = diretor.isEmpty() ? filme.getDiretor() : diretor;
        int novoAno = ano == null ? filme.getAno() : ano;
        Genero novoGenero = escolherGenero(filme.getGenero());
        repository.atualizar(id, novoTitulo, novoDiretor, novoAno, novoGenero);

        System.out.println("\nFilme atualizado com sucesso!");
        System.out.println(repository.buscarPorId(id));
    }

    private static void excluirFilme() throws IOException {
        System.out.println("\nEXCLUSÃO DE FILME");
        int id = lerInteiro("Digite o ID do filme que deseja excluir: ");
        Filme filme = repository.buscarPorId(id);

        if (filme == null) {
            System.out.println("Nenhum filme encontrado com esse ID.");
            return;
        }

        System.out.println("\nFilme selecionado:");
        System.out.println(filme);
        String confirmacao = lerTextoOpcional("Tem certeza que deseja excluir? (s/n): ");

        if (confirmacao.equalsIgnoreCase("s")) {
            repository.excluir(id);
            System.out.println("Filme excluído com sucesso.");
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }

    private static void listarFilmes(List<Filme> filmes) {
        System.out.println("\nRESULTADO");
        if (filmes.isEmpty()) {
            System.out.println("Nenhum filme encontrado. Cadastre um filme ou tente outra busca.");
            return;
        }

        System.out.println("Encontrado(s): " + filmes.size());
        for (Filme filme : filmes) {
            System.out.println("--------------------------------------");
            System.out.println(filme);
        }
        System.out.println("--------------------------------------");
    }

    private static Genero escolherGenero(Genero generoAtual) {
        Genero[] generos = Genero.values();
        System.out.println("\nGêneros disponíveis:");
        for (int i = 0; i < generos.length; i++) {
            String atual = generos[i] == generoAtual ? " (atual)" : "";
            System.out.printf("%d. %s%s%n", i + 1, generos[i], atual);
        }

        while (true) {
            String entrada = lerTextoOpcional(
                    generoAtual == null
                            ? "Escolha o número do gênero: "
                            : "Escolha o número do gênero (Enter para manter): "
            );

            if (entrada.isEmpty() && generoAtual != null) {
                return generoAtual;
            }

            try {
                int opcao = Integer.parseInt(entrada);
                if (opcao >= 1 && opcao <= generos.length) {
                    return generos[opcao - 1];
                }
            } catch (NumberFormatException ignored) {
                // A entrada inválida será solicitada novamente.
            }

            System.out.println("Escolha um número entre 1 e " + generos.length + ".");
        }
    }

    private static int lerAno(String mensagem) {
        while (true) {
            String entrada = lerTextoOpcional(mensagem);
            try {
                return validarAno(entrada);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static Integer lerAnoOpcional(String mensagem) {
        while (true) {
            String entrada = lerTextoOpcional(mensagem);
            if (entrada.isEmpty()) {
                return null;
            }
            try {
                return validarAno(entrada);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static int validarAno(String entrada) {
        try {
            int ano = Integer.parseInt(entrada);
            int anoAtual = java.time.Year.now().getValue();
            if (ano < 1888 || ano > anoAtual + 10) {
                throw new IllegalArgumentException(
                        "Informe um ano entre 1888 e " + (anoAtual + 10) + "."
                );
            }
            return ano;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Digite um ano válido.");
        }
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            String entrada = lerTextoOpcional(mensagem);
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private static String lerTextoObrigatorio(String mensagem) {
        while (true) {
            String entrada = lerTextoOpcional(mensagem);
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("Este campo não pode ficar vazio.");
        }
    }

    private static String lerTituloObrigatorio(String mensagem) {
        while (true) {
            String titulo = lerTextoObrigatorio(mensagem);
            if (!contemAlgarismo(titulo)) {
                return titulo;
            }
            System.out.println("O título não pode conter algarismos. Escreva os números por extenso.");
        }
    }

    private static String lerTituloOpcional(String mensagem) {
        while (true) {
            String titulo = lerTextoOpcional(mensagem);
            if (titulo.isEmpty() || !contemAlgarismo(titulo)) {
                return titulo;
            }
            System.out.println("O título não pode conter algarismos. Escreva os números por extenso.");
        }
    }

    private static boolean contemAlgarismo(String texto) {
        return texto.codePoints().anyMatch(Character::isDigit);
    }

    private static boolean desejaContinuar() {
        while (true) {
            String resposta = lerTextoOpcional("\nDeseja realizar outra operação? (s/n): ");
            if (resposta.equalsIgnoreCase("s")) {
                return true;
            }
            if (resposta.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Digite s para continuar ou n para sair.");
        }
    }

    private static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static String lerTextoOpcional(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }
}
