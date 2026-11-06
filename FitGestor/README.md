# FitGestor App

Aplicativo Android para gestão de alunos, controle financeiro e acompanhamento de mensalidades em academias, ginásios e centros de treinamento.

## Descrição do projeto
O FitGestor App foi desenvolvido para facilitar a rotina administrativa de gestão de alunos. A aplicação permite cadastrar alunos, controlar planos e vencimentos, visualizar o status financeiro de cada matrícula e registrar pagamentos de forma simples e prática.

O sistema foi pensado para uso em ambientes pequenos e médios, onde o gestor precisa acompanhar rapidamente quem está em dia e quem está inadimplente, sem depender de planilhas ou registros manuais.

## Objetivo principal
Organizar a gestão de alunos e mensalidades por meio de uma interface mobile simples, funcional e eficiente, com armazenamento local em banco de dados SQLite.

## Funcionalidades
- Cadastro de aluno com validação de CPF;
- definição de plano e valor da mensalidade;
- seleção do dia de vencimento;
- busca por nome ou CPF;
- filtros por situação do aluno;
- indicadores de alunos regulares, inadimplentes e totais;
- baixa de pagamento com escolha da forma de pagamento;
- compartilhamento de comprovante de pagamento;
- arquivamento/reativação de aluno sem exclusão definitiva;
- armazenamento local de dados.

## Requisitos prévios
Antes de executar o projeto, certifique-se de ter instalado:
- Android Studio;
- JDK 17 ou superior;
- Android SDK configurado;
- Emulador Android ou dispositivo físico conectado;
- Git (opcional, para versionamento).

## Passo a passo para executar o projeto

### 1. Clonar ou abrir o projeto
Abra a pasta do projeto no Android Studio:

`FitGestor`

Ou clone o repositório e abra a pasta do Android Studio.

### 2. Sincronizar o Gradle
Ao abrir o projeto, aguarde o Android Studio sincronizar as dependências do Gradle.

Se necessário, clique em:
- File > Sync Project with Gradle Files

### 3. Verificar SDK
Confirme que o Android SDK está instalado e configurado corretamente.

### 4. Conectar um emulador ou dispositivo
Você pode executar a aplicação em:
- emulador Android;
- dispositivo Android físico conectado via USB;
- Android Studio Device Manager.

### 5. Executar a aplicação
No Android Studio:
- selecione o módulo `app`;
- escolha o emulador ou dispositivo;
- clique em Run > Run 'app'.

### 6. Testar as principais funcionalidades
Depois de abrir o aplicativo, teste:
- cadastro de aluno;
- busca por nome ou CPF;
- filtros por status;
- baixa de pagamento;
- compartilhamento do comprovante;
- arquivamento/reativação de aluno.

## Estrutura do projeto

```text
FitGestor/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/fitgestor/
│   │   │   │   ├── Aluno.java
│   │   │   │   ├── AlunoAdapter.java
│   │   │   │   ├── CadastroAlunoActivity.java
│   │   │   │   ├── DatabaseHelper.java
│   │   │   │   └── MainActivity.java
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       ├── drawable/
│   │   │       └── values/
│   └── build.gradle
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
├── local.properties
└── README.md
```

## Principais classes
- `MainActivity`: tela principal com listagem, filtros e indicadores;
- `CadastroAlunoActivity`: formulário de cadastro do aluno;
- `DatabaseHelper`: responsável pela persistência em SQLite;
- `Aluno`: modelo de dados do aluno;
- `AlunoAdapter`: adapter para a lista de alunos na RecyclerView.

## Banco de dados
O projeto utiliza SQLite para persistir os dados localmente no dispositivo. A tabela principal é a tabela `alunos`, contendo informações como:
- id;
- nome;
- CPF;
- telefone;
- plano;
- valor;
- dia de vencimento;
- status de pendência;
- status de atividade;
- data de início;
- último pagamento;
- forma de pagamento.

## Regras de negócio implementadas
- não pode haver dois alunos ativos com o mesmo CPF;
- aluno pendente deve ser identificado como inadimplente;
- a inativação do aluno não exclui seu histórico;
- a baixa de pagamento atualiza status e forma de pagamento;
- o comprovante pode ser compartilhado por aplicativos externos.

## Fluxo de uso da aplicação
1. O usuário acessa a tela inicial.
2. Cadastra um aluno com nome, CPF e telefone.
3. Define plano e vencimento.
4. O app salva as informações localmente.
5. O responsável pode buscar e filtrar alunos.
6. Quando o pagamento é confirmado, baixa a mensalidade.
7. O comprovante é gerado e pode ser enviado.
8. O aluno pode ser arquivado ou reativado conforme necessidade.

## Tecnologias utilizadas
- Java;
- Android Studio;
- AndroidX;
- RecyclerView;
- SQLite;
- Gradle.

## Observações importantes
- O projeto foi validado em ambiente local e compilou com sucesso usando Gradle.
- A aplicação funciona com dados locais, sem backend externo.
- A estrutura é adequada para evoluções futuras, como autenticação, relatórios, exportação e integração com API.

## Link do GitHub
Insira o link do repositório público do projeto aqui:

`https://github.com/SEU_USUARIO/SEU_REPOSITORIO`

## Autor
Josiel dos Santos

## Matrícula
202511319
