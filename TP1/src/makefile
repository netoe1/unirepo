JAVAC := javac
JAVA := java

all:
	$(JAVAC) -encoding UTF-8 ./*.java

test: all
	$(JAVA) -cp . TestesCorretude

benchmark:
	./testes_tp1.sh desempenho

run: all
	$(JAVA) -cp . Main --help

clean:
	rm -f ./*.class
	
clean_all:
	rm -rf ./*.class ../resultados/*.csv

gen_ctx:
	gitingest . -o ctx.txt

.PHONY: all test benchmark run clean
