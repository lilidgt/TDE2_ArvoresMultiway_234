package estrutura;

//no da arvore 2-3-4
public class No234 {
    public static final int MAX_KEYS = 3;
    public static final int MAX_CHILDREN = 4;

    public int quantidadeChaves; //qtd de chaves usadas neste no (0..3)
    public int[] chaves; //chaves ordenadas localmente
    public No234[] filhos; //ponteiros p filhos (0..4)
    public boolean ehFolha; //true se nao tem filhos

    public No234() {
        this.quantidadeChaves = 0;
        this.chaves = new int[MAX_KEYS];
        this.filhos = new No234[MAX_CHILDREN];
        this.ehFolha = true;
    }

    //checa se o no ta cheio (4-no: 3 chaves)
    public boolean estaCheio() { return quantidadeChaves == MAX_KEYS; }

    //varre as chaves ate achar posicao (ou onde deveria inserir)
    public int encontrarPosicaoChave(int k) {
        int i = 0;
        while (i < quantidadeChaves && k > chaves[i]) i++;
        return i; // se i<quantidadeChaves && chaves[i]==k -> achou
    }

    //insere chave em pos, deslocando as demais p direita
    public void inserirChaveEm(int pos, int k) {
        int i = quantidadeChaves - 1;
        while (i >= pos) {
            chaves[i + 1] = chaves[i];
            i--;
        }
        chaves[pos] = k;
        quantidadeChaves++;
    }

    //insere ponteiro de filho em pos, deslocando demais p direita
    public void inserirFilhoEm(int pos, No234 c) {
        int i = quantidadeChaves; //qtd de filhos pode ser quantidadeChaves+1
        while (i >= pos) {
            filhos[i + 1] = filhos[i];
            i--;
        }
        filhos[pos] = c;
    }

    //remove chave em pos, deslocando demais p esquerda
    public int removerChaveEm(int pos) {
        int removida = chaves[pos];
        for (int i = pos; i < quantidadeChaves - 1; i++) {
            chaves[i] = chaves[i + 1];
        }
        quantidadeChaves--;
        return removida;
    }

    //remove ponteiro de filho em pos, deslocando p esquerda
    public No234 removerFilhoEm(int pos) {
        No234 removido = filhos[pos];
        int limite = quantidadeChaves + 1; //qntd atual de filhos
        for (int i = pos; i < limite - 1; i++) {
            filhos[i] = filhos[i + 1];
        }
        filhos[limeiteIndex(limite)] = null;
        return removido;
    }

    // helper interno p/ evitar usar .length
    private int limeiteIndex(int limite) { return limite - 1; }
}