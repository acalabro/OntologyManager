package it.cnr.isti.sedc.bieco.ontologyManager.rest.ontology;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.cnr.isti.sedc.bieco.ontologyManager.Component;
import it.cnr.isti.sedc.bieco.ontologyManager.CoreOntology;
import it.cnr.isti.sedc.bieco.ontologyManager.Device;
import it.cnr.isti.sedc.bieco.ontologyManager.OntologyEntitiesNames;
import it.cnr.isti.sedc.bieco.ontologyManager.Skill;
import it.cnr.isti.sedc.bieco.ontologyManager.SoS;
import it.cnr.isti.sedc.bieco.ontologyManager.Student;
import it.cnr.isti.sedc.bieco.ontologyManager.utils.BiecoMessageTypes;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/**
 * Root resource (exposed at "myresource" path)
 */

@Path("ontologymanager/biecointerface")

public class OntologyManager {

	private String incomingToken = "MDM2grHjCbdRy";
	private String outcomingToken = "qJACs1J0apruOOJCg";

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */

	@GET

	@Produces({ MediaType.TEXT_HTML })
	public String getIt() {
		return homePage();

	}

	private String homePage() {

		return "<!DOCTYPE html><head><meta charset=\"utf-8\"><title>OntologyManager</title>" + "</head><style>\n"
				+ "body {\n" + "  background-color: #ddebef;\n" + "}\n"
				+ "</style><body><h2>OntologyManager</h2><h3>Status: " + OntologyStatus() + "</h3>"
				+ "<h4>Ontology logs:</h4><textarea id=\"logs\" name=\"debugLog\" rows=\"30\" cols=\"200\">\n"
				+ getLoggerData() + "</textarea></body></html>";

	}

	@POST

	@Consumes(MediaType.APPLICATION_JSON)
	public Response biecointerface(String jsonMessage, @Context HttpHeaders headers) {

		JSONParser parser = new JSONParser();
		Response output = null;
		JSONObject bodyMessage;
		String authorization = headers.getRequestHeader("Authorization").get(0);
		if (authorization.compareTo(outcomingToken) == 0) {

			// JSONObject bodyMessage = new JSONObject(jsonMessage);

			// bodyMessage = (JSONObject) parser.parse(jsonMessage);

			/**
			 * if (((String)
			 * bodyMessage.get("messageType")).compareTo(BiecoMessageTypes.HEARTBEAT) == 0)
			 * { return this.heartbeat(); } else if (((String)
			 * bodyMessage.get("messageType")).compareTo(BiecoMessageTypes.START) == 0) {
			 * return this.start(); } else if (((String)
			 * bodyMessage.get("messageType")).compareTo(BiecoMessageTypes.STOP) == 0) {
			 * return this.stop(); } else if (((String)
			 * bodyMessage.get("messageType")).compareTo(BiecoMessageTypes.GETSTATUS) == 0)
			 * { return this.getStatus(); } else if (((String)
			 * bodyMessage.get("messageType")).compareTo(BiecoMessageTypes.CONFIGURE) == 0)
			 * { return this.configure(); } else if (((String)
			 * bodyMessage.get("messageType")).compareTo(BiecoMessageTypes.DATA) == 0) {
			 * return this.data((String) bodyMessage.get("event")); } else if (((String)
			 * bodyMessage.get("messageType")).compareTo(BiecoMessageTypes.EVENT) == 0) {
			 * return this.event(); } else if (((String)
			 * bodyMessage.get("messageType")).compareTo(BiecoMessageTypes.DEMO) == 0) {
			 * return this.demo(); } else return Response.status(400).entity("invalid
			 * messageType").build();
			 */

			try {
				bodyMessage = (JSONObject) parser.parse(jsonMessage);
				switch ((String) bodyMessage.get("messageType")) {
				case BiecoMessageTypes.HEARTBEAT:
					output = this.heartbeat();
					break;
				case BiecoMessageTypes.START:
					output = this.start();
					break;
				case BiecoMessageTypes.STOP:
					output = this.stop();
					break;
				case BiecoMessageTypes.GETSTATUS:
					output = this.getStatus();
					break;
				case BiecoMessageTypes.CONFIGURE:
					output = this.configure();
					break;
				case BiecoMessageTypes.DATA:
					output = this.data((JSONObject) bodyMessage.get("event"));
					break;
				case BiecoMessageTypes.EVENT:
					output = this.event();
					break;
				case BiecoMessageTypes.DEMO:
					output = this.demo();
					break;
				default:
					output = Response.status(400).entity("invalid messageType").build();
					break;
				}
			} catch (ParseException e) { // TODO Auto-generated catch block e.printStackTrace();
			}

		} else {
			output = Response.status(401).entity("invalid access token").build();
		}

		return output;
	}

