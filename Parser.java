import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class Parser {

	BufferedReader br;
	PrintWriter pr;
	
	public Parser(String inputName, String outputName) {
		try {
			br = new BufferedReader(new FileReader(new File(inputName)));
			pr = new PrintWriter(new File(outputName));
						
			pr.println("@relation 'TreeWorks'");
			pr.println("@attribute Text string");
			pr.println("@attribute opinion {0,1}");
			pr.println("@data");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void parse() {
		String linie, continut, clasa;
		int nrClasa = 0;
		String[] campuri, cuvinte;
		
		try {
			br.readLine();
			
			while ( (linie = br.readLine()) != null ) {
				while ( linie.length() <= 1 || linie.charAt(linie.length() - 1) != '\"' ) {
					linie += "\\n" + br.readLine();
				}
				
				campuri = linie.split("\" \"");
				// Ignora liniile care nu au text sau clasa
				if ( campuri.length == 2 && campuri[0].length() > 0 ) {
				
					continut = campuri[0];
					continut = continut.substring(1, continut.length());
					/*cuvinte = continut.split("");
					for (String cuvant:cuvinte)
						System.out.print(cuvant + "|");
					System.out.println();*/
					
					continut = continut.replace("'", "\\'");
					
					
					clasa = campuri[1];
					clasa = clasa.substring(0, clasa.length() - 1);
					
					if ( clasa.equals("nici favorabil, nici nefavorabil") == true ) {
						nrClasa = 0;
					}
					else if ( clasa.contains("nefavorabil"))
						nrClasa = 1;
					else
						nrClasa = 2;
					
						
					pr.println("'" + continut + "'," + nrClasa);
		
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pr.close();
	}
	
	public static void main(String[] args) {
		//Parser parser = new Parser("monitor_export_bcr.csv", "bcr2.arff");
		//Parser parser = new Parser("monitor_export_blogjuan-or-blog-juan-1.csv", "blogjuan-all.arff");
		//Parser parser = new Parser("monitor_export_cremosso-or-cremoso.csv", "cremosso-all.arff");
		//Parser parser = new Parser("monitor_export_danone.csv", "danone2.arff");
		//Parser parser = new Parser("monitor_export_fratelli.csv", "fratelli-all.arff");
		Parser parser = new Parser("monitor_export_upc.csv", "upc3clase.arff");
		//Parser parser = new Parser("monitor_export_ursus.csv", "ursus2.arff");
		parser.parse();
		
	}
}
