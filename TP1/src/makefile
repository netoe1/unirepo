all:
	echo "Compilando os arquivos .java..."
	javac -encoding UTF-8 *.java

run:
	echo "Rodando Main.java"
	java Main.java

test:
	./testes_tp1.sh corretude

benchmark:
	./testes_tp1.sh desempenho

clean:
	echo "Removendo bytecode..."
	rm -f *.class

.PHONY: all run test benchmark clean
