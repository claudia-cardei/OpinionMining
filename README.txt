How to run

Conversie din CSV in ARFF
	

Antrenarea si testarea modelelor

1. Pentru modele* noi

Daca se doreste verificarea modelului prin cross-validare:
	java --trainingDataset=<fisier_training>.arff --applyFilter=true --load=false --crossValidation=true
	
	** <fisier_training>.arff este fisierul de training obtinut in urma conversiei unui csv in arff (are extensia .arff)
	** in urma rularii se vor obtine alte doua fisiere:
		*** un fisier numit <fisier_trainig>_model in care s-a salvat modelul creat pentru a putea fi folosit si in alte dati (fara a-l mai crea) 
		*** un fisier numit <fisier_training>_transformed.arff este fisierul de intrare se va trebui folosit cand se doreste folosirea unui model deja creat	

Daca se doreste verificarea modelului pe un set de test:
	java --trainingDataset=<fisier_training>.arff --applyFilter=true --load=false --crossValidation=false --testDataset=<fisier_test>.arff
	
	** <fisier_training>.arff are semnificatia de mai sus
	** <fisier_test>.arff este fisierul pe care se doreste testarea modelului (este tot un fisier obtinut in urma conversiei unui csv in arff; are extensia .arff)
	** in urma rularii se vor obtine alte trei fisiere:
		*** cele doua de mai sus
		*** <fisier_test>_results.txt ce contine resultatele (opiniile) pe setul de test

2. Pentru modele* existente (care au fost deja create prin metoda de mai sus)

Daca se doreste verificarea modelului prin cross-validare:
	java --trainingDataset=<fisier_training>_transformed.arff --applyFilter=false --load=true --crossValidation=true
	
	** <fisier_training>_transformed.arff este fisierul obtinut mai sus (aferent trebuie sa existe si <fisier_training>_model)
	
Daca se doreste verificarea modelului pe un set de test:
	java --trainingDataset=<fisier_training>_transformed.arff --applyFilter=true --load=false --crossValidation=false --testDataset=<fisier_test>.arff
	
	** <fisier_training>_transformed.arff si <fisier_test>.arff au semnificatiile de mai sus

* model obtinut in urma antrenarii pe un anumit set



Exemplu:
1. convertire csv -> arff
2. java --trainingDataset=bcr_1400.arff --applyFilter=true --load=false --crossValidation=true
	=> rezulta bcr_1400_transformed.arff si bcr_1400_model
	
Daca se doreste din nou rularea pe setul de date bcr_1400.arff se poate folosi direct comanda 
(e mai rapid intrucat nu se mai creaza modelul, ci se incarca direct din fisierul bcr_1400_model):
	java --trainingDataset=bcr_1400_transformed.arff --applyFilter=false --load=true --crossValidation=true


