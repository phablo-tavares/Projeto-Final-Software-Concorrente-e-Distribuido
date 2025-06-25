# Projeto de Mensageria com Java e Apache Kafka

Este projeto é uma solução desenvolvida para a disciplina de Software Concorrente e Distribuído, demonstrando um sistema de processamento de eventos em tempo real utilizando Java, Spring Boot e Apache Kafka.

## 1. Explicação do Projeto

O objetivo do sistema é simular um fluxo de processamento de pedidos de uma plataforma de e-commerce. A arquitetura é baseada em eventos e composta por três serviços lógicos que se comunicam através de um broker Kafka:

* **Order-Service**: Expõe uma API REST para receber novos pedidos. Ao receber um pedido, ele o publica no tópico `orders` do Kafka. 
* **Inventory-Service**: Consome os pedidos do tópico `orders`, simula uma verificação de estoque em memória e publica o resultado (sucesso ou falha) no tópico `inventory-events`.
* **Notification-Service**: Consome os eventos de resultado do tópico `inventory-events` e simula o envio de uma notificação para o cliente (registrando a ação no console). 

O fluxo de eventos completo é:

`API REST` → `Order-Service` → **Kafka (tópico `orders`)** → `Inventory-Service` → **Kafka (tópico `inventory-events`)** → `Notification-Service` → `Console`

## 2. Requisitos Não-Funcionais

Respostas objetivas para as questões propostas na definição do trabalho.

### Escalabilidade
*A escalabilidade no Kafka é alcançada principalmente através do conceito de **partições**.*

Para escalar o sistema, poderíamos aumentar o número de partições dos tópicos (ex: `orders`). Em seguida, poderíamos iniciar múltiplas instâncias do serviço consumidor (ex: `Inventory-Service`), todas com o mesmo `group.id`. O Kafka automaticamente distribuiria as partições entre as instâncias disponíveis, permitindo que os pedidos fossem processados em paralelo, aumentando a vazão (throughput) do sistema.

### Tolerância à Falha
*Tolerância à falha é a capacidade de um sistema continuar operando corretamente mesmo após a falha de um ou mais de seus componentes.* 

O Kafka lida com isso através da **replicação de dados**. Ao criar um tópico, podemos definir um `replication-factor`. Se tivermos um cluster com 3 brokers (servidores) Kafka e um tópico com fator de replicação 3, cada partição desse tópico terá uma cópia em cada um dos 3 brokers.

* **Situação de Falha:** Se um dos servidores do cluster Kafka falhar (ex: queda de energia, crash de hardware), o sistema não para. As cópias das partições que estavam naquele servidor continuam disponíveis nos outros dois brokers. O Kafka automaticamente elege uma nova "partição líder" a partir das réplicas saudáveis, e os produtores e consumidores continuam a operar normalmente, sem perda de dados.

### Idempotência
*Idempotência, no contexto de mensageria, é a garantia de que o processamento de uma mesma mensagem várias vezes produz o mesmo resultado que processá-la uma única vez, evitando duplicatas.* 

Isso é crucial em casos de falha de rede, onde um produtor pode reenviar uma mensagem sem ter certeza se a primeira tentativa foi recebida. Para garantir a idempotência no Kafka, basta configurar o produtor com a seguinte propriedade:

`enable.idempotence = true`

Com essa configuração, o Kafka associa um ID de produtor (PID) e um número de sequência a cada mensagem. Se o broker receber uma mensagem com um PID e número de sequência que ele já processou, ele a descarta, garantindo a semântica de processamento "exatamente uma vez" (exactly-once) por partição.

## 3. Como Rodar o Sistema

Siga os passos abaixo para executar a solução completa.

#### Pré-requisitos
* Java 21
* Maven
* Docker e Docker Compose

#### Passo 1: Iniciar a Infraestrutura (Kafka e Zookeeper)

O arquivo `docker-compose.yml` na raiz do projeto automatiza a criação do ambiente. Ele inicia um contêiner para o Kafka e um para o Zookeeper.

No terminal, na pasta raiz do projeto, execute:
```bash
docker-compose up -d
```
Isso iniciará os serviços em segundo plano.

#### Passo 2: Iniciar a Aplicação Spring Boot

A aplicação Spring é responsável por se conectar ao Kafka e criar os tópicos (`orders` e `inventory-events`) programaticamente na inicialização.

No terminal, na pasta raiz do projeto, execute:
```bash
mvn spring-boot:run
```
Aguarde até que o console mostre a mensagem final `Started ProjetoFinalScdApplication in X.XXX seconds`.

#### Passo 3: Testar a Aplicação

Com tudo rodando, você pode enviar um pedido de teste via `curl` em um novo terminal.

**Exemplo de pedido com sucesso:**
```bash
curl -X POST http://localhost:8080/orders \
-H "Content-Type: application/json" \
-d '["teclado-mecanico", "mouse-gamer"]'
```