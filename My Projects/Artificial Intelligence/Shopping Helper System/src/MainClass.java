import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;


public class MainClass {
		private static HashMap<String,Integer> interestsMap = new HashMap<String,Integer>();
		public static void main(String[] args){
			try {
				ArrayList<String> inputList = read(args[0]);
				ArrayList<String> interestsList = read("interests.txt");
				for(int a=0;a<interestsList.size();a++){
					interestsMap.put(interestsList.get(a), 0);
				}
				ArrayList<String> interestStringList = new ArrayList<String>();
				for(int i=0;i<inputList.size();i++){
					String interest = "";
					String inputString = inputList.get(i);
					StringTokenizer st = new StringTokenizer(inputString," ");
					while(st.hasMoreElements()){
						String str = (String) st.nextElement();
						if(interestsMap.containsKey(str)){
							interest = str;
							interestStringList.add(interest);
							break;
						}
					}
				}
				
				/*
				 * Read the created OWL file using the JENNA API
				 */
				InputStream in = FileManager.get().open("C://Users//Vinay//workspace//Work Space New//AIFinalProject//ShoppingComplexKB.owl");
				OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_RULE_INF );
				m.read(in,"RDF/XML");
				
				/*
				 * Once the Knowledge base is read in to OWL Ontology model
				 * Create a SPARQL query string and execute it. Once the execution is done
				 * write the result set to output.xml
				 */
				
				String sparqlQuery = buildSparqlQuery(interestStringList);
				Query queryCreate = QueryFactory.create(sparqlQuery);
				QueryExecution queryExecute = QueryExecutionFactory.create(queryCreate, m);
				ResultSet resultSet = queryExecute.execSelect();
				FileOutputStream file = new FileOutputStream("output.xml");
				ResultSetFormatter.outputAsXML(file, resultSet);
				//System.out.println("XML File Created");
				
				/*
				 * Once the Output xml is created parse the XML to get the Knowledge
				 */
				ArrayList<String> searchPoints = new ArrayList<String>();
				searchPoints = parseXML("output.xml");
				
				ArrayList<String> shopNumbersList = new ArrayList<String>();
				shopNumbersList = read("ShopNumbers.txt");
				HashMap<String,Integer> shopMap = new HashMap<String,Integer>();
				for(int i=0;i<shopNumbersList.size();i++){
					String localString = shopNumbersList.get(i);
					StringTokenizer str = new StringTokenizer(localString," ");
					String shopName = "";
					int shopNumber = 0;
					while(str.hasMoreElements()){
						shopNumber = Integer.parseInt(str.nextToken());
						shopName = str.nextToken();
					}
					shopMap.put(shopName, shopNumber);
				}
				ArrayList<Integer> interestInput = new ArrayList<Integer>();
				boolean allreadyExist = false;
				System.out.println("*********  SHOPS NEEDED TO VISIT   ********");
				for(int i=0;i<searchPoints.size();i++){
					
					allreadyExist = false;
					for(int j=0;j<interestInput.size();j++){
						if(shopMap.get(searchPoints.get(i)).equals(interestInput.get(j))){
							allreadyExist = true;
							break;
						}
					}
					if(!allreadyExist){
						System.out.print(searchPoints.get(i)+" , ");
						interestInput.add(shopMap.get(searchPoints.get(i)));
					}
				}
				System.out.println("");
				GraphAdjList.Main(interestInput);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public static ArrayList<String> read(String filename) throws Exception{
			FileReader fileReader = new FileReader(filename);
			BufferedReader readText = new BufferedReader(fileReader);
			ArrayList<String> list = new ArrayList<String>();
			while(true){
				String str = readText.readLine();
				if(str == null){
					break;
				}
				list.add(str);
			}
			readText.close();
			return list;
		}
		/*
		 * Constructing the SPARQL Query String Dynamically Based on the number of Inputs
		 */
		public static String buildSparqlQuery(ArrayList<String> interestStringList){
			String sparqlQuery =  "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+        
			        "PREFIX owl: <http://www.w3.org/2002/07/owl#>"+
			        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"+
			        "PREFIX ex: <http://www.utdallas.edu/kisna/shoppingsystem#>" +
			        "SELECT ?Shops WHERE {{ex:"+interestStringList.get(0)+
			        " ex:type_of ?BrandCatalouge."
			        + "?BrandCatalouge ex:product_of ?Brands."
			        + "?Brands ex:available_at ?Shops}" ;
			for(int i=1;i<interestStringList.size();i++){
				String localString = "UNION "
				        + "{ ex:"+interestStringList.get(i)+
		        		" ex:type_of ?BrandCatalouge."
		        + "?BrandCatalouge ex:product_of ?Brands."
		        + "?Brands ex:available_at ?Shops}";
				sparqlQuery = sparqlQuery+localString;
			}
			sparqlQuery = sparqlQuery+"}";
			return sparqlQuery;
		}
		/*
		 * Parsing the Output XML from SPARQL Query result using XML DOM Parser
		 */
		public static ArrayList<String> parseXML(String outputXml) throws Exception{
			ArrayList<String> points = new ArrayList<String>();
			File fileXml = new File(outputXml);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fileXml);
			NodeList nList = doc.getElementsByTagName("binding");
			for(int i=0;i<nList.getLength();i++){
				Node nNode = nList.item(i);
				Element eElement = (Element) nNode;
				String point = eElement.getElementsByTagName("uri").item(0).getTextContent();
				point = point.replace("http://www.utdallas.edu/kisna/shoppingsystem#", "");
				points.add(point);
			}
			return points;
		}
		
		
		
		
}
