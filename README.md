# 🖨️ Sistema de Controle de Impressora Elgin
### Controle de Impressora Térmica Elgin em Java
---

## 📘 Sobre o Projeto

Este projeto foi desenvolvido por Leonardo Callou, Sofia Pelario para se comunicar com uma impressora térmica **Elgin i8/i9** usando a biblioteca `E1_Impressora01.dll`. O sistema é um programa de console com login e um menu de operações: configurar e abrir a conexão, imprimir texto, QR Code, código de barras e cupons SAT, abrir a gaveta, emitir sinal sonoro, avançar o papel e consultar o status do papel. Faz parte da disciplina de **Programação Orientada a Objetos**.

---

## 🧰 Tecnologias Utilizadas

- **Java** (JDK 27, Oracle OpenJDK 27.0.1)
- **IntelliJ IDEA**
- **JNA 5.15.0** (comunicação com a DLL)
- **DLL E1_Impressora01** (SDK da Elgin)
- **Impressora Elgin [i8 ou i9]** (usada nos testes)

---

## 📁 Estrutura do Projeto

```
/src                      → Código-fonte
   Main.java              → Inicia o programa, faz o login e mostra o menu
   Usuario.java           → Guarda o login e a senha e confere se estão certos
   Impressora.java        → Guarda os dados da conexão e tem um método para cada comando
   ImpressoraDLL.java     → Ligação do Java com a DLL (fornecida, não alterada)
/Bibliotecas E1 Impressora → DLLs da Elgin (x64 e x86)
/libs Java                → jna-5.15.0.jar
XMLSAT.xml                → Cupom de venda de exemplo
CANC_SAT.xml              → Cupom de cancelamento de exemplo
```

---

## ⚙️ Instalação e Execução

### 1. Baixar o projeto
1. Acesse o repositório no GitHub.
2. Clique em **Code → Download ZIP**.
3. Extraia o arquivo.

### 2. Abrir no IntelliJ IDEA
1. **File → Open** e escolha a pasta extraída.
2. Em **File → Project Structure → Libraries**, confirme que o `jna-5.15.0.jar` está adicionado.
3. Conecte e ligue a impressora.
4. Abra `Main.java` e clique em ▶️ **Run**.

Observação: em versões recentes do Java pode aparecer um aviso sobre acesso nativo. Ele não impede o funcionamento. Para removê-lo, adicione `--enable-native-access=ALL-UNNAMED` em **Run → Edit Configurations → VM options**.

---

## 🔐 Login

- **Usuário:** `admin`
- **Senha:** `1234`
- São permitidas **3 tentativas**. A cada erro, o programa mostra quantas restam. Após a terceira, ele é encerrado.

---

## 📋 Funcionalidades do Menu

| Opção | O que faz |
|---|---|
| 1 | Configura a conexão (tipo, modelo, porta e parâmetro) |
| 2 | Abre a conexão com a impressora |
| 3 | Fecha a conexão |
| 4 | Imprime um texto (posição, estilo, tamanho e cópias) |
| 5 | Imprime um QR Code |
| 6 | Imprime um código de barras |
| 7 | Imprime o XML SAT (cupom de venda) |
| 8 | Imprime o XML de cancelamento SAT |
| 9 | Abre a gaveta |
| 10 | Emite sinal sonoro (bip) |
| 11 | Avança o papel |
| 12 | Mostra o status do papel |
| 0 | Sai do programa |

As opções de impressão só funcionam com a conexão aberta. Se ela estiver fechada, o programa avisa "Conexão fechada. Abra a conexão antes." e volta ao menu sem fazer perguntas.

---

## 🧪 Testes Realizados

Sem a impressora:

- Login com senha errada 3 vezes: mostrou as tentativas restantes e encerrou o programa.
- Login correto: entrou no menu.
- Letras e valores inválidos no menu: o programa avisou e não travou.
- Opções 4 a 12 sem conexão: avisou que a conexão está fechada, sem fazer perguntas.
- Abrir a conexão sem a impressora: a DLL foi carregada e o programa mostrou o código de erro.

Com a impressora: Não tivemos tempo para testar com a impresssora

---

## 📚 Documentação Adicional

- Manual da impressora Elgin
- Documentação do SDK E1_Impressora: https://elgindevelopercommunity.github.io/group___m1.html

---

## 👥 Autores


- [Leonardo Callou]
- [Sofia Pelario]
- [Yasmin Croti]

---

## Contribuições

Este projeto está aberto a melhorias e contribuições para
aprimorar o sistema de controle da impressora térmica Elgin.