# CineStar - Gerenciamento de Filmes

Aplicação de console em Java para cadastrar e gerenciar uma coleção de filmes. O projeto foi desenvolvido para praticar CRUD, orientação a objetos, organização em pacotes, validação de entradas e persistência em arquivo.

## Funcionalidades

- Cadastrar filmes com título, diretor, ano e gênero.
- Listar todos os filmes.
- Buscar filmes por título, gênero ou ID.
- Editar os dados de um filme.
- Excluir filmes com confirmação.
- Validar campos obrigatórios, ano de lançamento e títulos sem algarismos.
- Salvar as alterações em `filmes.txt`, preservando os dados ao fechar e abrir o programa.
- Pesquisar títulos ignorando diferenças entre letras maiúsculas/minúsculas e acentos.



## Requisitos

- JDK instalado, com `java` e `javac` disponíveis no terminal.
- Para executar pelo VS Code, a extensão **Extension Pack for Java**.

## Estrutura do projeto

```text
.
├── .vscode/
│   ├── launch.json
│   └── settings.json
├── src/
│   └── br/com/cinestar/
│       ├── Main.java
│       ├── model/
│       │   ├── Filme.java
│       │   └── Genero.java
│       └── repository/
│           └── FilmeRepository.java
└── README.md
```

`filmes.txt` é criado automaticamente na pasta de trabalho a partir da qual o programa é iniciado.

## Executar pelo terminal no Windows

Abra o terminal integrado do VS Code na pasta do projeto. Para compilar e iniciar:

```powershell
chcp 65001
javac -encoding UTF-8 -d out src\br\com\cinestar\Main.java src\br\com\cinestar\model\Filme.java src\br\com\cinestar\model\Genero.java src\br\com\cinestar\repository\FilmeRepository.java
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp out br.com.cinestar.Main
```

Quando o menu aparecer, digite o número da opção e pressione Enter. Após cada operação, informe `s` para limpar a tela e voltar ao menu ou `n` para encerrar.

## Executar pelo VS Code

1. Abra a pasta do projeto no VS Code.
2. Abra **Run and Debug**.
3. Selecione **CineStar (UTF-8)**.
4. Pressione **F5**.

As configurações em `.vscode/settings.json` e `.vscode/launch.json` ajustam o terminal integrado e a execução Java para UTF-8. Se já houver um terminal integrado aberto, feche-o e abra um novo para aplicar o perfil de terminal.

## Dados e limitações

- Os registros são armazenados em texto UTF-8 no arquivo `filmes.txt`; este projeto não usa um banco de dados relacional.
- Cadastro, edição e exclusão atualizam esse arquivo. Os dados permanecem disponíveis após reiniciar o programa.
- Para recomeçar com os exemplos iniciais, encerre o programa e remova `filmes.txt`. Na próxima execução, o arquivo será recriado com os dados de exemplo.
- Títulos não podem conter algarismos; escreva números por extenso.
- O ano aceito vai de 1888 até dez anos após o ano atual.
