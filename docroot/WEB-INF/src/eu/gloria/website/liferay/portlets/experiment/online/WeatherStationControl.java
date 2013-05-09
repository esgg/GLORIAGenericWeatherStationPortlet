package eu.gloria.website.liferay.portlets.experiment.online;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import eu.gloria.gs.services.core.client.GSClientProvider;

public class WeatherStationControl extends MVCPortlet{
	
	private String viewJSP = null;
	private String errorJSP = null;
	
	private int reservationId = -1;
	
	private static Log log = LogFactoryUtil.getLog(WeatherStationControl.class);

	public void init() throws PortletException {
		viewJSP = getInitParameter("view-jsp");
		errorJSP = getInitParameter("error-jsp");
		
		super.init();
	}
	
	public void doView(RenderRequest request, RenderResponse response){
	
		PortletPreferences prefs = request.getPreferences();
		
		try {
			
			reservationId = Integer.parseInt((String) prefs.getValue(
					"reservationId", "-1"));
						
			include(viewJSP, request, response);
			
		} catch (IOException e) {
			log.error("Error to render portlet:" + e.getMessage());
		} catch (PortletException e) {
			log.error("Error to render portlet:" + e.getMessage());
		}
	}
	
	public void serveResource(ResourceRequest request, ResourceResponse response)
			throws PortletException, IOException {
		
		final JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		
		log.info("Weather parameters requested");
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse ("http://meteows.ot-admin.net/services/meteorobot/xml");
			NodeList parameters = doc.getElementsByTagName("parameter");
			
			for (int i=0;i<parameters.getLength();i++){
				Node firstParameterNode = parameters.item(i);
				if(firstParameterNode.getNodeType() == Node.ELEMENT_NODE){
					
					Element firstParameterElement = (Element)firstParameterNode;
					
					NodeList conditionsList = firstParameterElement.getChildNodes();
					
					String paramName ="";
					
					for (int condition = 0; condition<conditionsList.getLength(); condition++){
						Node firstConditionNode = conditionsList.item(condition);
						
						if(firstConditionNode.getNodeType() == Node.ELEMENT_NODE){
							Element conditionElement = (Element)firstConditionNode;
							
							String value = conditionElement.getChildNodes().item(0).getNodeValue();
							
							if (conditionElement.getNodeName().equals("name")){
								if (value.startsWith("Humedad")){
									paramName="humidity";
								} else if (value.startsWith("Direc")){
									paramName="windDirection";
								} else if (value.startsWith("Velocidad")){
									paramName="windVelocity";
								}  else if (value.startsWith("Pres")){
									paramName="pressure";
								} else if (value.startsWith("Temperatura")){
									paramName="temperature";
								}
							} else if (conditionElement.getNodeName().equals("value")){
								jsonObject.put(paramName+"Value", value);
							}
							else if (conditionElement.getNodeName().equals("alarm")){
								jsonObject.put(paramName+"Alarm", value);
							}
						}
					}
	                                
				}
			}
		      jsonObject.put("success",true);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       

		PrintWriter writer = response.getWriter();
		writer.write(jsonObject.toString());
	}

	protected void include(String path, RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		PortletRequestDispatcher portletRequestDispatcher = getPortletContext()
				.getRequestDispatcher(path);
		if (portletRequestDispatcher == null) {
			log.error(path + " is not a valid include");
		} else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}
	
}
