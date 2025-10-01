package estrutura;

//arvore 2-3-4 (b-tree de ordem 4) com insercao top-down e remocao
public class Arvore234 {
    private No234 raiz;

    public Arvore234() {
        raiz = null;
    }

    //busca iterativa
    public boolean buscar(int k) {
        No234 noAtual = raiz;
        while (noAtual != null) {
            int posicao = noAtual.encontrarPosicaoChave(k);
            if (posicao < noAtual.quantidadeChaves && noAtual.chaves[posicao] == k) return true; //achou
            if (noAtual.ehFolha) return false; //nao tem pra onde descer
            noAtual = noAtual.filhos[posicao]; //desce no filho adequado
        }
        return false;
    }

    //insercao top-down: sempre quebra (split) o filho cheio antes de descer
    public void inserir(int k) {
        if (raiz == null) {
            raiz = new No234();
            raiz.chaves[0] = k;
            raiz.quantidadeChaves = 1;
            return;
        }
        if (raiz.estaCheio()) {
            No234 s = new No234();
            s.ehFolha = false;
            s.filhos[0] = raiz;
            dividirFilhoSeCheio(s, 0);
            raiz = s;
        }
        inserirEmNaoCheio(raiz, k);
    }

    // divide um filho 4-no (3 chaves) do parent em idx
    private void dividirFilhoSeCheio(No234 pai, int indice) {
        No234 filhoCheio = pai.filhos[indice]; // filho cheio
        if (!filhoCheio.estaCheio()) return;

        No234 novoNo = new No234();     // novo no recebe a parte da direita
        novoNo.ehFolha = filhoCheio.ehFolha;

        // chaves: [k0, k1, k2] -> sobe k1; filhoCheio fica com k0; novoNo fica com k2
        int chaveDoMeio = filhoCheio.chaves[1];
        novoNo.chaves[0] = filhoCheio.chaves[2];
        novoNo.quantidadeChaves = 1;

        // se nao for folha, mover filhos da direita para novoNo
        if (!filhoCheio.ehFolha) {
            novoNo.filhos[0] = filhoCheio.filhos[2];
            novoNo.filhos[1] = filhoCheio.filhos[3];
            filhoCheio.filhos[2] = null;
            filhoCheio.filhos[3] = null;
        }

        // filhoCheio reduz para 1 chave
        filhoCheio.quantidadeChaves = 1;

        // no pai: insere chave do meio e ponteiro p novoNo logo apos indice
        pai.inserirFilhoEm(indice + 1, novoNo);
        pai.inserirChaveEm(indice, chaveDoMeio);
    }

    // insere em no garantidamente nao-cheio
    private void inserirEmNaoCheio(No234 noAtual, int k) {
        if (noAtual.ehFolha) {
            int posicao = noAtual.encontrarPosicaoChave(k);
            if (posicao < noAtual.quantidadeChaves && noAtual.chaves[posicao] == k) return; // evita duplicado
            noAtual.inserirChaveEm(posicao, k);
            return;
        }

        int posicao = noAtual.encontrarPosicaoChave(k);
        if (noAtual.filhos[posicao].estaCheio()) {
            dividirFilhoSeCheio(noAtual, posicao);
            if (k > noAtual.chaves[posicao]) posicao++; // decide lado apos o split
        }
        inserirEmNaoCheio(noAtual.filhos[posicao], k);
    }

    // impressao por nivel
    public void imprimirNivelPorNivel() {
        ImpressoraArvore.imprimirNivelPorNivel(raiz);
    }

    // remocao com garantias top-down (empresta/merge antes de descer) e limpeza simples da raiz
    public boolean remover(int k) {
        if (raiz == null) return false;
        boolean sucesso = removerDoNo(raiz, k);

        // se a raiz ficou sem chaves:
        if (raiz.quantidadeChaves == 0) {
            // se tinha filhos, desce um nivel; se nao, arvore fica vazia
            raiz = raiz.ehFolha ? null : raiz.filhos[0];
        }
        return sucesso;
    }

    // remove k partindo do no x (garantindo invariantes ao descer)
    private boolean removerDoNo(No234 noAtual, int k) {
        int indice = noAtual.encontrarPosicaoChave(k);

        // caso 1: chave k esta neste no
        if (indice < noAtual.quantidadeChaves && noAtual.chaves[indice] == k) {
            if (noAtual.ehFolha) {
                noAtual.removerChaveEm(indice); // remove direto na folha
                return true;
            } else {
                // interno: substitui por predecessor e remove na subarvore esquerda
                int pred = obterPredecessor(noAtual, indice);
                noAtual.chaves[indice] = pred;
                garantirFilhoComPeloMenosDuasChaves(noAtual, indice);
                return removerDoNo(noAtual.filhos[indice], pred);
            }
        }

        // caso 2: chave nao esta neste no
        if (noAtual.ehFolha) return false; // chegou em folha e nao achou

        // vamos descer para o filho na posicao indice
        garantirFilhoComPeloMenosDuasChaves(noAtual, indice);
        if (indice > noAtual.quantidadeChaves) indice = noAtual.quantidadeChaves; // ajuste se houve merge
        return removerDoNo(noAtual.filhos[indice], k);
    }

    // garante que child[idx] tenha pelo menos 2 chaves antes de descer
    private void garantirFilhoComPeloMenosDuasChaves(No234 pai, int indice) {
        No234 filho = pai.filhos[indice];
        if (filho.quantidadeChaves >= 2) return;

        // tenta emprestar do irmao esquerdo
        if (indice - 1 >= 0 && pai.filhos[indice - 1].quantidadeChaves >= 2) {
            emprestarDoEsquerdo(pai, indice);
            return;
        }
        // tenta emprestar do irmao direito
        if (indice + 1 <= pai.quantidadeChaves && pai.filhos[indice + 1].quantidadeChaves >= 2) {
            emprestarDoDireito(pai, indice);
            return;
        }
        // senao, faz merge com algum irmao
        if (indice - 1 >= 0) {
            fundirComEsquerdo(pai, indice);
        } else {
            fundirComDireito(pai, indice);
        }
    }