	private Response start() {
		return Response.status(200).entity(OntologyStart()).header("Authorization", incomingToken).build();
	}

	private Response stop() {
		return Response.status(200).entity(OntologyStop()).header("Authorization", incomingToken).build();
	}

	private Response heartbeat() {
		return Response.status(200).build();
	}

	private Response getStatus() {
		return Response.status(200).entity(OntologyStatus()).build();
	}

	private Response demo() {
		return Response.status(200).entity(DemoStatus()).build();
	}

	private Response configure() {
		return Response.status(404).build();
	}

	/**
	 * OntologyRequest is a JSON Object containing { "operationName": "_",
	 * "idOntResource" : "45" }
	 * 
	 * We have defined Four operation: 1) getsoss 2) getdevices 3) getcomponents 4)
	 * getskills
	 * 
	 * 
	 * @param ontologyRequest
	 * @return
	 */

	private Response dataOLD(String ontologyRequest) {

		Response output;
		switch (ontologyRequest) {
		case "getsoss":
			output = Response.status(200).entity("sos1,sos2,sos3").build();
			break;
		case "getdevices":
			output = Response.status(200).entity("device1,device2,device3").build();
			break;
		case "getcomponents":
			output = Response.status(200).entity("component1,component2,component3").build();
			break;
		case "getskills":
			output = Response.status(200).entity("skill1,skill2,skill3").build();
			break;
		default:
			output = Response.status(404).entity("Invalid data request: " + ontologyRequest).build();
		}
		return output;
	}

	private Response event() {
		return Response.status(404).build();
	}

	private String OntologyStart() {
		try {
			CoreOntology.getInstance();
			return "Daemon Ontology Manager started";
		} catch (InterruptedException e) {
			return "failed to start Daemon Ontology Manager";
		}
	}

	private String getLoggerData() {
		return CoreOntology.getLoggerData();
	}

	private String OntologyStop() {
		if (CoreOntology.isRunning()) {
			CoreOntology.killInstance();
			return "Daemon Ontology Manager stopped";
		} else
			return "Daemon Ontology Manager was not running";
	}

	private String OntologyStatus() {
		if (CoreOntology.isRunning()) {
			return "Running";
		}
		return "Online";
	}

