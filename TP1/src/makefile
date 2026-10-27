JAVAC := javac
JAVA := java

all:
	$(JAVAC) -encoding UTF-8 ./*.java

benchmark:
	./testes_tp1.sh desempenho

test: all
	$(JAVA) -cp . TestesCorretude	

experimento: all
	chmod +x experimento_completo_tp1.sh
	./experimento_completo_tp1.sh

run: all
	$(JAVA) -cp . Main --help

clean:
	rm -f ./*.class
	
clean_all:
	rm -rf ./*.class resultados/*.csv

gen_ctx:
	gitingest . -o ctx.txt


.PHONY: all test benchmark experimento run clean clean_all gen_ctx
