# embiro

### Descrição:
  
Aplicação para divisão de valores de um evento coletivo

### Desafio

Distrubuir valores quebrados entre recebedores e pagantes

#### Versions

- Version #1.1.0

desafio

- Version #1.0.0
## Classe P - Pessoa 
Atributos
- nome
- valor
- pagante - o pagante entra na divisão o não pagante recebe o valor pago integral
- diff - valor a pagar ou receber
## Classe RP - relação entre quem recebe e quem paga
Atributos
- valor - resto caso haja, a ideia seria um subtotal.
- receber - pessoa que recebe
-  pagar - pessoa que paga
## Classe T - Tarefas 
Metodos
---
- getLista - busca lista de pessoas
  - retorna um ArrayList de pessoas
---
- calculaTotalMaisListaPagantes - ação confome nome
  - recebe a lista de pessoas e uma lista vazia de pagantes
  - altera a entrada lista de pagantes adicionando somente os pagantes
  - retorna um double com o total gasto
---
- calculaDividaIndividual - ação confome nome
  - recebe o total, a lista de pessoa e a lista de pagantes 
  - usa a lista de pagantes para calcular o valor por pessoa
  - calcula o valor que cada um tem que pagar ou receber e armazena no atributo diff da pessoa, alterando direto na entrada lista de pessoas
  - retorna o valor total por pessoa
---
- print - ação confome nome
  - imprime os valores 
---
- calcularReceberPagantesIgual
  - recebe a lista de pessoas e o a lista de relação entre quem recebe e quem paga
  - tenta encontrar pessoas que devem receber e pagar que tenham o mesmo valor, adicionar na lista de relação e remover da lista de pessoas para não tentar resolver de novo
  - retorna lista de pessoas que ainda não foram resolvidas
---
- calcularReceberPagantesSomaIgual
  - recebe a lista de pessoas e o a lista de relação entre quem recebe e quem paga
   - tenta encontrar pessoas que devem receber e duas que devem pagar, e a soma da divida tenham o mesmo valor da que tenha que receber, adicionar na lista de relação e remover da lista de pessoas para não tentar resolver de novo
   - retorna lista de pessoas que ainda não foram resolvidas 
  