    // pega o predecessor da chave em x.keys[idx] (maior na subarvore esquerda)
    private int obterPredecessor(No234 x, int idx) {
        No234 atual = x.filhos[idx];
        while (!atual.ehFolha) {
            atual = atual.filhos[atual.quantidadeChaves]; // vai pro filho mais a direita
        }
        return atual.chaves[atual.quantidadeChaves - 1];
    }

    // empresta uma chave do irmao esquerdo para child[idx]
    private void emprestarDoEsquerdo(No234 pai, int idx) {
        No234 esquerdo = pai.filhos[idx - 1];
        No234 atual  = pai.filhos[idx];

        // abre espaco na frente de atual (desloca chaves e filhos 1 p direita)
        for (int i = atual.quantidadeChaves - 1; i >= 0; i--) atual.chaves[i + 1] = atual.chaves[i];
        for (int i = atual.quantidadeChaves; i >= 0; i--)     atual.filhos[i + 1] = atual.filhos[i];

        // chave do pai desce para atual[0]
        atual.chaves[0] = pai.chaves[idx - 1];
        // se tiver filhos, traz o ultimo filho do esquerdo para atual.filhos[0]
        if (!atual.ehFolha) {
            atual.filhos[0] = esquerdo.filhos[esquerdo.quantidadeChaves];
            esquerdo.filhos[esquerdo.quantidadeChaves] = null;
        }
        atual.quantidadeChaves++;

        // a ultima chave do esquerdo sobe pro pai
        pai.chaves[idx - 1] = esquerdo.chaves[esquerdo.quantidadeChaves - 1];
        esquerdo.quantidadeChaves--;
    }

    // empresta uma chave do irmao direito para child[idx]
    private void emprestarDoDireito(No234 pai, int idx) {
        No234 direito = pai.filhos[idx + 1];
        No234 atual   = pai.filhos[idx];

        // chave do pai vai para o fim de atual
        atual.chaves[atual.quantidadeChaves] = pai.chaves[idx];
        // se tiver filhos, traz o primeiro filho do direito para o fim de atual
        if (!atual.ehFolha) {
            atual.filhos[atual.quantidadeChaves + 1] = direito.filhos[0];
            // compacta os filhos do direito para a esquerda
            for (int i = 0; i < direito.quantidadeChaves; i++) {
                direito.filhos[i] = direito.filhos[i + 1];
            }
            direito.filhos[direito.quantidadeChaves] = null;
        }
        atual.quantidadeChaves++;

        // a primeira chave do direito sobe pro pai
        pai.chaves[idx] = direito.chaves[0];

        // compacta as chaves do direito para a esquerda
        for (int i = 0; i < direito.quantidadeChaves - 1; i++) {
            direito.chaves[i] = direito.chaves[i + 1];
        }
        direito.quantidadeChaves--;
    }

    // merge child[idx] com o irmao esquerdo (idx-1); chave do pai desce
    private void fundirComEsquerdo(No234 pai, int idx) {
        No234 esquerdo = pai.filhos[idx - 1];
        No234 atual  = pai.filhos[idx];

        int filhosEsquerdoAntes = esquerdo.quantidadeChaves + 1;

        // 1) desce a chave do pai para o fim do esquerdo
        esquerdo.chaves[esquerdo.quantidadeChaves] = pai.chaves[idx - 1];
        esquerdo.quantidadeChaves++;

        // 2) copia as chaves de atual para o fim do esquerdo
        for (int i = 0; i < atual.quantidadeChaves; i++) {
            esquerdo.chaves[esquerdo.quantidadeChaves] = atual.chaves[i];
            esquerdo.quantidadeChaves++;
        }

        // 3) se interno, copia os filhos de atual para o fim dos filhos de esquerdo
        if (!esquerdo.ehFolha) {
            for (int i = 0; i <= atual.quantidadeChaves; i++) {
                esquerdo.filhos[filhosEsquerdoAntes + i] = atual.filhos[i];
            }
        }

        // 4) remove do pai o ponteiro do atual e a chave correspondente
        pai.removerFilhoEm(idx);
        pai.removerChaveEm(idx - 1);
    }

    // merge child[idx] com o irmao direito (idx+1); chave do pai desce
    private void fundirComDireito(No234 pai, int idx) {
        No234 atual   = pai.filhos[idx];
        No234 direito = pai.filhos[idx + 1];

        int filhosAtualAntes = atual.quantidadeChaves + 1;

        // 1) desce a chave do pai para o fim de atual
        atual.chaves[atual.quantidadeChaves] = pai.chaves[idx];
        atual.quantidadeChaves++;

        // 2) copia as chaves de direito para o fim de atual
        for (int i = 0; i < direito.quantidadeChaves; i++) {
            atual.chaves[atual.quantidadeChaves] = direito.chaves[i];
            atual.quantidadeChaves++;
        }

        // 3) se interno, copia os filhos de direito para o fim dos filhos de atual
        if (!atual.ehFolha) {
            for (int i = 0; i <= direito.quantidadeChaves; i++) {
                atual.filhos[filhosAtualAntes + i] = direito.filhos[i];
            }
        }

        // 4) remove do pai o ponteiro do direito e a chave correspondente
        pai.removerFilhoEm(idx + 1);
        pai.removerChaveEm(idx);
    }
}