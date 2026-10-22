#!/bin/bash
function bubble(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BubbleSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BubbleSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BubbleSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BubbleSort --disposicao CRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BubbleSort --disposicao DECRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BubbleSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
}

function inserction(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo InsertionSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo InsertionSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo InsertionSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo InsertionSort --disposicao CRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo InsertionSort --disposicao DECRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo InsertionSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
}

function selection(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo SelectionSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo SelectionSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo SelectionSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo SelectionSort --disposicao CRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo SelectionSort --disposicao DECRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo SelectionSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
}

function shell(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao CRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao DECRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao CRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao DECRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao ALEATORIO --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao CRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao DECRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo ShellSort --disposicao ALEATORIO --n 1000000 --saida resultados/tp1
}


function merge(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao CRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao DECRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao CRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao DECRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao ALEATORIO --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao CRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao DECRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo MergeSort --disposicao ALEATORIO --n 1000000 --saida resultados/tp1
}

function quick(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo QuickSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo QuickSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo QuickSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo QuickSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo QuickSort --disposicao ALEATORIO --n 100000 --saida resultados/tp1
}

function heap(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao CRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao DECRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao CRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao DECRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao ALEATORIO --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao CRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao DECRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo HeapSort --disposicao ALEATORIO --n 1000000 --saida resultados/tp1
}

function counting(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao CRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao DECRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao CRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao DECRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao ALEATORIO --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao CRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao DECRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo CountingSort --disposicao ALEATORIO --n 1000000 --saida resultados/tp1
}

function radix(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao CRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao DECRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao CRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao DECRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao ALEATORIO --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao CRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao DECRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo RadixSort --disposicao ALEATORIO --n 1000000 --saida resultados/tp1
}

function bucket(){
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao CRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao DECRESCENTE --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao ALEATORIO --n 1000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao CRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao DECRESCENTE --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao ALEATORIO --n 10000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao CRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao DECRESCENTE --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao ALEATORIO --n 100000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao CRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao DECRESCENTE --n 1000000 --saida resultados/tp1
    ./executar_suite_tp1.sh --repeticoes 10 --caso MEDIO --algoritmo BucketSort --disposicao ALEATORIO --n 1000000 --saida resultados/tp1
}
 