	private String DemoStatus() {
		try {
			CoreOntology.getInstance();
			CoreOntology.DemoStart();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		return "Starting Daemon Ontology Manager Demo";
	}

	// Daemon Ontology Embedded Version

	@GET

	@Path("/getsoss")

	@Produces(MediaType.APPLICATION_JSON)
	public List<SoS> getSOSs() {
		List<SoS> soss = this.loadOntDatabaseSoSs();
		return soss;
	}

	private List<SoS> loadOntDatabaseSoSs() {
		List<SoS> soSs = new ArrayList<SoS>();
		JSONParser parser = new JSONParser();
		System.out.println("########### Start ###########");
		try (FileReader registro = new FileReader("ontology.json");) {
			JSONArray array = (JSONArray) parser.parse(registro);
			for (Object jo : array) {
				soSs.add(SoS.fromJSON((JSONObject) jo));
				System.out.println(soSs.get(soSs.size() - 1));
				System.out.println(soSs.get(soSs.size() - 1).toJson());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return soSs;
	}

	@GET

	@Path("/getdevices")

	@Produces(MediaType.APPLICATION_JSON)
	public List<Device> getDevices(@DefaultValue("") @QueryParam(OntologyEntitiesNames.SOS_ID) String sosID) {
		List<Device> devices = this.loadOntDatabaseDevices(sosID);
		return devices;
	}

	private List<Device> loadOntDatabaseDevices(String sosID) {
		List<Device> devices = new ArrayList<Device>();
		JSONParser parser = new JSONParser();
		System.out.println("########### Start ###########");
		try (FileReader registro = new FileReader("ontology.json");) {
			JSONArray array = (JSONArray) parser.parse(registro);
			/**
			 * for each SoS
			 */

			for (Object joSoS : array) {
				SoS currentSoS = SoS.fromJSON((JSONObject) joSoS);
				/**
				 * if the current SoS is the one we search
				 */

				if (currentSoS.getSosId().equals(sosID)) {
					JSONArray devicesJson = (JSONArray) ((JSONObject) joSoS).get(OntologyEntitiesNames.DEVICES);
					if (devicesJson == null) {
						return devices;
					}
					System.out.println(devicesJson.toJSONString());
					/**
					 * for each Device associated with the SoS
					 */

					for (Object deviceObject : devicesJson) {
						devices.add(Device.fromJSON((JSONObject) deviceObject));
						System.out.println(devices.get(devices.size() - 1));
						System.out.println(devices.get(devices.size() - 1).toJson());
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return devices;

	}

	@GET

	@Path("/getcomponents")

	@Produces(MediaType.APPLICATION_JSON)
	public List<Component> getComponents(@DefaultValue("") @QueryParam(OntologyEntitiesNames.SOS_ID) String sosID,

			@QueryParam(OntologyEntitiesNames.DEVICE_ID) String deviceID) {

		List<Component> components = new ArrayList<Component>();
		JSONParser parser = new JSONParser();
		System.out.println("########### Start ###########");
		try (FileReader registro = new FileReader("ontology.json");) {
			JSONArray array = (JSONArray) parser.parse(registro);
			// for each SoS
			for (Object joSoS : array) {
				SoS currentSoS = SoS.fromJSON((JSONObject) joSoS);
				// if the current SoS is the one we search
				if (currentSoS.getSosId().equals(sosID)) {
					JSONArray devicesJson = (JSONArray) ((JSONObject) joSoS).get(OntologyEntitiesNames.DEVICES);
					if (devicesJson != null) {
						System.out.println(devicesJson.toJSONString());
						// for each Device associated with the SoS
						for (Object deviceObject : devicesJson) {
							Device currentDevice = Device.fromJSON((JSONObject) deviceObject);
							// if current device is the one we search
							if (currentDevice.getDeviceId().equals(deviceID)) {
								JSONArray componentsJson = (JSONArray) ((JSONObject) deviceObject)
										.get(OntologyEntitiesNames.COMPONENTS);
								if (componentsJson != null) {
									System.out.println(componentsJson.toJSONString());
									// for each Device associated with the SoS
									for (Object componentObject : componentsJson) {
										components.add(Component.fromJSON((JSONObject) componentObject));
										System.out.println(components.get(components.size() - 1));
										System.out.println(components.get(components.size() - 1).toJson());
									}
								}
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return components;
	}

	@GET

	@Path("/getskills")

	@Produces(MediaType.APPLICATION_JSON)
	public List<Skill> getSkills(@DefaultValue("") @QueryParam(OntologyEntitiesNames.SOS_ID) String sosID,

			@QueryParam(OntologyEntitiesNames.DEVICE_ID) String deviceID,

			@QueryParam(OntologyEntitiesNames.COMPONENT_ID) String componentID) {

		List<Skill> skills = new ArrayList<Skill>();
		JSONParser parser = new JSONParser();
		System.out.println("########### Start ###########");
		try (FileReader registro = new FileReader("ontology.json");) {
			JSONArray array = (JSONArray) parser.parse(registro);
			// for each SoS
			for (Object joSoS : array) {
				SoS currentSoS = SoS.fromJSON((JSONObject) joSoS);
				// if the currentSoS is the one we search

				if (currentSoS.getSosId().equals(sosID)) {
					JSONArray devicesJson = (JSONArray) ((JSONObject) joSoS).get(OntologyEntitiesNames.DEVICES);
					if (devicesJson != null) {
						System.out.println(devicesJson.toJSONString());
						// for each Device associated with the SoS
						for (Object deviceObject : devicesJson) {
							Device currentDevice = Device.fromJSON((JSONObject) deviceObject);
							// if current device is the one we search
							if (currentDevice.getDeviceId().equals(deviceID)) {
								JSONArray componentsJson = (JSONArray) ((JSONObject) deviceObject)
										.get(OntologyEntitiesNames.COMPONENTS);
								if (componentsJson != null) {
									System.out.println(componentsJson.toJSONString()); // for each Device associated
																						// with the SoS
									for (Object componentObject : componentsJson) {
										Component currentComponent = Component.fromJSON((JSONObject) componentObject); // retrieve
																														// the
																														// component
																														// with
																														// id
																														// "componentID"
										if (currentComponent.getComponentId().equals(componentID)) {
											JSONArray skillsJson = (JSONArray) ((JSONObject) componentObject)
													.get(OntologyEntitiesNames.SKILLS);

											if (skillsJson != null) {
												System.out.println(skillsJson.toJSONString()); // for each Device
																								// associated with the
																								// SoS
												for (Object skillObject : skillsJson) {
													skills.add(Skill.fromJSON((JSONObject) skillObject));
													System.out.println(skills.get(skills.size() - 1));
													System.out.println(skills.get(skills.size() - 1).toJson());
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return skills;
	}

	private static List<SoS> loadOntDatabase() {
		List<SoS> soSs = new ArrayList<SoS>();
		JSONParser parser = new JSONParser();
		System.out.println("########### Start ###########");
		try (FileReader registro = new FileReader("ontology.json");) {
			System.out.println(registro.toString());

			System.out.println(registro);

			JSONArray array = (JSONArray) parser.parse(registro);

			System.out.println(array.toJSONString());
			for (Object jo : array) {

				// to be removed // soSs.add(SoS.fromJSON((JSONObject) jo));

				SoS currentSoS = new SoS((JSONObject) jo);
				currentSoS.parse();
				soSs.add(currentSoS);

				System.out.println(soSs.get(soSs.size() - 1).toJson());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return soSs;
	}

	private void dumpOntDatabase(List<SoS> soSs) {
		JSONArray jsonSos = new JSONArray();
		for (SoS s : soSs) {
			jsonSos.add(s.toJson());
		}
		try (FileWriter registro = new FileWriter("ontology.json");) {
			jsonSos.writeJSONString(registro);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void dumpDatabase(List<Student> students) {
		JSONArray jsonStudents = new JSONArray();
		for (Student s : students) {
			jsonStudents.add(s.toJson());
		}
		try (FileWriter registro = new FileWriter("registro.json");) {
			jsonStudents.writeJSONString(registro);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@POST

	@Path("/biecointerfaceOLD")

	@Consumes(MediaType.APPLICATION_JSON)
	public Response biecointerfaceOLD(String jsonMessage, @Context HttpHeaders headers) {
		JSONParser parser = new JSONParser();
		JSONObject bodyMessage;
		Response output = null;

		System.out.println(jsonMessage);

		try {
			bodyMessage = (JSONObject) parser.parse(jsonMessage);
			switch ((String) bodyMessage.get("messageType")) {
			case "Data":
				output = this.data((JSONObject) bodyMessage.get("event"));
				break;

			default:
				output = Response.status(400).entity("invalid messageType").build();
				break;
			}
		} catch (ParseException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output;
	}

	/**
	 * OntologyRequest is a JSON Object containing { "operationName": "_",
	 * "idOntResource" : "45" }
	 * 
	 * We have defined Four operation: 1) getsoss 2) getdevices 3) getcomponents 4)
	 * getskills
	 * 
	 * 
	 * @param ontologyRequest
	 * @return
	 */
	private Response data(JSONObject event) {

		Response output;

		System.out.println(event.toJSONString());

		JSONObject ontologyRequest = (JSONObject) event.get(OntologyEntitiesNames.ONTOLOGY_REQUEST);

		System.out.println(ontologyRequest.toJSONString());

		String operationName = (String) ontologyRequest.get(OntologyEntitiesNames.OPERATION_NAME);
		System.out.println(operationName);

		String result;
		String sosID;
		String deviceID;
		String componentID;

		switch (operationName) {
		case "getsoss":

			result = getSOSs().toString();
			System.out.println(result);
			output = Response.status(200).entity(result).build();

			break;
		case "getdevices":
			sosID = (String) ontologyRequest.get(OntologyEntitiesNames.SOS_ID);
			System.out.println(sosID);

			result = getDevices(sosID).toString();
			output = Response.status(200).entity(result).build();
			break;
		case "getcomponents":

			sosID = (String) ontologyRequest.get(OntologyEntitiesNames.SOS_ID);
			System.out.println(sosID);

			deviceID = (String) ontologyRequest.get(OntologyEntitiesNames.DEVICE_ID);
			System.out.println(deviceID);

			result = getComponents(sosID, deviceID).toString();

			output = Response.status(200).entity(result).build();
			break;
		case "getskills":

			sosID = (String) ontologyRequest.get(OntologyEntitiesNames.SOS_ID);
			System.out.println(sosID);

			deviceID = (String) ontologyRequest.get(OntologyEntitiesNames.DEVICE_ID);
			System.out.println(deviceID);

			componentID = (String) ontologyRequest.get(OntologyEntitiesNames.COMPONENT_ID);
			System.out.println(componentID);

			result = getSkills(sosID, deviceID, componentID).toString();

			output = Response.status(200).entity(result).build();
			break;
		default:
			output = Response.status(404).entity("Invalid data request: " + ontologyRequest).build();
		}
		return output;
	}

}
