# ♟️ Jogo de Damas em Java

Implementação de um **jogo de Damas** em **Java com interface gráfica**, utilizando **persistência de objetos, tratamento de exceções e princípios de Programação Orientada a Objetos (POO)**.

O projeto demonstra conceitos importantes de desenvolvimento de software como **encapsulamento, herança, polimorfismo, manipulação de arquivos e arquitetura modular**.

Projeto desenvolvido por: 

- Cecília Lucchesi Mardegan (usuário: [ceciLcchM](https://github.com/ceciLcchM))
- Christine von Schmalz (usuário: [cvschmalz](https://github.com/cvschmalz))
- Erick Maestri de Souza (usuário: [ErickMS18](https://github.com/ErickMS18))

---

## 🚀 Tecnologias e Conceitos

- Java  
- Interface gráfica (GUI)  
- Programação Orientada a Objetos  
- Encapsulamento, Herança e Polimorfismo  
- Persistência de objetos em **arquivo binário**  
- Registro de logs em **CSV**  
- Tratamento de **exceções customizadas**  
- Organização em **pacotes**

---

## 🧩 Estrutura do Projeto

O sistema é dividido em dois programas principais.

### P1 — Inicialização de Dados

Responsável por:

- Ler dados iniciais de um **arquivo texto**
- Criar objetos persistentes
- Salvar esses objetos em **formato binário**

Esses dados representam a **configuração inicial do jogo**, incluindo jogadores e peças.

---

### P2 — Execução do Jogo

Responsável por:

- Restaurar os dados persistidos
- Exibir o **tabuleiro com interface gráfica**
- Permitir interação entre dois jogadores
- Registrar os movimentos da partida

Os movimentos são armazenados em um **arquivo CSV**, funcionando como log da partida.

---

## ⚙️ Funcionalidades

- Interface gráfica para o tabuleiro  
- Movimentação das peças  
- Registro automático das jogadas  
- Persistência de dados  
- Tratamento de erros com exceções personalizadas  

---

## ▶️ Como Executar

1. Execute **P1** para gerar os dados iniciais do jogo.  
2. Execute **P2** para iniciar a interface gráfica e jogar.

Durante a partida será gerado um arquivo:

```
game_log.csv
```

com o histórico de jogadas.

---

## 📁 Arquivos Gerados

| Arquivo | Descrição |
|------|------|
| `board.dat` | Estado inicial do jogo em formato binário |
| `game_log.csv` | Log das jogadas da partida |

---

## 💡 Objetivo do Projeto

Aplicar conceitos de **POO, persistência de dados e manipulação de arquivos** em uma aplicação interativa com interface gráfica.
