package de.onto_med.webprotege_rest_api.api;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.semanticweb.binaryowl.BinaryOWLOntologyDocumentPreamble;
import org.semanticweb.binaryowl.BinaryOWLParseException;
import org.semanticweb.binaryowl.stream.BinaryOWLInputStream;
import org.semanticweb.binaryowl.stream.BinaryOWLStreamUtil;
import org.semanticweb.owlapi.apibinding.OWLManager;

/**
 * This class is a simplified binaryowl parser.
 * 
 * @author Christoph Beger
 */
public class BinaryOwlUtils {
	
	/**
	 * Test.
	 * @param args Not required.
	 */
	public static void main(String[] args) {
		try {
			String iri = BinaryOwlUtils.getOntologyIriFromBinaryOwl(
				args.length > 0 ? args[0]
					: "H:/Projekte/Leipzig Health Atlas/Development/Web-Service/data/webprotege/data-store/project-data/"
					+ "15b91048-eb84-4b15-a602-a52938ca3401/ontology-data/root-ontology.binary"
			);
			System.out.println(iri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Returns the IRI of the binaryowl ontology.
	 * @param path The path to the binaryowl file
	 * @return IRI as String
	 */
	public static String getOntologyIriFromBinaryOwl(String path) {
		FileInputStream fi      = null;
		DataInputStream di      = null;
		BinaryOWLInputStream bi = null;
		String iri = null;
		
		try {
			fi = new FileInputStream(path);
			di = BinaryOWLStreamUtil.getDataInputStream(fi);
			bi = new BinaryOWLInputStream(
				di,
				OWLManager.getOWLDataFactory(),
				new BinaryOWLOntologyDocumentPreamble(di).getFileFormatVersion()
			);
			
			ArrayList<String> strings = new ArrayList<String>();
			
			boolean newWord = true;
			int current;
			while ((current = bi.read()) != -1 && strings.size() <= 3) {
				String string = String.valueOf((char) current);
				if (!string.matches("[\\p{Graph}]") || string.matches("\"")) {
					newWord = true;
					continue;
				}
				
				if (newWord) {
					strings.add("" + (char) current);
					newWord = false;
				} else
					strings.set(strings.size() - 1, strings.get(strings.size() - 1) + (char) current);
			}
			iri = strings.get(1) + strings.get(2);
		} catch (BinaryOWLParseException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fi.close();
				di.close();
				bi.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return iri;
	}

}