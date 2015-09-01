# AkkaMessageBenchmark
Benchmarking of an Akka Cluster using Slurm.

01/09/2015 - Acertado a geração de arquivos CSV para levantamento estatistico.
ARTIFICE CLUSTER v0.3.4.5
> - Modificar o momento da coleta da terceira timestamp. (De: durante envio para DBActor; Para: dentro do destinatario, no último momento do processamento)
> PENDENTE:
> + Medir o tempo de inacessibilidade dos atores;

31/08/2015 - Melhorado o tratamento estatístico dos backends
ARTIFICE CLUSTER v0.3.4.4
> - Desativada a exibição dos registros encontrados no banco dentro de StatisticsAnaliser;
> - Alterado mecanismo de finalização de simulaçao - De shutdown() para stop();
> - Migrada a execução do StatisticsAnaliser() para método postStop();
> - Migrada a execução do DBCleaner de frontend para backends;
> - Implementado o mecanismo de avaliação de perda de mensagens;
> PENDENTE
> + Pesquisar envio de mensagens via UDP, para acelerar envio de mensagens;
> + Estudar a migração do registro da segunda timestamp de Backend para Backend.Mailbox;
> + Investigar nó UNREACHABLE (10.1.255.222:2551 (localBackend) proximo ao fim da simulaçao;

27/08/2015 - Simulação propriamente iniciada
> Corrigido o bug dos backends que não iniciam (inserido loop para enviar mensagem “startSimulation”)

25/08/2015b - Investigação dos bugs nos backends
> - Melhorado o log do DBActor.
> PENDENTE:
> + Investigar o problema das mensagens só serem enviadas por IP com final .220

25/08/2015a - Simulação distribuída funcional
ARTIFICE CLUSTER v0.3.4.1
> - Migrada a configuração dos nós do cluster de application.conf para artifice.xml;
> - Migrada a configuração do numero minimo de backends de application.conf para artifice.xml;
> - Adicionada a configuração da duraçao da simulação em artifice.xml;
> - Adicionados parametros para arquivo XML: hosts e #Backends;
> - Atualizado pom.xml para utilizar versão postgres conforme cluster;
> - Corrigido bug dos 2 unicos frontends (configuracao importada do arquivo XML);
> PENDENTE:
> + Alterar o StatisticsAnaliser para pegar dados dos outros backends tambem (de local para distribuido);
> + Pesquisar envio de mensagens via UDP, para acelerar envio de mensagens;
> + Implementar mecanismo de avaliacao de perda de mensagens;
> + Estudar a migração do registro da segunda timestamp de Backend para Backend.Mailbox;
> + Sincronizar os backends para iniciarem juntos;
> + Investigar nó UNREACHABLE (10.1.255.222:2551 (localBackend) proximo ao fim da simulaçao;

24/08/2015 - Correção do roteamento nos backends
ARTIFICE CLUSTER v0.3.4
> > - Incluida a RoutedSenderMessage, para identificar mensagens vindas de outros backends;
> > - Incluida a CreationOrder para orientar a criacao de componentes na simulacao;
> > - Alterado o mecanismo de criacao de componentes (originalmente, criados sob demanda. Atualmente, criados de uma vez);
> > - Alterado o application.conf para contemplar apenas as maquinas utilizadas;
> > - Alterada a condicao de inicio para o StatisticsAnaliser (de: assim que nova thread for aberta. Para: assim que shutdown foi enviado para backends);
> > OBS.: BANCO LOCAL SENDO UTILIZADO.
> > PENDENTE:
> > + Alterar o StatisticsAnaliser para pegar dados dos outros backends tambem (de local para distribuido);
> > + Pesquisar envio de mensagens via UDP, para acelerar envio de mensagens;
> > + Implementar mecanismo de avaliacao de perda de mensagens.

16/08/2015 - Remoção de testes antigos, acerto das condições de início e parada, mensuração dos tempos de envio
6 commits diferentes.
> ARTIFICE CLUSTER v0.3.3
> > - Acertada a Mailbox para encaminhar StampedSenderMessage;
> > - Adaptado o backend para adicionar estampa assim que mensagem chega para ser encaminhada;
> > - Adaptados criatura e cacto para responder apropriadamente a chegada da StampedSenderMessage.
> > + TESTE FUNCIONAL COM TIMESTAMPS FUNCIONANDO APROPRIADAMENTE xD
> 
> ARTIFICE CLUSTER v0.3.2.1
> > - Alteradas as classes SenderMessage, StampedSenderMessage e ReceiverMessage para implementarem Serializable;
> > - Modificado o frontend para agendar shutdown apenas depois das atividades comecarem;
> > - Modificado o backend para notificar o frontend de roteadores construidos;
> > - Modificado o frontend para autorizar inicio de simulacao quando roteadores estiverem construidos;
> > - Modificado o backend para propagar autorizacao de inicio para seus filhos;
> > - Modificados os ArtificeActors para iniciar suas atividades apenas mediante autorizacao.
> > PENDENTE:
> > + Diferenciar mensagem vinda de backend e mensagem vinda de agente interno.

> ARTIFICE CLUSTER v0.3.2
> > - Removidos todos os pacotes antigos (AkkaMessageBenchmark, artificeCluster, artificeClusterBroken);
> > - Criado o pacote Artifice;
> > - Movidos os pacotes relacionados para dentro do pacote Artifice;
> > - Criado o pacote Artifice/Tools e movidas as classes StatisticsAnaliser, DataExtractor, DBCleaner, MetricsListener;
> > - Mantidas as mudancas e acertos do Cluster para iniciar no recebimento de start e finalizar no recebimento de shutdown.
> > PENDENTES:
> > + Configuracao dos roteadores interno e externo(backends).
> 
> > Removidos testes inutilizados (pastas AkkaMessageBenchmark, ArtificeC…
> > …lusterBroken e artificeCluster)
> 
> > Removendo .classes 
> 
> > Separando pacotes com Novo Cluster v0.3.1 

12/07/2015 - Reativação do Akka Cluster
Removido artifice.xml, que nao era usado. 
ARTIFICE CLUSTER v0.2
> - Ativado o uso do Cluster
> - Modificados os arquivos .conf
> - Incluido um novo package de testes para o teste com o Akka Cluster (artificeCluster)
> - Alterado o nome dos backends para contemplar o nome da maquina sendo usada e a porta
> - Incluido roteador broadcast no frontend
> PENDENTE:
> + acertar o uso das timestamps
> + configurar as condições de parada
> + acertar o uso do banco de dados

09/07/2015 - Inserção dos parâmetros do banco no artifice.xml
ARTIFICE CLUSTER v0.1
> - Acertadas mensagens de erro para problemas de conexao com o banco. Arquivo externo artifice.xml funcional e integrado.

08/07/2015 - Inserçao do artifice.xml e customização do pom.xml
ARTIFICE CLUSTER v0.1b
> - Adicionado um arquivo de configuracao, de onde sao lidas as configuracoes de banco de dados: artifice.xml
> - Modificado o pom.xml para que o maven gere um JAR compativel com o cluster ao final de cada simulacao
> - Criada a classe DataExtractor para realizar a extracao dos dados xml
> - Roteador dos backends foi corrigido para adicionar os routees dentro do metodo prestart

22/06/2015 - Múltiplos CSV e testes cíclicos com timestamp
Tester com Variacao de N atores e geracao automatica de multiplos CSV
> - Adicionado um loop externo que varia o numero de Criaturas e Cactus, e executa a simulacao, com a exportacao de dados inclusa, para cada variacao: 2^1 ~ 2^6 atores.
> - Alterado o padrao de nomenclatura dos testes para incluir os parametros (ageXcacY, onde X é o numero de criaturas e Y é o numero de criaturas)
> - Alterado o tipo de scheduler do scheduler automatico (a cada N ms) para o scheduler on-demand (faz um novo schedule a cada vez que consome o aviso)
> - Adicionada a contagem do total de mensagens por teste, e retornada ao fim de cada execucao.
> - Renomeada a classe ArtificeApp para Frontend
> PENDENTE
> + Teste com o cenario onde ha um ator de banco unificado para todos os componentes (comparar performance)
> + PENDENTE: Alterar o momento onde é gravada a segunda timestamp de dentro do onReceive, no Ator, para o enqueue da respectiva Mailbox.

21/06/2015 - DBCleaner e exportação dos dados para único CSV 
> - Scheduler adicionado
> - Configurado ActorDB para exportar para CSV no diretorio HOME/output
> - SenderMessage reestruturado para conter apenas estimulo e horario de envio
> - Mailbox reconfigurada para nao bloquear incoming messages do tipo String
> - Ator ArtificeActor (abstrato) incluido para ser extendido pelas outras classes e minimizar refatoracao
> - Construcao da ReceiverMessage movida da Mailbox para o onReceive, para captar apropriadamente o destinatario
> - StatisticsAnaliser alterado para exibir o Sender e o Receiver
> - Criada main() unificada, que limpa a tabela do banco, realiza o teste, exibe os resultados todos na tela e salva uma copia para um arquivo .csv

16/06/2015 - Criado StatisticsAnaliser
> > Acertei o pom.xml, que estava problematico, e acertei a primeira linha da exibição dos dados do banco.
> 
> > Adicionado o módulo para análise estatística preliminar da tabela de resultados do banco.


15/06/2015 - Testes com novo branch
Testes akka com mailbox e timestamps. 
> Novo branch: Database 

14/06/2015 - Teste inicial 2
> Refactorado o pacote de testes. 

12/06/2015 - Teste inicial 1
> Teste basico de troca de mensagem atraves de RandomRoutingLogic 

