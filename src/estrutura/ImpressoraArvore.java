package estrutura;

//impressao por nivel usando fila manual com array
public class ImpressoraArvore {
    private static final int TAMANHO_FILA = 256; //tamanho da fila

    public static void imprimirNivelPorNivel(No234 raiz) {
        if (raiz == null) {
            System.out.println("(arvore vazia)");
            return;
        }
        No234[] fila = new No234[TAMANHO_FILA];
        int cabeca = 0, cauda = 0;

        //separador de nivel (null = marcador)
        fila[cauda++] = raiz;
        fila[cauda++] = null;

        while (cabeca != cauda) {
            No234 atual = fila[cabeca++];
            if (atual == null) {
                System.out.println(); //quebra de linha entre niveis
                if (cabeca == cauda) break; //cabou
                fila[cauda++] = null; //marca prox nivel
                continue;
            }

            //imprime no como [k1|k2|k3] (só chaves existentes)
            System.out.print('[');
            for (int i = 0; i < atual.quantidadeChaves; i++) {
                System.out.print(atual.chaves[i]);
                if (i + 1 < atual.quantidadeChaves) System.out.print('|');
            }
            System.out.print(']');
            System.out.print(' ');

            //enfileira filhos se tiver
            if (!atual.ehFolha) {
                for (int i = 0; i <= atual.quantidadeChaves; i++) {
                    if (atual.filhos[i] != null) fila[cauda++] = atual.filhos[i];
                }
            }
        }
        System.out.println();
    }
}