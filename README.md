# t1_distribuida

Tarefas : 
  - [x] Passar o tipo de nodo como parâmetro durante a carga do programa;
  - [ ] Passar informações de configuração durante a carga do programa;
  - [ ] Os supernodos sincronizam recursos de outros nodos realizando comunicação em um grupo fechado, IP multicast;
  - [ ] Os nodos devem se registrar em um supernodo (em apenas um, IP unicast) para poderem realizar a troca de informações com outros nodos. Durante o registro, um nodo informa os recursos disponíveis (usar um diretório com alguns arquivos, gerando-se uma hash MD5 para cada).
  - [ ] O supernodo associa cada recurso (hash, nome do arquivo, IP) e armazena a em uma lista local.
  - [ ] Os nodos podem solicitar uma lista de recursos (arquivos) ou recursos individuais a um supernodo, que irá consultar outros supernodos em busca do recurso.
  - [ ] Ao solicitar um recurso a um supernodo, o nodo recebe a informação sobre a localização do recurso (IP de outro nodo) e deve então realizar essa comunicação diretamente com o nodo que possui o mesmo (P2P).
  - [ ] Cada supernodo é responsável por manter parte da estrutura da rede de overlay. Para isso, os nodos devem enviar mensagens periódicas ao um supernodo (a cada 5 segundos). Caso um nodo não envie 2 solicitações seguidas a um supernodo, o mesmo é removido da lista.
