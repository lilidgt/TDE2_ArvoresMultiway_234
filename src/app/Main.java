package app;

import estrutura.*;

public class Main {
    public static void main(String[] args) {
        //teste: insercao + busca + impressao por nivel
        Arvore234 arvore = new Arvore234();

        //insere varios valores para forcar splits visiveis
        int[] sequencia = {50,40,60,30,70,20,80,10,90,0,15,35,45,55,65,75};
        for (int valor : sequencia) {
            arvore.inserir(valor);
        }

        System.out.println("nivel a nivel:");
        arvore.imprimirNivelPorNivel();

        System.out.println("busca 65: " + arvore.buscar(65)); // true
        System.out.println("busca 999: " + arvore.buscar(999)); // false

        System.out.println("antes das remocoes:");
        arvore.imprimirNivelPorNivel();

        //casos de remocao
        int[] remocoes = {7, 15, 35, 40, 60};
        for (int removerValor : remocoes) {
            System.out.println("\nremovendo " + removerValor + "...");
            boolean sucesso = arvore.remover(removerValor);
            System.out.println("remove(" + removerValor + "): " + sucesso);
            arvore.imprimirNivelPorNivel();
        }
    }
}